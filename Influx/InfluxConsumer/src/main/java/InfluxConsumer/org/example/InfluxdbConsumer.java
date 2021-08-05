package InfluxConsumer.org.example;

import InfluxConsumer.org.example.serde.JsonDeserializer;
import InfluxConsumer.org.example.types.WinLog;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;

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
        final String serverURL = AppConfigs.serverURL, username = AppConfigs.id, password = AppConfigs.pw;
        final InfluxDB influxDB = InfluxDBFactory.connect(serverURL, username, password);
        String databaseName = "logs";
        influxDB.setDatabase(databaseName);

//        String retentionPolicyName = "one_hour_only";
//        influxDB.query(new Query("CREATE RETENTION POLICY " + retentionPolicyName
//                + " ON " + databaseName + " DURATION 1h REPLICATION 1 DEFAULT"));
//        influxDB.setRetentionPolicy(retentionPolicyName);

        influxDB.enableBatch(BatchOptions.DEFAULTS);

        while (true) {
            ConsumerRecords<String, WinLog> records = consumer.poll(Duration.ofSeconds(10));
            for (ConsumerRecord<String, WinLog> record : records) {
                System.out.println(record.value());
                String ts = record.value().getTimestamp();
                String createdtime = ts.substring(0,4)+"-"+ts.substring(4,6)+"-"+ts.substring(6,8)+"T"+ts.substring(9,11)+":"+ts.substring(11,13)+":"+ts.substring(13,15)+"."+ts.substring(16,19)+"+0900";
//                Instant current = Instant.now();
//                String str = "1971-01-01 09:00:00";
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = null;

                try{
                    date = format.parse(createdtime);
                }catch(ParseException e){}

                long timeMillis = date.getTime();
//                String tmp = Long.toString(timeMillis) + "000000";
//                long timestamp = Long.parseLong(tmp);
//                System.out.println(timestamp);
//                System.out.println(System.currentTimeMillis());
//                long curTime = System.currentTimeMillis();
                influxDB.write(Point.measurement("50tps_logs")
//                        .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                        .time(timeMillis,TimeUnit.MILLISECONDS) //timestamp
                        .tag("bidId", record.value().getBidId())
                        .tag("adAccountId", Integer.toString(record.value().getAdAccountId()))
                        .tag("creativeId", Integer.toString(record.value().getCreativeId()))
                        .tag("gender", record.value().getGender())
                        .tag("age", Integer.toString(record.value().getAge()))
                        .tag("tagId", record.value().getTagId())
                        .addField("chargeAmount", record.value().getChargeAmount())
                        .addField("mapped", record.value().getMapped())
                        .build());
            }
        }
    }
}
