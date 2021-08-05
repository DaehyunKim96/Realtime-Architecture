package KafkaWinConsumer.org.example;

import KafkaWinConsumer.org.example.serde.AppSerdes;
import KafkaWinConsumer.org.example.serde.JsonSerializer;
import KafkaWinConsumer.org.example.types.AdAccount;
import KafkaWinConsumer.org.example.types.GAS;
import KafkaWinConsumer.org.example.types.SimpleValue;
import KafkaWinConsumer.org.example.types.WinLog;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class WinConsumerPublicNewFin {
    private static Logger logger = LogManager.getLogger();
    private static Consumer<String, String> createConsumer() {
        //consumer의 property 설정
        final Properties prop_con_win = new Properties();
        prop_con_win.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServersPub);
        prop_con_win.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        prop_con_win.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        prop_con_win.put(ConsumerConfig.GROUP_ID_CONFIG, AppConfigs.groupID[0]);
        prop_con_win.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        prop_con_win.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        // consuming할 topic 설정 및 consumer 객체 생성
        final KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(prop_con_win);
        consumer.subscribe(Arrays.asList(AppConfigs.sourceTopicNames[0]));

        return consumer;
    }
    private static Producer<String, WinLog> createWinProducer() {
        //producer(for winlog)의 property 설정
        Properties prop_pro_win = new Properties();
        prop_pro_win.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServersPri);
        prop_pro_win.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        prop_pro_win.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        KafkaProducer<String, WinLog> producer = new KafkaProducer<String, WinLog>(prop_pro_win);

        return producer;
    }
    private static Producer<String, SimpleValue> createAdProducer() {
        //producer(for group by adaccount)의 property 설정
        Properties prop_pro_win_a = new Properties();
        prop_pro_win_a.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServersPri);
        prop_pro_win_a.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        prop_pro_win_a.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        KafkaProducer<String, SimpleValue> producerA = new KafkaProducer<>(prop_pro_win_a);

        return producerA;
    }
    private static Producer<String, SimpleValue> createGasProducer() {
        Properties prop_pro_win_g = new Properties();
        //producer(for group by gas)의 property 설정
        prop_pro_win_g.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServersPri);
        prop_pro_win_g.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        prop_pro_win_g.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        KafkaProducer<String, SimpleValue> producerG = new KafkaProducer<>(prop_pro_win_g);

        return producerG;
    }
    static WinLog parseLog(String log) {
        //consumer로 가져온 원본 log 중 필요한 부분만 파싱
        JSONObject jsonObject = new JSONObject(log);
        JSONObject rawM = jsonObject.getJSONObject("rawMessage");
        JSONObject winner = rawM.getJSONObject("winner");
        JSONObject user = rawM.getJSONObject("user");
        JSONObject ssp = rawM.getJSONObject("ssp");
        JSONObject bid = rawM.getJSONObject("bid");
        JSONObject action = rawM.getJSONObject("action");
        //필요한 데이터를 winLog 객체에 담아서 return
        WinLog winLog = new WinLog();
        winLog.setTimestamp(action.getString("actionDttm"));
        winLog.setAdAccountId(winner.getInt("adAccountId"));
        winLog.setCreativeId(winner.getInt("creativeId"));
        winLog.setGender(user.getString("gender"));
        winLog.setAge(user.getInt("age"));
        winLog.setTagId(ssp.getString("tagId"));
        winLog.setBidId(bid.getString("bidRequestId"));
        winLog.setChargeAmount(winner.getDouble("chargeAmount"));
        winLog.setMapped(0);

        return winLog;
    }
    static void runConsumer() throws InterruptedException {
        final Consumer<String, String> consumer = createConsumer();
        final Producer<String, WinLog> producer = createWinProducer();
        final Producer<String, SimpleValue> producerA = createAdProducer();
        final Producer<String, SimpleValue> producerG = createGasProducer();
        while(true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(10));
            for(ConsumerRecord<String, String> record : records) {
                // 원본 log를 파싱하여 winlog 객체에 저장
                WinLog winLog =  parseLog(record.value());
                // adAccount는 광고주 기준 prodcuer의 key 값
                // [adAccountId]-[creativeId] 형태
                String adAccountId = Integer.toString(winLog.getAdAccountId());
                String creativeId = Integer.toString(winLog.getCreativeId());
                String adAccount = adAccountId + '-' + creativeId;
                // gas는 GAS(Gender, Age, TagId) 기준 prodcuer의 key 값
                // [gender]-[age]-[TagId] 형태
                String gender = winLog.getGender();
                String age = Integer.toString(winLog.getAge());
                String tagId = winLog.getTagId();
                String gas = gender + '-' + age + '-' + tagId;
                // simpleValue는 광고주, GAS 기준 producer의 value 값
                String timestamp = winLog.getTimestamp();
                Double chargeAmount = winLog.getChargeAmount();
                SimpleValue simpleValue = new SimpleValue();
                simpleValue.setTimestamp(timestamp);
                simpleValue.setChargeAmount(chargeAmount);
                simpleValue.setMapped(0);
                //win-log topic으로 produce
                String bidId = winLog.getBidId();
                ProducerRecord<String, WinLog> prod = new ProducerRecord<String, WinLog>(AppConfigs.winTopicName, bidId, winLog);
                producer.send(prod);
                //ad-log topic으로 produce
                ProducerRecord<String, SimpleValue> prodA = new ProducerRecord<String, SimpleValue>(AppConfigs.adTopicName, adAccount, simpleValue);
                producerA.send(prodA);
                //gas-log topic으로 produce
                ProducerRecord<String, SimpleValue> prodG = new ProducerRecord<String, SimpleValue>(AppConfigs.gasTopicName, gas, simpleValue);
                producerG.send(prodG);
            }
        }
    }
    public static void main(String[] args) throws Exception{
        runConsumer();
    }

}
