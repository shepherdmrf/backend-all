package shu.xai.sys.aop;

import shu.xai.sys.service.LogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by yuziyi on 2021/6/19.
 */
@Aspect//这是一个切面
@Component//告诉Spring需要将其加入到IOC容器
public class LogAop {

    @Autowired
    private LogService logService;

    private final static Logger logger = LoggerFactory.getLogger(LogAop.class);


    /**
     * 配置切入点
     */
    @Pointcut("execution(* shu.xai.*.controller..*.*(..))")
    public void executeService(){

    }


    /**
     * 前置增强
     */
    @Before("executeService()")
    public void before(JoinPoint joinPoint){
//        System.out.println("前置增强开始.....Before.");
    }

    @After("executeService()")
    public void after(JoinPoint joinPoint) {
//        System.out.println("方法执行结束.....After");
    }

    /**
     * 返回成功后的增强
     */
    @AfterReturning("executeService()")
    public void AfterReturning(JoinPoint joinPoint) {
//        System.out.println("返回成功......AfterReturning");
        try{

//        //用的最多 通知的签名
//        Signature signature = joinPoint.getSignature();
//        //代理的是哪一个方法
//        System.out.println("==> 代理的是哪一个方法 :"+signature.getName());
//        //AOP代理类的名字
//        System.out.println("==> AOP代理类的名字:"+signature.getDeclaringTypeName());
        //获取RequestAttributes
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        //从获取RequestAttributes中获取HttpServletRequest的信息
            HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        //如果要获取Session信息的话，可以这样写：
//        HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);
            if(request.getSession(false) != null) {
                logService.logInsert(String.valueOf(request.getSession(false).getAttribute("userId")), request.getRequestURI());
            }
        }catch (Exception e){
            logger.error("error",e);
        }
    }


    /**
     * 方法抛出异常退出时执行的通知
     */
    @AfterThrowing("executeService()")
    public void AfterThrowing(JoinPoint joinPoint){
//        System.out.println("出现异常了......AfterThrowing");
    }


//    @Around("executeService()")
//    public Object around(JoinPoint jointPoint){
//
//    }


}
