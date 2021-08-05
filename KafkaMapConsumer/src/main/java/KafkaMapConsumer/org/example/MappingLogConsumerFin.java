package KafkaMapConsumer.org.example;

import KafkaMapConsumer.org.example.serde.JsonDeserializer;
import KafkaMapConsumer.org.example.serde.JsonSerializer;
import KafkaMapConsumer.org.example.types.SimpleValue;
import KafkaMapConsumer.org.example.types.WinLog;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class MappingLogConsumerFin {
    private static Logger logger = LogManager.getLogger();
    private static Consumer<String, WinLog> createConsumer() {
        //consumer의 property 설정
        final Properties prop_map = new Properties();
        prop_map.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServersPri);
        prop_map.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        prop_map.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        prop_map.put(JsonDeserializer.VALUE_CLASS_NAME_CONFIG, WinLog.class);
        prop_map.put(ConsumerConfig.GROUP_ID_CONFIG, AppConfigs.groupID[2]);
        prop_map.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        prop_map.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        // consuming할 topic 설정 및 consumer 객체 생성
        // mapping이 완료된 log를 광고주, GAS 기준 topic으로 전송
        KafkaConsumer<String, WinLog> consumer = new KafkaConsumer<>(prop_map);
        consumer.subscribe(Arrays.asList(AppConfigs.mappedTopicName));

        return consumer;
    }
    private static Producer<String, SimpleValue> createAdProducer() {
        //producer(for group by adaccount)의 property 설정
        Properties prop_a = new Properties();
        prop_a.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServersPri);
        prop_a.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        prop_a.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        KafkaProducer<String, SimpleValue> producerA = new KafkaProducer<>(prop_a);

        return producerA;
    }
    private static Producer<String, SimpleValue> createGasProducer() {
        Properties prop_pro_win_g = new Properties();
        //producer(for group by gas)의 property 설정
        Properties prop_g = new Properties();
        prop_g.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServersPri);
        prop_g.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        prop_g.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        KafkaProducer<String, SimpleValue> producerG = new KafkaProducer<>(prop_g);

        return producerG;
    }
    static void runConsumer() throws InterruptedException {
        final Consumer<String, WinLog> consumer = createConsumer();
        final Producer<String, SimpleValue> producerA = createAdProducer();
        final Producer<String, SimpleValue> producerG = createGasProducer();
        while (true) {
            ConsumerRecords<String, WinLog> records = consumer.poll(Duration.ofSeconds(10));
            for (ConsumerRecord<String, WinLog> record : records) {
                //mapping된 log의 data WinLog 객체에 저장
                WinLog winLog = record.value();
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
                simpleValue.setMapped(1);
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

