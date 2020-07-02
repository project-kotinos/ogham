package fr.sii.springboot.runtime.testing;

import static junit.filter.Assumptions.requires;

import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import fr.sii.ogham.core.service.MessagingService;

import org.jsmpp.bean.SubmitSm;
import fr.sii.ogham.runtime.cloudhopper.CloudhopperRunner;
import fr.sii.ogham.runtime.cloudhopper.CloudhopperChecker;
import fr.sii.ogham.testing.extension.junit.JsmppServerRule;
import fr.sii.ogham.testing.extension.junit.SmppServerRule;

import org.springframework.beans.factory.annotation.Autowired;

public class SmsCloudhopperIT {
	@Rule public final SmppServerRule<SubmitSm> smppServer = new JsmppServerRule();
	
	CloudhopperChecker checker;
	Properties props;
	
	@Before
	public void setup() {
		requires("sms-cloudhopper");

		props = new Properties();
		props.setProperty("ogham.sms.smpp.host", "127.0.0.1");
		props.setProperty("ogham.sms.smpp.port", String.valueOf(smppServer.getPort()));

		checker = new CloudhopperChecker(smppServer);
	}

	@Test
	public void smsWithoutTemplate() throws Exception {
		CommandLineExecutor.run(CloudhopperRunner.class, "sendSmsWithoutTemplate", props);
		checker.assertSmsWithoutTemplate();
	}
	
	@Test
	public void smsWithThymeleaf() throws Exception {
		requires("template-thymeleaf");
		
		CommandLineExecutor.run(CloudhopperRunner.class, "sendSmsWithThymeleaf", props);
		checker.assertSmsWithThymeleaf();
	}
	
	@Test
	public void smsWithFreemarker() throws Exception {
		requires("template-freemarker");
		
		CommandLineExecutor.run(CloudhopperRunner.class, "sendSmsWithFreemarker", props);
		checker.assertSmsWithFreemarker();
	}
}
