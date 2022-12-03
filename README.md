# Arudino_IoTHumidifier
-아두이노(MKR wifi 1010)와 [AWS](https://aws.amazon.com/ko/console/), 안드로이드 스튜디오를 이용하여 제작한 IoT가습기

## 실행 방법
- [Arduino](#1-arduino)
- [AWS](#2-aws)
- [APP](#3-app)

## 1. Arduino

>회로 구성
- (회로 이미지 넣기)

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


## 2. AWS


## 3. APP

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
