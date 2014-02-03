package org.finra.scxmlexec;

import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.finra.datagenerator.SystemProperties;

public class LogInitializer {

    private final static Logger log = Logger.getLogger(LogInitializer.class);
    private static final AtomicBoolean init = new AtomicBoolean(false);

    public static void initialize() {
        if (!init.compareAndSet(false, true)) {
            return;
        }

        BasicConfigurator.configure();

        @SuppressWarnings("unchecked")
        Enumeration<Appender> appenders = (Enumeration<Appender>) Logger.getRootLogger().getAllAppenders();
        while (appenders.hasMoreElements()) {
            Appender appender = (Appender) appenders.nextElement();
            appender.setLayout(new PatternLayout("<%d{yyMMdd HHmmss} %5p %C{1}:%L> %m%n"));
        }

        Level level;
        switch (SystemProperties.logLevel.toLowerCase()) {
            case "all":
                level = Level.ALL;
                break;
            case "debug":
                level = Level.DEBUG;
                break;
            case "error":
                level = Level.ERROR;
                break;
            case "fatal":
                level = Level.FATAL;
                break;
            case "info":
                level = Level.INFO;
                break;
            case "off":
                level = Level.OFF;
                break;
            case "trace":
                level = Level.TRACE;
                break;
            case "warn":
                level = Level.WARN;
                break;
            default:
                level = Level.WARN;
        }

        Logger.getLogger("org.finra").setLevel(level);

        System.out.println("Set loglevel to " + level.toString());
    }
}
