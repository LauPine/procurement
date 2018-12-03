package cn.com.liucm;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by liucm on 2017-8-28.
 */
public class TimestampTest {

    @Test
    public void testTimestamp() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date then = sdf.parse("2017-08-24");
        System.out.println(then.getTime());
    }
}
