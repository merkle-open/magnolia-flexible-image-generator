package com.merkle.oss.magnolia.imaging.flexible.generator;

import info.magnolia.imaging.ParameterProvider;
import info.magnolia.imaging.ParameterProviderFactory;
import info.magnolia.imaging.caching.CachingStrategy;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;

public class FlexibleParameterProviderFactory implements ParameterProviderFactory<HttpServletRequest, FlexibleParameter> {

	@Override
	public CachingStrategy<FlexibleParameter> getCachingStrategy() {
		return new FlexibleParameterCachingStrategy();
	}

	@Override
	public ParameterProvider<FlexibleParameter> newParameterProviderFor(final HttpServletRequest request) {
		final String uri = URLDecoder.decode(request.getRequestURI(), FlexibleImageVariation.CHARSET);
		return new FlexibleParameterProvider(uri);
	}
}
