package fr.sii.standalone.runtime.testing;

import java.util.Properties;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.service.MessagingService;

public class StandaloneApp {
	public MessagingService init() {
		return init(new Properties());
	}
	
	public MessagingService init(Properties properties) {
		// Instantiate the messaging service using default behavior and
		// provided properties
		MessagingService service = MessagingBuilder.standard()
				.environment()
					.properties(properties)
					.and()
				.build();
		return service;
	}
}
