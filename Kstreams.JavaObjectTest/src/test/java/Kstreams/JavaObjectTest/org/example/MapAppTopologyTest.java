package Kstreams.JavaObjectTest.org.example;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.*;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MapAppTopologyTest {
    private static TopologyTestDriver topologyTestDriver;
    @BeforeAll
    static void setUpAll() {
        Properties props = new Properties();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServersTest);
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, AppConfigs.applicationID);
        props.put(StreamsConfig.STATE_DIR_CONFIG, AppConfigs.stateStoreUnitTest);
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 0);

        StreamsBuilder streamsBuilder = new StreamsBuilder();
        MapAppTopology.withBuilder(streamsBuilder);
        Topology topology = streamsBuilder.build();
        topologyTestDriver = new TopologyTestDriver(topology, props);
    }
    @Test
    @Order(1)
    @DisplayName("1130 구간 WinLog가 하나만 들어간 경우 Simple Group By Key")
    void adGroupByKeyOneWinTest() {

    }

}