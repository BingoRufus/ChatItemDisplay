package com.bingorufus.chatitemdisplay.util.logger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.core.impl.MutableLogEvent;
import org.apache.logging.log4j.message.Message;
import org.bukkit.Bukkit;

import java.util.Iterator;

public class ConsoleFilter extends AbstractFilter {

    public void register() {
        Logger logger = (Logger) LogManager.getRootLogger();
        Iterator<Filter> filters = logger.getFilters();
        while (filters.hasNext()) { // Prevents duplicate loggers
            Filter f = filters.next();

            if (f.getClass().getName().equals(ConsoleFilter.class.getName())) {// Check if the filter is one of mine
                f.stop();
            }
        }
        logger.addFilter(this);
    }

    @Override
    public Result filter(LogEvent event) {
        if (this.isStopped() || !(event instanceof MutableLogEvent)) {
            return Result.NEUTRAL;
        }
        ConsoleLogEvent cle = new ConsoleLogEvent((MutableLogEvent) event, !Bukkit.isPrimaryThread());
        Bukkit.getPluginManager().callEvent(cle);


        return cle.getResult();
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
        return Result.NEUTRAL;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
        return Result.NEUTRAL;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
        return Result.NEUTRAL;
    }


}
