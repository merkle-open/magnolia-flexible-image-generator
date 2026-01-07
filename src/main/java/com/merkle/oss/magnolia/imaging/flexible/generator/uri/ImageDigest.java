package com.merkle.oss.magnolia.imaging.flexible.generator.uri;

import info.magnolia.init.MagnoliaConfigurationProperties;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Provider;

public class ImageDigest {
	static final String SALT_PROPERTY_KEY = "com.merkle.oss.magnolia.imaging.flexible.generator.uri.hash.salt";
	private final Provider<String> saltProvider;

	@Inject
	public ImageDigest(final MagnoliaConfigurationProperties properties) {
		this.saltProvider = () ->
			Optional.ofNullable(properties.getProperty(SALT_PROPERTY_KEY)).orElseThrow(() ->
					new NullPointerException("salt not configured! Set " + SALT_PROPERTY_KEY + " in magnolia.properties")
			);
	}

	public String getHash(final String input) {
		return String.valueOf((saltProvider.get() + input).hashCode());
	}
}
