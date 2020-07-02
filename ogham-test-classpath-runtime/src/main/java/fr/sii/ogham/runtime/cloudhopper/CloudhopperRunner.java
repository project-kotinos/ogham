package fr.sii.ogham.runtime.cloudhopper;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.sms.message.Sms;
import mock.context.SimpleBean;

public class CloudhopperRunner {
	private final MessagingService messagingService;
	
	public CloudhopperRunner(MessagingService messagingService) {
		super();
		this.messagingService = messagingService;
	}

	public void sendSmsWithoutTemplate() throws MessagingException {
		messagingService.send(new Sms()
				.message().string("Hello world !!")
				.from("+33601020304")
				.to("0709080706"));
	}

	public void sendSmsWithThymeleaf() throws MessagingException {
		messagingService.send(new Sms()
				.message().template("classpath:/sms/thymeleaf/source/simple.txt", new SimpleBean("foo", 42))
				.from("+33601020304")
				.to("0709080706"));
	}

	public void sendSmsWithFreemarker() throws MessagingException {
		messagingService.send(new Sms()
				.message().template("classpath:/sms/freemarker/source/simple.txt.ftl", new SimpleBean("foo", 42))
				.from("+33601020304")
				.to("0709080706"));
	}
}
