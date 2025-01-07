//package shu.xai.sys;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import javax.annotation.Resource;
//import java.util.List;
//import java.util.Map;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class BackendDemoWorkApplicationTests {
//
//    @Resource(name = "jdbcTemplateWork")
//    private JdbcTemplate jdbcTemplateWork;
//
//    @Autowired
//    @Qualifier("jdbcTemplateTest")
//    private JdbcTemplate jdbcTemplateTest;
//
//
//
//    private final static Logger logger = LoggerFactory.getLogger(BackendDemoWorkApplicationTests.class);
//
//    @Test
//    public void testDruid() {
//        List<Map<String,Object>> books1 = jdbcTemplateWork.queryForList("select * from platform_info");
//        List<Map<String,Object>> books2 = jdbcTemplateTest.queryForList("select * from indexinfo");
//        System.out.println(books1.toString());
//        System.out.println(books2.toString());
//
//
//    }
//
//    @Test
//    public void testLog() {
//        logger.info("info level");
//        logger.warn("warn level");
//        logger.error("error level");///后面在catch中用logger.error(e)来处理即可
//
//        //若项目启动失败怎么写入error，好像不行logger都没注入怎么写入文件，
//        // 应该是运维上看到项目失败信息解决，基本上不会出现
//        //log4j2本身在error里不会输出e
//
//    }
//
//
//
//
//
//    @Test
//    public void jkk() {
//        System.out.println();
//    }
//}
