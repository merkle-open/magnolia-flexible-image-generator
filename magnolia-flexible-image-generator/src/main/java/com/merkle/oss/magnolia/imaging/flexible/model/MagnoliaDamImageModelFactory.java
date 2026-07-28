package com.merkle.oss.magnolia.imaging.flexible.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.merkle.oss.magnolia.imaging.flexible.generator.uri.FlexibleImageUriFactory;
import com.merkle.oss.magnolia.imaging.flexible.model.MagnoliaDamAssetModelFactory.MagnoliaAssetModel;
import com.merkle.oss.magnolia.imaging.flexible.model.bundle.ProcessedBundle;
import com.merkle.oss.magnolia.imaging.flexible.model.bundle.ProcessedBundlesProvider;
import com.merkle.oss.magnolia.imaging.flexible.model.bundle.RatioParser;

import jakarta.annotation.Nullable;
import jakarta.inject.Inject;

public class MagnoliaDamImageModelFactory implements ImageModel.Factory {
	private static final Set<String> EXCLUDE_FROM_GENERATION_MIME_TYPES = Set.of(
			"image/svg+xml",
			"image/gif"
	);
    private final MagnoliaDamAssetModelFactory magnoliaDamAssetModelFactory;
    private final ProcessedBundlesProvider bundlesProvider;
	private final FlexibleImageUriFactory flexibleImageUriFactory;
	private final RatioParser ratioParser;

	@Inject
	public MagnoliaDamImageModelFactory(
			final MagnoliaDamAssetModelFactory magnoliaDamAssetModelFactory,
			final ProcessedBundlesProvider bundlesProvider,
			final FlexibleImageUriFactory flexibleImageUriFactory,
			final RatioParser ratioParser
	) {
        this.magnoliaDamAssetModelFactory = magnoliaDamAssetModelFactory;
        this.bundlesProvider = bundlesProvider;
		this.flexibleImageUriFactory = flexibleImageUriFactory;
		this.ratioParser = ratioParser;
	}

	@Override
	public Optional<ImageModel> create(final Locale locale, final String assetId, final String bundleName, @Nullable final DynamicImageParameter dynamicImageParameter) {
		return magnoliaDamAssetModelFactory.create(locale, assetId).flatMap(asset ->
				bundlesProvider.get(bundleName).map(bundle ->
						create(asset, bundle, dynamicImageParameter)
				)
		);
	}

	private ImageModel create(
			final MagnoliaAssetModel asset,
			final ProcessedBundle bundle,
			@Nullable final DynamicImageParameter dynamicImageParameter
	) {
		return new ImageModel(
				asset.getAlt(),
				asset.getTitle(),
				asset.getDescription(),
				asset.getSrc(),
				asset.getName(),
				getSrcSet(bundle, asset, dynamicImageParameter),
				getCustomRenditions(bundle, asset, dynamicImageParameter),
				asset.getMimeType().orElse(null)
		);
	}

	private List<ImageModel.Rendition> getSrcSet(final ProcessedBundle bundle, final MagnoliaAssetModel asset, @Nullable final DynamicImageParameter dynamicImageParameter) {
		if (shouldNotGenerateImage(asset)) {
			return Collections.emptyList();
		}
		return bundle.getImageSizes()
				.stream()
				.map(size -> getRendition(size, asset, dynamicImageParameter))
				.collect(Collectors.toList());
	}

	private ImageModel.Rendition getRendition(final ProcessedBundle.ImageSize size, final MagnoliaAssetModel asset, @Nullable final DynamicImageParameter dynamicImageParameter) {
		final String ratio = size.getRatio().or(asset::getRatio).orElseThrow(() ->
			new NullPointerException("ratio not present!")
		);
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

	private Map<String, String> getCustomRenditions(final ProcessedBundle bundle, final MagnoliaAssetModel asset, @Nullable final DynamicImageParameter dynamicImageParameter) {
		if (shouldNotGenerateImage(asset)) {
			return Collections.emptyMap();
		}
		return bundle.getCustomRenditions()
				.stream()
				.collect(Collectors.toUnmodifiableMap(
						ProcessedBundle.ImageSize::getId,
						rendition -> {
							final String ratio = rendition.getRatio().or(asset::getRatio).orElseThrow(() ->
								new NullPointerException("ratio not present!")
							);
							return getUrl(asset, rendition.getWidth(), ratio, dynamicImageParameter);
						},
						(r1, r2) -> r1 // merge function: if there is a duplicate definition use the first one
				));
	}

	private String getUrl(final MagnoliaAssetModel asset, final int width, final String ratio, @Nullable final DynamicImageParameter dynamicImageParameter) {
		final FlexibleParameter parameter = new FlexibleParameter(
				dynamicImageParameter,
				ratio,
				width,
				asset.getVersion(),
				asset.getAsset()
		);
		return flexibleImageUriFactory.create(parameter).toString();
	}

	private boolean shouldNotGenerateImage(final AssetModel asset) {
		return EXCLUDE_FROM_GENERATION_MIME_TYPES.contains(asset.getMimeType().orElse(null));
	}
}
