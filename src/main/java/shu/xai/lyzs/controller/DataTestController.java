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
import shu.xai.lyzs.entity.DataTest;
import shu.xai.lyzs.entity.PatentData;
import shu.xai.lyzs.entity.WangLiteData;
import shu.xai.lyzs.service.DataRetrieveService;
import shu.xai.lyzs.service.DataTestService;
import shu.xai.sys.domain.Result;
import shu.xai.sys.enums.ResultCodeEnums;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/dataTest")
public class DataTestController {

    @Autowired
    DataTestService dataTestService;

    @GetMapping("/getTestData")
    public List<DataTest> getInterData(@RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "10") Integer pageSize) {
        System.out.println(pageSize);
        Integer offset = (pageNum - 1) * pageSize;
        return dataTestService.getTest(offset,pageSize);
    }
    @GetMapping("/getAllData")
    public List<DataTest> getAllData() {
        return dataTestService.getAll();
    }







}
