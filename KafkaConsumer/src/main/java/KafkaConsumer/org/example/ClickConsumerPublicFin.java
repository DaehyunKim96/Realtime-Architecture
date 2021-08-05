package KafkaConsumer.org.example;

import KafkaConsumer.org.example.serde.JsonSerializer;
import KafkaConsumer.org.example.types.ClickLog;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ClickConsumerPublicFin {
    private static Logger logger = LogManager.getLogger();
    private static Consumer<String, String> createConsumer() {
        //consumer의 property 설정
        final Properties prop_con_click= new Properties();
        prop_con_click.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServersPub);
        prop_con_click.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        prop_con_click.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        prop_con_click.put(ConsumerConfig.GROUP_ID_CONFIG, AppConfigs.groupID[1]);
        prop_con_click.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        prop_con_click.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        // consuming할 topic 설정 및 consumer 객체 생성
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(prop_con_click);
        consumer.subscribe(Arrays.asList(AppConfigs.sourceTopicNames[1]));

        return consumer;
    }
    private static Producer<String, ClickLog> createClickProducer() {
        //producer(for clicklog)의 property 설정
        Properties prop_pro_click = new Properties();
        prop_pro_click.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServersPri);
        prop_pro_click.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        prop_pro_click.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        KafkaProducer<String, ClickLog> producer = new KafkaProducer<String, ClickLog>(prop_pro_click);

        return producer;
    }
    static ClickLog parseLog(String log) {
        //consumer로 가져온 원본 log 중 필요한 부분만 파싱
        JSONObject jsonObject = new JSONObject(log);
        JSONObject rawM = jsonObject.getJSONObject("rawMessage");
        JSONObject winner = rawM.getJSONObject("winner");
        JSONObject bid = rawM.getJSONObject("bid");
        JSONObject action = rawM.getJSONObject("action");
        //필요한 데이터를 clickLog 객체에 담아서 return
        ClickLog clickLog = new ClickLog();
        clickLog.setBidId(bid.getString("bidRequestId"));
        clickLog.setTimestamp(action.getString("actionDttm"));
        clickLog.setChargeAmount(winner.getDouble("chargeAmount"));

        return clickLog;
    }
    static void runConsumer() throws InterruptedException {
        final Consumer<String, String> consumer = createConsumer();
        final Producer<String, ClickLog> producer = createClickProducer();
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(10));
            for (ConsumerRecord<String, String> record : records) {
                // 원본 log를 파싱하여 clicklog 객체에 저장
                ClickLog clickLog = parseLog(record.value());
                // Winlog와 mapping을 하기 위해 bidId(bidRequestId)를 key 값으로 설정
                // click-log topic으로 produce
                String bidId = clickLog.getBidId();
                ProducerRecord<String, ClickLog> prod = new ProducerRecord<String, ClickLog>(AppConfigs.clickTopicName, bidId, clickLog);
                producer.send(prod);
            }
        }
    }
    public static void main(String[] args) throws Exception{
        runConsumer();
    }
}
