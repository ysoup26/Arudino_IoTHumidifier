# Arudino_IoTHumidifier
-아두이노(MKR wifi 1010)와 [AWS](https://aws.amazon.com/ko/console/), 안드로이드 스튜디오를 이용하여 제작한 IoT가습기

## 실행 방법
### 해당 깃헙의 파일을 다운 받아서 각 순서에 따라 실행함 
- [Arduino](#1-arduino)
- [AWS](#2-aws)
- [API](#3-api)
- [APP](#4-app)

# 1. Arduino

>회로 구성

![회로도](/img/fritzing.png)


>보드 매니저 및 라이브러리 설치
- 보드 매니저: 툴>보드>보드 매니저>Arduino SAMD Boards
- 라이브러리:  툴>라이브러리 관리
  - WiFiNINA (or WiFi101 for the MKR1000)
  - ArduinoBearSSL
  - ArduinoECCX08
  - ArduinoMqttClient
  - Arduino Cloud Provider Examples

>디바이스 인증서 생성
1. ArduinoECCX08-Tools-ECCX08CSR 파일을 보드에 업로드하여 x.509 인증서 획득
2. common Name을 입력하고 나온 인증서 내용을 csr.txt라는 파일을 만들어 저장
3. AWS 디바이스 생성시에 해당 csr.txt를 인증서로 사용

>코드 설정 및 실행
1. arduino/IoTHumidifier/IoTHumidifier.ino 오픈
2. arduino_secrets.h 파일의 변수들 수정(wifi, pass, endpoint ..)
3. 파일 실행


# 2. AWS

## 1. 디바이스 등록

AWS IoT Core에 접속

사물 이름 : Humidifier

이름 없는 섀도우 (클래식)

모든 것을 허용하는 정책


## 2. DynamoDB

### DynamoDB 테이블 등록

테이블 이름 : HumidLog

파티션 키 : device (문자열)

정렬 키 : time (숫자)

### Lambda 함수 등록

1. Eclipse용 AWS Toolkit을 이용하여 자바 람다 프로젝트 생성
2. HumidDynamoDB2.java 파일을 복사하여 사용
3. 람다 함수를 AWS 상에 업로드

### 규칙 설정

AWS IoT Core > 메시지 라우팅 > 규칙 > 규칙 생성

SQL문 : SELECT *, 'Humidifier' as device FROM '$aws/things/Humidifier/shadow/update/documents'

작업에서 작업 구성에 Lambda(Lambda 함수에 메시지 보내기) 선택, 방금 등록한 Lamda함수 선택


## 3. 상태조회 API

### Lambda 함수

1. Eclipse용 AWS Toolkit을 이용하여 자바 람다 프로젝트 생성
2. pom.xml 파일을 열어 다음과 같은 내용 추가

<pre>
<code>
  &lt;dependencies>
    ...    
    &lt;dependency>
      &lt;groupId>com.amazonaws&lt;/groupId>
      &lt;artifactId\>aws-java-sdk-iot&lt;/artifactId>
    &lt;/dependency>

  &lt;/dependencies>  
</code>
</pre>

3. GetDeviceHandler.java 파일을 복사하여 사용
4. 람다 함수를 AWS 상에 업로드


## 4. 상태 변경 API

### Lambda 함수

1. Eclipse용 AWS Toolkit을 이용하여 자바 람다 프로젝트 생성
2. pom.xml 파일을 열어 다음과 같은 내용 추가

<pre>
<code>
  &lt;dependencies>
    ...    
    &lt;dependency>
      &lt;groupId>com.amazonaws&lt;/groupId>
      &lt;artifactId\>aws-java-sdk-iot&lt;/artifactId>
    &lt;/dependency>

  &lt;/dependencies>  
</code>
</pre>

3. UpdateDeviceHandler.java 파일을 복사하여 사용
4. 람다 함수를 AWS 상에 업로드


## 5. 로그 조회 API

### Lambda 함수

1. Eclipse용 AWS Toolkit을 이용하여 자바 람다 프로젝트 생성
2. HumidLogHandler.java 파일을 복사하여 사용
3. 람다 함수를 AWS 상에 업로드

# 3. API
### 업로드한 Lambda 함수를 이용하여 API 구축

![api 구조](/img/api_structure.png)

>API 구조 설계
1.  AWS>API Gateway에서 api 생성>Rest API 선택
2.  작업을 선택하여 사진과 동일한 api 구조를 작성함
3.  메소드는 리소스를 클릭한 후, 작업>메소드 생성> 해당하는 Lambda함수와 연결 

>API 구조 설계 세부
## /device/{device}/ GET
1. GET>통합요청 선택
2. 매핑 템플릿>정의된 템플릿이 없는 경우(권장) 선택
3. 템플릿 내용에 아래 코드 입력 후 저장
<pre>
<code>
  {
    "device": "$input.params('device')"
  } 
</code>
</pre>

## /device/{device}/ PUT
1. API>모델 선택
2. 모델 생성> 모델 이름: UpdateDeviceInput , 콘텐츠 유형: application/json, 모델 스키마에 아래 코드 입력
<pre>
<code>
{
  "$schema": "http://json-schema.org/draft-04/schema#",
  "title": "UpdateDeviceInput",
  "type" : "object",
  "properties" : {
      "tags" : {
          "type": "array",
          "items": {
              "type": "object",
              "properties" : {
                "tagName" : { "type" : "string"},
                "tagValue" : { "type" : "string"}
              }
          }
      }
  }
}
</code>
</pre>
4. PUT>통합요청 선택
5. 매핑 템플릿>정의된 템플릿이 없는 경우(권장) 선택
6. 템플릿 내용에 아래 코드 입력 후 저장
<pre>
<code>
  #set($inputRoot = $input.path('$'))
{
    "device": "$input.params('device')",
    "tags" : [
    ##TODO: Update this foreach loop to reference array from input json
        #foreach($elem in $inputRoot.tags)
        {
            "tagName" : "$elem.tagName",
            "tagValue" : "$elem.tagValue"
        } 
        #if($foreach.hasNext),#end
        #end
    ]
  }
</code>
</pre>

## /device/{device}/log GET 
1. GET>통합요청 선택
2. 매핑 템플릿>정의된 템플릿이 없는 경우(권장) 선택
3. 템플릿 내용에 아래 코드 입력 후 저장
<pre>
<code>
  {
  "device": "$input.params('device')",
  "from": "$input.params('from')",
  "to":  "$input.params('to')"
  }
</code>
</pre>
4. GET>메소드요청 선택
5. URL 쿼리 문자열 파라미터 선택
6. 쿼리 문자열 추가로 from과 to 각각 추가

> API배포
### 만든 API를 링크 형식으로 사용할 수 있도록 배포
1. 리소스를 선택하고 작업> CORS 활성화 > CORS 활성화 및 기존의 CORS 헤더 대체 선택
2. 1의 내용을 3개의 리소스에 각각 수행
3. 작업>API 배포 선택
4. 배포 스테이지를 [새 스테이지]로 선택하고 api 입력
5. 배포 선택 후 주어진 링크로 테스트


# 4. APP
### 안드로이드 스튜디오에서 
/Arduino_IoTHumidifier/android_studio_app/IoTHumidifier 실행

> 앱 실행화면

![메인 화면](/img/app_main.png)
![로그 조회 화면](/img/app_log.png)


> 앱 기능 설명
- 메인화면
  - 조회 시작 버튼: 일정시간마다 디바이스 정보를 조회하여 현재 상태에 출력
  - 조회 종료 버튼: 일정시간마다 디바이스 정보를 조회하는 것을 멈춤
  - 상태 변경 버튼: 디바이스의 상태를 변경하는 다이얼로그를 띄움
  - 로그 조회 버튼: 로그를 조회할 수 있는 페이지를 띄움
- 로그 조회 화면
  - 조회 시작&종료 날짜, 시간 버튼: 날짜와 시간을 선택하는 타임피커를 띄움
  - 로그 조회 시작: 시작, 종료 날짜 사이의 데이터를 리스트 형식으로 반환
  