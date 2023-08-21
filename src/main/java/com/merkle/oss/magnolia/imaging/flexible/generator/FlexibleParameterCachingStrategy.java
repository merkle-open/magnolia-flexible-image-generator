package com.merkle.oss.magnolia.imaging.flexible.generator;

import info.magnolia.imaging.ImageGenerator;
import info.magnolia.imaging.ParameterProvider;
import info.magnolia.imaging.caching.CachingStrategy;
import info.magnolia.jcr.util.NodeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Property;
import javax.jcr.RepositoryException;
import java.lang.invoke.MethodHandles;
import java.util.Calendar;

public class FlexibleParameterCachingStrategy implements CachingStrategy<FlexibleParameter> {
	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Override
	public String getCachePath(
			final ImageGenerator<ParameterProvider<FlexibleParameter>> generator,
			final ParameterProvider<FlexibleParameter> parameterProvider
	) {
		try {
			final FlexibleParameter parameter = parameterProvider.getParameter();
			return "/" + FlexibleImageGenerator.GENERATOR_NAME + "/"
					+ parameter.getWidth().map(String::valueOf).orElse("unbounded") + "/"
					+ parameter.getHeight().map(String::valueOf).orElse("unbounded") + "/"
					+ (parameter.isCrop() ? "croped/" : "")
					+ parameter.getSession().getWorkspace().getName()
					+ parameter.getPath();
		} catch (Exception e) {
			LOG.error("Cannot generate cache path for " + parameterProvider, e);
			return null;
		}
	}

	@Override
	public boolean shouldRegenerate(
			final Property cachedBinary,
			final ParameterProvider<FlexibleParameter> parameterProvider
	) throws RepositoryException {
		final Calendar cacheLastMod = NodeTypes.LastModified.getLastModified(cachedBinary.getParent().getParent());
		final Calendar srcLastMod = NodeTypes.LastModified.getLastModified(parameterProvider.getParameter());
		return cacheLastMod.before(srcLastMod);
	}
}