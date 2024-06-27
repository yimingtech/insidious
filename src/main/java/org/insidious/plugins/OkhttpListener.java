package org.insidious.plugins;

import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatcher;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Arrays;

/**
 * @author cuiyiming
 * @date 2024/6/27
 */
public class OkhttpListener extends BaseTraceListener{

    private final String CLASS = "okhttp3.internal.http.BridgeInterceptor";

    public OkhttpListener(ThreadCompressIntercepter intercepter,
                          ModuleEventWatcher moduleEventWatcher) {
        super(intercepter, moduleEventWatcher);
    }

    public EventWatcher getWatcher() {
        return new EventWatchBuilder(moduleEventWatcher, EventWatchBuilder.PatternType.WILDCARD).onClass(CLASS)
                .onAnyBehavior().onWatch(this);
    }

    @Override
    protected String getType() {
        return "okhttp";
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
