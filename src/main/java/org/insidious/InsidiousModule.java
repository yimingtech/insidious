package org.insidious;

import com.alibaba.jvm.sandbox.api.Information;
import com.alibaba.jvm.sandbox.api.LoadCompleted;
import com.alibaba.jvm.sandbox.api.Module;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import org.insidious.plugins.*;
import org.insidious.web.ModuleDataHttp;
import org.kohsuke.MetaInfServices;

import javax.annotation.Resource;

/**
 * @Author: cuiyiming
 * @Description:
 * @Date: 2021/12/9
 */
@MetaInfServices(Module.class)
@Information(id = "insidious", version = "0.0.1", author = "cuiyiming90@163.com")
public class InsidiousModule extends ModuleDataHttp implements Module, LoadCompleted {
    @Resource
    private ModuleEventWatcher watcher;

    public void repairCheckState() {
        new MyBatisListener(intercepter, watcher).getWatcher().withProgress(progress);
        new DefaultListener(".*\\..*((Controller)|(Service(Impl)?))$", intercepter,
                watcher).getWatcher().withProgress(progress);
        new DubboListener(intercepter, watcher).getWatcher().withProgress(progress);
        new RedisListener(intercepter, watcher).getWatcher().withProgress(progress);
        new RocketMqListener(intercepter, watcher).getWatcher().withProgress(progress);
        new KafkaListener(intercepter, watcher).getWatcher().withProgress(progress);
    }

    @Override
    public void loadCompleted() {
        repairCheckState();
    }

    private ModuleEventWatcher.Progress progress = new ModuleEventWatcher.Progress() {
        @Override
        public void begin(int i) {
        }

        @Override
        public void progressOnSuccess(Class aClass, int i) {
        }

        @Override
        public void progressOnFailed(Class aClass, int i, Throwable throwable) {

        }

        @Override
        public void finish(int i, int i1) {
        }
    };

    public static void main(String[] args) {
        System.out.println("<cinit>".replaceAll("<|>", ""));
    }
}
