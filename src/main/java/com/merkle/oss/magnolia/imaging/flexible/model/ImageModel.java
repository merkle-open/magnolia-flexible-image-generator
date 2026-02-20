package com.merkle.oss.magnolia.imaging.flexible.model;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import jakarta.annotation.Nullable;

public class ImageModel {
	private final String alt;
	private final String title;
	private final String description;
	private final String original;
	private final String assetName;
	private final List<Rendition> srcset;
	private final Map<String, String> customRenditions;
    @Nullable
    private final String mimeType;

    public ImageModel(
			final String alt,
			final String title,
			final String description,
			final String original,
			final String assetName,
			final List<Rendition> srcset,
			final Map<String, String> customRenditions,
			@Nullable final String mimeType
	) {
		this.alt = alt;
		this.title = title;
		this.description = description;
		this.original = original;
		this.assetName = assetName;
		this.srcset = srcset;
		this.customRenditions = customRenditions;
        this.mimeType = mimeType;
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

	public Optional<String> getMimeType() {
		return Optional.ofNullable(mimeType);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ImageModel that)) {
			return false;
		}
        return Objects.equals(alt, that.alt) && Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(original, that.original) && Objects.equals(assetName, that.assetName) && Objects.equals(srcset, that.srcset) && Objects.equals(customRenditions, that.customRenditions) && Objects.equals(mimeType, that.mimeType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(alt, title, description, original, assetName, srcset, customRenditions, mimeType);
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
				", mimeType='" + mimeType + '\'' +
				'}';
	}

	public static class Rendition {
		private final String media;
		private final String src;
		private final int width;
		private final int height;
		private final double ratio;

		public Rendition(
				final String media,
				final String src,
				final int width,
				final int height,
				final double ratio
		) {
			this.media = media;
			this.src = src;
			this.width = width;
			this.height = height;
			this.ratio = ratio;
		}

		public String getMedia() {
			return media;
		}

		public String getSrc() {
			return src;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		public double getRatio() {
			return ratio;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Rendition rendition)) {
				return false;
			}
			return width == rendition.width && height == rendition.height && Double.compare(ratio, rendition.ratio) == 0 && Objects.equals(media, rendition.media) && Objects.equals(src, rendition.src);
		}

		@Override
		public int hashCode() {
			return Objects.hash(media, src, width, height, ratio);
		}

		@Override
		public String toString() {
			return "Rendition{" +
					"media='" + media + '\'' +
					", src='" + src + '\'' +
					", width=" + width +
					", height=" + height +
					", ratio=" + ratio +
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
