package KafkaConsumer.org.example;

import KafkaConsumer.org.example.serde.JsonDeserializer;
import KafkaConsumer.org.example.serde.JsonSerializer;
import KafkaConsumer.org.example.types.ClickLog;
import KafkaConsumer.org.example.types.WinLog;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONObject;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ClickConsumerPublic {
//    private static Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        Properties prop_con_click= new Properties();
        prop_con_click.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServersPub);
        prop_con_click.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        prop_con_click.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        prop_con_click.put(ConsumerConfig.GROUP_ID_CONFIG, AppConfigs.groupID[1]);
        prop_con_click.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        prop_con_click.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        Properties prop_pro_click = new Properties();
        prop_pro_click.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServersPri);
        prop_pro_click.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        prop_pro_click.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(prop_con_click);
        consumer.subscribe(Arrays.asList(AppConfigs.sourceTopicNames[1]));

        KafkaProducer<String, ClickLog> producer = new KafkaProducer<String, ClickLog>(prop_pro_click);

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(10));
            for(ConsumerRecord<String, String> record : records) {
                JSONObject jsonObject = new JSONObject(record.value());
                JSONObject rawM = jsonObject.getJSONObject("rawMessage");
                JSONObject winner = rawM.getJSONObject("winner");
                JSONObject bid = rawM.getJSONObject("bid");
                JSONObject action = rawM.getJSONObject("action");

                String timestamp = action.getString("actionDttm");
                String bidId = bid.getString("bidRequestId");
                Double chargeAmount = winner.getDouble("chargeAmount");

                ClickLog clicklog = new ClickLog();
                clicklog.setTimestamp(timestamp);
                clicklog.setBidId(bidId);
                clicklog.setChargeAmount(chargeAmount);
                System.out.println(timestamp);

                ProducerRecord<String, ClickLog> prod = new ProducerRecord<String, ClickLog>(AppConfigs.clickTopicName, bidId, clicklog);
                producer.send(prod);

            }

        }



    }

}