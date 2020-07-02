package fr.sii.springboot.runtime.testing;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import fr.sii.ogham.runtime.javamail.JavaMailRunner;
import fr.sii.ogham.runtime.cloudhopper.CloudhopperRunner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnClass(fr.sii.ogham.core.service.MessagingService.class)
public class RunnersConfig {
	@Bean
	public JavaMailRunner javaMailRunner(fr.sii.ogham.core.service.MessagingService service) {
		return new JavaMailRunner(service);
	}
	
	@Bean
	public CloudhopperRunner cloudhopperRunner(fr.sii.ogham.core.service.MessagingService service) {
		return new CloudhopperRunner(service);
	}
}
