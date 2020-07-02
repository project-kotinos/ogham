package fr.sii.standalone.runtime.testing;

import static junit.filter.Assumptions.requires;

import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;

import fr.sii.ogham.core.service.MessagingService;

import fr.sii.ogham.runtime.javamail.JavaMailRunner;
import fr.sii.ogham.runtime.javamail.JavaMailChecker;

public class JavaMailTest {
	@Rule public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);
	
	JavaMailRunner runner;
	JavaMailChecker checker;
	
	@Before
	public void setup() {
		requires("email-javamail");

		Properties props = new Properties();
		props.setProperty("mail.smtp.host", ServerSetupTest.SMTP.getBindAddress());
		props.setProperty("mail.smtp.port", String.valueOf(ServerSetupTest.SMTP.getPort()));
		
		MessagingService service = new StandaloneApp().init(props);
		runner = new JavaMailRunner(service);
		checker = new JavaMailChecker(greenMail);
	}

	@Test
	public void emailWithoutTemplate() throws Exception {
		runner.sendEmailWithoutTemplate();
		checker.assertEmailWithoutTemplate();
	}
	
	@Test
	public void emailWithThymeleaf() throws Exception {
		requires("template-thymeleaf");
		
		runner.sendEmailWithThymeleaf();
		checker.assertEmailWithThymeleaf();
	}
	
	@Test
	public void emailWithFreemarker() throws Exception {
		requires("template-freemarker");
		
		runner.sendEmailWithFreemarker();
		checker.assertEmailWithFreemarker();
	}
	
	@Test
	public void emailWithThymeleafAndFreemarker() throws Exception {
		requires("template-thymeleaf", "template-freemarker");
		
		runner.sendEmailWithThymeleafAndFreemarker();
		checker.assertEmailWithThymeleafAndFreemarker();
	}
}
