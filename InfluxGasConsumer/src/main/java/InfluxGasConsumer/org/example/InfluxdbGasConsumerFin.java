package InfluxGasConsumer.org.example;

import InfluxGasConsumer.org.example.serde.AppSerdes;
import InfluxGasConsumer.org.example.serde.JsonDeserializer;
import InfluxGasConsumer.org.example.types.WindowAgg;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.kstream.WindowedSerdes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class InfluxdbGasConsumerFin {
    private static Logger logger = LogManager.getLogger();
    private static Consumer<Windowed<String>, WindowAgg> createConsumer() {
        //consumer의 property 설정
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServersPri);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.VALUE_CLASS_NAME_CONFIG, WindowAgg.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, AppConfigs.groupID);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        // windowed된 string key의 deserializer
        final Deserializer<Windowed<String>> windowedDeserializer = WindowedSerdes.timeWindowedSerdeFrom(String.class).deserializer();
        // consuming할 topic 설정 및 consumer 객체 생성
        KafkaConsumer<Windowed<String>, WindowAgg> consumer = new KafkaConsumer<>(props, windowedDeserializer, AppSerdes.WindowAgg().deserializer());
        consumer.subscribe(Arrays.asList(AppConfigs.gasGroupbyName));

        return consumer;
    }
    static InfluxDB connectInfluxdb() {
        final String serverURL = AppConfigs.serverURL, username = AppConfigs.id, password = AppConfigs.pw;
        // influxDBFactory를 통해 DB와 연결
        final InfluxDB influxDB = InfluxDBFactory.connect(serverURL, username, password);
        influxDB.setDatabase(AppConfigs.databaseName);
        influxDB.enableBatch(BatchOptions.DEFAULTS);

        return influxDB;
    }
    static void runConsumer() throws InterruptedException {
        final Consumer<Windowed<String>, WindowAgg> consumer = createConsumer();
        final InfluxDB influxDB = connectInfluxdb();
        while (true) {
            ConsumerRecords<Windowed<String>, WindowAgg> records = consumer.poll(Duration.ofSeconds(10));
            for (ConsumerRecord<Windowed<String>, WindowAgg> record : records) {
                // log의 key를 '-'로 split
                String groupedKeyString = record.key().key();
                String[] groupedKey = groupedKeyString.split("-");
                String gender = groupedKey[0];
                String age = groupedKey[1];
                String tagId = groupedKey[2] + '-' + groupedKey[3];
                // WindowAgg에서 win, click, chargeAmount 추출하기
                Integer all = record.value().getWin();
                Integer click = record.value().getClick();
                Integer win = all - click;
                Double chargeAmount = record.value().getChargeAmount();
                //Time 정의
                long timeMillis = record.key().window().start();
                //utc 기준으로 맞추기 위해 9시간을 보정한다.
                long timeCorrection = 9 * 60 * 60 *1000;
                long eventTime = timeMillis - timeCorrection;
                //influxdb에 insert
                influxDB.write(Point.measurement("gas")
                        .time(eventTime, TimeUnit.MILLISECONDS)
                        .tag("gender", gender)
                        .tag("age", age)
                        .tag("tagId", tagId)
                        .addField("win", win)
                        .addField("click", click)
                        .addField("chargeAmount", chargeAmount)
                        .build());
            }
        }
    }
    public static void main(String[] args) throws Exception{
        runConsumer();
    }
}

