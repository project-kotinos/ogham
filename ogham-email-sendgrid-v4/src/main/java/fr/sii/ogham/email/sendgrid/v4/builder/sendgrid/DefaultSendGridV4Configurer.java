package fr.sii.ogham.email.sendgrid.v4.builder.sendgrid;

import static fr.sii.ogham.email.sendgrid.SendGridConstants.DEFAULT_SENDGRID_CONFIGURER_PRIORITY;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.ConfigurerFor;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.builder.mimetype.MimetypeDetectionBuilder;
import fr.sii.ogham.core.util.ClasspathUtils;

/**
 * Default SendGrid configurer that is automatically applied every time a
 * {@link MessagingBuilder} instance is created through
 * {@link MessagingBuilder#standard()}.
 * 
 * <p>
 * The configurer has a priority of 30000 in order to be applied after
 * templating configurers and JavaMail configurer.
 * </p>
 * 
 * This configurer is applied only if {@code com.sendgrid.SendGrid} is present
 * in the classpath. If not present, SendGrid implementation is not registered
 * at all.
 * 
 * <p>
 * This configurer inherits environment configuration (see
 * {@link BuildContext}).
 * </p>
 * <p>
 * This configurer inherits mimetype configuration (see
 * {@link MimetypeDetectionBuilder} and
 * {@link SendGridV4Builder#mimetype(MimetypeDetectionBuilder)}).
 * </p>
 * 
 * <p>
 * This configurer applies the following configuration:
 * <ul>
 * <li>Configures authentication by providing an <a href=
 * "https://sendgrid.com/docs/Classroom/Send/How_Emails_Are_Sent/api_keys.html">API
 * key</a>: using the property "ogham.email.sendgrid.api-key".<strong>WARNING:
 * SendGrid v4 doesn't allow username/password anymore. You must use API
 * keys</strong></li>
 * <li>Configures unit testing mode using the property
 * "ogham.email.sendgrid.unit-testing".
 * </ul>
 * 
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public final class DefaultSendGridV4Configurer {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSendGridV4Configurer.class);

	@ConfigurerFor(targetedBuilder = "standard", priority = DEFAULT_SENDGRID_CONFIGURER_PRIORITY)
	public static class SendGridV4Configurer implements MessagingConfigurer {
		@Override
		public void configure(MessagingBuilder msgBuilder) {
			if (!canUseSendGrid()) {
				LOG.debug("[{}] skip configuration", this);
				return;
			}
			LOG.debug("[{}] apply configuration", this);
			// @formatter:off
			SendGridV4Builder builder = msgBuilder.email().sender(SendGridV4Builder.class);
			// inherit mimetype configuration as parent builder
			builder.mimetype(msgBuilder.mimetype());
			builder
				.apiKey().properties("${ogham.email.sendgrid.api-key}").and()
				.unitTesting().properties("${ogham.email.sendgrid.unit-testing}");
			// @formatter:on
		}

		private static boolean canUseSendGrid() {
			return ClasspathUtils.exists("com.sendgrid.SendGrid") && ClasspathUtils.exists("com.sendgrid.helpers.mail.Mail");
		}
	}

	private DefaultSendGridV4Configurer() {
		super();
	}
}
