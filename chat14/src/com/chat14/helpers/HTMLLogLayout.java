package com.chat14.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.spi.LoggingEvent;

/**
 * This HTML Log Formatter is a simple replacement for the standard Log4J HTMLLayout formatter and replaces the default timestamp (milliseconds, relative to the
 * start of the log) with a more readable timestamp (an example of the default format is 2008-11-21-18:35:21.472-0800).
 * */
public class HTMLLogLayout extends org.apache.log4j.HTMLLayout {
    // RegEx pattern looks for <tr> <td> nnn...nnn </td> (all whitespace ignored)

    private static final String rxTimestamp = "\\s*<\\s*tr\\s*>\\s*<\\s*td\\s*>\\s*(\\d*)\\s*<\\s*/td\\s*>";

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd  HH:mm:ss");// Default format. Example: 2008-11-21-18:35:21.472-0800

    /** Override HTMLLayout's format() method */
    @Override
    public String format(LoggingEvent event) {
        String record = super.format(event); // Get the log record in the default HTMLLayout format.
        sdf.setTimeZone(TimeZone.getTimeZone("Europe/Minsk"));
        //System.out.println(record);
        Pattern pattern = Pattern.compile(rxTimestamp); // RegEx to find the default timestamp
        Matcher matcher = pattern.matcher(record);

        if (!matcher.find()) // If default timestamp cannot be found,
        {
            return record; // Just return the unmodified log record.
        }

        StringBuilder buffer = new StringBuilder(record);

        buffer.replace(matcher.start(1), // Replace the default timestamp with one formatted as desired.
                matcher.end(1), sdf.format(new Date(event.timeStamp)));

        return buffer.toString(); // Return the log record with the desired timestamp format.
    }
}
