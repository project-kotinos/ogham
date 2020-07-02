package fr.sii.springboot.runtime.testing;

import static junit.filter.Assumptions.requires;

import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;

import fr.sii.ogham.core.service.MessagingService;

import fr.sii.ogham.runtime.javamail.JavaMailRunner;
import fr.sii.ogham.runtime.javamail.JavaMailChecker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest({
	"mail.smtp.host=127.0.0.1",
	"mail.smtp.port=3025"
})
public class JavaMailTest {
	@Rule public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);
	
	@Autowired JavaMailRunner runner;
	JavaMailChecker checker;
	
	@Before
	public void setup() {
		requires("email-javamail");

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
