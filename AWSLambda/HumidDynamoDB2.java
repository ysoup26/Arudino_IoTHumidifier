/*
* 디바이스에서 받은 정보를 DynamoDB 테이블에 올리는 작업을 수행하는 람다 함수입니다.
*/

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class HumidDynamoDB2 implements RequestHandler<Document, String> {
    private DynamoDB dynamoDb;
    private String DYNAMODB_TABLE_NAME = "HumidLog";  // DynamoDB 테이블 이름.

    @Override
    public String handleRequest(Document input, Context context) {
        this.initDynamoDbClient();

        persistData(input);
        return "Success in storing to DB!";
    }

    private String persistData(Document document) throws ConditionalCheckFailedException {

        // Epoch Conversion Code: https://www.epochconverter.com/
        SimpleDateFormat sdf = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String timeString = sdf.format(new java.util.Date (document.timestamp*1000));

        if (document.current.state.reported.humidity.equals(document.previous.state.reported.humidity) && 
                document.current.state.reported.water.equals(document.previous.state.reported.water) &&
                document.current.state.reported.device_onoff.equals(document.previous.state.reported.device_onoff) &&
                document.current.state.reported.criteria.equals(document.previous.state.reported.criteria) &&
                document.current.state.reported.self.equals(document.previous.state.reported.self)) {
                return null;
        }
        
        return this.dynamoDb.getTable(DYNAMODB_TABLE_NAME)
                .putItem(new PutItemSpec().withItem(new Item().withPrimaryKey("device", document.device) // 파티션 키
                		.withLong("time", document.timestamp)  // 정렬 키
                        .withString("humidity", document.current.state.reported.humidity)
                        .withString("water", document.current.state.reported.water)
                        .withString("device_onoff", document.current.state.reported.device_onoff)
                        .withString("criteria", document.current.state.reported.criteria)
                        .withString("self", document.current.state.reported.self)
                        .withString("timestamp",timeString)))
                .toString();
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-1").build();

        this.dynamoDb = new DynamoDB(client);
    }

}


class Document {
    public Thing previous;       
    public Thing current;
    public long timestamp;
    public String device;       // AWS IoT에 등록된 사물 이름 
}

class Thing {
    public State state = new State();
    public long timestamp;
    public String device = "Humidifier";
    
    public class State {
        public Tag reported = new Tag();
        public Tag desired = new Tag();

        public class Tag {
            public String humidity;      // 습도
            public String water;         // 물의 잔량 
            public String device_onoff;  // 가슴기 On/Off
            public String criteria;      // 자동 On/Off 기준치
            public String self;          // Auto or Self 
        }
    }
}
