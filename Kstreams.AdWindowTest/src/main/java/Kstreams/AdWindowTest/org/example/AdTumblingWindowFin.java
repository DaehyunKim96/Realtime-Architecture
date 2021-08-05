package Kstreams.AdWindowTest.org.example;

import Kstreams.AdWindowTest.org.example.serde.AppSerdes;
import Kstreams.AdWindowTest.org.example.types.SimpleValue;
import Kstreams.AdWindowTest.org.example.types.WindowAgg;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.Properties;

public class AdTumblingWindowFin {
    private static Logger logger = LogManager.getLogger();
    private static Properties streamsProperties() {
        //kafka streams의 property 설정
        final Properties props = new Properties();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServersPri);
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, AppConfigs.applicationID);
        props.put(StreamsConfig.STATE_DIR_CONFIG, AppConfigs.stateStoreName);
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 0);

        return props;
    }
    public static void main(String[] args) {
        Properties props = streamsProperties();
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        AdAppTopology.withBuilder(streamsBuilder);
        KafkaStreams streams = new KafkaStreams(streamsBuilder.build(), props);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Stopping Streams");
            streams.close();
        }));
    }
}
