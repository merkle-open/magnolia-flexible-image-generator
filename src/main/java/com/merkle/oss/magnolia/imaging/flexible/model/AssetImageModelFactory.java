package com.merkle.oss.magnolia.imaging.flexible.model;

import info.magnolia.dam.api.Asset;
import info.magnolia.dam.api.AssetProviderRegistry;
import info.magnolia.dam.api.ItemKey;
import info.magnolia.dam.templating.functions.DamTemplatingFunctions;

import java.time.Instant;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.jetbrains.annotations.Nullable;

import com.merkle.oss.magnolia.imaging.flexible.generator.uri.FlexibleImageUriFactory;
import com.merkle.oss.magnolia.imaging.flexible.model.bundle.ProcessedBundle;
import com.merkle.oss.magnolia.imaging.flexible.model.bundle.ProcessedBundlesProvider;

public class AssetImageModelFactory implements ImageModel.Factory {
	private static final Set<String> EXCLUDE_FROM_GENERATION_MIME_TYPES = Set.of(
			"image/svg+xml",
			"image/gif"
	);
	private final ProcessedBundlesProvider bundlesProvider;
	private final FlexibleImageUriFactory flexibleImageUriFactory;
	private final DamTemplatingFunctions damTemplatingFunctions;
	private final AssetProviderRegistry assetProviderRegistry;
	private final LocalizedAsset.Factory localizedAssetFactory;

	@Inject
	public AssetImageModelFactory(
			final ProcessedBundlesProvider bundlesProvider,
			final FlexibleImageUriFactory flexibleImageUriFactory,
			final DamTemplatingFunctions damTemplatingFunctions,
			final AssetProviderRegistry assetProviderRegistry,
			final LocalizedAsset.Factory localizedAssetFactory
	) {
		this.bundlesProvider = bundlesProvider;
		this.flexibleImageUriFactory = flexibleImageUriFactory;
		this.damTemplatingFunctions = damTemplatingFunctions;
		this.assetProviderRegistry = assetProviderRegistry;
		this.localizedAssetFactory = localizedAssetFactory;
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
		final FlexibleParameter parameter = new FlexibleParameter(
				dynamicImageParameter,
				size.getRatio().orElse(null),
				size.getWidth(),
				String.valueOf(getModificationTime(asset).getEpochSecond()),
				asset
		);
		return flexibleImageUriFactory.create(parameter).toString();
	}

	private Instant getModificationTime(final Asset asset) {
		return Optional
				.ofNullable(asset.getLastModified())
				.or(() -> Optional.ofNullable(asset.getCreated()))
				.map(Calendar::toInstant)
				.orElseThrow(() ->
					new NullPointerException("Asset modification time not present! asset:" + asset.getPath())
				);
	}

	private boolean shouldNotGenerateImage(final Asset asset) {
		return EXCLUDE_FROM_GENERATION_MIME_TYPES.contains(asset.getMimeType());
	}
}