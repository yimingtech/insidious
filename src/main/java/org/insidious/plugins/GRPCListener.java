package org.insidious.plugins;

import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatcher;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;

import java.lang.reflect.Method;

public class GRPCListener extends BaseTraceListener {

    private final String CLASS = "org.apache.rpc.monitor.support.MonitorFilter";
    private final String METHOD = "invoke";

    private final String PATTERN = "org\\.apache\\.rpc\\.proxy\\.(InvokerInvocationHandler|AbstractProxyInvoker)";


    public GRPCListener(ThreadCompressIntercepter intercepter, ModuleEventWatcher moduleEventWatcher) {
        super(intercepter, moduleEventWatcher);
    }

    @Override
    public EventWatcher getWatcher() {
        return new EventWatchBuilder(moduleEventWatcher, EventWatchBuilder.PatternType.REGEX).onClass(PATTERN)
                .onBehavior(METHOD).onWatch(this);
    }

    @Override
    protected InvokeNode getInvokeNode(Advice advice) {
        InvokeNode invokeNode = null;
        Object[] array = advice.getParameterArray();
        if (array != null && array.length > 1) {
            if (array.length > 1 && array[1] instanceof Method) {//consumer
                invokeNode = new InvokeNode(((Method) array[1]).getDeclaringClass().getName(), ((Method) array[1]).getName());
            } else if (array.length == 1) {//provider
                String m = getMethodName(array[0]);
                String c = getMethodName(getInvoker(array[0]));
                if (c != null && m != null) {
                    invokeNode = new InvokeNode(c, m);
                }
            }
        }
        return invokeNode;
    }

    @Override
    protected String getParams(Advice advice) {
        Object[] array = advice.getParameterArray();
        if (array != null && array.length > 1) {//consumer
            return getStrValue(array[2]);
        } else if (array.length == 1) {//provider
            return getStrValue(getArguments(advice.getParameterArray()[0]));
        }
        return null;
    }

    @Override
    protected String getType() {
        return "GRPC";
    }

    private Method getArguments;

    private Object getArguments(Object invocation) {
        String arguments = null;
        if (invocation != null) {
            if (getArguments == null) {
                try {
                    getArguments = invocation.getClass().getMethod("getArguments");
                    getArguments.setAccessible(true);
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
            if (getArguments != null) {
                try {
                    arguments = (String) getArguments.invoke(invocation);
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        }
        return arguments;
    }

    private Method getMethodName;

    private String getMethodName(Object invocation) {
        String m = null;
        if (invocation != null) {
            if (getMethodName == null) {
                try {
                    getMethodName = invocation.getClass().getMethod("getMethodName");
                    getMethodName.setAccessible(true);
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
            if (getMethodName != null) {
                try {
                    m = (String) getMethodName.invoke(invocation);
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        }
        return m;
    }

    private Method getInvoker;

    private Object getInvoker(Object invocation) {
        Object i = null;
        if (invocation != null) {
            if (getInvoker == null) {
                try {
                    getInvoker = invocation.getClass().getMethod("getInvoker");
                    getInvoker.setAccessible(true);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
            if (getInvoker != null) {
                try {
                    i = getInvoker.invoke(invocation);
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        }
        return i;
    }
}
