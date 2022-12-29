package org.insidious.web;

import com.alibaba.jvm.sandbox.api.annotation.Command;
import org.insidious.io.ObjectSerializer;
import org.insidious.model.NodeMeta;
import org.insidious.model.StaticNode;
import org.insidious.model.TreeNode;
import org.insidious.view.ChartTreeElement;
import org.insidious.view.FoldTreeElement;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author: cuiyiming
 * @Description:
 * @Date: 2021/12/9
 */
public class ModuleDataHttp extends ModuleViewHttp {

    @Command("scene")
    public void scene(final Map<String, String> param,
                      final Map<String, String[]> params,
                      final PrintWriter writer) throws Exception {
        String idStr = param.get("id");
        long id = Long.valueOf(idStr);

        StaticNode node = intercepter.getScene(id);
        if (node != null) {
            ChartTreeElement element = ChartTreeElement.parse(node);
            writer.write(ObjectSerializer.getJsonStrValue(element));
        } else {
            writer.write("no result no record");
        }
    }

    @Command("node")
    public void node(final Map<String, String> param,
                     final Map<String, String[]> params,
                     final PrintWriter writer) throws Exception {

        String idStr = param.get("id");
        long id = Long.valueOf(idStr);

        TreeNode node = intercepter.getNode(id);
        if (node != null) {
            ChartTreeElement element = ChartTreeElement.parse(node);
            writer.write(ObjectSerializer.getJsonStrValue(element));
        } else {
            writer.write("no result no record");
        }
    }

    @Command("list")
    public void list(final PrintWriter writer) throws InterruptedException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Map<Long, LinkedBlockingQueue<Long>> queues = intercepter.getQueue();
        Map<String, Map<String, Map<String, LinkedBlockingQueue<Long>>>> resultMap = new TreeMap<String, Map<String, Map<String, LinkedBlockingQueue<Long>>>>();
        for (Entry<Long, LinkedBlockingQueue<Long>> entry : queues.entrySet()) {
            long mid = entry.getKey();
            String[] items = NodeMeta.getName(mid);
            Map<String, Map<String, LinkedBlockingQueue<Long>>> cMap = resultMap.get(items[0]);
            if (cMap == null) {
                resultMap.put(items[0], cMap = new TreeMap<String, Map<String, LinkedBlockingQueue<Long>>>());
            }
            Map<String, LinkedBlockingQueue<Long>> mMap = cMap.get(items[1]);
            if (mMap == null) {
                cMap.put(items[1], mMap = new TreeMap<String, LinkedBlockingQueue<Long>>());
            }
            mMap.put(items[2], queues.get(mid));
        }

        List<FoldTreeElement> elements = new ArrayList<FoldTreeElement>();
        for (Entry<String, Map<String, Map<String, LinkedBlockingQueue<Long>>>> typeEntry : resultMap.entrySet()) {
            FoldTreeElement element = new FoldTreeElement();
            elements.add(element);
            element.type = typeEntry.getKey();
            for (Entry<String, Map<String, LinkedBlockingQueue<Long>>> clazzEntry : typeEntry.getValue().entrySet()) {
                if (element.clazzs == null) {
                    element.clazzs = new ArrayList<FoldTreeElement.ClassElement>();
                }
                FoldTreeElement.ClassElement clazz = element.new ClassElement();
                element.clazzs.add(clazz);
                clazz.name = clazzEntry.getKey();
                for (Entry<String, LinkedBlockingQueue<Long>> methodEntry : clazzEntry.getValue()
                        .entrySet()) {
                    if (clazz.methods == null) {
                        clazz.methods = new ArrayList<FoldTreeElement.ClassElement.MethodElement>();
                    }
                    FoldTreeElement.ClassElement.MethodElement method = clazz.new MethodElement();
                    clazz.methods.add(method);
                    method.name = methodEntry.getKey();

                    for (long id : methodEntry.getValue()) {
                        TreeNode treeNode = intercepter.getNode(id);
                        method.mid = treeNode.getMid();
                        if (method.nodes == null) {
                            method.nodes = new ArrayList<FoldTreeElement.ClassElement.MethodElement.NodeElement>();
                        }
                        FoldTreeElement.ClassElement.MethodElement.NodeElement node = method.new NodeElement();
                        method.nodes.add(node);
                        node.id = id;
                        node.time = sdf.format(treeNode.getTime());
                        node.tid = treeNode.getTid();
                        node.totalRt = treeNode.getTotalRt();
                        node.invokeCnt = treeNode.getInvokeCnt();
                    }

                    StaticNode staticNode = intercepter.getScene(method.mid);
                    method.cnt = staticNode.getCnt();
                }
            }
        }
        writer.write(ObjectSerializer.getJsonStrValue(elements));
    }
}
