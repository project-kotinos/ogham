package fr.sii.standalone.runtime.testing;

import static junit.filter.Assumptions.requires;

import java.util.Properties;

import org.jsmpp.bean.SubmitSm;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import fr.sii.ogham.core.service.MessagingService;

import fr.sii.ogham.runtime.cloudhopper.CloudhopperRunner;
import fr.sii.ogham.runtime.cloudhopper.CloudhopperChecker;
import fr.sii.ogham.testing.extension.junit.JsmppServerRule;
import fr.sii.ogham.testing.extension.junit.SmppServerRule;

public class SmsCloudhopperTest {
	@Rule public final SmppServerRule<SubmitSm> smppServer = new JsmppServerRule();
	
	CloudhopperRunner runner;
	CloudhopperChecker checker;
	
	@Before
	public void setup() {
		requires("sms-cloudhopper");

		Properties props = new Properties();
		props.setProperty("ogham.sms.smpp.host", "127.0.0.1");
		props.setProperty("ogham.sms.smpp.port", String.valueOf(smppServer.getPort()));
		
		MessagingService service = new StandaloneApp().init(props);
		runner = new CloudhopperRunner(service);
		checker = new CloudhopperChecker(smppServer);
	}

	@Test
	public void smsWithoutTemplate() throws Exception {
		runner.sendSmsWithoutTemplate();
		checker.assertSmsWithoutTemplate();
	}
	
	@Test
	public void smsWithThymeleaf() throws Exception {
		requires("template-thymeleaf");
		
		runner.sendSmsWithThymeleaf();
		checker.assertSmsWithThymeleaf();
	}
	
	@Test
	public void smsWithFreemarker() throws Exception {
		requires("template-freemarker");
		
		runner.sendSmsWithFreemarker();
		checker.assertSmsWithFreemarker();
	}
	
}
