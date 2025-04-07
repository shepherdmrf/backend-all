package shu.xai.lyzs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import shu.xai.lyzs.service.DataUploadService;
import shu.xai.sys.enums.ResultCodeEnums;
import shu.xai.sys.utils.JSONUtil;
import shu.xai.sys.utils.ResultUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static shu.xai.sys.utils.CoreUtils.getUUID;


/**
 * controller层，接收前端请求
 * Created by yuziyi on 2022/1/24.
 */
@RestController
@RequestMapping("/dataUpload")
@Transactional(transactionManager ="TransactionManagerlyzs")
public class DataUploadController {

    @Autowired
    private DataUploadService dataUploadService;

    private final static Logger logger = LoggerFactory.getLogger(DataUploadController.class);




    /**
     * 数据集上传
     * @param file 上传文件
     * @param dataDescription 数据集表参数
     * @param experimentData 数据集辅助表-实验表参数
     * @return result 查询结果封装
     */
    @RequestMapping("/selectDataFileUpload")
    public String selectFileUpload(HttpServletRequest request,@RequestParam("file") MultipartFile file,@RequestParam("dataDescription") String dataDescription,@RequestParam("experimentData") String experimentData){
        String result="";
        try {
            String record_name=file.getOriginalFilename();
            if(dataUploadService.repeatData(record_name,"dataSet")>0){
                return ResultUtils.commonResult(ResultCodeEnums.REPEAT_FILENAME.getCode(), ResultCodeEnums.REPEAT_FILENAME.getMsg(),"");
            }
            JSONObject params=JSONObject.parseObject(dataDescription);
            String d_abstract = params.getString("d_abstract");
            Integer d_size_m = params.getInteger("d_size_m");
            Integer d_size_n = params.getInteger("d_size_n");
            String keywords = params.getString("keywords");
            String domain_type = params.getString("domain_type");
            String area_type = params.getString("area_type");

            String d_submission_name = params.getString("d_submission_name");
            String d_submission_unit = params.getString("d_submission_unit");
            String d_proofreader = params.getString("d_proofreader");
            String email = params.getString("email");
            String telephoneNumber = params.getString("telephoneNumber");
            String contact_address = params.getString("contact_address");


            JSONObject params2=JSONObject.parseObject(experimentData);
            String data_sort = params2.getString("data_sort");
            String decide_type = params2.getString("decide_type");
            String material_trademark = params2.getString("material_trademark");
            String m_name = params2.getString("m_name");
            String expcon_name = params2.getString("expcon_name");
            String exp_parasetting = params2.getString("exp_parasetting");
            String exp_device_name = params2.getString("exp_device_name");
            String data_source = params2.getString("data_source");


            String op_user= String.valueOf(request.getSession().getAttribute("userName"));

            String save_path = "uploadFile"+ File.separator +"dataset"+ File.separator +op_user;
            String path=System.getProperty("user.dir")+File.separator +save_path;

            String file_attachment=save_path+ File.separator +file.getOriginalFilename();
            String uuid=getUUID();

            JSONObject paramsResult1=dataUploadService.addNewData(d_abstract, d_size_m, d_size_n, keywords, domain_type, area_type, d_submission_name, d_submission_unit, d_proofreader, email, telephoneNumber, contact_address, op_user,record_name,file_attachment,uuid);
            JSONObject paramsResult2=dataUploadService.addNewExp(data_sort, decide_type, material_trademark, m_name, expcon_name, exp_parasetting, exp_device_name, data_source,uuid);

            File f = new File(path);
            // 如果不存在该路径就创建
            if (!f.exists()) {
                f.mkdirs();
            }
//            System.out.println(path);
            File dir = new File(path + File.separator +file.getOriginalFilename());
            // 文件写入
            file.transferTo(dir);

            result= ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMsg(),file.getOriginalFilename());
        } catch (Exception e) {
            logger.error("error",e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(), ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    /**
     * 添加新算法
     * @param file 算法文件
     * @param algoDescription 算法说明文件
     * @param otherParams 其他附带数据
     * @return result 查询结果封装
     */
    @RequestMapping("/addNewAlgo")
    public String addNewAlgo(@RequestParam("file") MultipartFile file,@RequestParam("file2") MultipartFile algoDescription,@RequestParam("otherParams") String otherParams){
        String result="";
        try {
            String originFilename=file.getOriginalFilename();
            String originFilename2=algoDescription.getOriginalFilename();
            if(dataUploadService.repeatDataAlgo(originFilename,originFilename2)>0){
                return ResultUtils.commonResult(ResultCodeEnums.REPEAT_FILENAME.getCode(), ResultCodeEnums.REPEAT_FILENAME.getMsg(),"");
            }

            JSONObject params=JSONObject.parseObject(otherParams);
            String algoName=params.getString("algoName");
            String cal_field=params.getString("cal_field");
            String algoAbstract=params.getString("algoAbstract");
            String algoSubmission_name=params.getString("algoSubmission_name");  //算法作者
            String algoType=params.getString("algoType");
            String realUser=params.getString("realUser");  //当前操作用户

            String filePath=System.getProperty("user.dir")+File.separator+"uploadFile"+ File.separator +"algorithm"+File.separator+realUser;
            String algoLocation="uploadFile"+ File.separator +"algorithm"+File.separator+realUser;
//            System.out.println(filePath);
            String filePath2=System.getProperty("user.dir")+File.separator+"uploadFile"+ File.separator +"algorithmDescription"+File.separator+realUser;
            String algoLocation2="uploadFile"+ File.separator +"algorithmDescription"+File.separator+realUser;

            algoLocation=algoLocation+File.separator+originFilename;
            algoLocation2=algoLocation2+File.separator+originFilename2;
            JSONObject paramsResult=dataUploadService.addNewAlgo(algoName,cal_field,algoAbstract,algoSubmission_name,algoType,algoLocation,algoLocation2,realUser);


            File targetPath=new File(filePath);
            File targetPath2=new File(filePath2);
            if(!targetPath.exists()){
                targetPath.mkdirs();
            }
            if(!targetPath2.exists()){
                targetPath2.mkdirs();
            }
            File dir = new File(filePath + File.separator +file.getOriginalFilename());
            File dir2 = new File(filePath2 + File.separator +algoDescription.getOriginalFilename());
            file.transferTo(dir);
            algoDescription.transferTo(dir2);

           result=paramsResult.toString();
        } catch (Exception e) {
            logger.error("error",e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(), ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }

    /**
     * 算法说明文件展示
     * @param request http参数
     * @param response http参数
     * @return result 查询结果封装
     */
    @RequestMapping("/algorithmDescriptionShow/{algoId}")
    public void fileDownload(HttpServletRequest request,HttpServletResponse response,@PathVariable("algoId")String algoId){

        String path=dataUploadService.getalgorithmDescriptionPath(algoId);
        File file = new File(System.getProperty("user.dir")+File.separator+ path);
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
        } catch (Exception e) {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-type", "application/octet-stream;charset=UTF-8");
            response.setContentType("application/octet-stream;charset=UTF-8");
            //如果不设置响应头大小，可能报错：“Excel 已完成文件级验证和修复。此工作簿的某些部分可能已被修复或丢弃”
            response.addHeader("Content-Length","0");
            response.setHeader("down-status", "downerror");
            logger.error("error",e);
        }
//        return result;
    }


    /**
     * 添加文献
     */
    @RequestMapping("/addNewLiterature")
    public String addNewLiterature(@RequestParam("file") MultipartFile file, @RequestParam("otherParams")String otherParams){
        String result="";
        try{
            String fileName=file.getOriginalFilename();
            if(dataUploadService.repeatData(fileName,"literature")>0){
                return ResultUtils.commonResult(ResultCodeEnums.REPEAT_FILENAME.getCode(), ResultCodeEnums.REPEAT_FILENAME.getMsg(),"");
            }

            JSONObject params = JSON.parseObject(otherParams);

            String titleE=params.getString("titleE");
            String title=params.getString("title");
            String authorE=params.getString("authorE");
            String author=params.getString("author");
            String l_abstractE=params.getString("l_abstractE");
            String l_abstract=params.getString("l_abstract");
            String keywordsE=params.getString("keywordsE");
            String keywords=params.getString("keywords");

            String publish_date=params.getString("date");
            String reference_type=params.getString("reference_type");
            String research_institute=params.getString("research_institute");
            Integer volume= params.getInteger("volume");
            Integer issue= params.getInteger("issue");
            String doi=params.getString("doi");
            String material_method=params.getString("material_method");
            String ml_method=params.getString("ml_method");
            Integer start_page= params.getInteger("start_page");
            Integer  end_page= params.getInteger("end_page");


            String realUser=params.getString("realUser");


            String path = System.getProperty("user.dir")+File.separator+"uploadFile"+ File.separator +"literature"+ File.separator+realUser;
            File f = new File(path);
            // 如果不存在该路径就创建
            if (!f.exists()) {
                f.mkdirs();
            }
            File dir = new File(path +File.separator+ file.getOriginalFilename());

            String file_attachment = "uploadFile"+ File.separator +"literature"+ File.separator+realUser + File.separator +file.getOriginalFilename();
            // result= request.getSession().getAttribute("userName").toString();

            JSONObject paramsResult=dataUploadService.addNewLiterature(titleE,authorE,l_abstractE,keywordsE,title,author,l_abstract,keywords, file_attachment,fileName,doi,material_method,ml_method, research_institute,publish_date,start_page,end_page,volume,issue,reference_type,realUser);

            // 文件写入
            file.transferTo(dir);
            result=paramsResult.toString();
        }catch (Exception e){
            logger.error("error",e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(), ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }


    /**
     * 添加专利
     * @param file 上传专利文件
     * @param otherParams 专利其他参数
     * @return result 查询结果封装
     */
    @RequestMapping("/addNewPatent")
    public String addNewPatent(@RequestParam("file") MultipartFile file, @RequestParam("otherParams") String otherParams){
        String result="";
        try {
            String originFilename=file.getOriginalFilename();
            if(dataUploadService.repeatData(originFilename,"patent")>0){
                return ResultUtils.commonResult(ResultCodeEnums.REPEAT_FILENAME.getCode(), ResultCodeEnums.REPEAT_FILENAME.getMsg(),"");
            }

            //获取专利信息
            JSONObject params = JSON.parseObject(otherParams);
            String title=params.getString("title");
            String author=params.getString("author");
            String l_abstract=params.getString("l_abstract");
            String keywords=params.getString("keywords");

            String publish_date = params.getString("publish_date");
            String patent_number=params.getString("patent_number");
            String patent_type=params.getString("patent_type");
            String patent_institute=params.getString("patent_institute");
            String patent_region=params.getString("patent_region");

            String patent_submission_name=params.getString("patent_submission_name");

            String filePath=System.getProperty("user.dir")+File.separator+"uploadFile"+File.separator+"patent"+File.separator+patent_submission_name;
            String patentLocation="uploadFile"+File.separator+"patent"+File.separator+patent_submission_name;



            File targetFile=new File(filePath);
            if(!targetFile.exists()){
                targetFile.mkdirs();
            }
            File dir = new File(filePath+File.separator + file.getOriginalFilename());

            patentLocation=patentLocation+File.separator+originFilename;

            JSONObject paramsResult=dataUploadService.addNewPatent(title,author,l_abstract,
                    keywords,publish_date,patent_number,patent_type,patent_institute,patent_region,
                    patentLocation,patent_submission_name);
            //事务测试，可行
//            int i=1/0;
            file.transferTo(dir);
            //返回
            result= ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMsg(),"");
        } catch (Exception e) {
            logger.error("error",e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(), ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
        }
        return result;
    }














//    //todo 质量检测的,本质只做了文件传输，用不了
//    @RequestMapping("/excelShow")
//    public void excelShow(HttpServletRequest request,HttpServletResponse response){
//        String path = System.getProperty("user.dir")+ File.separator +"src"+ File.separator +"main"+ File.separator +"webapp"+
//                File.separator +"uploadFile"+ File.separator +"123.xlsx";
//        File file = new File(path);
//        try {
//
//            //设置编码格式，防止下载的文件内乱码
//            response.setCharacterEncoding("UTF-8");
//            //获取路径文件对象
//            String realFileName = file.getName();
//            //设置响应头类型，这里可以根据文件类型设置，text/plain、application/vnd.ms-excel等
//            System.out.printf(realFileName);
////            response.setHeader("content-type", "application/octet-stream;charset=UTF-8");
////            response.setContentType("application/octet-stream;charset=UTF-8");
//            response.setHeader("content-type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
//            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
//            //如果不设置响应头大小，可能报错：“Excel 已完成文件级验证和修复。此工作簿的某些部分可能已被修复或丢弃”
//            response.addHeader("Content-Length", String.valueOf(file.length()));
//            response.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(realFileName.trim(),
//                    "UTF-8"));
//
//            byte[] buff = new byte[10240];
//            OutputStream outputStream = response.getOutputStream();
//            InputStream inputStream = new FileInputStream(file);
//            int len;
//            while((len = inputStream.read(buff)) > 0){
//                outputStream.write(buff, 0, len);
//            }
//            inputStream.close();
//            outputStream.flush();
//            outputStream.close();
////            result= ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg(),"");
//        } catch (Exception e) {
////            e.printStackTrace();
//            logger.error("error",e);
////            result= ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg(),"");
//        }
////        return result;
//    }

}
