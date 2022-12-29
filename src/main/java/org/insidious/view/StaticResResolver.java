package org.insidious.view;


import org.insidious.io.AgentResourceLoader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

/**
 * @Author: cuiyiming
 * @Description:
 * @Date: 2021/12/9
 */
public class StaticResResolver {

    public static void resolve(String path, Writer writer) throws Exception {
        InputStream in = AgentResourceLoader.getResourceAsStream(path);
        InputStreamReader reader = new InputStreamReader(in);
        char[] buf = new char[1024];
        int len = 0;
        while ((len = reader.read(buf)) > 0) {
            writer.write(buf, 0, len);
        }

    }
}
