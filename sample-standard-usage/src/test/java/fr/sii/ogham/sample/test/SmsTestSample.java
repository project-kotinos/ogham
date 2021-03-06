package fr.sii.ogham.sample.test;

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;

import org.jsmpp.bean.SubmitSm;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.testing.extension.junit.JsmppServerRule;
import fr.sii.ogham.testing.extension.junit.SmppServerRule;

public class SmsTestSample {
	private MessagingService oghamService;
	
	@Rule
	public final SmppServerRule<SubmitSm> smppServer = new JsmppServerRule();    // <1>
	
	@Before
	public void setUp() throws IOException {
		oghamService = MessagingBuilder.standard()
				.environment()
					.properties()
						.set("ogham.sms.from.default-value", "+33603040506")
						.set("ogham.sms.smpp.host", "localhost")                 // <2>
						.set("ogham.sms.smpp.port", smppServer.getPort())        // <3>
						.and()
					.and()
				.build();
	}
	
	@Test
	public void simple() throws MessagingException {
		// @formatter:off
		oghamService.send(new Sms()
							.message().string("sms content")
							.to("0601020304"));
		assertThat(smppServer).receivedMessages()                                // <4>
			.count(is(1))                                                        // <5>
			.message(0)                                                          // <6>
				.content(is("sms content"))                                      // <7>
				.from()
					.number(is("+33603040506"))                                  // <8>
					.and()
				.to()
					.number(is("0601020304"));                                   // <9>
		// @formatter:on
	}
}
