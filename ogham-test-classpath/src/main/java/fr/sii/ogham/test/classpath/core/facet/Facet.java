package fr.sii.ogham.test.classpath.core.facet;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Facet {
	CORE("ogham-core"),
	EMAIL_JAVAMAIL("email-javamail"),
	EMAIL_SENDGRID("email-sendgrid"),
	SMS_OVH("sms-ovh"),
	SMS_CLOUDHOPPER("sms-cloudhopper"),
	TEMPLATE_FREEMARKER("template-freemarker"),
	TEMPLATE_THYMELEAF("template-thymeleaf");
	
	private final String facetName;
	
}
