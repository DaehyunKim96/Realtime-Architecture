//package Kstreams.JavaObject.org.example;
//
//import Kstreams.JavaObject.org.example.serde.AppSerdes;
//import Kstreams.JavaObject.org.example.types.*;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.apache.kafka.common.serialization.Serdes;
//import org.apache.kafka.streams.KafkaStreams;
//import org.apache.kafka.streams.StreamsBuilder;
//import org.apache.kafka.streams.StreamsConfig;
//import org.apache.kafka.streams.kstream.*;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import java.time.Duration;
//import java.util.Properties;
//
//public class WinClickMappingNew {
//    private static Logger logger = LogManager.getLogger();
//
//    public static void main(String[] args) {
//        Properties props = new Properties();
//        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServersPri);
//        props.put(StreamsConfig.APPLICATION_ID_CONFIG, AppConfigs.applicationID);
//        props.put(StreamsConfig.STATE_DIR_CONFIG, AppConfigs.stateStoreName);
//        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 0);
//
//        StreamsBuilder streamsBuilder = new StreamsBuilder();
//        KStream<String, WinLog> KS0 = streamsBuilder.stream(AppConfigs.winTopicName,
//                Consumed.with(AppSerdes.String(), AppSerdes.WinLog())
//                        .withTimestampExtractor(AppTimestampExtractor.WinLog())
//        );
//        KStream<String, ClickLog> KS1 = streamsBuilder.stream(AppConfigs.clickTopicName,
//                Consumed.with(AppSerdes.String(), AppSerdes.ClickLog())
//                        .withTimestampExtractor(AppTimestampExtractor.ClickLog())
//        );
//
//        KStream<AdAccount, SimpleValue> mappingLogA = KS0.join(KS1,
//                (v1, v2) -> {
//                    AdAccount adAccount = new AdAccount();
//                    SimpleValue simpleValue = new SimpleValue();
//                    if (v1 != null) {
//                        adAccount.withAdAccountId(v1.getAdAccountId())
//                                .withCreativeId(v1.getCreativeId());
//                        simpleValue.withChargeAmount(v2.getChargeAmount())
//                                .withTimestamp(v1.getTimestamp())
//                                .withMapped(1);
//                    }
//                    System.out.println(simpleValue.getTimestamp());
//                    return adAccount, gas, simpleValue;
//                },
//                JoinWindows.of(Duration.ofMinutes(10)),
//                Joined.with(AppSerdes.String(), AppSerdes.WinLog(), AppSerdes.ClickLog())
//        );
//        KStream<String, WinLog> mappingLogG = KS0.join(KS1,
//                (v1, v2) -> {
//                    AdAccount adAccount = new AdAccount();
//                    GAS gas = new GAS();
//                    SimpleValue simpleValue = new SimpleValue();
//                    if (v1 != null) {
//                        adAccount.withAdAccountId(v1.getAdAccountId())
//                                .withCreativeId(v1.getCreativeId());
//                        gas.withGender(v1.getGender())
//                                .withAge(v1.getAge())
//                                .withTagId(v1.getBidId());
//                        simpleValue.withChargeAmount(v2.getChargeAmount())
//                                .withTimestamp(v1.getTimestamp())
//                                .withMapped(1);
//                    }
//                    System.out.println(simpleValue.getTimestamp());
//                    return adAccount, gas, simpleValue;
//                },
//                JoinWindows.of(Duration.ofMinutes(10)),
//                Joined.with(AppSerdes.String(), AppSerdes.WinLog(), AppSerdes.ClickLog())
//        );
//
//        AdAccount adAccount = new AdAccount();
//        GAS gas = new GAS();
//        SimpleValue simpleValue = new SimpleValue();
//        mappingLog.to(AppConfigs.mappedTopicName, Produced.with(Serdes.String(), AppSerdes.WinLog()));
//        mappingLog.to(AppConfigs.adTopicName, Produced.with(Serdes.String(), AppSerdes.WinLog()));
//        mappingLog.to(AppConfigs.gasTopicName, Produced.with(Serdes.String(), AppSerdes.WinLog()));
//        logger.info("Starting Stream...");
//        KafkaStreams streams = new KafkaStreams(streamsBuilder.build(), props);
//        streams.start();
//
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            logger.info("Stopping Streams...");
//            streams.close();
//        }));
//    }
//}
