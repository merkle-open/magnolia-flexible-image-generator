package com.merkle.oss.magnolia.imaging.flexible.generator.uri;

import info.magnolia.init.MagnoliaConfigurationProperties;

import java.security.MessageDigest;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

public class ImageDigest {
	static final String SALT_PROPERTY_KEY = "com.merkle.oss.magnolia.imaging.flexible.generator.uri.hash.salt";
	private final Provider<String> stringProvider;

	@Inject
	public ImageDigest(final MagnoliaConfigurationProperties properties) {
		this.stringProvider = () -> Optional.ofNullable(properties.getProperty(SALT_PROPERTY_KEY)).orElseThrow(() ->
			new NullPointerException("salt not configured! Set "+SALT_PROPERTY_KEY+" in magnolia.properties")
		);
	}

	public String getMD5Hex(final String input) {
		final MessageDigest md = DigestUtils.getMd5Digest();
		md.update(stringProvider.get().getBytes());
		return DigestUtils.md5Hex(md.digest(input.getBytes()));
	}
}
