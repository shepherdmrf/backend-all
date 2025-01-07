package shu.xai.sys.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by yuziyi on 2021/6/28.
 */

@Configuration
public class SessionConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    private SessionInterceptor sessionInterceptor;

    //注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor).
                addPathPatterns("/**").
                excludePathPatterns("/user/login","/user/getPlatformInfo","/user/getSinglePlatformInfo","/user/userRegister3","/index.html","/static/**","/","/mongodb/demo").
                excludePathPatterns("/user/valRepeatEmail","/user/valRepeatTelephone","/user/valRepeatUserName").
                excludePathPatterns("/user/emailRegister/**");
    }

//    Spring Boot 默认为我们提供了静态资源处理，提供的静态资源映射如下:
//    classpath:/META-INF/resources
//    classpath:/resources
//    classpath:/static
//    classpath:/public

    //前端已做过-不用了
//    // 设置跨域访问
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("*")
//                .allowedMethods("GET", "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "TRACE")
//                .allowCredentials(true);
//    }
}
