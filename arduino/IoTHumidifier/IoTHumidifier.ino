/*아두이노에서 센서 정보를 수집하여 AWS로 전송하고, 요청을 받아서 처리함*/

//Library
#include <ArduinoBearSSL.h>
#include <ArduinoECCX08.h>
#include <ArduinoMqttClient.h>
#include <WiFiNINA.h>
#include <ArduinoJson.h>
#include "arduino_secrets.h" //
#include "DHT.h"
#include "Humidifier.h"

//aws set
const char ssid[]        = SECRET_SSID;
const char pass[]        = SECRET_PASS;
const char broker[]      = SECRET_BROKER;
const char* certificate  = SECRET_CERTIFICATE;

WiFiClient    wifiClient;            // Used for the TCP socket connection
BearSSLClient sslClient(wifiClient); // Used for SSL/TLS connection, integrates with ECC508
MqttClient    mqttClient(sslClient);

unsigned long lastMillis = 0;

//Sensor  
#define DHTPIN 2
#define HUMIDIFIER 5
#define WATERPIN A3     
#define DHTTYPE DHT11   
DHT dht(DHTPIN, DHTTYPE);
Humidifier hdr(HUMIDIFIER);

//variance
int criteria = 50;//습도 기준점
String mode_self="auto"; //평소 auto모드, 유저가 on-off명령 내렸을때 mode_self모드
void setup() {
  Serial.begin(115200);
  while (!Serial);
  pinMode(HUMIDIFIER,OUTPUT);
  dht.begin();
  
  if (!ECCX08.begin()) {//인증 요청
    Serial.println("No ECCX08 present!");
    while (1);
  }
  ArduinoBearSSL.onGetTime(getTime);
  sslClient.setEccSlot(0, certificate);
  mqttClient.onMessage(onMessageReceived);//mqtt로부터 요청 받음
}

//mqtt와 매루프마다 연결해서 메세지를 보내고 받음
void loop() {
  if (WiFi.status() != WL_CONNECTED) {
    connectWiFi();
  }

  if (!mqttClient.connected()) {
    // MQTT client is disconnected, connect
    connectMQTT();
  }
  
  // poll for new MQTT messages and send keep alives
  mqttClient.poll();

  // publish a message roughly every 5 seconds.
  if (millis() - lastMillis > 5000) {
    lastMillis = millis();
    char payload[512];
    getDeviceStatus(payload); //센서 정보 수집
    sendMessage(payload); //전송
  }
}
unsigned long getTime() {
  // get the current time from the WiFi module  
  return WiFi.getTime();
}
//와이파이 연결-처음 시작할때만
void connectWiFi() {
  Serial.print("Attempting to connect to SSID: ");
  Serial.print(ssid);
  Serial.print(" ");

  while (WiFi.begin(ssid, pass) != WL_CONNECTED) {
    // failed, retry
    Serial.print(".");
    delay(5000);
  }
  Serial.println();

  Serial.println("You're connected to the network");
  Serial.println();
}

void connectMQTT() {
  Serial.print("Attempting to MQTT broker: ");
  Serial.print(broker);
  Serial.println(" ");

  while (!mqttClient.connect(broker, 8883)) {
    // failed, retry
    Serial.print(".");
    delay(5000);
  }
  Serial.println();

  Serial.println("You're connected to the MQTT broker");
  Serial.println();

  // subscribe to a topic//디바이스 상태 업데이트 관련 구독
  mqttClient.subscribe("$aws/things/Humidifier/shadow/update/delta");
}
//디바이스 센서정보 습득 + 자동 습도 조절
void getDeviceStatus(char* payload) {
  float h =dht.readHumidity(); //습도
  int w = analogRead(WATERPIN)/6;//0~600. 6으로 나눠서 백분율표시

  //자동제어보다 유저제어를 우선시함.
  if(mode_self=="ON"){//유저 제어-ON
    hdr.on();
  }else if(mode_self=="OFF"){//유저 제어-OFF
    hdr.off();
  }else{
    if(h>criteria)  hdr.off(); //기준습도보다 크면 종료
    else            hdr.on();  
  }
  //sprintf로 전송 문장을 만들기 위해 const char*생성
  
  const char* s = (!mode_self.equals("auto")) ? "true" : "false"; //아두이노에선 ON-OFF-auto 자체를 저장하고, 서버로 보낼땐 true-false
  const char* hum = (hdr.getState() == HUMIDIFIER_ON)? "ON" : "OFF";//가습기 상태 체크
  // make payload for the device update topic ($aws/things/Humidifier/shadow/update)
  //습도, 물 잔량, 디바이스 온오프, 기준 습도, 모드->셀프 여부
  sprintf(payload,"{\"state\":{\"reported\":{\"humidity\":\"%0.2f\",\"water\":\"%d\",\"device_onoff\":\"%s\",\"criteria\":\"%d\",\"self\":\"%s\"}}}",h,w,hum,criteria,s);
  Serial.println(payload);
}
void sendMessage(char* payload) {
  char TOPIC_NAME[]= "$aws/things/Humidifier/shadow/update";
  
  Serial.print("Publishing send message:");
  Serial.println(payload);
  mqttClient.beginMessage(TOPIC_NAME);
  mqttClient.print(payload);
  mqttClient.endMessage();
}
//오쳥을 받았을때-on:off or 습도 기준점 변경
//메세지 확인하고 변경후 결과 반환
void onMessageReceived(int messageSize) {
  // we received a message, print out the topic and contents
  Serial.print("Received a message with topic '");
  Serial.print(mqttClient.messageTopic());
  Serial.print("', length ");
  Serial.print(messageSize);
  Serial.println(" bytes:");

  // store the message received to the buffer
  char buffer[512] ;
  int count=0;
  while (mqttClient.available()) {
     buffer[count++] = (char)mqttClient.read();
  }
  buffer[count]='\0'; // 버퍼의 마지막에 null 캐릭터 삽입
  Serial.println(buffer);
  Serial.println();

  /* JSon 형식의 문자열인 buffer를 파싱하여 필요한 값을 얻어옴.
  // 디바이스가 구독한 토픽이 $aws/things/Humidifier/shadow/update/delta
   {
      "version":391,
      "timestamp":1572784097,
      "state":{
          "device_onoff":"ON",
          "criteria":"40",
      },
      "metadata":{
          "humidifier":{
            "timestamp":15727840
           }
      }
   }
  */
  DynamicJsonDocument doc(1024);
  deserializeJson(doc, buffer);
  JsonObject root = doc.as<JsonObject>();
  JsonObject state = root["state"];
  const char * d = state["device_onoff"]; 
  String crt = state["criteria"];
  Serial.print(d);
  Serial.println(crt);
  char payload[512];
  if(d){//디바이스에 대한 요청이 있으면
    if (strcmp(d,"ON")==0) {
      hdr.on();
      sprintf(payload,"{\"state\":{\"reported\":{\"device_onoff\":\"%s\"}}}","ON");//요청 확인 반환
      sendMessage(payload);
      mode_self="ON";
    }else if (strcmp(d,"OFF")==0) {
      hdr.off();
      sprintf(payload,"{\"state\":{\"reported\":{\"device_onoff\":\"%s\"}}}","OFF");
      sendMessage(payload);
      mode_self="OFF";  
    }else if(strcmp(d,"")==0) { //auto모드는 변경없이 현재 상태만 반환
      mode_self="auto"; 
      const char* hum = (hdr.getState() == HUMIDIFIER_ON)? "ON" : "OFF";
      getDeviceStatus(payload);
      sprintf(payload,"{\"state\":{\"reported\":{\"device_onoff\":\"%s\"}}}",hum);
      sendMessage(payload);

    }
  }
  if(crt){//기준습도에 대한 요청이 있으면 습도 변경
    if(crt.toInt()!=0){ 
      criteria=crt.toInt();
      sprintf(payload,"{\"state\":{\"reported\":{\"criteria\":\"%d\"}}}",criteria);
      sendMessage(payload);
    }
  }
}
