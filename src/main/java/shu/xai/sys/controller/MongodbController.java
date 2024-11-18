//package shu.xai.sys.controller;
//
//import shu.xai.sys.service.MongodbService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
///**
// * Created by yuziyi on 2021/8/27.
// */
//@RestController
//@RequestMapping("/mongodb")
//public class MongodbController {
//
//    @Autowired
//    private MongodbService mongodbService;
//
//    @RequestMapping("/demo")
//    public String demo(HttpServletRequest request, HttpServletResponse response){
//        String result="";
//        try{
//            result=mongodbService.demo().toJSONString();
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }finally {
//
//        }
//
//        return result;
//    }
//
//}
