package shu.xai.sys.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yuziyi on 2021/6/21.
 */
public class CoreUtils {

    /**
     * 返回集合大小
     *
     * @param arr 要计算大小的数组
     * @return 集合大小。
     */
    public static int nullSafeSize(Object[] arr) {
        if (arr == null)
            return 0;
        return arr.length;
    }

    /**
     * 返回字符串(字节)长度
     *
     * @param str 要计算大小的字符串
     * @return 字节大小。
     */
    public static int nullSafeSize(String str) {
        if (str == null)
            return 0;
        Pattern pattern = Pattern.compile("[^\\x00-\\xff]");// 匹配双字节字符(包括汉字在内)
        Matcher matcher = pattern.matcher(str);
        String s = matcher.replaceAll("xx");
        return s.length();
    }

    /**
     * 返回集合大小
     *
     * @param collection 要计算大小的集合
     * @return 集合大小。
     */
    public static int nullSafeSize(Collection<?> collection) {
        if (collection == null)
            return 0;
        return collection.size();
    }


    /**
     * 返回Map大小
     *
     * @param map 要计算大小的Map
     * @return Map大小。
     */
    public static int nullSafeSize(Map<?, ?> map) {
        if (map == null)
            return 0;
        return map.size();
    }


    /**
     * 返回集合的总大小
     *
     * @param arr 要计算大小的字符串数组
     * @return 集合大小。
     */
    public static int nullSafeSizeCount(String[] arr) {
        if (arr == null)
            return 0;
        int count = 0;
        for (String str : arr) {
            count += nullSafeSize(str);
        }
        return count;
    }
    //树结构生成
    // 可以改为递归，就两级也没必要改
    public static JSONArray getMenuTree(List<Map<String,Object>> params){
        JSONArray menuTree=new JSONArray();
        for(Map item:params){
            if(item.get("menu_level").equals("1")){
                JSONObject level1=new JSONObject();
                JSONArray children=new JSONArray();
                level1.put("name",item.get("menu_name"));
                level1.put("menuLevel",item.get("menu_level"));
                level1.put("order",item.get("menu_id"));
                level1.put("path",item.get("menu_path"));
                level1.put("menuActive",item.get("menu_active"));
                level1.put("children",children);
                menuTree.add(level1);
            }else {
                JSONObject level2=new JSONObject();
                level2.put("name",item.get("menu_name"));
                level2.put("menuLevel",item.get("menu_level"));
                level2.put("order",item.get("menu_id"));
                level2.put("path",item.get("menu_path"));
                level2.put("menuActive",item.get("menu_active"));
                for(int i=0;i<menuTree.size();i++){
                    if( menuTree.getJSONObject(i).get("order").equals(item.get("menu_father_id"))){
                        menuTree.getJSONObject(i).getJSONArray("children").add(level2);
                    }
                }
            }
        }

        return menuTree;

    }

    public static String getUUID (){
        //注意replaceAll前面的是正则表达式
        return UUID.randomUUID().toString().replaceAll("-","");

    }

    public static String getDateTime(){
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();// 获取当前时间
        return sdf.format(date); // 输出已经格式化的现在时间(24小时制)
    }

    public static String getDateDay(){
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("yyyy-MM-dd");
        Date date = new Date();// 获取当前时间
        return sdf.format(date); // 输出已经格式化的现在时间(24小时制)
    }

}
