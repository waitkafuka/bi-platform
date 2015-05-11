package com.baidu.rigel.biplatform.ma.auth.mail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "file:src/test/resources/applicationContext.xml")
public class SendMailTest {

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

	@Value("${biplatform.ma.auth.register.mail.sender.password}")
	private String mailSenderPassowrd;

	/**
     * 
     */
	@Test
	public void test() throws Exception {
		SendMail sendMail = new SendMail();
		// 发送方
		sendMail.setFromAddress(mailSender);
		// 接收方，平台管理员
		sendMail.setToAddress(mailReceiver);
		// 邮件主题
		sendMail.setSubject(mailSubject);
		// 邮件服务器地址
		sendMail.setMailServerHost(mailServer);
		// 设置是否需要验证
		sendMail.setNeedAuth(false);
		// 设置发送方名字
		sendMail.setUserName(mailSender);
		// 设置发送邮件用户密码
		if (!StringUtils.isEmpty(mailSenderPassowrd)) {
			sendMail.setPassword(mailSenderPassowrd);
		}
		// 设置发送内容和格式
		sendMail.setBody("test case for send mail", SendMail.HTML);		
		sendMail.send();
		// 设置发送内容为HTML格式
		sendMail.setBodyAsHTML("test case for send mail using html type");
		sendMail.send();
		// 设置发送内容为文本格式
		sendMail.setBodyAsText("test case for send mail using txt type");
		// 设置附件
		sendMail.addAttachFromString("test", "showname");
		sendMail.send();
		// 设置邮件内容来自文件
		sendMail.setBodyFromFile("classPath:/src/test/resources/applicationContext.xml");
		// 设置附件
		sendMail.addAttachFromFile("classPath:/src/test/resources/applicationContext.xml", "showName");
		sendMail.send();
		
		// 设置邮件内容来自url
		sendMail.setBodyFromUrl("http://www.github.com");
		sendMail.addAttachFromUrl("http://www.github.com", "showName");
		sendMail.send();
	}
}
