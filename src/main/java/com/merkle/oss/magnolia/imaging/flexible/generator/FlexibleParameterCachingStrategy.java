package com.merkle.oss.magnolia.imaging.flexible.generator;

import com.merkle.oss.magnolia.imaging.flexible.model.FlexibleParameter;
import info.magnolia.imaging.ImageGenerator;
import info.magnolia.imaging.ParameterProvider;
import info.magnolia.imaging.caching.CachingStrategy;
import info.magnolia.jcr.util.NodeTypes;
import org.apache.jackrabbit.util.Text;

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
		return "/" + generator.getName() + "/"
				+ Text.escapeIllegalJcrChars(parameter.getItemKey().asString()) + "/"
				+ parameter.getWidth() + "/"
				+ parameter.getRatio().map(Text::escapeIllegalJcrChars).orElse("keepRatio") + "/"
				+ parameter.getDynamicImageParameter().hashCode();
	}

	@Override
	public boolean shouldRegenerate(
			final Property cachedBinary,
			final ParameterProvider<FlexibleParameter> parameterProvider
	) throws RepositoryException {
		// getParent - this is assuming the cached node's metadata was updated, not just the binary
		final Calendar cacheLastMod = NodeTypes.LastModified.getLastModified(cachedBinary.getParent());
		final Calendar srcLastMod = parameterProvider.getParameter().getLastModified();
		return cacheLastMod.before(srcLastMod);
	}
}