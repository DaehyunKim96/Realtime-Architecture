package Influx2Consumer.org.example;
import Influx2Consumer.org.example.serde.JsonDeserializer;
import Influx2Consumer.org.example.types.WinLog;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class testConsumer {
    private static Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServersPri);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.VALUE_CLASS_NAME_CONFIG, WinLog.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,"win-tmp");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");

        KafkaConsumer<String, WinLog> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(AppConfigs.winTopicName));

        while (true) {
            ConsumerRecords<String, WinLog> records = consumer.poll(Duration.ofSeconds(10));
            for(ConsumerRecord<String, WinLog> record : records) {
                System.out.println(record.key());
                System.out.println(record.value().toString());
            }
        }
    }

}
