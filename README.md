# Arudino_IoTHumidifier
아두이노와 AWS로 제작한 IoT가습기

-실행 방법: 


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
2. 
