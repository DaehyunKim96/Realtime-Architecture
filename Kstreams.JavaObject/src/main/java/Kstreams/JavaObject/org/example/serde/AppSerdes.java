package Kstreams.JavaObject.org.example.serde;

import Kstreams.JavaObject.org.example.types.ClickLog;
import Kstreams.JavaObject.org.example.types.WinLog;
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
