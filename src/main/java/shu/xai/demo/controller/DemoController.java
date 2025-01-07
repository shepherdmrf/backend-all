package shu.xai.demo.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import shu.xai.demo.service.DemoService;
import shu.xai.sys.enums.ResultCodeEnums;
import shu.xai.sys.utils.JSONUtil;
import shu.xai.sys.utils.ResultUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * controller层，接收前端请求
 * Created by yuziyi on 2022/1/24.
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    private DemoService demoService;

    private final static Logger logger = LoggerFactory.getLogger(DemoController.class);

    /**
     * 查询专利列表并返回
     * @param request http参数
     * @param response http参数
     * @return result 查询结果封装
     */
    @RequestMapping("/getPatentData")
    public String getPatentData(HttpServletRequest request, HttpServletResponse response){
        String result="";
        try{
            JSONArray paramsResult=demoService.getPatentList();
            result= ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg(),paramsResult.toJSONString());
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    /**
     * 添加专利
     * @param request http参数
     * @param response http参数
     * @return result 操作结果封装返回
     */
    @RequestMapping("/addNewPatent")
    public String addNewPatent(HttpServletRequest request,HttpServletResponse response){
        String result="";
        try{
            JSONObject params= JSONUtil.getRequestPayload(request);
            String title=params.getString("title");
            String titleE=params.getString("titleE");
            String author=params.getString("author");
            String authorE=params.getString("authorE");
            String l_abstract=params.getString("l_abstract");
            String l_abstractE=params.getString("l_abstractE");
            String keywords=params.getString("keywords");
            String keywordsE=params.getString("keywordsE");
            JSONObject paramsResult=demoService.addNewPatent(title,titleE,author,authorE,l_abstract,l_abstractE,keywords,keywordsE);
            result=paramsResult.toString();
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    /**
     * 查询专利列表并返回
     * @param file 上传文件
     * @param otherParams 其他参数
     * @return result 查询结果封装
     */
    @RequestMapping("/selectFileUpload")
    public String selectFileUpload(@RequestParam("file") MultipartFile file,@RequestParam("otherParams") String otherParams){
        String result="";
        //判断非空
        if (file.isEmpty()) {
            result= ResultUtils.commonResult(ResultCodeEnums.FILE_NULL.getCode(),ResultCodeEnums.FILE_NULL.getMsg(),"");
            return result;
        }
        try {

//            System.out.printf(JSONObject.parseObject(otherParams).getString("aa"));

            String path = System.getProperty("user.dir")+ File.separator +"uploadFile";
            File f = new File(path);
            // 如果不存在该路径就创建
            if (!f.exists()) {
                f.mkdir();
            }
            File dir = new File(path + File.separator +file.getOriginalFilename());
            // 文件写入
            file.transferTo(dir);
            result= ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg(),"");
        } catch (Exception e) {
            e.printStackTrace();
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }


}
