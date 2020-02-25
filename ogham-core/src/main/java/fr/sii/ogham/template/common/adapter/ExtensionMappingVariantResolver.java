package fr.sii.ogham.template.common.adapter;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.message.capability.HasVariant;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.message.content.Variant;
import fr.sii.ogham.core.resource.path.ResolvedPath;
import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.resource.path.UnresolvedPath;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.template.exception.UnknownVariantException;
import fr.sii.ogham.template.exception.VariantResolutionException;

/**
 * Simple implementation that maps a variant instance to an extension.
 * 
 * @author Aurélien Baudet
 *
 */
public class ExtensionMappingVariantResolver implements VariantResolver {
	private static final Logger LOG = LoggerFactory.getLogger(ExtensionMappingVariantResolver.class);

	private final ResourceResolver resourceResolver;
	private final Map<Variant, String> mapping;

	public ExtensionMappingVariantResolver(ResourceResolver resourceResolver) {
		this(resourceResolver, new HashMap<Variant, String>());
	}

	public ExtensionMappingVariantResolver(ResourceResolver resourceResolver, Map<Variant, String> mapping) {
		super();
		this.resourceResolver = resourceResolver;
		this.mapping = mapping;
	}

	@Override
	public ResourcePath getRealPath(TemplateContent template) throws VariantResolutionException {
		String originalPath = template.getPath().getOriginalPath();
		if (!(template instanceof HasVariant)) {
			return resourceResolver.resolve(new UnresolvedPath(originalPath));
		}
		String extension = mapping.get(((HasVariant) template).getVariant());
		if (extension == null) {
			throw new UnknownVariantException("Failed to resolve template due to unknown variant/extension", template.getPath(), template.getContext(), ((HasVariant) template).getVariant());
		}
		return resolveVariantPath(originalPath, extension);
	}

	public ExtensionMappingVariantResolver register(Variant variant, String extension) {
		mapping.put(variant, extension.startsWith(".") ? extension : ("." + extension));
		return this;
	}

	@Override
	public boolean variantExists(TemplateContent template) {
		if (!(template instanceof HasVariant)) {
			return false;
		}

		String extension = mapping.get(((HasVariant) template).getVariant());
		if (extension == null) {
			return false;
		}

		ResourcePath templatePath = template.getPath();
		try {
			resourceResolver.getResource(resolveVariantPath(template.getPath().getOriginalPath(), extension));
			return true;
		} catch (ResourceResolutionException e) {
			LOG.trace("template {}.{} not found", templatePath, extension, e);
			return false;
		}
	}

	private ResourcePath resolveVariantPath(String originalPath, String extension) {
		// if extension already explicitly set, try without adding the extension provided by variant
		ResolvedPath path = useExplicitExtensionIfSameAsVariant(originalPath, extension);
		if (path != null) {
			return path;
		}
		return resourceResolver.resolve(new UnresolvedPath(originalPath + extension));
	}
	
	private ResolvedPath useExplicitExtensionIfSameAsVariant(String originalPath, String extension) {
		if (originalPath.endsWith(extension)) {
			return resourceResolver.resolve(new UnresolvedPath(originalPath));
		}
		return null;
	}


}
