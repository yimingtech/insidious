package org.insidious.plugins;

import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatcher;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;

import java.lang.reflect.Method;

/**
 * @Author: cuiyiming
 * @Description:
 * @Date: 2021/12/9
 */
public class KafkaListener extends BaseTraceListener {
    private final String PATTERN = "org\\.apache\\.kafka\\.clients\\.(consumer\\.KafkaConsumer|producer\\.KafkaProducer)";
    private final String METHOD = "(send.*)|(seek.*)|(position.*)";
    private final String MSG_CLASS_NAME = "org.apache.kafka.clients.producer.ProducerRecord";

    public KafkaListener(ThreadCompressIntercepter intercepter,
                         ModuleEventWatcher moduleEventWatcher) {
        super(intercepter, moduleEventWatcher);
    }

    public EventWatcher getWatcher() {
        System.out.println("kafka Watcher:" + 1);
        return new EventWatchBuilder(moduleEventWatcher, EventWatchBuilder.PatternType.REGEX).onClass(PATTERN).includeSubClasses()
                .onBehavior(METHOD).onWatch(this);
    }

    @Override
    protected String getType() {
        return "Kafka";
    }

    @Override
    public InvokeNode getInvokeNode(Advice advice) {
        InvokeNode invokeNode = null;
        Object[] array = advice.getParameterArray();
        if (array.length > 0) {
            String topic = getTopic(array);
            if (msgClass == null) {
                try {
                    msgClass = advice.getBehavior().getDeclaringClass().getClassLoader()
                            .loadClass(MSG_CLASS_NAME);
                } catch (Exception e) {
                }
            }
            String className = advice.getBehavior().getDeclaringClass().getName();
            System.out.println("kafka 得到类名：" + className);
            if (topic != null) {
                System.out.println("topic不为空: " + topic + "className: " + className);
                invokeNode = new InvokeNode(className, topic);
            }
        }
        return invokeNode;
    }

    @Override
    protected String getParams(Advice advice) {
        System.out.println("kafka 参数:" + advice);
        return getStrValue(advice.getParameterArray());
    }

    private static Class<?> msgClass;
    private static Method getTopic;

    private final String getTopic(Object[] array) {
        String topic = null;
        if (msgClass != null) {
            for (Object obj : array) {
                System.out.println("array值：" + getStrValue(obj));
                if (null != obj) {
                    if (msgClass.isAssignableFrom(obj.getClass())) {
                        if (getTopic == null) {
                            try {
                                getTopic = obj.getClass().getMethod("topic");
                                System.out.println("得到topic信息：" + getTopic);
                                getTopic.setAccessible(true);
                            } catch (Exception e) {
                                //e.printStackTrace();
                            }
                        }
                        if (getTopic != null) {
                            try {
                                topic = (String) getTopic.invoke(obj);
                            } catch (Exception e) {
                                //e.printStackTrace();
                            }
                        }
                    }
                }
                if (topic != null) {
                    break;
                }
            }
        }

        return topic;
    }
}
