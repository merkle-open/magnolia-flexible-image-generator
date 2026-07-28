package com.merkle.oss.magnolia.imaging.flexible.model;

import info.magnolia.dam.api.Asset;
import info.magnolia.dam.api.AssetProviderRegistry;
import info.magnolia.dam.api.ItemKey;
import info.magnolia.dam.templating.functions.DamTemplatingFunctions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.merkle.oss.magnolia.imaging.flexible.generator.uri.FlexibleImageUriFactory;
import com.merkle.oss.magnolia.imaging.flexible.model.bundle.ProcessedBundle;
import com.merkle.oss.magnolia.imaging.flexible.model.bundle.ProcessedBundlesProvider;
import com.merkle.oss.magnolia.imaging.flexible.model.bundle.RatioParser;

import jakarta.annotation.Nullable;
import jakarta.inject.Inject;

public class AssetImageModelFactory implements ImageModel.Factory {
	private static final Set<String> EXCLUDE_FROM_GENERATION_MIME_TYPES = Set.of(
			"image/svg+xml",
			"image/gif"
	);
	private static final String FALLBACK_VERSION = "1";

	private final ProcessedBundlesProvider bundlesProvider;
	private final FlexibleImageUriFactory flexibleImageUriFactory;
	private final DamTemplatingFunctions damTemplatingFunctions;
	private final AssetProviderRegistry assetProviderRegistry;
	private final LocalizedAsset.Factory localizedAssetFactory;
	private final RatioParser ratioParser;
	private final AssetRatioProvider assetRatioProvider;

	@Inject
	public AssetImageModelFactory(
			final ProcessedBundlesProvider bundlesProvider,
			final FlexibleImageUriFactory flexibleImageUriFactory,
			final DamTemplatingFunctions damTemplatingFunctions,
			final AssetProviderRegistry assetProviderRegistry,
			final LocalizedAsset.Factory localizedAssetFactory,
			final RatioParser ratioParser,
			final AssetRatioProvider assetRatioProvider
	) {
		this.bundlesProvider = bundlesProvider;
		this.flexibleImageUriFactory = flexibleImageUriFactory;
		this.damTemplatingFunctions = damTemplatingFunctions;
		this.assetProviderRegistry = assetProviderRegistry;
		this.localizedAssetFactory = localizedAssetFactory;
		this.ratioParser = ratioParser;
		this.assetRatioProvider = assetRatioProvider;
	}

	@Override
	public Optional<ImageModel> create(final Locale locale, final String assetId, final String bundleName, @Nullable final DynamicImageParameter dynamicImageParameter) {
		return Optional
				.of(assetId)
				.filter(this::isValid)
				.map(damTemplatingFunctions::getAsset)
				.map(asset -> localizedAssetFactory.create(locale, asset))
				.flatMap(localizedAsset ->
						bundlesProvider.get(bundleName).map(bundle ->
								create(localizedAsset, bundle, dynamicImageParameter)
						)
				);
	}

	private boolean isValid(final String assetId) {
		return ItemKey.isValid(assetId) && hasAssetProviderFor(ItemKey.from(assetId));
	}

	private boolean hasAssetProviderFor(final ItemKey itemKey) {
		try {
			assetProviderRegistry.getProviderFor(itemKey);
			return true;
		} catch (AssetProviderRegistry.NoSuchAssetProviderException e) {
			return false;
		}
	}

	private ImageModel create(
			final LocalizedAsset asset,
			final ProcessedBundle bundle,
			@Nullable final DynamicImageParameter dynamicImageParameter
	) {
		return new ImageModel(
				asset.getCaption(),
				asset.getTitle(),
				asset.getDescription(),
				asset.getLink(),
				asset.getName(),
				getSrcSet(bundle, asset, dynamicImageParameter),
				getCustomRenditions(bundle, asset, dynamicImageParameter),
				asset.getMimeType()
		);
	}

	private List<ImageModel.Rendition> getSrcSet(final ProcessedBundle bundle, final Asset asset, @Nullable final DynamicImageParameter dynamicImageParameter) {
		if (shouldNotGenerateImage(asset)) {
			return Collections.emptyList();
		}
		return bundle.getImageSizes()
				.stream()
				.map(size -> getRendition(size, asset, dynamicImageParameter))
				.collect(Collectors.toList());
	}

	private ImageModel.Rendition getRendition(final ProcessedBundle.ImageSize size, final Asset asset, @Nullable final DynamicImageParameter dynamicImageParameter) {
		final String ratio = size.getRatio().orElseGet(() -> assetRatioProvider.get(asset));
		final double parsedRatio = ratioParser.parse(ratio);
		return new ImageModel.Rendition(
				size.getId(),
				getUrl(asset, size.getWidth(), ratio, dynamicImageParameter),
				size.getWidth(),
				calculateHeight(size.getWidth(), parsedRatio),
				parsedRatio
		);
	}

	private int calculateHeight(final long width, final double ratio) {
		return BigDecimal.valueOf(width / ratio).setScale(0, RoundingMode.UP).intValue();
	}

	private Map<String, String> getCustomRenditions(final ProcessedBundle bundle, final Asset asset, @Nullable final DynamicImageParameter dynamicImageParameter) {
		if (shouldNotGenerateImage(asset)) {
			return Collections.emptyMap();
		}
		return bundle.getCustomRenditions()
				.stream()
				.collect(Collectors.toUnmodifiableMap(
						ProcessedBundle.ImageSize::getId,
						rendition -> {
							final String ratio = rendition.getRatio().orElseGet(() -> assetRatioProvider.get(asset));
							return getUrl(asset, rendition.getWidth(), ratio, dynamicImageParameter);
						},
						(r1, r2) -> r1 // merge function: if there is a duplicate definition use the first one
				));
	}

	private String getUrl(final Asset asset, final int width, final String ratio, @Nullable final DynamicImageParameter dynamicImageParameter) {
		final FlexibleParameter parameter = new FlexibleParameter(
				dynamicImageParameter,
				ratio,
				width,
				getModificationTime(asset).map(Instant::getEpochSecond).map(String::valueOf).orElse(FALLBACK_VERSION),
				asset
		);
		return flexibleImageUriFactory.create(parameter).toString();
	}

	private Optional<Instant> getModificationTime(final Asset asset) {
		return Optional
				.ofNullable(asset.getLastModified())
				.or(() -> Optional.ofNullable(asset.getCreated()))
				.map(Calendar::toInstant);
	}

	private boolean shouldNotGenerateImage(final Asset asset) {
		return EXCLUDE_FROM_GENERATION_MIME_TYPES.contains(asset.getMimeType());
	}
}
