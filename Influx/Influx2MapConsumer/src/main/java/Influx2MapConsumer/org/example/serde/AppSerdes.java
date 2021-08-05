package Influx2MapConsumer.org.example.serde;

import Influx2MapConsumer.org.example.types.WinLog;
import Influx2MapConsumer.org.example.types.ClickLog;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

import java.util.HashMap;
import java.util.Map;

public class AppSerdes extends Serdes {
    static final class WinLogSerde extends WrapperSerde<WinLog> {
        WinLogSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>());
        }
    }

    public static Serde<WinLog> WinLog() {
        WinLogSerde serde = new WinLogSerde();

        Map<String, Object> serdeConfigs = new HashMap<>();
        serdeConfigs.put(JsonDeserializer.VALUE_CLASS_NAME_CONFIG, WinLog.class);
        serde.configure(serdeConfigs, false);

        return serde;
    }
    static final class ClickLogSerde extends WrapperSerde<ClickLog> {
        ClickLogSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>());
        }
    }

    public static Serde<ClickLog> ClickLog() {
        ClickLogSerde serde = new ClickLogSerde();

        Map<String, Object> serdeConfigs = new HashMap<>();
        serdeConfigs.put(JsonDeserializer.VALUE_CLASS_NAME_CONFIG, ClickLog.class);
        serde.configure(serdeConfigs, false);

        return serde;
    }
}

