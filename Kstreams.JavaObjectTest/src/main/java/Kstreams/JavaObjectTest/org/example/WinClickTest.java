package Kstreams.JavaObjectTest.org.example;

import Kstreams.JavaObjectTest.org.example.serde.AppSerdes;
import Kstreams.JavaObjectTest.org.example.types.ClickLog;
import Kstreams.JavaObjectTest.org.example.types.WinLog;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.Properties;

public class WinClickTest {
    private static Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServersPri);
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, AppConfigs.applicationID);
        props.put(StreamsConfig.STATE_DIR_CONFIG, AppConfigs.stateStoreName);
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 0);

        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KStream<String, WinLog> KS0 = streamsBuilder.stream(AppConfigs.winTopicName,
                Consumed.with(AppSerdes.String(), AppSerdes.WinLog())
                        .withTimestampExtractor(AppTimestampExtractor.WinLog())
        );
        KStream<String, ClickLog> KS1 = streamsBuilder.stream(AppConfigs.clickTopicName,
                Consumed.with(AppSerdes.String(), AppSerdes.ClickLog())
                        .withTimestampExtractor(AppTimestampExtractor.ClickLog())
        );

        KS0.join(KS1, (v1, v2) ->
                v1 + "mapping" + v2,
                JoinWindows.of(Duration.ofMinutes(10)),
                Joined.with(AppSerdes.String(), AppSerdes.WinLog(), AppSerdes.ClickLog())
        ).print(Printed.toSysOut());
        logger.info("Starting Stream...");
        KafkaStreams streams = new KafkaStreams(streamsBuilder.build(), props);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Stopping Streams...");
            streams.close();
        }));
    }
}
