package com.merkle.oss.magnolia.imaging.flexible.model;

import com.merkle.oss.magnolia.imaging.flexible.bundle.Bundle;
import com.merkle.oss.magnolia.imaging.flexible.bundle.BundlesProvider;
import com.merkle.oss.magnolia.imaging.flexible.bundle.ImageSize;
import com.merkle.oss.magnolia.imaging.flexible.generator.FlexibleImageVariation;
import info.magnolia.dam.api.Asset;
import info.magnolia.dam.jcr.DamConstants;
import info.magnolia.dam.templating.functions.DamTemplatingFunctions;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

public class DamImageModelFactory implements ImageModel.Factory {
	private static final Set<String> EXCLUDE_FROM_GENERATION_MIME_TYPES = Set.of(
			"image/svg+xml",
			"image/gif"
	);
	private final BundlesProvider bundlesProvider;
	private final FlexibleImageVariation flexibleImageVariation;
	private final DamTemplatingFunctions damTemplatingFunctions;
	private final LocalizedAsset.Factory localizedAssetFactory;

	@Inject
	public DamImageModelFactory(
			final BundlesProvider bundlesProvider,
			final FlexibleImageVariation flexibleImageVariation,
			final DamTemplatingFunctions damTemplatingFunctions,
			final LocalizedAsset.Factory localizedAssetFactory
	) {
		this.bundlesProvider = bundlesProvider;
		this.flexibleImageVariation = flexibleImageVariation;
		this.damTemplatingFunctions = damTemplatingFunctions;
		this.localizedAssetFactory = localizedAssetFactory;
	}

	@Override
	public Optional<ImageModel> create(
			final Locale locale,
			final String assetId,
			final String bundleName
	) {
		return Optional
				.of(assetId)
				.filter(id -> StringUtils.startsWith(id, DamConstants.DEFAULT_JCR_PROVIDER_ID + ":"))
				.map(damTemplatingFunctions::getAsset)
				.map(asset -> localizedAssetFactory.create(locale, asset))
				.flatMap(localizedAsset ->
						bundlesProvider.get(bundleName).map(bundle ->
								create(localizedAsset, bundle)
						)
				);
	}

	private ImageModel create(
			final LocalizedAsset asset,
			final Bundle bundle) {
		return new ImageModel(
				asset.getCaption(),
				asset.getTitle(),
				asset.getDescription(),
				asset.getLink(),
				asset.getName(),
				getSrcSet(bundle, asset),
				getCustomRenditions(bundle, asset)
		);
	}

	private List<ImageModel.Rendition> getSrcSet(final Bundle bundle, final Asset asset) {
		if (shouldNotGenerateImage(asset)) {
			return Collections.emptyList();
		}
		return bundle.getImageSizes()
				.stream()
				.map(size ->
						new ImageModel.Rendition(size.getMedia(), size.getWidth(), size.getHeight(), getUrl(size, asset))
				)
				.collect(Collectors.toList());
	}

	private Map<String, String> getCustomRenditions(final Bundle bundle, final Asset asset) {
		if (shouldNotGenerateImage(asset)) {
			return Collections.emptyMap();
		}
		return bundle.getCustomRenditions()
				.stream()
				.collect(Collectors.toUnmodifiableMap(
						CustomRendition::getName,
						rendition -> getUrl(rendition, asset),
						(r1, r2) -> r1 // merge function: if there is a duplicate definition use the first one
				));
	}

	private String getUrl(final ImageSize size, final Asset asset) {
		return flexibleImageVariation.createLink(size, asset);
	}

	private boolean shouldNotGenerateImage(final Asset asset) {
		return EXCLUDE_FROM_GENERATION_MIME_TYPES.contains(asset.getMimeType());
	}
}
