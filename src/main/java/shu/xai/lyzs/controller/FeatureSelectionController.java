package shu.xai.lyzs.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shu.xai.lyzs.service.FeatureSelectionService;
import shu.xai.sys.enums.ResultCodeEnums;
import shu.xai.sys.utils.JSONUtil;
import shu.xai.sys.utils.ResultUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Created by yuziyi on 2022/10/22.
 */
@RestController
@RequestMapping("/featureSelection")
public class FeatureSelectionController {

    @Autowired
    private FeatureSelectionService featureSelectionService;

    private final static Logger logger = LoggerFactory.getLogger(FeatureSelectionController.class);


    //通用
    //返回数据集选项信息
    @RequestMapping("/getDatasetOP")
    public String getDatasetOP(HttpServletRequest request, HttpServletResponse response){
        String result="";
        try{
            JSONArray paramsResult=featureSelectionService.getDatasetOP();
            result= ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMsg(),paramsResult.toJSONString());
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(), ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }



    //DMLFSdek
    @RequestMapping("/getDatasetInfo")
    public String getDatasetInfo(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONObject params= JSONUtil.getRequestPayload(request);
            String datasetId=params.getString("datasetId");
            JSONObject paramsResult=featureSelectionService.getDatasetInfo(datasetId);
            result=ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg(),paramsResult.toString());
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    @RequestMapping("/getDatasetData")
    public String getDatasetData(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONObject params= JSONUtil.getRequestPayload(request);
            String datasetId=params.getString("datasetId");
            JSONObject paramsResult=featureSelectionService.getDatasetData(datasetId);
            result=ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg(),paramsResult.toString());
        }catch (IOException e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.FILE_ERROR.getCode(),ResultCodeEnums.FILE_ERROR.getMsg(),"");
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

}
