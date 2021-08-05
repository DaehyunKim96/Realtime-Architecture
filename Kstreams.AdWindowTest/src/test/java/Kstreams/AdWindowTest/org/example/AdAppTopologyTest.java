package Kstreams.AdWindowTest.org.example;

import Kstreams.AdWindowTest.org.example.serde.AppSerdes;
import Kstreams.AdWindowTest.org.example.types.SimpleValue;
import Kstreams.AdWindowTest.org.example.types.WindowAgg;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.kstream.WindowedSerdes;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.apache.logging.log4j.core.util.FileUtils;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdAppTopologyTest {
    private static TopologyTestDriver topologyTestDriver;
    @BeforeAll
    static void setUpAll() {
        Properties props = new Properties();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServersTest);
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, AppConfigs.applicationID);
        props.put(StreamsConfig.STATE_DIR_CONFIG, AppConfigs.stateStoreUnitTest);
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 0);

        StreamsBuilder streamsBuilder = new StreamsBuilder();
        AdAppTopology.withBuilder(streamsBuilder);
        Topology topology = streamsBuilder.build();
        topologyTestDriver = new TopologyTestDriver(topology, props);
    }

    @Test
    @Order(1)
    @DisplayName("1130 구간 WinLog가 하나만 들어간 경우 Simple Group By Key")
    void adGroupByKeyOneWinTest() {
        SimpleValue SimpleValue = new SimpleValue().withChargeAmount(0.0).withMapped(0).withTimestamp("20210728 113116 849");
        ConsumerRecordFactory<String, SimpleValue> adLogFactory = new ConsumerRecordFactory<>(
                AppSerdes.String().serializer(), AppSerdes.SimpleValue().serializer()
        );
        topologyTestDriver.pipeInput(adLogFactory.create(
                AppConfigs.adTopicName,
                "1-1",
                SimpleValue
        ));
        ProducerRecord<Windowed<String>, WindowAgg> record = topologyTestDriver.readOutput(
                AppConfigs.adGroupbyName,
                WindowedSerdes.timeWindowedSerdeFrom(String.class).deserializer(),
                AppSerdes.WindowAgg().deserializer());

        assertAll(
                () -> assertEquals(1, record.value().getWin()),
                () -> assertEquals(0, record.value().getClick()),
                () -> assertEquals(0.0, record.value().getChargeAmount())
        );
    }
    @Test
    @Order(2)
    @DisplayName("1130 구간 WinLog가 하나 더 들어간 경우 Simple Group By Key")
    void adGroupByKeyTwoWinTest() {
        SimpleValue SimpleValue = new SimpleValue().withChargeAmount(0.0).withMapped(0).withTimestamp("20210728 113216 849");
        ConsumerRecordFactory<String, SimpleValue> adLogFactory = new ConsumerRecordFactory<>(
                AppSerdes.String().serializer(), AppSerdes.SimpleValue().serializer()
        );
        topologyTestDriver.pipeInput(adLogFactory.create(
                AppConfigs.adTopicName,
                "1-1",
                SimpleValue
        ));
        ProducerRecord<Windowed<String>, WindowAgg> record = topologyTestDriver.readOutput(
                AppConfigs.adGroupbyName,
                WindowedSerdes.timeWindowedSerdeFrom(String.class).deserializer(),
                AppSerdes.WindowAgg().deserializer());
        assertAll(
                () -> assertEquals(2, record.value().getWin()),
                () -> assertEquals(0, record.value().getClick()),
                () -> assertEquals(0.0, record.value().getChargeAmount())
        );
    }
    @Test
    @Order(3)
    @DisplayName("1130 구간 WinLog 두개와 Click Log 한개가 들어간 경우 Simple Group By Key")
    void adGroupByKeyTwoWinOneClickTest() {
        SimpleValue SimpleValue = new SimpleValue().withChargeAmount(100.0).withMapped(1).withTimestamp("20210728 113416 849");
        ConsumerRecordFactory<String, SimpleValue> adLogFactory = new ConsumerRecordFactory<>(
                AppSerdes.String().serializer(), AppSerdes.SimpleValue().serializer()
        );
        topologyTestDriver.pipeInput(adLogFactory.create(
                AppConfigs.adTopicName,
                "1-1",
                SimpleValue
        ));
        ProducerRecord<Windowed<String>, WindowAgg> record = topologyTestDriver.readOutput(
                AppConfigs.adGroupbyName,
                WindowedSerdes.timeWindowedSerdeFrom(String.class).deserializer(),
                AppSerdes.WindowAgg().deserializer());
        assertAll(
                () -> assertEquals(3, record.value().getWin()),
                () -> assertEquals(1, record.value().getClick()),
                () -> assertEquals(100.0, record.value().getChargeAmount())
        );
    }
    @Test
    @Order(4)
    @DisplayName("1135 구간 WinLog 한개가 들어간 경우 Simple Group By Key")
    void adGroupByKeyOneWinDiffRangeTest() {
        SimpleValue SimpleValue = new SimpleValue().withChargeAmount(0.0).withMapped(0).withTimestamp("20210728 113716 849");
        ConsumerRecordFactory<String, SimpleValue> adLogFactory = new ConsumerRecordFactory<>(
                AppSerdes.String().serializer(), AppSerdes.SimpleValue().serializer()
        );
        topologyTestDriver.pipeInput(adLogFactory.create(
                AppConfigs.adTopicName,
                "1-1",
                SimpleValue
        ));
        ProducerRecord<Windowed<String>, WindowAgg> record = topologyTestDriver.readOutput(
                AppConfigs.adGroupbyName,
                WindowedSerdes.timeWindowedSerdeFrom(String.class).deserializer(),
                AppSerdes.WindowAgg().deserializer());
        assertAll(
                () -> assertEquals(1, record.value().getWin()),
                () -> assertEquals(0, record.value().getClick()),
                () -> assertEquals(0.0, record.value().getChargeAmount())
        );
    }
    @Test
    @Order(4)
    @DisplayName("1130 구간 WinLog 한개가 뒤늦게 경우 Simple Group By Key")
    void adGroupByKeyOneWinLateTest() {
        SimpleValue SimpleValue = new SimpleValue().withChargeAmount(0.0).withMapped(0).withTimestamp("20210728 113316 849");
        ConsumerRecordFactory<String, SimpleValue> adLogFactory = new ConsumerRecordFactory<>(
                AppSerdes.String().serializer(), AppSerdes.SimpleValue().serializer()
        );
        topologyTestDriver.pipeInput(adLogFactory.create(
                AppConfigs.adTopicName,
                "1-1",
                SimpleValue
        ));
        ProducerRecord<Windowed<String>, WindowAgg> record = topologyTestDriver.readOutput(
                AppConfigs.adGroupbyName,
                WindowedSerdes.timeWindowedSerdeFrom(String.class).deserializer(),
                AppSerdes.WindowAgg().deserializer());
        assertAll(
                () -> assertEquals(4, record.value().getWin()),
                () -> assertEquals(1, record.value().getClick()),
                () -> assertEquals(100.0, record.value().getChargeAmount())
        );
    }
    @AfterAll
    static void cleanUpAll() throws IOException {
        try {
            topologyTestDriver.close();
        } catch (Exception e) {
//            FileUtils.deleteDirectory(new File(AppConfigs.stateStoreUnitTest));
        }
    }
}