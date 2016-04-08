/*
 * Copyright 2014 DataGenerator Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.finra.datagenerator.exec;

import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * Initializes log4j
 */
public final class LogInitializer {

    /**
     * Whether or not we initialized log4j.
     */
    private static final AtomicBoolean INIT = new AtomicBoolean(false);

    private LogInitializer() {

    }

    /**
     * Initializes log4j
     *
     * @param loggerLevel log level to initialize to
     */
    public static void initialize(String loggerLevel) {
        if (!INIT.compareAndSet(false, true)) {
            return;
        }

        BasicConfigurator.resetConfiguration();
        ConsoleAppender consoleAppender = new ConsoleAppender(
            new PatternLayout("<%d{yyMMdd HHmmss} %5p %C{1}:%L> %m%n"), ConsoleAppender.SYSTEM_ERR);
        BasicConfigurator.configure(consoleAppender);

        Level level;

        String logLevel;

        if (loggerLevel != null) {
            logLevel = loggerLevel.toLowerCase();
        } else {
            logLevel = "default";
        }

        switch (logLevel) {
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
                level = Level.INFO;
                break;
        }

        Logger.getRootLogger().setLevel(Level.OFF);
        Logger.getLogger("org.finra").setLevel(level);

        System.err.println("Set loglevel to " + level.toString());
    }
}
