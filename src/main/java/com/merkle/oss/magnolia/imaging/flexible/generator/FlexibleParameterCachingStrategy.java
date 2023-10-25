package com.merkle.oss.magnolia.imaging.flexible.generator;

import com.merkle.oss.magnolia.imaging.flexible.model.FlexibleParameter;
import info.magnolia.imaging.ImageGenerator;
import info.magnolia.imaging.ParameterProvider;
import info.magnolia.imaging.caching.CachingStrategy;
import info.magnolia.jcr.util.NodeTypes;

import javax.jcr.Property;
import javax.jcr.RepositoryException;
import java.util.Calendar;

public class FlexibleParameterCachingStrategy implements CachingStrategy<FlexibleParameter> {

	@Override
	public String getCachePath(
			final ImageGenerator<ParameterProvider<FlexibleParameter>> generator,
			final ParameterProvider<FlexibleParameter> parameterProvider
	) {
		final FlexibleParameter parameter = parameterProvider.getParameter();
		return "/" + FlexibleImageGenerator.GENERATOR_NAME + "/"
				+ parameter.getItemKey().asString() + "/"
				+ parameter.getWidth() + "/"
				+ parameter.getHeight() + "/"
				+ (parameter.isCrop() ? "cropped/" : "");
	}

	@Override
	public boolean shouldRegenerate(
			final Property cachedBinary,
			final ParameterProvider<FlexibleParameter> parameterProvider
	) throws RepositoryException {
		final Calendar cacheLastMod = NodeTypes.LastModified.getLastModified(cachedBinary.getParent().getParent());
		final Calendar srcLastMod = parameterProvider.getParameter().getLastModified();
		return cacheLastMod.before(srcLastMod);
	}
}