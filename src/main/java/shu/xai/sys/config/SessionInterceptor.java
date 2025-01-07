package shu.xai.sys.config;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import shu.xai.sys.enums.ResultCodeEnums;
import shu.xai.sys.enums.StatusCodeEnums;
import shu.xai.sys.service.UserService;
import shu.xai.sys.utils.ResultUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Created by yuziyi on 2021/6/28.
 */
@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //session过期
        if (request.getSession(false)==null||request.getSession(false).getAttribute("userId")==null) {
            JSONObject res = ResultUtils.commonResultJSON(ResultCodeEnums.SESSION_OVERDUR.getCode(),ResultCodeEnums.SESSION_OVERDUR.getMsg());
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            PrintWriter out = null;
            out = response.getWriter();
            out.append(res.toString());
            return false;
        }
        //用户被管理员禁用校验
        String userId= String.valueOf(request.getSession(false).getAttribute("userId"));
        if(!userService.getUserStatus(userId).equals(StatusCodeEnums.ACTIVE.getCode())){
            JSONObject res = ResultUtils.commonResultJSON(ResultCodeEnums.NO_USER.getCode(),ResultCodeEnums.NO_USER.getMsg());
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            PrintWriter out = null;
            out = response.getWriter();
            out.append(res.toString());
            return false;
        }
        // 用户页面功能权限校验：需要新建用户-页面-操作的对应表解决或者直接前端解决，可以在想想（与同一浏览器session覆盖有关），
        // 不解决也问题不大,页面限制也够了


        return true;
    }
}
