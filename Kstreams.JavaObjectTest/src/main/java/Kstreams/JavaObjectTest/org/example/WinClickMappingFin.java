package Kstreams.JavaObjectTest.org.example;

import Kstreams.JavaObjectTest.org.example.serde.AppSerdes;
import Kstreams.JavaObjectTest.org.example.types.ClickLog;
import Kstreams.JavaObjectTest.org.example.types.WinLog;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.Properties;

public class WinClickMappingFin {
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
        MapAppTopology.withBuilder(streamsBuilder);
        logger.info("Starting Stream...");
        KafkaStreams streams = new KafkaStreams(streamsBuilder.build(), props);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Stopping Streams...");
            streams.close();
        }));
    }
}

