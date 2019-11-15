package fr.sii.ogham.spring.template;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("ogham.freemarker")
public class OghamFreemarkerProperties {
	/**
	 * Default charset encoding for Freemarker templates
	 */
	private String defaultEncoding;

	/**
	 * Access Spring beans from templates using
	 * {@code @beanName.methodName(args)}
	 */
	private boolean enableSpringBeans = true;

	public String getDefaultEncoding() {
		return defaultEncoding;
	}

	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}

	public boolean isEnableSpringBeans() {
		return enableSpringBeans;
	}

	public void setEnableSpringBeans(boolean enableSpringBeans) {
		this.enableSpringBeans = enableSpringBeans;
	}
}
