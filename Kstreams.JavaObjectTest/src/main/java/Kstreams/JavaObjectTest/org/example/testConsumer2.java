package Kstreams.JavaObjectTest.org.example;

import Kstreams.JavaObjectTest.org.example.serde.JsonDeserializer;
import Kstreams.JavaObject.org.example.types.*;
import Kstreams.JavaObjectTest.org.example.types.SimpleValue;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.kstream.WindowedSerdes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class testConsumer2 {
    private static Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServersPri);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.VALUE_CLASS_NAME_CONFIG, SimpleValue.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,"monkey");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"latest");
        final Deserializer<Windowed<String>> windowedDeserializer = WindowedSerdes.timeWindowedSerdeFrom(String.class).deserializer();
//        KafkaConsumer<Windowed<String>, WindowAgg> consumer = new KafkaConsumer<>(props,windowedDeserializer , AppSerdes.WindowAgg().deserializer());
        KafkaConsumer<String, SimpleValue> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(AppConfigs.adTopicName));

        while (true) {
//            ConsumerRecords<Windowed<String> , WindowAgg> records = consumer.poll(Duration.ofSeconds(10));
            ConsumerRecords<String , SimpleValue> records = consumer.poll(Duration.ofSeconds(10));
//            for(ConsumerRecord<Windowed<String>, WindowAgg> record : records) {
            for(ConsumerRecord<String, SimpleValue> record : records) {
//                System.out.println(record.key().window().start());
                System.out.println(record.key());
//                System.out.println(record.key().key());
//                String ts = record.value().getTimestamp();
//                String createdtime = ts.substring(0,4)+"-"+ts.substring(4,6)+"-"+ts.substring(6,8)+"T"+ts.substring(9,11)+":"+ts.substring(11,13)+":"+ts.substring(13,15)+"."+ts.substring(16)+"Z";
//                System.out.println(createdtime);
                System.out.println(record.value().getTimestamp());
                System.out.println(record.value().getMapped());
                System.out.println(record.value().getChargeAmount());
            }
        }
    }

}
