package com.baidu.rigel.biplatform.ma.auth.mail;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 测试注册邮件配置类
 * @author yichao.jiang
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= "file:src/test/resources/applicationContext.xml")
public class RegisterMailConfigTest {
    /**
     * mailReceiver
     */
    @Value("${biplatform.ma.auth.register.mail.administrator}")
    private String mailReceiver;
    
    /**
     * mailSubject
     */
    @Value("${biplatform.ma.auth.register.mail.subjectForRegister}")
    private String mailSubject;
    
    /**
     * mailServer
     */
    @Value("${biplatform.ma.auth.register.mail.mailServerHost}")
    private String mailServer;
    
    /**
     * mailSender
     */
    @Value("${biplatform.ma.auth.register.mail.senderMail}")
    private String mailSender;
    
    /**
     * openServiceSubject
     */
    @Value("${biplatform.ma.auth.register.mail.subjectForOpenService}")
    private String openServiceSubject;
    
    /**
     * 
     */
    @Test 
    public void test() {
    	Assert.assertEquals(mailReceiver, RegisterMailConfig.getAdministrator());
    	Assert.assertEquals(mailSender, RegisterMailConfig.getSenderMail());
    	Assert.assertEquals(mailServer, RegisterMailConfig.getMailServerHost());
    	Assert.assertEquals(mailSubject, RegisterMailConfig.getSubjectForRegister());
    	Assert.assertEquals(openServiceSubject, RegisterMailConfig.getSubjectForOpenService());
    }
}
