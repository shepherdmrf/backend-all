package shu.xai.lyzs.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import shu.xai.lyzs.entity.DataDescription;
import shu.xai.lyzs.entity.PatentData;
import shu.xai.lyzs.entity.WangLiteData;
import shu.xai.lyzs.service.DataRetrieveService;
import shu.xai.sys.controller.UserController;
import shu.xai.sys.domain.Result;
import shu.xai.sys.enums.ResultCodeEnums;
import shu.xai.sys.utils.ResultUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/dataRetrieve")
public class DataRetrieveController {

    @Autowired
    DataRetrieveService dataRetrieveService;

    private final static Logger logger = LoggerFactory.getLogger(DataRetrieveController.class);


    /**
     * 高温合金数据集检索
     */
    @GetMapping("/getInterData")
    public Result<Object> getInterData(@RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                       @RequestParam(defaultValue = "") String d_abstract,
                                       @RequestParam(defaultValue = "") String d_submission_name,
                                       @RequestParam(defaultValue = "") String d_submission_unit,
                                       @RequestParam(defaultValue = "") String keywords,
                                       @RequestParam(defaultValue = "") String d_size_m) {
        Result<Object> result = new Result<>();
        try{
            PageHelper.startPage(pageNum, pageSize);
            List<DataDescription> data = dataRetrieveService.getInterData(pageNum, pageSize, d_abstract, d_submission_name, d_submission_unit, keywords, d_size_m);
            PageInfo<DataDescription> DataDescriptionPageInfo = new PageInfo<>(data);
            Long total = DataDescriptionPageInfo.getTotal();
            Map<String, Object> res = new HashMap<>();
            res.put("list", data);
            res.put("total", total);
            result.setCode(ResultCodeEnums.SUCCESS.getCode());
            result.setMsg(ResultCodeEnums.SUCCESS.getMsg());
            result.setData(res);

        }catch (Exception e){
            logger.error("error",e);
            result.setCode(ResultCodeEnums.UNKNOWN_ERROR.getCode());
            result.setMsg(ResultCodeEnums.UNKNOWN_ERROR.getMsg());
        }
        return result;
    }

    @GetMapping("/getCurrentUserData")
    public Result<Object> getCurrentUserData(@RequestParam("currentUser") String currentUser) {
        Result<Object> result = new Result<>();
        try{
            List<DataDescription> data = dataRetrieveService.getCurrentUserData(currentUser);
            result.setCode(ResultCodeEnums.SUCCESS.getCode());
            result.setMsg(ResultCodeEnums.SUCCESS.getMsg());
            result.setData(data);
        }catch (Exception e){
            logger.error("error",e);
            result.setCode(ResultCodeEnums.UNKNOWN_ERROR.getCode());
            result.setMsg(ResultCodeEnums.UNKNOWN_ERROR.getMsg());
        }
        return result;
    }

    @RequestMapping("/dataDownload")
    public void literatureDownload(HttpServletRequest request, HttpServletResponse response, @RequestParam("file_save_path") String fileSavePath){
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
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(realFileName.trim(), "UTF-8"));
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
        }catch (FileNotFoundException e){
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-type", "application/octet-stream;charset=UTF-8");
            response.setContentType("application/octet-stream;charset=UTF-8");
            //如果不设置响应头大小，可能报错：“Excel 已完成文件级验证和修复。此工作簿的某些部分可能已被修复或丢弃”
            response.addHeader("Content-Length","0");
            response.setHeader("down-status", "downnofound");
        }catch (Exception e) {
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





    @GetMapping("/getPatentData")
    public Result<Object> getPatentData(@RequestParam(defaultValue = "1") Integer pageNum,
                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                        @RequestParam(defaultValue = "") String maximum,
                                        @RequestParam(defaultValue = "") String minimum,
                                        @RequestParam(defaultValue = "") String elementName) {
        Result<Object> result = new Result<>();
        try{
            PageHelper.startPage(pageNum, pageSize);
            List<PatentData> data = dataRetrieveService.getPatentData(maximum, minimum, elementName);
            PageInfo<PatentData> PatentDataPageInfo = new PageInfo<>(data);
            Long total = PatentDataPageInfo.getTotal();
            Map<String, Object> res = new HashMap<>();
            res.put("list", data);
            res.put("total", total);
            result.setCode(ResultCodeEnums.SUCCESS.getCode());
            result.setMsg(ResultCodeEnums.SUCCESS.getMsg());
            result.setData(res);
        }catch (Exception e){
            logger.error("error",e);
            result.setCode(ResultCodeEnums.UNKNOWN_ERROR.getCode());
            result.setMsg(ResultCodeEnums.UNKNOWN_ERROR.getMsg());
        }
        return result;
    }

    @GetMapping("/exportElementData")
    public void export(HttpServletResponse response,@RequestParam("idlist") String idlist) {
        try {
            //idlist ="1,2,3,4,5,6,7"
            List<PatentData> list= dataRetrieveService.getDownloadPatentData(idlist);
//            int i=1/0;
            //用工具类创建writer，写出到浏览器
            ExcelWriter writer = ExcelUtil.getWriter(true);
            //自定义标题名
    //        writer.addHeaderAlias("sid","学号");
    //        writer.addHeaderAlias("name","姓名");
    //        writer.setOnlyAlias(true);
            //写出list内的对象到excel，强制输出标题
            writer.write(list,true);
            //设置浏览器响应格式
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            String fileName = URLEncoder.encode("dl","UTF-8");
            response.setHeader("Content-Disposition","attachment;filename=" + fileName + ".xlsx");
            response.setHeader("down-status", "downsuccess");

            ServletOutputStream out = response.getOutputStream();
            writer.flush(out, true);
            out.close();
            writer.close();
        } catch (Exception e) {
//            e.printStackTrace();

            response.setHeader("down-status", "downerror");
            logger.error("error",e);
        }

    }

    @PostMapping("/multiDelete")
    public Result<Object> multiDelete(@RequestBody String ids) {
        Result<Object> result = new Result<>();
        try{
    //        System.out.println(ids);
            JSONObject jsonObject = JSONUtil.parseObj(ids);
    //        System.out.println(jsonObject.getStr("ids"));
            JSONArray arr = JSONUtil.parseArray(jsonObject.getStr("ids"));
    //        System.out.println(arr.size());
    //        for (int i = 0; i < arr.size(); i++) {
    //            System.out.println(arr.get(i));
    //        }
            Integer data = dataRetrieveService.multiDelete(arr);

            result.setCode(ResultCodeEnums.SUCCESS.getCode());
            result.setMsg("成功删除" + data + "条数据");
            result.setData(data);
        }catch (Exception e){
            logger.error("error",e);
            result.setCode(ResultCodeEnums.UNKNOWN_ERROR.getCode());
            result.setMsg(ResultCodeEnums.UNKNOWN_ERROR.getMsg());
        }
        return result;
    }


    @GetMapping("/getWangData")
    public Result<Object> getInterData(@RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "10") Integer pageSize) {
        Result<Object> result = new Result<>();
        try{
            PageHelper.startPage(pageNum, pageSize);
            List<WangLiteData> data = dataRetrieveService.getWangData();
            PageInfo<WangLiteData> WangLiteDataPageInfo = new PageInfo<>(data);
            Long total = WangLiteDataPageInfo.getTotal();
            Map<String, Object> res = new HashMap<>();
            res.put("list", data);
            res.put("total", total);
            result.setCode(ResultCodeEnums.SUCCESS.getCode());
            result.setMsg(ResultCodeEnums.SUCCESS.getMsg());
            result.setData(res);
        }catch (Exception e){
            logger.error("error",e);
            result.setCode(ResultCodeEnums.UNKNOWN_ERROR.getCode());
            result.setMsg(ResultCodeEnums.UNKNOWN_ERROR.getMsg());
        }
        return result;
    }






}
