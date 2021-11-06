package com.bingorufus.chatitemdisplay.util.logger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.impl.MutableLogEvent;
import org.apache.logging.log4j.core.impl.ThrowableProxy;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.StringMap;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * @author BingoRufus
 * @version 1.0.0
 */
public class ConsoleLogEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final MutableLogEvent mutableLogEvent;
    private Filter.Result result = Filter.Result.NEUTRAL;

    public ConsoleLogEvent(MutableLogEvent mutableLogEvent, boolean async) {
        super(async);
        //Makes the event async, this is REQUIRED otherwise lots of stuff will break
        this.mutableLogEvent = mutableLogEvent;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Filter.Result getResult() {
        return result;
    }

    /**
     * Determines whether the message will be sent to console or not.
     *
     * @param result outcome result: DENY - Message will be cancelled and isCancelled() will return true
     */
    public void setResult(Filter.Result result) {
        this.result = result;
    }

    @Override
    public boolean isCancelled() {
        return result == Filter.Result.DENY;
    }

    /**
     * Cancels the event. It is not recommended to use this method, instead
     *
     * @param cancel a boolean with true causing the event to be canceled
     * @see ConsoleLogEvent#setResult(Filter.Result)
     */
    @Override
    public void setCancelled(boolean cancel) {
        result = cancel ? Filter.Result.DENY : Filter.Result.NEUTRAL;
    }

    public int getThreadPriority() {
        return mutableLogEvent.getThreadPriority();
    }

    public void setThreadPriority(int threadPriority) {
        mutableLogEvent.setThreadPriority(threadPriority);
    }

    public long getThreadId() {
        return mutableLogEvent.getThreadId();
    }

    public void setThreadId(long threadId) {
        mutableLogEvent.setThreadId(threadId);
    }

    public long getTimeMillis() {
        return mutableLogEvent.getTimeMillis();
    }

    public void setTimeMillis(long timeMillis) {
        mutableLogEvent.setTimeMillis(timeMillis);
    }

    public long getNanoTime() {
        return mutableLogEvent.getNanoTime();
    }

    public void setNanoTime(long nanoTime) {
        mutableLogEvent.setNanoTime(nanoTime);
    }

    public short getParameterCount() {
        return mutableLogEvent.getParameterCount();
    }

    public boolean isIncludeLocation() {
        return mutableLogEvent.isIncludeLocation();
    }

    public void setIncludeLocation(boolean includeLocation) {
        mutableLogEvent.setIncludeLocation(includeLocation);
    }

    public boolean isEndOfBatch() {
        return mutableLogEvent.isEndOfBatch();
    }

    public void setEndOfBatch(boolean endOfBatch) {
        mutableLogEvent.setEndOfBatch(endOfBatch);
    }

    public Level getLevel() {
        return mutableLogEvent.getLevel();
    }

    public void setLevel(Level level) {
        mutableLogEvent.setLevel(level);
    }

    public String getThreadName() {
        return mutableLogEvent.getThreadName();
    }

    public void setThreadName(String threadName) {
        mutableLogEvent.setThreadName(threadName);
    }

    public String getLoggerName() {
        return mutableLogEvent.getLoggerName();
    }

    public void setLoggerName(String loggerName) {
        mutableLogEvent.setLoggerName(loggerName);
    }

    public Message getMessage() {
        return mutableLogEvent.getMessage();
    }

    public void setMessage(Message message) {
        mutableLogEvent.setMessage(message);
    }

    public String getFormattedMessage() {
        return mutableLogEvent.getFormattedMessage();
    }

    public String getFormat() {
        return mutableLogEvent.getFormat();
    }

    public Object[] getParameters() {
        return mutableLogEvent.getParameters();
    }

    public Object[] swapParameters(Object[] emptyReplacement) {
        return mutableLogEvent.swapParameters(emptyReplacement);
    }

    public Throwable getThrown() {
        return mutableLogEvent.getThrown();
    }

    public void setThrown(Throwable thrown) {
        mutableLogEvent.setThrown(thrown);
    }

    public ThrowableProxy getThrownProxy() {
        return mutableLogEvent.getThrownProxy();
    }

    public StringMap getContextData() {
        return (StringMap) mutableLogEvent.getContextData();
    }

    public void setContextData(StringMap contextData) {
        mutableLogEvent.setContextData(contextData);
    }

    public Marker getMarker() {
        return mutableLogEvent.getMarker();
    }

    public void setMarker(Marker marker) {
        mutableLogEvent.setMarker(marker);
    }

    public String getLoggerFqcn() {
        return mutableLogEvent.getLoggerFqcn();
    }

    public void setLoggerFqcn(String loggerFqcn) {
        mutableLogEvent.setLoggerFqcn(loggerFqcn);
    }

    public StackTraceElement getSource() {
        return mutableLogEvent.getSource();
    }

    public ThreadContext.ContextStack getContextStack() {
        return mutableLogEvent.getContextStack();
    }

    public void setContextStack(ThreadContext.ContextStack contextStack) {
        mutableLogEvent.setContextStack(contextStack);
    }

    public void formatTo(StringBuilder buffer) {
        mutableLogEvent.formatTo(buffer);
    }


}