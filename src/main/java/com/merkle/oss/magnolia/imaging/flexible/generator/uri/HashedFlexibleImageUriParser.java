package com.merkle.oss.magnolia.imaging.flexible.generator.uri;

import com.merkle.oss.magnolia.imaging.flexible.model.FlexibleParameter;
import com.merkle.oss.magnolia.imaging.flexible.model.bundle.ProcessedBundlesProvider;
import info.magnolia.dam.api.Asset;
import info.magnolia.dam.templating.functions.DamTemplatingFunctions;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.merkle.oss.magnolia.imaging.flexible.generator.uri.HashedFlexibleImageUriFactory.HashedFlexibleParameter.HASH_PARAM;

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
			final ImageDigest imageDigest
	) {
		super(damTemplatingFunctions, bundlesProvider, flexibleParameterFactory);
		this.imageDigest = imageDigest;
	}

	protected Optional<FlexibleParameter> parse(final String uri, final Asset asset) {
		final Matcher matcher = HASH_PATH_PARAM_PATTERN.matcher(uri);
		if(matcher.matches()) {
			final String urlWithoutHashParam = matcher.group(1) + matcher.group(3);
			final String hashParamValue = matcher.group(2);
			if(Objects.equals(imageDigest.getMD5Hex(urlWithoutHashParam), hashParamValue)) {
				return super.parse(uri, asset);
			}
		}
		return Optional.empty();
	}
}