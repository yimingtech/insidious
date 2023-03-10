package org.insidious.plugins;

import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatcher;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Arrays;

/**
 * @Author: cuiyiming
 * @Description:
 * @Date: 2021/12/9
 */
public class RedisListener extends BaseTraceListener {

    private final String CLASS = "redis.clients.jedis.Jedis";

    public RedisListener(ThreadCompressIntercepter intercepter,
                         ModuleEventWatcher moduleEventWatcher) {
        super(intercepter, moduleEventWatcher);
    }

    public EventWatcher getWatcher() {
        return new EventWatchBuilder(moduleEventWatcher, EventWatchBuilder.PatternType.WILDCARD).onClass(CLASS)
                .onAnyBehavior().onWatch(this);
    }

    @Override
    protected String getType() {
        return "Redis";
    }

    @Override
    public InvokeNode getInvokeNode(Advice advice) {
        return new InvokeNode(CLASS, advice.getBehavior().getName());
    }

    @Override
    protected String getParams(Advice advice) {
        String params = null;
        try {
            params = getStrValue(advice.getParameterArray());
        } catch (Throwable t1) {
            try {
                params = ToStringBuilder.reflectionToString(advice.getParameterArray());
            } catch (Throwable t2) {
                params = Arrays.toString(advice.getParameterArray());
            }
        }
        return params;
    }
}
