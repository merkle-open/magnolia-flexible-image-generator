package com.merkle.oss.magnolia.imaging.flexible.generator;

import com.merkle.oss.magnolia.imaging.flexible.generator.uri.FlexibleImageUriParser;
import com.merkle.oss.magnolia.imaging.flexible.model.FlexibleParameter;
import info.magnolia.imaging.ParameterProvider;
import info.magnolia.imaging.ParameterProviderFactory;
import info.magnolia.imaging.caching.CachingStrategy;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

public class FlexibleParameterProviderFactory implements ParameterProviderFactory<HttpServletRequest, FlexibleParameter> {
	private final FlexibleImageUriParser flexibleImageUriParser;

	@Inject
	public FlexibleParameterProviderFactory(final FlexibleImageUriParser flexibleImageUriParser) {
		this.flexibleImageUriParser = flexibleImageUriParser;
	}

	@Override
	public CachingStrategy<FlexibleParameter> getCachingStrategy() {
		return new FlexibleParameterCachingStrategy();
	}

	@Override
	public ParameterProvider<FlexibleParameter> newParameterProviderFor(final HttpServletRequest request) {
		return () -> flexibleImageUriParser.parse(request).orElseThrow(() ->
				new IllegalArgumentException("Failed to parse flexible parameter from " + request.getRequestURI()+" - "+request.getQueryString())
		);
	}
}
