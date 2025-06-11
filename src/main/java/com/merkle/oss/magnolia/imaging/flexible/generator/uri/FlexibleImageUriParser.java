package com.merkle.oss.magnolia.imaging.flexible.generator.uri;

import info.magnolia.dam.api.Asset;
import info.magnolia.dam.templating.functions.DamTemplatingFunctions;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import com.merkle.oss.magnolia.imaging.flexible.model.AssetRatioProvider;
import com.merkle.oss.magnolia.imaging.flexible.model.FlexibleParameter;
import com.merkle.oss.magnolia.imaging.flexible.model.bundle.ProcessedBundle;
import com.merkle.oss.magnolia.imaging.flexible.model.bundle.ProcessedBundlesProvider;

public class FlexibleImageUriParser {
	/*
	 * /<context>/.imaging/flex/assetItemKey/param1Key/param1Value/.../fileName
	 * e.g. /author/.imaging/flex/jcr:b3ee7444-4830-4454-abbb-20fc35387032/crop/true/height/316/width/560/dummy1-1600x900.jpg
	 */
	private static final Pattern ASSET_ITEM_KEY_PATTERN = Pattern.compile("/.imaging/flex/([^/]+)");

	private final DamTemplatingFunctions damTemplatingFunctions;
	private final ProcessedBundlesProvider bundlesProvider;
	private final FlexibleParameter.Factory flexibleParameterFactory;
    private final AssetRatioProvider assetRatioProvider;

	@Inject
	public FlexibleImageUriParser(
            final DamTemplatingFunctions damTemplatingFunctions,
            final ProcessedBundlesProvider bundlesProvider,
            final FlexibleParameter.Factory flexibleParameterFactory,
            final AssetRatioProvider assetRatioProvider
    ) {
		this.damTemplatingFunctions = damTemplatingFunctions;
		this.bundlesProvider = bundlesProvider;
		this.flexibleParameterFactory = flexibleParameterFactory;
        this.assetRatioProvider = assetRatioProvider;
    }

	public Optional<FlexibleParameter> parse(final HttpServletRequest request) {
		final String uri = request.getRequestURI();
		return getAsset(uri).flatMap(asset -> parse(uri, asset));
	}

	protected Optional<FlexibleParameter> parse(final String uri, final Asset asset) {
		return flexibleParameterFactory.create(asset, key -> getParameter(uri, key)).filter(parameter ->
				isSizeValid(parameter.getWidth(), parameter.getRatio(), asset)
		);
	}

	private Optional<String> getParameter(final String uri, final String key) {
		final Pattern pattern = Pattern.compile("/"+key+"/([^/]+)");
		return Optional
				.of(pattern.matcher(uri))
				.filter(Matcher::find)
				.map(matcher -> matcher.group(1));
	}

	private Optional<Asset> getAsset(final String uri) {
		return Optional
				.of(ASSET_ITEM_KEY_PATTERN.matcher(uri))
				.filter(Matcher::find)
				.map(matcher -> matcher.group(1))
				.map(damTemplatingFunctions::getAsset);
	}

	private boolean isSizeValid(final int width, final String ratio, final Asset asset) {
		return bundlesProvider.get().stream()
				.flatMap(bundle ->
						Stream.concat(
								bundle.getImageSizes().stream(),
								bundle.getCustomRenditions().stream()
						)
				)
				.anyMatch(size ->
						Objects.equals(size.getWidth(), width) && isRatioValid(size, ratio, asset)
				);
	}

    private boolean isRatioValid(final ProcessedBundle.ImageSize size, final String actualRatio, final Asset asset) {
		final String validRatio = size.getRatio().orElseGet(() -> assetRatioProvider.get(asset));
		return Objects.equals(actualRatio, validRatio);
    }
}
