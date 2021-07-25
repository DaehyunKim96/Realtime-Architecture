package Influx2Consumer.org.example;

import Influx2Consumer.org.example.serde.JsonDeserializer;
import Influx2Consumer.org.example.types.WinLog;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class InfluxdbConsumer {
    private static Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServersPri);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.VALUE_CLASS_NAME_CONFIG, WinLog.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "win-tmp");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        KafkaConsumer<String, WinLog> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(AppConfigs.winTopicName));
        final String serverURL = AppConfigs.serverURL, org = AppConfigs.org, bucket = AppConfigs.bucket;
        final char[] token = AppConfigs.token;
        InfluxDBClient influxDBClient = InfluxDBClientFactory.create(serverURL, token, org, bucket);

        while (true) {
            ConsumerRecords<String, WinLog> records = consumer.poll(Duration.ofSeconds(10));
            for (ConsumerRecord<String, WinLog> record : records) {
                System.out.println(record.value());
                String ts = record.value().getTimestamp();
                String createdtime = ts.substring(0, 4) + "-" + ts.substring(4, 6) + "-" + ts.substring(6, 8) + "T" + ts.substring(9, 11) + ":" + ts.substring(11, 13) + ":" + ts.substring(13, 15) + "." + ts.substring(16) + "Z";
//                Instant current = Instant.now();
//                String str = "1971-01-01 09:00:00";
                Instant instant = Instant.parse(createdtime);
                try (WriteApi writeApi = influxDBClient.getWriteApi()) {
                    Point point = Point.measurement("mixed_logs")
                            .addTag("bidId", record.value().getBidId())
                            .addField("adAccountId", record.value().getAdAccountId())
                            .addField("creativeId", record.value().getCreativeId())
                            .addField("gender", record.value().getGender())
                            .addField("age", record.value().getAge())
                            .addField("tagId", record.value().getTagId())
                            .addField("chargeAmount", record.value().getChargeAmount())
                            .addField("mapped", record.value().getMapped())
                            .time(Instant.now().toEpochMilli(), WritePrecision.MS);
                    writeApi.writePoint(point);
                }
            }
//            influxDBClient.close();
        }
    }
}
