package me.bingorufus.chatitemdisplay.util.logger;

import java.util.logging.Level;

public enum JULConverter { // Converts Log4j Logging levels to JUL
    OFF(Level.OFF), FATAL(Level.SEVERE), ERROR(Level.SEVERE), WARN(Level.WARNING), INFO(Level.INFO), DEBUG(Level.FINE),
    TRACE(Level.FINER),
    ALL(Level.ALL);

    private final Level level;

    JULConverter(Level level) {
        this.level = level;
    }

    public static JULConverter fromLog4j(org.apache.logging.log4j.Level lvl) {
        return JULConverter.valueOf(lvl.name());
    }

    public Level toJUL() {
        return this.level;
    }
}
