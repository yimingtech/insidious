package org.insidious.io;


import com.google.gson.Gson;

/**
 * @author cuiyiming
 */
public class ObjectSerializer {


    public static String getJsonStrValue(Object obj) {
        return new Gson().toJson(obj);
    }


    public static String getStrValue(Object obj) {
        String str = null;
        if (obj != null) {
            if (obj.getClass().isArray()) {
                String spliter = ",";
                StringBuilder sb = new StringBuilder("[");
                Object[] array = (Object[]) obj;
                for (int i = 0; i < array.length; i++) {
                    sb.append(spliter);
                    spliter = ",";
                    sb.append(getStrValue(array[i]));
                }
                sb.append("]");
                str = sb.toString();
            } else {
                str = String.valueOf(obj);
            }
        }
        return str;
    }
}
