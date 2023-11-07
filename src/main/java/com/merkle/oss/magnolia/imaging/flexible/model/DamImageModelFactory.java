package com.merkle.oss.magnolia.imaging.flexible.model;

import com.merkle.oss.magnolia.imaging.flexible.generator.FlexibleImageUriFactory;
import com.merkle.oss.magnolia.imaging.flexible.model.bundle.ProcessedBundle;
import com.merkle.oss.magnolia.imaging.flexible.model.bundle.ProcessedBundlesProvider;
import info.magnolia.dam.api.Asset;
import info.magnolia.dam.jcr.DamConstants;
import info.magnolia.dam.templating.functions.DamTemplatingFunctions;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

public class DamImageModelFactory implements ImageModel.Factory {
	private static final Set<String> EXCLUDE_FROM_GENERATION_MIME_TYPES = Set.of(
			"image/svg+xml",
			"image/gif"
	);
	private final ProcessedBundlesProvider bundlesProvider;
	private final FlexibleImageUriFactory flexibleImageUriFactory;
	private final DamTemplatingFunctions damTemplatingFunctions;
	private final LocalizedAsset.Factory localizedAssetFactory;

	@Inject
	public DamImageModelFactory(
			final ProcessedBundlesProvider bundlesProvider,
			final FlexibleImageUriFactory flexibleImageUriFactory,
			final DamTemplatingFunctions damTemplatingFunctions,
			final LocalizedAsset.Factory localizedAssetFactory
	) {
		this.bundlesProvider = bundlesProvider;
		this.flexibleImageUriFactory = flexibleImageUriFactory;
		this.damTemplatingFunctions = damTemplatingFunctions;
		this.localizedAssetFactory = localizedAssetFactory;
	}

	@Override
	public Optional<ImageModel> create(final Locale locale, final String assetId, final String bundleName, @Nullable final DynamicImageParameter dynamicImageParameter) {
		return Optional
				.of(assetId)
				.filter(id -> StringUtils.startsWith(id, DamConstants.DEFAULT_JCR_PROVIDER_ID + ":"))
				.map(damTemplatingFunctions::getAsset)
				.map(asset -> localizedAssetFactory.create(locale, asset))
				.flatMap(localizedAsset ->
						bundlesProvider.get(bundleName).map(bundle ->
								create(localizedAsset, bundle, dynamicImageParameter)
						)
				);
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
				getCustomRenditions(bundle, asset, dynamicImageParameter)
		);
	}

	private List<ImageModel.Rendition> getSrcSet(final ProcessedBundle bundle, final Asset asset, @Nullable final DynamicImageParameter dynamicImageParameter) {
		if (shouldNotGenerateImage(asset)) {
			return Collections.emptyList();
		}
		return bundle.getImageSizes()
				.stream()
				.map(size ->
						new ImageModel.Rendition(size.getId(), getUrl(size, asset, dynamicImageParameter))
				)
				.collect(Collectors.toList());
	}

	private Map<String, String> getCustomRenditions(final ProcessedBundle bundle, final Asset asset, @Nullable final DynamicImageParameter dynamicImageParameter) {
		if (shouldNotGenerateImage(asset)) {
			return Collections.emptyMap();
		}
		return bundle.getCustomRenditions()
				.stream()
				.collect(Collectors.toUnmodifiableMap(
						ProcessedBundle.ImageSize::getId,
						rendition -> getUrl(rendition, asset, dynamicImageParameter),
						(r1, r2) -> r1 // merge function: if there is a duplicate definition use the first one
				));
	}

	private String getUrl(final ProcessedBundle.ImageSize size, final Asset asset, @Nullable final DynamicImageParameter dynamicImageParameter) {
		final FlexibleParameter parameter = new FlexibleParameter(dynamicImageParameter, size.getRatio().orElse(null), size.getWidth(), asset);
		return flexibleImageUriFactory.create(parameter).toString();
	}

	private boolean shouldNotGenerateImage(final Asset asset) {
		return EXCLUDE_FROM_GENERATION_MIME_TYPES.contains(asset.getMimeType());
	}
}