package shu.xai.sys.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import shu.xai.sys.controller.UserController;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Created by yuziyi on 2022/7/4.
 */

@Component
public class EmailUtils {
    @Resource
    JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String emailServer;

    @Value("${project-params.project-ip}")
    private String projectIP;

    @Value("${server.port}")
    private String projectPort;

    private final static Logger logger = LoggerFactory.getLogger(EmailUtils.class);

    // 生成链接,并给接收的邮箱发送邮件
    public boolean sendCode(String email,String platformId){
        MimeMessage message = mailSender.createMimeMessage();
        try{
            MimeMessageHelper messageHelper = new MimeMessageHelper(message);
            messageHelper.setFrom(emailServer); //发送方的邮箱地址，而不是接收方的邮箱地址
            messageHelper.setTo(email); // 接收方的邮箱地址
            messageHelper.setSubject("平台注册用户账号激活");  // 邮箱标题
            String mass0=email+","+platformId;  //加密密文
            String mass= Base64.getEncoder().encodeToString(mass0.getBytes(StandardCharsets.UTF_8));
            String html = "<html>\n" +
                    "<body>\n" +
                    "<p>请点击下方链接注册</p>\n" +
                    "<a href=\"http://"+projectIP+":"+projectPort+"/api/user/emailRegister/"+mass+"\">http://"+projectIP+":"+projectPort+"/api/user/emailRegister/"+mass+"</a>" +
                    "</body>\n" +
                    "</html>";
            messageHelper.setText(html,true); // 邮箱内容
            mailSender.send(message);  // 发送邮箱
//            System.out.println("发送成功");
            return true;
        }catch (Exception e){
            logger.error("error",e);
//            e.printStackTrace();
//            System.out.println("发送失败");
            return false;
        }
    }


}
