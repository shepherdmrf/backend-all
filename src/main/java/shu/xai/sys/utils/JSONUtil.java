package shu.xai.sys.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class JSONUtil {

    //axios post默认传递payload参数，getparamter取不到，要用流取，get方法不用request.getParameter即可
    public static JSONObject getRequestPayload(HttpServletRequest req) {
        StringBuilder sb = new StringBuilder();
        try(BufferedReader reader = req.getReader();) {
            char[]buff = new char[1024];
            int len;
            while((len = reader.read(buff)) != -1) {
                sb.append(buff,0, len);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return JSONObject.parseObject(sb.toString());
    }


    /**
     * string转json
     *
     * @param string 要转换的String
     * @return json
     */
    public static JSON strToJson(String string) {
        JSONObject jsonObj = null;
        try {
            jsonObj = JSON.parseObject(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObj;
    }

    /**
     * json转arrayList
     *
     * @param jsonStr 要转换的json
     * @return arrayList
     */
    public static ArrayList<String> jsonStrToAL(String jsonStr) {
        ArrayList list = null;
        try {
            list = JSONObject.parseObject(jsonStr, ArrayList.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * json转ListHashMap
     *
     * @param jsonStr 要转换的json
     * @return ListHashMap
     */
    public static LinkedHashMap<String, JSONArray> jsonStrToLHMJsonArray(String jsonStr) {
        LinkedHashMap map = null;
        try {
            map = JSONObject.parseObject(jsonStr, LinkedHashMap.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * json转ListHashMap
     *
     * @param jsonStr 要转换的json
     * @return ListHashMap
     */
    public static LinkedHashMap<String, String> jsonStrToLHM(String jsonStr) {
        LinkedHashMap map = null;
        try {
            map = JSONObject.parseObject(jsonStr, LinkedHashMap.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
