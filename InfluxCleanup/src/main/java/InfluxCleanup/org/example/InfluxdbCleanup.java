package InfluxCleanup.org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class InfluxdbCleanup {
    private static Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        final String serverURL = AppConfigs.serverURL, username = AppConfigs.id, password = AppConfigs.pw;
        final InfluxDB influxDB = InfluxDBFactory.connect(serverURL, username, password);
        String databaseName = "logs";
        influxDB.setDatabase(databaseName);
        SimpleDateFormat format1 = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        format1.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        String starttime = "2021-07-25T15:41:00.000+0900";

//        Date current = new Date(System.currentTimeMillis());
//        SimpleDateFormat format_tmp = new SimpleDateFormat("yyyyMMddHHmm");
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(current);
//        cal.add(Calendar.MINUTE, -20);
//        Date currentMinus20 = cal.getTime();

        try{
            date = format1.parse(starttime);
        }catch(ParseException e){}
        Timer scheduler_cu = new Timer();
        TimerTask cleanup = new TimerTask() {
            @Override
            public void run() {
                Calendar time = Calendar.getInstance();
                String current = format1.format(time.getTime());
                System.out.println(current);
                influxDB.query(new Query("DELETE FROM \"test\" WHERE time < now() - 25m"));
            }
        };
        scheduler_cu.schedule(cleanup, date, 60000);

        Timer scheduler_ds = new Timer();
        TimerTask downsampling = new TimerTask() {
            @Override
            public void run() {
                Date current = new Date(System.currentTimeMillis());
                SimpleDateFormat format_tmp = new SimpleDateFormat("yyyyMMddHHmm");
                Calendar cal = Calendar.getInstance();
                cal.setTime(current);
                cal.add(Calendar.HOUR, -9);
                cal.add(Calendar.MINUTE, -20);
                Date currentMinus20 = cal.getTime();
                cal.add(Calendar.MINUTE, 10);
                Date currentMinus10 = cal.getTime();
                String cm20 = format_tmp.format(currentMinus20);
                String cm10 = format_tmp.format(currentMinus10);
                String time_10 = cm10.substring(0,4)+"-"+cm10.substring(4,6)+"-"+cm10.substring(6,8)
                        +"T"+cm10.substring(8,10)+":"+cm10.substring(10,11)+"0:00.000Z";
                String time_20 = cm20.substring(0,4)+"-"+cm20.substring(4,6)+"-"+cm20.substring(6,8)
                        +"T"+cm20.substring(8,10)+":"+cm20.substring(10,11)+"0:00.000Z";
                System.out.println(time_10);
                System.out.println(time_20);
                influxDB.query(new Query("select count(mapped) as win, sum(mapped) as click, sum(chargeAmount) as charge " +
                        "into test2 from test where time < '"+time_10+"' and time >= '"+time_20+"' " +
                        "group by time(10m), gender, age, tagId"));
                System.out.println("success1");
                influxDB.query(new Query("select count(mapped) as win, sum(mapped) as click, sum(chargeAmount) as charge " +
                        "into test3 from test where time < '"+time_10+"' and time >= '"+time_20+"' " +
                        "group by time(10m), creativeId, adAccountId"));
                System.out.println("success2");
            }
        };
        scheduler_ds.schedule(downsampling, date, 600000);
    }

}
