package shu.xai.lyzs.controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import shu.xai.lyzs.entity.data_Cluster;
import shu.xai.lyzs.entity.data_all_Cluster;
import shu.xai.lyzs.service.dataClusterService;
import shu.xai.lyzs.service.dataAllClusterService;
import shu.xai.lyzs.service.impl.readcluster;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;

@RestController
@RequestMapping("/Cluster")
public class getClusternum {
//    public String firstChar="4";
//    @RequestMapping("/getclusternum")
//    public String getnum(@RequestBody String num){
//         firstChar = num.substring(0,1);
//        return firstChar;
//
//
//    @Autowired
//    private readcluster readcluster;
    @Autowired
    dataClusterService dataClusterService;
    @Autowired
    dataAllClusterService dataAllClusterService;
    @GetMapping("/readColumns")
    public List<data_Cluster> getData_cluster(@RequestParam String cluster,@RequestParam String clusterAlgorithmValue) {
//        System.out.println("111"+cluster);
//        System.out.println("clusterAlgorithmValue="+clusterAlgorithmValue);

        return dataClusterService.getData_cluster(cluster,clusterAlgorithmValue);
    }
    @GetMapping("/readAllColumns")
    public List<data_all_Cluster> getAllData_cluster(@RequestParam String cluster,@RequestParam String clusterAlgorithmValue) {
//        System.out.println(cluster);
        int cluster1=Integer.parseInt(cluster);
//        System.out.println("cluster1="+cluster1);
        return dataAllClusterService.getAllData_cluster(cluster1,clusterAlgorithmValue);
    }

}
