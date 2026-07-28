package com.merkle.oss.magnolia.imaging.flexible.generator.uri;

import static com.merkle.oss.magnolia.imaging.flexible.generator.uri.HashedFlexibleImageUriFactory.HashedFlexibleParameter.HASH_PARAM;

import info.magnolia.dam.templating.functions.DamTemplatingFunctions;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.merkle.oss.magnolia.imaging.flexible.model.AssetRatioProvider;
import com.merkle.oss.magnolia.imaging.flexible.model.FlexibleParameter;
import com.merkle.oss.magnolia.imaging.flexible.model.bundle.ProcessedBundlesProvider;

import jakarta.inject.Inject;

public class HashedFlexibleImageUriParser extends FlexibleImageUriParser {
	/*
	 * /<context>/.imaging/flex/assetItemKey/hash/hashValue/param1Key/param1Value/.../fileName
	 * e.g. /author/.imaging/flex/jcr:b3ee7444-4830-4454-abbb-20fc35387032/crop/true/hash/2a2ca0ea6452010c507f67d3e2dbb823/height/316/width/560/dummy1-1600x900.jpg
	 */
	private static final Pattern HASH_PATH_PARAM_PATTERN = Pattern.compile("(.*)/" + HASH_PARAM + "/([^/]+)(/.+)");
	private final ImageDigest imageDigest;

	@Inject
	public HashedFlexibleImageUriParser(
			final DamTemplatingFunctions damTemplatingFunctions,
			final ProcessedBundlesProvider bundlesProvider,
			final FlexibleParameter.Factory flexibleParameterFactory,
			final ImageDigest imageDigest,
			final AssetRatioProvider assetRatioProvider
	) {
		super(damTemplatingFunctions, bundlesProvider, flexibleParameterFactory, assetRatioProvider);
		this.imageDigest = imageDigest;
	}

	@Override
	public Optional<FlexibleParameter> parse(final String uri) {
		if (!isHashValid(uri)) {
			return Optional.empty();
		}
		return super.parse(uri);
	}

	protected boolean isHashValid(final String uri) {
		final Matcher matcher = HASH_PATH_PARAM_PATTERN.matcher(uri);
		if (matcher.matches()) {
			final String urlWithoutHashParam = matcher.group(1) + matcher.group(3);
			final String hashParamValue = matcher.group(2);
			return Objects.equals(imageDigest.getHash(urlWithoutHashParam), hashParamValue);
		}
		return false;
	}
}
