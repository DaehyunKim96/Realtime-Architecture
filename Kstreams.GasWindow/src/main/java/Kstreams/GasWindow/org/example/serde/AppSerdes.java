package Kstreams.GasWindow.org.example.serde;

import Kstreams.GasWindow.org.example.types.*;
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
    static final class AdAccountSerde extends WrapperSerde<AdAccount> {
        AdAccountSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>());
        }
    }

    public static Serde<AdAccount> AdAccount() {
        AdAccountSerde serde = new AdAccountSerde();

        Map<String, Object> serdeConfigs = new HashMap<>();
        serdeConfigs.put(JsonDeserializer.VALUE_CLASS_NAME_CONFIG, AdAccount.class);
        serde.configure(serdeConfigs, false);

        return serde;
    }
    static final class SimpleValueSerde extends WrapperSerde<SimpleValue> {
        SimpleValueSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>());
        }
    }

    public static Serde<SimpleValue> SimpleValue() {
        SimpleValueSerde serde = new SimpleValueSerde();

        Map<String, Object> serdeConfigs = new HashMap<>();
        serdeConfigs.put(JsonDeserializer.VALUE_CLASS_NAME_CONFIG, SimpleValue.class);
        serde.configure(serdeConfigs, false);

        return serde;
    }
    static final class WindowAggSerde extends WrapperSerde<WindowAgg> {
        WindowAggSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>());
        }
    }

    public static Serde<WindowAgg> WindowAgg() {
        WindowAggSerde serde = new WindowAggSerde();

        Map<String, Object> serdeConfigs = new HashMap<>();
        serdeConfigs.put(JsonDeserializer.VALUE_CLASS_NAME_CONFIG, WindowAgg.class);
        serde.configure(serdeConfigs, false);

        return serde;
    }
}
