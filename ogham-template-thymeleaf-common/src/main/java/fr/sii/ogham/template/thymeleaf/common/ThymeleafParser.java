package fr.sii.ogham.template.thymeleaf.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.exceptions.TemplateEngineException;

import fr.sii.ogham.core.exception.template.ContextException;
import fr.sii.ogham.core.exception.template.ParseException;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.template.context.Context;
import fr.sii.ogham.core.template.parser.TemplateParser;
import fr.sii.ogham.template.exception.TemplateRuntimeException;

/**
 * Implementation for Thymeleaf template engine.
 * 
 * @author Aurélien Baudet
 *
 */
public class ThymeleafParser implements TemplateParser {
	private static final Logger LOG = LoggerFactory.getLogger(ThymeleafParser.class);

	/**
	 * Thymeleaf engine
	 */
	private TemplateEngine engine;
	
	/**
	 * Converts general context into Thymeleaf specific context
	 */
	private ThymeleafContextConverter contextConverter;
	
	public ThymeleafParser(TemplateEngine engine, ThymeleafContextConverter contextConverter) {
		super();
		this.engine = engine;
		this.contextConverter = contextConverter;
	}

	public ThymeleafParser(TemplateEngine engine) {
		this(engine, new SimpleThymeleafContextConverter());
	}
	
	@Override
	public Content parse(ResourcePath template, Context ctx) throws ParseException {
		try {
			LOG.debug("Parsing Thymeleaf template {} with context {}...", template, ctx);
			String result = engine.process(template.getOriginalPath(), contextConverter.convert(ctx));
			LOG.debug("Template {} successfully parsed with context {}. Result:", template, ctx);
			LOG.debug(result);
			return new StringContent(result);
		} catch (TemplateEngineException | TemplateRuntimeException e) {
			throw new ParseException("Failed to parse template with thymeleaf", template, ctx, e);
		} catch (ContextException e) {
			throw new ParseException("Failed to parse template with thymeleaf due to conversion error", template, ctx, e);
		}
	}

	@Override
	public String toString() {
		return "ThymeleafParser";
	}
	
}
