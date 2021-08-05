package Kstreams.AdWindowTest.org.example;

import Kstreams.AdWindowTest.org.example.types.WinLog;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

import java.time.Instant;

public class winLogTimeExtractor implements TimestampExtractor {
    @Override
    public long extract(ConsumerRecord<Object, Object> consumerRecord, long prevTime) {
//        "2019-02-05T10:07:11.00Z"
//        20210707 225022 491
//        2021-07-07T22:50:24.449Z
        WinLog log = (WinLog) consumerRecord.value();
        String ts = log.getTimestamp();
        String createdtime = ts.substring(0,4)+"-"+ts.substring(4,6)+"-"+ts.substring(6,8)+"T"+ts.substring(9,11)+":"+ts.substring(11,13)+":"+ts.substring(13,15)+"."+ts.substring(16)+"Z";
        long eventTime = Instant.parse(createdtime).toEpochMilli();
        return ((eventTime>0) ? eventTime : prevTime);
    }
}
