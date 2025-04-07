package shu.xai.lyzs.controller;

import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shu.xai.lyzs.service.ConstantParamsService;
import shu.xai.sys.enums.ResultCodeEnums;
import shu.xai.sys.utils.ResultUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Created by yuziyi on 2022/10/22.
 */
@RestController
@RequestMapping("/constantParams")
public class ConstantParamsController {

    @Autowired
    private ConstantParamsService constantParamsService;

    private final static Logger logger = LoggerFactory.getLogger(ConstantParamsController.class);

    /**
     * 查询算法领域列表并返回
     * @param request http参数
     * @param response http参数
     * @return result 查询结果封装
     */
    @RequestMapping("/getCalFieldOptions")
    public String getCalFieldOptions(HttpServletRequest request, HttpServletResponse response){
        String result="";
        try{
            JSONArray paramsResult=constantParamsService.getCalFieldOptions();
            result= ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMsg(),paramsResult.toJSONString());
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(), ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    /**
     * 查询算法类型列表并返回
     * @param request http参数
     * @param response http参数
     * @return result 查询结果封装
     */
    @RequestMapping("/getAlgoTypeOptions")
    public String getAlgoTypeOptions(HttpServletRequest request, HttpServletResponse response){
        String result="";
        try{
            JSONArray paramsResult=constantParamsService.getAlgoTypeOptions();
            result= ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMsg(),paramsResult.toJSONString());
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(), ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

}
