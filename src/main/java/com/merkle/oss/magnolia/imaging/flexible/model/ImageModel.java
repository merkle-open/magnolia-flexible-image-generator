package com.merkle.oss.magnolia.imaging.flexible.model;

import javax.annotation.Nullable;
import java.util.*;

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

	public interface Factory {
		/**
		 * @return an imageModel if the assedId matches (e.g. "jcr:..." for dam) or empty
		 */
		default Optional<ImageModel> create(Locale locale, String assetId, String bundleName) {
			return create(locale, assetId, bundleName, null);
		}
		Optional<ImageModel> create(Locale locale, String assetId, String bundleName, @Nullable DynamicImageParameter dynamicImageParameter);
	}
}
