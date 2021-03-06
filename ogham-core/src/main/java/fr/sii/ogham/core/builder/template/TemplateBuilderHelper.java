package fr.sii.ogham.core.builder.template;

import static fr.sii.ogham.core.util.BuilderUtils.instantiateBuilder;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.builder.configurer.Configurer;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.priority.ImplementationPriorityProvider;
import fr.sii.ogham.core.builder.priority.PriorityProvider;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.message.content.MultiTemplateContent;
import fr.sii.ogham.core.message.content.Variant;
import fr.sii.ogham.core.template.detector.FixedEngineDetector;
import fr.sii.ogham.core.template.detector.TemplateEngineDetector;
import fr.sii.ogham.core.template.parser.AutoDetectTemplateParser;
import fr.sii.ogham.core.template.parser.AutoDetectTemplateParser.TemplateImplementation;
import fr.sii.ogham.core.template.parser.TemplateParser;
import fr.sii.ogham.core.util.PriorizedList;
import fr.sii.ogham.template.common.adapter.FailIfNotFoundVariantResolver;
import fr.sii.ogham.template.common.adapter.FailIfNotFoundWithTestedPathsVariantResolver;
import fr.sii.ogham.template.common.adapter.FirstExistingResourceVariantResolver;
import fr.sii.ogham.template.common.adapter.NullVariantResolver;
import fr.sii.ogham.template.common.adapter.VariantResolver;

/**
 * Helps to configure a {@link TemplateParser} builder.
 * 
 * <p>
 * It registers and uses {@link Builder}s to instantiate and configure
 * {@link TemplateParser} specialized implementations.
 * </p>
 * 
 * <p>
 * It also configures how to handle missing variant (either fail or do nothing).
 * </p>
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent builder used by custom
 *            {@link TemplateParser} {@link Builder}
 */
public class TemplateBuilderHelper<P> {
	private static final Logger LOG = LoggerFactory.getLogger(TemplateBuilderHelper.class);

	private final P parent;
	private final List<Builder<? extends TemplateParser>> templateBuilders;
	private final BuildContext buildContext;
	private final ConfigurationValueBuilderHelper<TemplateBuilderHelper<P>, Boolean> missingVariantFailValueBuilder;
	private final ConfigurationValueBuilderHelper<TemplateBuilderHelper<P>, Boolean> listPossiblePathsValueBuilder;
	private final PriorityProvider<TemplateParser> priorityProvider;
	private VariantResolver missingResolver;

	/**
	 * Initializes the builder with a parent builder. The parent builder is used
	 * when calling and() method of any registered {@link TemplateParser}
	 * {@link Builder}. The {@link EnvironmentBuilder} is used to evaluate
	 * properties at build time (used by {@link TemplateParser}
	 * {@link Builder}s).
	 * 
	 * @param parent
	 *            the parent builder
	 * @param buildContext
	 *            for registering instances and property evaluation
	 */
	public TemplateBuilderHelper(P parent, BuildContext buildContext) {
		super();
		this.parent = parent;
		this.buildContext = buildContext;
		templateBuilders = new ArrayList<>();
		missingVariantFailValueBuilder = buildContext.newConfigurationValueBuilder(this, Boolean.class);
		listPossiblePathsValueBuilder = buildContext.newConfigurationValueBuilder(this, Boolean.class);
		priorityProvider = new ImplementationPriorityProvider<>(buildContext);
	}

	/**
	 * Indicates if some {@link TemplateParser} {@link Builder}s have been
	 * registered
	 * 
	 * @return true if at least one builder has been registered
	 */
	public boolean hasRegisteredTemplates() {
		return !templateBuilders.isEmpty();
	}

	/**
	 * If a variant is missing, then force to fail.
	 * 
	 * <p>
	 * This may be useful if you want for example to always provide a text
	 * fallback when using an html template. So if a client can't read the html
	 * version, the fallback version will still always be readable. So to avoid
	 * forgetting to write text template, set this to true.
	 * </p>
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #failIfMissingVariant()}.
	 * 
	 * <pre>
	 * .failIfMissingVariant(false)
	 * .failIfMissingVariant()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * <pre>
	 * .failIfMissingVariant(false)
	 * .failIfMissingVariant()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * In both cases, {@code failIfMissingVariant(false)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param fail
	 *            fail if a variant is missing
	 * @return this instance for fluent chaining
	 */
	public TemplateBuilderHelper<P> failIfMissingVariant(Boolean fail) {
		missingVariantFailValueBuilder.setValue(fail);
		return this;
	}

	/**
	 * If a variant is missing, then force to fail.
	 * 
	 * <p>
	 * This may be useful if you want for example to always provide a text
	 * fallback when using an html template. So if a client can't read the html
	 * version, the fallback version will still always be readable. So to avoid
	 * forgetting to write text template, set this to true.
	 * </p>
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .failIfMissingVariant()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #failIfMissingVariant(Boolean)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .failIfMissingVariant(false)
	 * .failIfMissingVariant()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * The value {@code false} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<TemplateBuilderHelper<P>, Boolean> failIfMissingVariant() {
		return missingVariantFailValueBuilder;
	}

	/**
	 * When {@link #failIfMissingVariant()} is enabled, also indicate which
	 * paths were tried in order to help debugging why a variant was not found.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #listPossiblePaths()}.
	 * 
	 * <pre>
	 * .listPossiblePaths(true)
	 * .listPossiblePaths()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * <pre>
	 * .listPossiblePaths(true)
	 * .listPossiblePaths()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * In both cases, {@code listPossiblePaths(true)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param enable
	 *            enable/disable tracking of possible paths for template
	 *            variants
	 * @return this instance for fluent chaining
	 */
	public TemplateBuilderHelper<P> listPossiblePaths(Boolean enable) {
		listPossiblePathsValueBuilder.setValue(enable);
		return this;
	}

	/**
	 * When {@link #failIfMissingVariant()} is enabled, also indicate which
	 * paths were tried in order to help debugging why a variant was not found.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .listPossiblePaths()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #listPossiblePaths(Boolean)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .listPossiblePaths(true)
	 * .listPossiblePaths()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * The value {@code true} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<TemplateBuilderHelper<P>, Boolean> listPossiblePaths() {
		return listPossiblePathsValueBuilder;
	}

	/**
	 * Provide custom resolver that will handle a missing variant.
	 * 
	 * @param resolver
	 *            the custom resolver
	 */
	public void missingVariant(VariantResolver resolver) {
		this.missingResolver = resolver;
	}

	/**
	 * Registers and configures a {@link TemplateParser} through a dedicated
	 * builder.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * .register(ThymeleafEmailBuilder.class)
	 *     .detector(new ThymeleafEngineDetector());
	 * </pre>
	 * 
	 * <p>
	 * Your {@link Builder} may implement {@link VariantBuilder} to handle
	 * template {@link Variant}s (used for {@link MultiTemplateContent} that
	 * provide a single path to templates with different extensions for
	 * example).
	 * </p>
	 * 
	 * <p>
	 * Your {@link Builder} may also implement {@link DetectorBuilder} in order
	 * to indicate which kind of templates your {@link TemplateParser} is able
	 * to parse. If your template parse is able to parse any template file you
	 * are using, you may not need to implement {@link DetectorBuilder}.
	 * </p>
	 * 
	 * <p>
	 * In order to be able to keep chaining, you builder instance may provide a
	 * constructor with two arguments:
	 * <ul>
	 * <li>The type of the parent builder ({@code &lt;P&gt;})</li>
	 * <li>The {@link EnvironmentBuilder} instance</li>
	 * </ul>
	 * If you don't care about chaining, just provide a default constructor.
	 * 
	 * @param builderClass
	 *            the builder class to instantiate
	 * @param <B>
	 *            the type of the builder
	 * @return the builder to configure the implementation
	 */
	@SuppressWarnings("unchecked")
	public <B extends Builder<? extends TemplateParser>> B register(Class<B> builderClass) {
		// if already registered => provide same instance
		for (Builder<? extends TemplateParser> builder : templateBuilders) {
			if (builderClass.isAssignableFrom(builder.getClass())) {
				return (B) builder;
			}
		}
		// create the builder instance
		B builder = instantiateBuilder(builderClass, parent, buildContext);
		templateBuilders.add(builder);
		return builder;
	}

	/**
	 * Build the template parser according to options previously enabled. If
	 * only one template engine has been activated then the parser will be this
	 * template engine parser. If there are several activated engines, then the
	 * builder will generate an {@link AutoDetectTemplateParser}. This kind of
	 * parser is able to detect which parser to use according to the provided
	 * template at runtime. The auto-detection is delegated to each defined
	 * {@link TemplateEngineDetector} associated with each engine.
	 * 
	 * @return the template parser instance
	 * @throws BuildException
	 *             when template parser couldn't be initialized
	 */
	public TemplateParser buildTemplateParser() {
		// TODO: handle enable?
		List<TemplateImplementation> impls = buildTemplateParserImpls();
		if (impls.isEmpty()) {
			// if no template parser available => exception
			throw new BuildException("No parser available. Either disable template features or register a template engine");
		}
		if (impls.size() == 1) {
			// if no detector defined or only one available parser => do not use
			// auto detection
			TemplateParser parser = impls.get(0).getParser();
			LOG.info("Using single template engine: {}", parser);
			return parser;
		}
		LOG.info("Using auto detection mechanism");
		LOG.debug("Auto detection mechanisms: {}", impls);
		return buildContext.register(new AutoDetectTemplateParser(impls));
	}

	/**
	 * Instantiates and configures the variant resolution. Variant resolution is
	 * a chain of resolvers. The first resolver that is able to resolve a
	 * variant is used. If no resolver is able to resolve a variant, it uses the
	 * default variant resolver (see {@link #failIfMissingVariant(Boolean)} and
	 * {@link #missingVariant(VariantResolver)}).
	 * 
	 * @return the variant resolver
	 */
	public VariantResolver buildVariant() {
		FirstExistingResourceVariantResolver variantResolver = buildContext.register(new FirstExistingResourceVariantResolver(buildDefaultVariantResolver()));
		for (Builder<? extends TemplateParser> builder : templateBuilders) {
			if (builder instanceof VariantBuilder) {
				variantResolver.addVariantResolver(((VariantBuilder<?>) builder).buildVariant());
			}
		}
		return variantResolver;
	}

	@SuppressWarnings("squid:S5411")
	private VariantResolver buildDefaultVariantResolver() {
		if (missingResolver != null) {
			return missingResolver;
		}
		if (missingVariantFailValueBuilder.getValue(false)) {
			return buildFailingVariantResolver();
		}
		return buildContext.register(new NullVariantResolver());
	}

	@SuppressWarnings("squid:S5411")
	private VariantResolver buildFailingVariantResolver() {
		if (!listPossiblePathsValueBuilder.getValue(false)) {
			return buildContext.register(new FailIfNotFoundVariantResolver());
		}
		FailIfNotFoundWithTestedPathsVariantResolver failResolver = new FailIfNotFoundWithTestedPathsVariantResolver();
		for (Builder<? extends TemplateParser> builder : templateBuilders) {
			if (builder instanceof VariantBuilder) {
				failResolver.addVariantResolver(((VariantBuilder<?>) builder).buildVariant());
			}
		}
		return buildContext.register(failResolver);
	}

	private List<TemplateImplementation> buildTemplateParserImpls() {
		PriorizedList<TemplateImplementation> impls = new PriorizedList<>();
		for (Builder<? extends TemplateParser> builder : templateBuilders) {
			TemplateEngineDetector detector;
			if (builder instanceof DetectorBuilder) {
				detector = ((DetectorBuilder<?>) builder).buildDetector();
			} else {
				detector = buildContext.register(new FixedEngineDetector(true));
			}
			TemplateParser templateParser = builder.build();
			impls.register(new TemplateImplementation(detector, templateParser), priorityProvider.provide(templateParser));
		}
		return impls.getOrdered();
	}

}
