package com.company.toutiao.utils;

import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.util.Map;
import java.util.Properties;

@Service
public class MailSender implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(MailSender.class);
    private JavaMailSenderImpl mailSender;
    @Autowired
    private FreeMarkerConfigurer configurer;


    /**
     * 使用HTML模板发送邮件
     *
     * @param to       收件人
     * @param subject  主题
     * @param template 使用的模板
     * @param model    模板中要替换的内容
     * @return 邮件是否发送成功
     */
    public boolean sendWithHTMLTemplate (String to, String subject, String template, Map<String, Object> model) {
        try {
            String nick = MimeUtility.encodeText("牛客高级课");
            InternetAddress from = new InternetAddress(nick + "<1092811022@qq.com>");
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            Template template1 = configurer.getConfiguration().getTemplate(template);
            String text = FreeMarkerTemplateUtils.processTemplateIntoString(template1, model);
            mimeMessageHelper.setText(text);
            mailSender.send(mimeMessage);
        }
        catch (Exception e) {
            logger.error("发送邮件失败" + e.getMessage());
        }
        return false;
    }

    @Override
    public void afterPropertiesSet () throws Exception {
        mailSender = new JavaMailSenderImpl();
        mailSender.setUsername("1092811022@qq.com");
        mailSender.setPassword("laj19950504");
        mailSender.setHost("smtp.qq.com");
        mailSender.setPort(465);
        mailSender.setProtocol("smtps");
        mailSender.setDefaultEncoding("utf-8");
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.ssl.enable", true);
        mailSender.setJavaMailProperties(javaMailProperties);
    }
}
