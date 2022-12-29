package org.insidious.view;

import java.util.List;

/**
 * @Author: cuiyiming
 * @Description:
 * @Date: 2021/12/9
 */
public class FoldTreeElement {
    public String type;
    public List<ClassElement> clazzs;

    public class ClassElement {
        public String name;
        public List<MethodElement> methods;

        public class MethodElement {
            public String name;
            public long mid;
            public long cnt;
            public List<NodeElement> nodes;

            public class NodeElement {
                public long id;
                public long totalRt;
                public int invokeCnt;
                public String time;
                public long tid;
            }
        }
    }

}
