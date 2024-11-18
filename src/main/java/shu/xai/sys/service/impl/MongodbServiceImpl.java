//package shu.xai.sys.service.impl;
//
//import com.alibaba.fastjson.JSONObject;
//import shu.xai.sys.entity.mongodb.Test;
//import shu.xai.sys.service.MongodbService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.regex.Pattern;
//
///**
// * Created by yuziyi on 2021/8/27.
// */
//@Service("MongodbService")
//public class MongodbServiceImpl implements MongodbService {
//    @Autowired
//    private MongoTemplate mongoTemplate;
//
//
//    @Override
//    public JSONObject demo() {
//        JSONObject result=new JSONObject();
//        Test test=new Test("阿萨","aaa","bbb");
//        //C(增)
//        //单个增
//        //若新增数据的主键已存在：save会对存在数据更新、insert不会保存
//        mongoTemplate.save(test);
//
//        //批量增
//        //若新增数据的主键已经存在，则会抛异常提示主键重复，不保存当前数据。
//        //mongoTemplate.insert(testlist);
//
//
//        //R(查)
//        //查询所有
//        List<Test> data= mongoTemplate.findAll(Test.class);
//        result.put("d",data);
//        //根据ID查询
//        //mongoTemplate.findById(id,Test.class);
//
//        //根据姓名准确查询
//        String name="aaaa";
//        Query query = new Query(Criteria.where("name1").is(name));
//        List<Test> data1= mongoTemplate.find(query, Test.class);
//        result.put("a",data1);
//
//        //模糊查询
//        // Pattern.CASE_INSENSITIVE 忽略大小写
//        Pattern pattern=Pattern.compile("^.*"+"aa"+".*$", Pattern.CASE_INSENSITIVE);
//        Query query1 = new Query(Criteria.where("name1").regex(pattern));
//        List<Test> data2= mongoTemplate.find(query1, Test.class);
//        result.put("b",data2);
//
//        //多条件查询
//        //所给例为与条件、若为或条件，将andOperator改为orOperator
//        Query query3 = new Query(new Criteria().andOperator(Criteria.where("aaa").is("aaa"), Criteria.where("bbb").is("bbb")));
//        List<Test> data3= mongoTemplate.find(query3, Test.class);
//        result.put("c",data3);
//
//        //分页查询
//
//
//        //D(删)
//        mongoTemplate.remove(test);
//
//        return result;
//    }
//}
