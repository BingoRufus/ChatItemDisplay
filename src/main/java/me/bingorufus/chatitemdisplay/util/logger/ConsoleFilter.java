package me.bingorufus.chatitemdisplay.util.logger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.core.impl.MutableLogEvent;
import org.apache.logging.log4j.message.Message;
import org.bukkit.Bukkit;

public class ConsoleFilter extends AbstractFilter {

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
