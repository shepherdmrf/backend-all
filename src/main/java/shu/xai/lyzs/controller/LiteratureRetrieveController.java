package shu.xai.lyzs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import shu.xai.lyzs.service.DataUploadService;
import shu.xai.lyzs.service.LiteratureRetrieveService;
import shu.xai.sys.enums.ResultCodeEnums;
import shu.xai.sys.utils.JSONUtil;
import shu.xai.sys.utils.ResultUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import static shu.xai.sys.utils.CoreUtils.getUUID;

/**
 * controller层，接收前端请求
 * Created by yuziyi on 2022/1/24.
 */
@RestController
@RequestMapping("/literatureRetrieve")
public class LiteratureRetrieveController {

    @Autowired
    private LiteratureRetrieveService literatureRetrieveService;

    private final static Logger logger = LoggerFactory.getLogger(LiteratureRetrieveController.class);



    /**
     * 查询文献列表并返回
     * @param request http参数
     * @param response http参数
     * @return result 查询结果封装
     */
    @RequestMapping("/getLiteratureData")
    public String getLiteratureData(HttpServletRequest request, HttpServletResponse response){
        String result="";
        try{
            JSONArray paramsResult=literatureRetrieveService.getLiteratureList();
            result= ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMsg(),paramsResult.toJSONString());
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(), ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    /**
     * 普通搜索文献列表并返回
     * @param request http参数
     * @param response http参数
     * @return result 查询结果封装
     */
    @RequestMapping("/getLiteratureDataOrdinary")
    public String getLiteratureDataOrdinary(HttpServletRequest request, HttpServletResponse response){
        String result="";
        try{

            JSONObject params= JSONUtil.getRequestPayload(request);
            String literatureSearch=params.getString("literatureSearch");
            JSONArray paramsResult=literatureRetrieveService.LiteratureSearchTemp(literatureSearch);
            result= ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMsg(),paramsResult.toJSONString());
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(), ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }


    /**
     * 高级搜索文献列表并返回
     * @param request http参数
     * @param response http参数
     * @return result 查询结果封装
     */
    @RequestMapping("/getLiteratureDataSenior")
    public String getLiteratureDataSenior(HttpServletRequest request, HttpServletResponse response){
        String result="";
        try{

            JSONObject params= JSONUtil.getRequestPayload(request);
            String authorE=params.getString("authorE");
            String keywordsE=params.getString("keywordsE");
            String titleE=params.getString("titleE");
            String referenceType=params.getString("referenceType");
            JSONArray paramsResult=literatureRetrieveService.LiteratureSearchSenior(authorE,keywordsE,titleE,referenceType);
            result= ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMsg(),paramsResult.toJSONString());
        }catch (Exception e){
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(), ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    /**
     * 文献下载,通过setHeader的自定义参数设置成功失败
     */
    @RequestMapping("/literatureDownload")
    public void literatureDownload(HttpServletRequest request, HttpServletResponse response,@RequestParam("file_save_path") String fileSavePath){
        String path = System.getProperty("user.dir")+ File.separator +fileSavePath;
        File file = new File(path);
        try {

            //设置编码格式，防止下载的文件内乱码
            response.setCharacterEncoding("UTF-8");
            //获取路径文件对象
            String realFileName = file.getName();
            //设置响应头类型，这里可以根据文件类型设置，text/plain、application/vnd.ms-excel等
            response.setHeader("content-type", "application/octet-stream;charset=UTF-8");
            response.setContentType("application/octet-stream;charset=UTF-8");
            //如果不设置响应头大小，可能报错：“Excel 已完成文件级验证和修复。此工作簿的某些部分可能已被修复或丢弃”
            response.addHeader("Content-Length", String.valueOf(file.length()));
            response.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(realFileName.trim(), "UTF-8"));
            response.setHeader("down-status", "downsuccess");
//            int i=1/0;
            byte[] buff = new byte[10240];
            OutputStream outputStream = response.getOutputStream();
            InputStream inputStream = new FileInputStream(file);
            int len;
            while((len = inputStream.read(buff)) > 0){
                outputStream.write(buff, 0, len);
            }
            inputStream.close();
            outputStream.flush();
            outputStream.close();
        }catch (FileNotFoundException e){
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-type", "application/octet-stream;charset=UTF-8");
            response.setContentType("application/octet-stream;charset=UTF-8");
            //如果不设置响应头大小，可能报错：“Excel 已完成文件级验证和修复。此工作簿的某些部分可能已被修复或丢弃”
            response.addHeader("Content-Length","0");
            response.setHeader("down-status", "downnofound");
        } catch (Exception e) {
//            e.printStackTrace();
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-type", "application/octet-stream;charset=UTF-8");
            response.setContentType("application/octet-stream;charset=UTF-8");
            //如果不设置响应头大小，可能报错：“Excel 已完成文件级验证和修复。此工作簿的某些部分可能已被修复或丢弃”
            response.addHeader("Content-Length","0");
            response.setHeader("down-status", "downerror");
            logger.error("error",e);
        }
//        }
    }


    /**
     * 查询关键词数量
     * @param request http参数
     * @param response http参数
     * @return result 查询结果封装
     */
    @RequestMapping("/getKeywordsNum")
    public String getKeywordsNum(HttpServletRequest request, HttpServletResponse response){
        String result="";
        try {
            JSONObject params= JSONUtil.getRequestPayload(request);
            String keywordsE=params.getString("keywordsE");
            String[] vresult= literatureRetrieveService.getKeywordsNum(keywordsE);
            JSONObject re=new JSONObject();
            re.put("countresult",vresult);
            result= ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg(),re.toJSONString());
        } catch (Exception e) {
            logger.error("error",e);
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(), ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }




}
