package Kstreams.AdWindowTest.org.example;

import Kstreams.AdWindowTest.org.example.serde.AppSerdes;
import Kstreams.AdWindowTest.org.example.types.SimpleValue;
import Kstreams.AdWindowTest.org.example.types.WindowAgg;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;

public class AdAppTopology {
    private static Logger logger = LogManager.getLogger();
    static void withBuilder(StreamsBuilder builder) {
        //광고주 기준 topic에서 데이터를 가져와 stream으로 변환
        KStream<String, SimpleValue> KS0 = builder.stream(AppConfigs.adTopicName,
                Consumed.with(AppSerdes.String(), AppSerdes.SimpleValue())
                        // timestamp를 추출하여 Millsecond로 변환
                        .withTimestampExtractor(AppTimestampExtractor.SimpleValue())
        );
        // group by key를 통해 집계
        KTable<Windowed<String>, WindowAgg> KT0 = KS0.groupByKey(Grouped.with(AppSerdes.String(), AppSerdes.SimpleValue()))
                // 몇분 사이의 데이터를 groupby해줄 것인지를 정의하는 부분
                .windowedBy(TimeWindows.of(Duration.ofMinutes(5)))
                // WindowAgg는 시간 범위 안에서 win, click, chargeamount 집계 결과를 저장하는 객체
                .aggregate(() -> new WindowAgg(0.0,0,0),
                        (key, value, aggregate) -> {
                            // log가 추가될 떄마다 win + 1
                            aggregate.setWin(aggregate.getWin() + 1);
                            // mapping이 이루어진 경우 mapped 값이 1이기 떄문에 sum해준다.
                            aggregate.setClick(aggregate.getClick() + value.getMapped());
                            // mapping이 완료된 log의 charge값을 더해준다.
                            aggregate.setChargeAmount(aggregate.getChargeAmount() + value.getChargeAmount());
                            return aggregate;
                        },
                        Materialized.<String, WindowAgg, WindowStore<Bytes, byte[]>>as(AppConfigs.stateStoreName).withValueSerde(AppSerdes.WindowAgg()));
        // ktable에 로그가 추가되어 변경사항이 생길 때마다 해당 결과를 결과 topic에 전송
        KT0.toStream().to(AppConfigs.adGroupbyName);
    }
}
