package fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.client;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGridAPI;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;

import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.SendGridException;

/**
 * Facade wrapping the {@link SendGrid} object.
 */
public final class DelegateSendGridClient implements SendGridClient {

	private static final Logger LOG = LoggerFactory.getLogger(DelegateSendGridClient.class);

	private SendGridAPI delegate;

	/**
	 * Constructor.
	 * 
	 * @param delegate
	 *            the entry point to the SendGrid library
	 * @throws IllegalArgumentException
	 *             if provided delegate is null
	 */
	public DelegateSendGridClient(final SendGridAPI delegate) {
		if (delegate == null) {
			throw new IllegalArgumentException("[delegate] cannot be null");
		}

		this.delegate = delegate;
	}

	@Override
	public void send(final Mail email) throws SendGridException {
		if (email == null) {
			throw new IllegalArgumentException("[email] cannot be null");
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Sending to SendGrid client: FROM {}", debug(email.getFrom()));
			LOG.debug("Sending to SendGrid client: TO {}", debug(email));
			LOG.debug("Sending to SendGrid client: SUBJECT {}", email.getSubject());
		}

		try {
			Request request = new Request();
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(email.build());
			final Response response = delegate.api(request);

			if (isSuccess(response.getStatusCode())) {
				LOG.debug("Response from SendGrid client: ({}) {}", response.getStatusCode(), response.getBody());
			} else {
				throw new SendGridException(new IOException("Sending to SendGrid failed: (" + response.getStatusCode() + ") " + response.getBody()));
			}
		} catch (IOException e) {
			throw new SendGridException("Preparing email for SendGrid failed", e);
		}
	}

	private boolean isSuccess(int statusCode) {
		return statusCode >= 200 && statusCode < 300;
	}

	private String debug(Email address) {
		if (address == null) {
			return null;
		}
		if (address.getName() != null) {
			return address.getName() + "<" + address.getEmail() + ">";
		}
		return address.getEmail();
	}

	private List<String> debug(final Mail email) {
		if (email.getPersonalization() == null) {
			return null;	// NOSONAR
		}
		return email.getPersonalization().stream().flatMap(p -> p.getTos() == null ? Stream.empty() : p.getTos().stream()).map(this::debug).collect(toList());
	}
}