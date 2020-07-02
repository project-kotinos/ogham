package fr.sii.ogham.runtime.javamail;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import mock.context.SimpleBean;

public class JavaMailRunner {
	private final MessagingService messagingService;
	
	public JavaMailRunner(MessagingService messagingService) {
		super();
		this.messagingService = messagingService;
	}

	public void sendEmailWithoutTemplate() throws MessagingException {
		messagingService.send(new Email()
				.subject("Simple")
				.body().string("string body")
				.from("Sender Name <sender@sii.fr>")
				.to("Recipient Name <recipient@sii.fr>"));
	}

	public void sendEmailWithThymeleaf() throws MessagingException {
		messagingService.send(new Email()
				.subject("Thymeleaf")
				.body().template("classpath:/email/thymeleaf/source/simple", new SimpleBean("foo", 42))
				.from("Sender Name <sender@sii.fr>")
				.to("Recipient Name <recipient@sii.fr>"));
	}

	public void sendEmailWithFreemarker() throws MessagingException {
		messagingService.send(new Email()
				.subject("Freemarker")
				.body().template("classpath:/email/freemarker/source/simple", new SimpleBean("foo", 42))
				.from("Sender Name <sender@sii.fr>")
				.to("Recipient Name <recipient@sii.fr>"));
	}

	public void sendEmailWithThymeleafAndFreemarker() throws MessagingException {
		messagingService.send(new Email()
				.subject("Thymeleaf+Freemarker")
				.body().template("classpath:/email/mixed/source/simple", new SimpleBean("foo", 42))
				.from("Sender Name <sender@sii.fr>")
				.to("Recipient Name <recipient@sii.fr>"));
	}

}
