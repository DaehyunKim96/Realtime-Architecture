package Influx2Consumer.org.example;

import Influx2Consumer.org.example.serde.JsonSerializer;
import Influx2Consumer.org.example.types.WinLog;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
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

public class WinConsumerPublic {
    private static Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        Properties prop_con_win = new Properties();
        prop_con_win.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServersPub);
        prop_con_win.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        prop_con_win.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        prop_con_win.put(ConsumerConfig.GROUP_ID_CONFIG, AppConfigs.groupID[0]);
        prop_con_win.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        prop_con_win.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        Properties prop_pro_win = new Properties();
        prop_pro_win.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServersPri);
        prop_pro_win.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        prop_pro_win.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(prop_con_win);
        consumer.subscribe(Arrays.asList(AppConfigs.sourceTopicNames[0]));

        KafkaProducer<String, WinLog> producer = new KafkaProducer<String, WinLog>(prop_pro_win);

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(10));
            for(ConsumerRecord<String, String> record : records) {
                JSONObject jsonObject = new JSONObject(record.value());
                JSONObject rawM = jsonObject.getJSONObject("rawMessage");
                JSONObject winner = rawM.getJSONObject("winner");
                JSONObject user = rawM.getJSONObject("user");
                JSONObject ssp = rawM.getJSONObject("ssp");
                JSONObject bid = rawM.getJSONObject("bid");
                JSONObject action = rawM.getJSONObject("action");

                String timestamp = action.getString("actionDttm");
                Integer adAccountId = winner.getInt("adAccountId");
                Integer creativeId = winner.getInt("creativeId");
                Integer age = user.getInt("age");
                String gender = user.getString("gender");
                String tagId = ssp.getString("tagId");
                String bidId = bid.getString("bidRequestId");
                Double chargeAmount = winner.getDouble("chargeAmount");

                WinLog winlog = new WinLog();
                winlog.setTimestamp(timestamp);
                winlog.setAdAccountId(adAccountId);
                winlog.setCreativeId(creativeId);
                winlog.setAge(age);
                winlog.setGender(gender);
                winlog.setTagId(tagId);
                winlog.setBidId(bidId);
                winlog.setChargeAmount(chargeAmount);
                winlog.setMapped(0);
                System.out.println(bidId+" "+timestamp+" "+adAccountId+" "+creativeId+" "+age+" "+gender+" "+tagId+" "+chargeAmount);

                ProducerRecord<String, WinLog> prod = new ProducerRecord<String, WinLog>(AppConfigs.winTopicName, bidId, winlog);
                producer.send(prod);

            }

        }



    }

}
