package shu.xai.sys.utils;


import com.alibaba.fastjson.JSONObject;
import shu.xai.sys.constant.Constants;
import shu.xai.sys.domain.Result;
import shu.xai.sys.enums.ResultCodeEnums;

public class ResultUtils {
    public static Result success(String data) {
        Result result = new Result();
        result.setCode(ResultCodeEnums.SUCCESS.getCode());
        result.setMsg(ResultCodeEnums.SUCCESS.getMsg());
        //result.setData(data);
        return result;
    }

    public static Result fail(Integer code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
//        result.setData();
        return result;
    }

    //通用方法
    public static String commonResult(Integer code, String msg,String data){
        JSONObject result=new JSONObject();
        result.put(Constants.RET_CODE,code);
        result.put(Constants.RET_MESSAGE,msg);
        result.put(Constants.RET_DATA,data);
        return result.toString();
    }

    public static JSONObject commonResultJSON(Integer code, String msg){
        JSONObject result=new JSONObject();
        result.put(Constants.RET_CODE,code);
        result.put(Constants.RET_MESSAGE,msg);
        result.put(Constants.RET_DATA,"");
        return result;
    }

    public static JSONObject commonJSONSuccess(String data){
        JSONObject result=new JSONObject();
        result.put(Constants.RET_CODE,ResultCodeEnums.SUCCESS.getCode());
        result.put(Constants.RET_MESSAGE,ResultCodeEnums.SUCCESS.getMsg());
        result.put(Constants.RET_DATA,data);
        return result;
    }


}
