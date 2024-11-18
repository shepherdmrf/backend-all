package shu.xai.sys.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shu.xai.sys.component.EmailUtils;

import javax.annotation.Resource;

/**
 * Created by yuziyi on 2022/7/4.
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    EmailUtils emailUtils;

    @RequestMapping("/email")
    public boolean sendCode() {
        // 调用验证链接生成工具类中的生成链接和发送邮件函数
        return emailUtils.sendCode("yuziyishu@163.com", "111");
    }


}
