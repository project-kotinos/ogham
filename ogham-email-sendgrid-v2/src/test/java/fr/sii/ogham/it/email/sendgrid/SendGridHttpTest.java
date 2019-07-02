package fr.sii.ogham.it.email.sendgrid;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.okForJson;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extensions;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.MultipartValuePatternBuilder;
import com.sendgrid.SendGridException;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.MultiContent;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.core.template.context.SimpleContext;
import fr.sii.ogham.core.util.IOUtils;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.sendgrid.v2.builder.sendgrid.SendGridV2Builder;
import fr.sii.ogham.junit.LoggingTestExtension;

@Extensions(@ExtendWith(LoggingTestExtension.class))
public class SendGridHttpTest {
	private static final String SUBJECT = "Example email";
	private static final String CONTENT_TEXT = "This is a default content.";
	private static final String NAME = "you";
	private static final String CONTENT_TEXT_TEMPLATE = "Hello [(${name})]";
	private static final String CONTENT_TEXT_RESULT = "Hello " + NAME;
	private static final String CONTENT_HTML_TEMPLATE = "<html xmlns:th=\"http://www.thymeleaf.org\" th:inline=\"text\"><body><p>Hello [[${name}]]</p></body></html>";
	private static final String CONTENT_HTML_RESULT = "<html><body><p>Hello " + NAME + "</p></body></html>";
	private static final String FROM_ADDRESS = "SENDER <from@example.com>";
	private static final String TO_ADDRESS_1 = "to.1@yopmail.com";
	private static final String TO_ADDRESS_2 = "to.2@yopmail.com";

	private MessagingService messagingService;
	private WireMockServer server;

	@BeforeEach
	public void setup() {
		server = new WireMockServer(options().dynamicPort());
		server.start();
		messagingService = MessagingBuilder.standard()
				.email()
					.sender(SendGridV2Builder.class)
						.url("http://localhost:"+server.port())
						.apiKey("foobar")
						.and()
					.and()
				.build();
	}
	
	@AfterEach
	public void clean() {
		server.stop();
	}
	
	@Test
	public void simpleEmail() throws MessagingException, JsonParseException, JsonMappingException, IOException {
		// @formatter:off
		server.stubFor(post("/api/mail.send.json")
			.willReturn(okForJson("{\"message\": \"success\"}")));
		// @formatter:on
		// @formatter:off
		Email email = new Email()
			.subject(SUBJECT)
			.content(CONTENT_TEXT)
			.from(FROM_ADDRESS)
			.to(TO_ADDRESS_1);
		// @formatter:on
		
		messagingService.send(email);
		
		// @formatter:off
		server.verify(postRequestedFor(urlEqualTo("/api/mail.send.json"))
			.withRequestBodyPart(new MultipartValuePatternBuilder()
					.withName("from")
					.withBody(equalTo("from@example.com"))
					.build())
			.withRequestBodyPart(new MultipartValuePatternBuilder()
					.withName("fromname")
					.withBody(equalTo("SENDER"))
					.build())
			.withRequestBodyPart(new MultipartValuePatternBuilder()
					.withName("to[]")
					.withBody(equalTo(TO_ADDRESS_1))
					.build())
			.withRequestBodyPart(new MultipartValuePatternBuilder()
					.withName("toname[]")
					.withBody(equalTo(""))
					.build())
			.withRequestBodyPart(new MultipartValuePatternBuilder()
					.withName("subject")
					.withBody(equalTo(SUBJECT))
					.build())
			.withRequestBodyPart(new MultipartValuePatternBuilder()
					.withName("text")
					.withBody(equalTo(CONTENT_TEXT))
					.build()));
		// @formatter:on
	}
	
	@Test
	public void templatedEmail() throws MessagingException, JsonParseException, JsonMappingException, IOException {
		// @formatter:off
		server.stubFor(post("/api/mail.send.json")
			.willReturn(okForJson("{\"message\": \"success\"}")));
		// @formatter:on
		// @formatter:off
		Email email = new Email()
			.subject(SUBJECT)
			.content(new MultiContent(
					new TemplateContent("string:"+CONTENT_TEXT_TEMPLATE, new SimpleContext("name", NAME)),
					new TemplateContent("string:"+CONTENT_HTML_TEMPLATE, new SimpleContext("name", NAME))))
			.from(FROM_ADDRESS)
			.to(TO_ADDRESS_1, TO_ADDRESS_2);
		// @formatter:on
		
		messagingService.send(email);
		
		// @formatter:off
		server.verify(postRequestedFor(urlEqualTo("/api/mail.send.json"))
			.withRequestBodyPart(new MultipartValuePatternBuilder()
					.withName("from")
					.withBody(equalTo("from@example.com"))
					.build())
			.withRequestBodyPart(new MultipartValuePatternBuilder()
					.withName("fromname")
					.withBody(equalTo("SENDER"))
					.build())
			.withRequestBodyPart(new MultipartValuePatternBuilder()
					.withName("to[]")
					.withBody(equalTo(TO_ADDRESS_1))
					.build())
			.withRequestBodyPart(new MultipartValuePatternBuilder()
					.withName("to[]")
					.withBody(equalTo(TO_ADDRESS_2))
					.build())
			.withRequestBodyPart(new MultipartValuePatternBuilder()
					.withName("toname[]")
					.withBody(equalTo(""))
					.build())
			.withRequestBodyPart(new MultipartValuePatternBuilder()
					.withName("subject")
					.withBody(equalTo(SUBJECT))
					.build())
			.withRequestBodyPart(new MultipartValuePatternBuilder()
					.withName("text")
					.withBody(equalTo(CONTENT_TEXT_RESULT))
					.build())
			.withRequestBodyPart(new MultipartValuePatternBuilder()
					.withName("html")
					.withBody(equalTo(CONTENT_HTML_RESULT))
					.build()));
		// @formatter:on
	}
	
	
	@Test
	public void emailWithAttachments() throws MessagingException, JsonParseException, JsonMappingException, IOException {
		// @formatter:off
		server.stubFor(post("/api/mail.send.json")
			.willReturn(okForJson("{\"message\": \"success\"}")));
		// @formatter:on
		// @formatter:off
		Email email = new Email()
			.subject(SUBJECT)
			.content(CONTENT_TEXT)
			.from(FROM_ADDRESS)
			.to(TO_ADDRESS_1)
			.attach(new Attachment(new File(getClass().getResource("/attachment/04-Java-OOP-Basics.pdf").getFile())),
					new Attachment(new File(getClass().getResource("/attachment/ogham-grey-900x900.png").getFile())));
		// @formatter:on
		
		messagingService.send(email);
		
		// @formatter:off
		server.verify(postRequestedFor(urlEqualTo("/api/mail.send.json"))
			.withRequestBodyPart(new MultipartValuePatternBuilder()
					.withName("from")
					.withBody(equalTo("from@example.com"))
					.build())
			.withRequestBodyPart(new MultipartValuePatternBuilder()
					.withName("fromname")
					.withBody(equalTo("SENDER"))
					.build())
			.withRequestBodyPart(new MultipartValuePatternBuilder()
					.withName("to[]")
					.withBody(equalTo(TO_ADDRESS_1))
					.build())
			.withRequestBodyPart(new MultipartValuePatternBuilder()
					.withName("toname[]")
					.withBody(equalTo(""))
					.build())
			.withRequestBodyPart(new MultipartValuePatternBuilder()
					.withName("subject")
					.withBody(equalTo(SUBJECT))
					.build())
			.withRequestBodyPart(new MultipartValuePatternBuilder()
					.withName("text")
					.withBody(equalTo(CONTENT_TEXT))
					.build())
			.withRequestBodyPart(new MultipartValuePatternBuilder()
					.withName("files[04-Java-OOP-Basics.pdf]")
					.withBody(equalTo(IOUtils.toString(getClass().getResourceAsStream("/attachment/04-Java-OOP-Basics.pdf"))))
					.build())
			.withRequestBodyPart(new MultipartValuePatternBuilder()
					.withName("files[ogham-grey-900x900.png]")
					.withBody(equalTo(IOUtils.toString(getClass().getResourceAsStream("/attachment/ogham-grey-900x900.png"))))
					.build()));
		// @formatter:on
	}
	
	@Test
	public void authenticationFailed() throws MessagingException, JsonParseException, JsonMappingException, IOException {
		// @formatter:off
		server.stubFor(post("/api/mail.send.json")
			.willReturn(aResponse()
					.withStatus(400)
					.withBody(loadJson("/stubs/responses/authenticationFailed.json"))));
		// @formatter:on
		// @formatter:off
		Email email = new Email()
			.subject(SUBJECT)
			.content(CONTENT_TEXT)
			.from(FROM_ADDRESS)
			.to(TO_ADDRESS_1);
		// @formatter:on
		
		MessagingException e = assertThrows(MessagingException.class, () -> {
			messagingService.send(email);
		});
		assertThat(e.getCause(), allOf(notNullValue(), instanceOf(SendGridException.class)));
		assertThat(e.getCause().getCause(), allOf(notNullValue(), instanceOf(IOException.class)));
		assertThat(e.getCause().getCause().getMessage(), Matchers.equalTo("Sending to SendGrid failed: (400) {\n" + 
				"	\"errors\": [\"The provided authorization grant is invalid, expired, or revoked\"],\n" + 
				"	\"message\": \"error\"\n" + 
				"}"));
	}
	
	
	private String loadJson(String path) throws IOException, JsonParseException, JsonMappingException {
		return IOUtils.toString(getClass().getResourceAsStream(path));
	}
}
