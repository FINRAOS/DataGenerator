package org.finra.scxmlexec;

import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class LoggerInitializer
{
	private final static Logger log = Logger.getLogger(LoggerInitializer.class);
	private static final AtomicBoolean init = new AtomicBoolean(false);

	public static void initialize()
	{
		if (!init.compareAndSet(false, true))
			return;

		BasicConfigurator.configure();

		@SuppressWarnings("unchecked")
		Enumeration<Appender> appenders = (Enumeration<Appender>) Logger.getRootLogger().getAllAppenders();
		while (appenders.hasMoreElements())
		{
			Appender appender = (Appender) appenders.nextElement();
			appender.setLayout(new PatternLayout("<%d{yyMMdd HHmmss} %C{1}:%L> %m%n"));

		}

		// Set the root logger level
		String loglevel = SystemParameter.getString("loglevel", "WARN");

		if (loglevel != null)
		{
			Level level = null;
			if (loglevel.equalsIgnoreCase("all"))
				level = Level.ALL;
			if (loglevel.equalsIgnoreCase("debug"))
				level = Level.DEBUG;
			if (loglevel.equalsIgnoreCase("error"))
				level = Level.ERROR;
			if (loglevel.equalsIgnoreCase("fatal"))
				level = Level.FATAL;
			if (loglevel.equalsIgnoreCase("info"))
				level = Level.INFO;
			if (loglevel.equalsIgnoreCase("off"))
				level = Level.OFF;
			if (loglevel.equalsIgnoreCase("trace"))
				level = Level.TRACE;
			if (loglevel.equalsIgnoreCase("warn"))
				level = Level.WARN;

			if (level != null)
				Logger.getLogger("org.finra").setLevel(level);

			log.debug("Set loglevel to " + level.toString());
		}
	}
}