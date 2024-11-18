package shu.xai.材料.controller;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shu.xai.sys.controller.UserController;
import shu.xai.sys.enums.ResultCodeEnums;
import shu.xai.sys.utils.ResultUtils;
import shu.xai.材料.page.value;
import shu.xai.材料.page.PageRequest;
import shu.xai.材料.page.PageRequestCopy;
import shu.xai.材料.service.PlatformService;

import javax.servlet.http.HttpServletRequest;

import static sun.misc.Version.print;

@RestController
@RequestMapping("/user")
public class PlatformController {
    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private PlatformService platformService;

//    @RequestMapping("/getfeatureTable")
//    public String getFeatureTable(@RequestBody PageRequest pageRequest, HttpServletRequest request) {
//        String result="";
//        try{
//            String userId= String.valueOf(request.getSession().getAttribute("userId"));
//            String roleId= String.valueOf(request.getSession().getAttribute("roleId"));
//            int page = pageRequest.getPage();
//            int pagesize = pageRequest.getPageSize();
//            JSONObject paramsResult=platformService.GetFeatureTable(userId,roleId,page,pagesize);
//            result= ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg(),paramsResult.toJSONString());
//        }catch (Exception e){
//            logger.error("error",e);
//            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
//        }
//        return result;
//    }
    @RequestMapping("/getSign")
    public String getFeatureTable( HttpServletRequest request) {
        String result="";
        try{
            String userId= String.valueOf(request.getSession().getAttribute("userId"));
            String roleId= String.valueOf(request.getSession().getAttribute("roleId"));
            JSONObject paramsResult=platformService.GetSign(userId,roleId);
            result= ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg(),paramsResult.toJSONString());
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }
    @RequestMapping("/searchFeatureTable")
    public String SearchTable(@RequestBody PageRequestCopy pageRequest, HttpServletRequest request) {
        String result="";
        try{
            String userId= String.valueOf(request.getSession().getAttribute("userId"));
            String roleId= String.valueOf(request.getSession().getAttribute("roleId"));
            int page = pageRequest.getPage();
            int pagesize = pageRequest.getPageSize();
            String value1= pageRequest.getValue1();
            String value2=pageRequest.getValue2();
            JSONObject paramsResult=platformService.SolveFeatureTable(userId,roleId,page,pagesize,value1,value2);
            result= ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg(),paramsResult.toJSONString());
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }
    @RequestMapping("/getKnowledgeKernel")
    public String sendKnowledgeKernel(@RequestBody value KnowledgeKernel, HttpServletRequest request)
    {
        String result="";
        try {
            String userId = String.valueOf(request.getSession().getAttribute("userId"));
            String roleId = String.valueOf(request.getSession().getAttribute("roleId"));
            String value =  KnowledgeKernel.getValue();
            JSONObject paramsResult = platformService.SolveKnowledgeKernel(userId, roleId, value);
            result = ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMsg(), paramsResult.toJSONString());
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }
    @RequestMapping("/getPositiveKernel")
    public String sendPositiveKernel(@RequestBody value PositiveKernel, HttpServletRequest request)
    {
        String result="";
        try {
            String userId = String.valueOf(request.getSession().getAttribute("userId"));
            String roleId = String.valueOf(request.getSession().getAttribute("roleId"));
            String value =  PositiveKernel.getValue();
            JSONObject paramsResult = platformService.SolveKnowledgeKernel(userId, roleId, value);
            result = ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMsg(), paramsResult.toJSONString());
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }
    }

