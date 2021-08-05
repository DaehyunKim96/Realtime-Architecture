package Kstreams.JavaObjectTest.org.example;

import Kstreams.JavaObjectTest.org.example.serde.AppSerdes;
import Kstreams.JavaObjectTest.org.example.types.ClickLog;
import Kstreams.JavaObjectTest.org.example.types.WinLog;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;

public class MapAppTopology {
    private static Logger logger = LogManager.getLogger();
    static void withBuilder(StreamsBuilder builder) {
        KStream<String, WinLog> KS0 = builder.stream(AppConfigs.winTopicName,
                Consumed.with(AppSerdes.String(), AppSerdes.WinLog())
                        .withTimestampExtractor(AppTimestampExtractor.WinLog())
        );
        KStream<String, ClickLog> KS1 = builder.stream(AppConfigs.clickTopicName,
                Consumed.with(AppSerdes.String(), AppSerdes.ClickLog())
                        .withTimestampExtractor(AppTimestampExtractor.ClickLog())
        );

        KStream<String, WinLog> mappingLog = KS0.join(KS1,
                (v1, v2) -> {
                    WinLog mappedlog = new WinLog();
                    if (v1 != null) {
                        mappedlog.withBidId(v1.getBidId())
                                .withTimestamp(v1.getTimestamp())
                                .withAdAccountId(v1.getAdAccountId())
                                .withCreativeId(v1.getCreativeId())
                                .withAge(v1.getAge())
                                .withGender(v1.getGender())
                                .withTagId(v1.getTagId())
                                .withChargeAmount(v2.getChargeAmount())
                                .withMapped(1);
                    }
                    System.out.println(mappedlog.getTimestamp());
                    return mappedlog;
                },
                JoinWindows.of(Duration.ofMinutes(10)),
                Joined.with(AppSerdes.String(), AppSerdes.WinLog(), AppSerdes.ClickLog())
        );
        mappingLog.to(AppConfigs.mappedTopicName, Produced.with(Serdes.String(), AppSerdes.WinLog()));
    }
}
