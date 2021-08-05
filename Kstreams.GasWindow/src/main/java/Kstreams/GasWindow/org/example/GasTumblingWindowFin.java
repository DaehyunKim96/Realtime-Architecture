package Kstreams.GasWindow.org.example;

import Kstreams.GasWindow.org.example.types.WindowAgg;
import Kstreams.GasWindow.org.example.serde.AppSerdes;
import Kstreams.GasWindow.org.example.types.SimpleValue;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.Properties;

public class GasTumblingWindowFin {
    private static Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServersPri);
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, AppConfigs.applicationID);
        props.put(StreamsConfig.STATE_DIR_CONFIG, AppConfigs.stateStoreName);
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 0);

        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KStream<String, SimpleValue> KS0 = streamsBuilder.stream(AppConfigs.gasTopicName,
                Consumed.with(AppSerdes.String(), AppSerdes.SimpleValue())
                        .withTimestampExtractor(AppTimestampExtractor.SimpleValue())
        );
//        final Serializer<Windowed<AdAccount>> windowedSerializer = WindowedSerdes.timeWindowedSerdeFrom(AdAccount.class).serializer();
        KTable<Windowed<String>, WindowAgg> KT0 = KS0.groupByKey(Grouped.with(AppSerdes.String(), AppSerdes.SimpleValue()))
                .windowedBy(TimeWindows.of(Duration.ofMinutes(5)))
                .aggregate(() -> new WindowAgg(0.0,0,0),
                        (key, value, aggregate) -> {
                            aggregate.setWin(aggregate.getWin() + 1);
                            aggregate.setClick(aggregate.getClick() + value.getMapped());
                            aggregate.setChargeAmount(aggregate.getChargeAmount() + value.getChargeAmount());
                            return aggregate;
                        },
                        Materialized.<String, WindowAgg, WindowStore<org.apache.kafka.common.utils.Bytes, byte[]>>as(AppConfigs.stateStoreName).withValueSerde(AppSerdes.WindowAgg()));

        KT0.toStream().to(AppConfigs.gasGroupbyName);
        KafkaStreams streams = new KafkaStreams(streamsBuilder.build(), props);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Stopping Streams");
            streams.close();
        }));
    }
}
