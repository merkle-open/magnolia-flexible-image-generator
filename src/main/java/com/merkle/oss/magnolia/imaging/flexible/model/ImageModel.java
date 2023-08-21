package com.merkle.oss.magnolia.imaging.flexible.model;

import com.merkle.oss.magnolia.imaging.flexible.bundle.Bundle;
import com.merkle.oss.magnolia.imaging.flexible.bundle.BundlesProvider;
import com.merkle.oss.magnolia.imaging.flexible.generator.FlexibleImageVariation;
import com.merkle.oss.magnolia.imaging.flexible.bundle.ImageSize;
import info.magnolia.dam.api.Asset;
import info.magnolia.dam.templating.functions.DamTemplatingFunctions;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

public class ImageModel {
	private final String alt;
	private final String title;
	private final String description;
	private final String original;
	private final String assetName;
	private final List<Rendition> srcset;
	private final Map<String, String> customRenditions;

	public ImageModel(
			final String alt,
			final String title,
			final String description,
			final String original,
			final String assetName,
			final List<Rendition> srcset,
			final Map<String, String> customRenditions
	) {
		this.alt = alt;
		this.title = title;
		this.description = description;
		this.original = original;
		this.assetName = assetName;
		this.srcset = srcset;
		this.customRenditions = customRenditions;
	}

	public String getAlt() {
		return alt;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getOriginal() {
		return original;
	}

	public String getAssetName() {
		return assetName;
	}

	public List<Rendition> getSrcset() {
		return srcset;
	}

	public Map<String, String> getCustomRenditions() {
		return customRenditions;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ImageModel that = (ImageModel) o;
		return Objects.equals(alt, that.alt) && Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(original, that.original) && Objects.equals(assetName, that.assetName) && Objects.equals(srcset, that.srcset) && Objects.equals(customRenditions, that.customRenditions);
	}

	@Override
	public int hashCode() {
		return Objects.hash(alt, title, description, original, assetName, srcset, customRenditions);
	}

	@Override
	public String toString() {
		return "ImageModel{" +
				"alt='" + alt + '\'' +
				", title='" + title + '\'' +
				", description='" + description + '\'' +
				", original='" + original + '\'' +
				", assetName='" + assetName + '\'' +
				", srcset=" + srcset +
				", customRenditions=" + customRenditions +
				'}';
	}

	public static class Rendition {
		private final String media;
		private final Integer width;
		private final Integer height;
		private final String src;

		public Rendition(
				final String media,
				final Integer width,
				final Integer height,
				final String src
		) {
			this.media = media;
			this.width = width;
			this.height = height;
			this.src = src;
		}

		public String getMedia() {
			return media;
		}

		public Integer getWidth() {
			return width;
		}

		public Integer getHeight() {
			return height;
		}

		public String getSrc() {
			return src;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Rendition rendition = (Rendition) o;
			return Objects.equals(media, rendition.media) && Objects.equals(width, rendition.width) && Objects.equals(height, rendition.height) && Objects.equals(src, rendition.src);
		}

		@Override
		public int hashCode() {
			return Objects.hash(media, width, height, src);
		}

		@Override
		public String toString() {
			return "Rendition{" +
					"media='" + media + '\'' +
					", width=" + width +
					", height=" + height +
					", src='" + src + '\'' +
					'}';
		}
	}

	public static class Factory {
		private static final Set<String> EXCLUDE_FROM_GENERATION_MIME_TYPES = Set.of(
				"image/svg+xml",
				"image/gif"
		);
		private final BundlesProvider bundlesProvider;
		private final FlexibleImageVariation flexibleImageVariation;
		private final DamTemplatingFunctions damTemplatingFunctions;
		private final LocalizedAsset.Factory localizedAssetFactory;

		@Inject
		public Factory(
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

		public Optional<ImageModel> create(
				final Locale locale,
				final String assetId,
				final String bundleName
		) {
			return Optional.ofNullable(damTemplatingFunctions.getAsset(assetId))
					.map(asset -> localizedAssetFactory.create(locale, asset))
					.flatMap(localizedAsset ->
							bundlesProvider.get(bundleName).map(bundle ->
									new ImageModel(
											localizedAsset.getCaption(),
											localizedAsset.getTitle(),
											localizedAsset.getDescription(),
											localizedAsset.getLink(),
											localizedAsset.getName(),
											getSrcSet(bundle, localizedAsset),
											getCustomRenditions(bundle, localizedAsset)
									)
							)
					);
		}

		private List<Rendition> getSrcSet(final Bundle bundle, final Asset asset) {
			if(shouldNotGenerateImage(asset)) {
				return Collections.emptyList();
			}
			return bundle.getImageSizes()
					.stream()
					.map(size ->
							new Rendition(size.getMedia(), size.getWidth(), size.getHeight(), getUrl(size, asset))
					)
					.collect(Collectors.toList());
		}

		private Map<String, String> getCustomRenditions(final Bundle bundle, final Asset asset) {
			if(shouldNotGenerateImage(asset)) {
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
}
