package org.insidious.plugins;

import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatcher;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;

/**
 * @Author: cuiyiming
 * @Description:
 * @Date: 2021/12/9
 */
public class DefaultListener extends BaseTraceListener {
    private final String pattern;

    public DefaultListener(String pattern, ThreadCompressIntercepter intercepter,
                           ModuleEventWatcher moduleEventWatcher) {
        super(intercepter, moduleEventWatcher);
        this.pattern = pattern;
    }

    public EventWatcher getWatcher() {
        return new EventWatchBuilder(moduleEventWatcher, EventWatchBuilder.PatternType.REGEX).onClass(pattern)
                .onAnyBehavior().onWatch(this);
    }

    @Override
    protected String getType() {
        return "local";
    }

    @Override
    public InvokeNode getInvokeNode(Advice advice) {
        String c = advice.getBehavior().getDeclaringClass().getName();
        String m = advice.getBehavior().getName();
        InvokeNode invokeNode = new InvokeNode(c, m);
        return invokeNode;
    }

    @Override
    protected String getParams(Advice advice) {
        return getStrValue(advice.getParameterArray());
    }
}
