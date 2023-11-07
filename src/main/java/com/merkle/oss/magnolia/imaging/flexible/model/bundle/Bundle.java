package com.merkle.oss.magnolia.imaging.flexible.model.bundle;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

class Bundle {
	@SerializedName("bundle")
	private final String name;
	@Nullable
	private final String ratio;
	private final List<ImageSize> imageSizes;
	private final List<ImageSize> customRenditions;

	Bundle(
			final String name,
			@Nullable final String ratio,
			final List<ImageSize> imageSizes,
			final List<ImageSize> customRenditions
	) {
		this.name = name;
		this.ratio = ratio;
		this.imageSizes = imageSizes;
		this.customRenditions = customRenditions;
	}

	public String getName() {
		return name;
	}

	public Optional<String> getRatio() {
		return Optional.ofNullable(ratio);
	}

	public List<ImageSize> getImageSizes() {
		return imageSizes;
	}

	public List<ImageSize> getCustomRenditions() {
		return customRenditions;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Bundle bundle = (Bundle) o;
		return Objects.equals(name, bundle.name) && Objects.equals(ratio, bundle.ratio) && Objects.equals(imageSizes, bundle.imageSizes) && Objects.equals(customRenditions, bundle.customRenditions);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, ratio, imageSizes, customRenditions);
	}

	@Override
	public String toString() {
		return "Bundle{" +
				"name='" + name + '\'' +
				", ratio=" + ratio +
				", imageSizes=" + imageSizes +
				", customRenditions=" + customRenditions +
				'}';
	}

	static class ImageSize {
		@SerializedName(value = "id", alternate = {"media", "name"})
		private final String id;
		private final int width;
		@Nullable
		private final String ratio;

		ImageSize(
				final String id,
				final int width,
				@Nullable final String ratio
		) {
			this.width = width;
			this.id = id;
			this.ratio = ratio;
		}

		public String getId() {
			return id;
		}

		public int getWidth() {
			return width;
		}

		public Optional<String> getRatio() {
			return Optional.ofNullable(ratio);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			ImageSize imageSize = (ImageSize) o;
			return width == imageSize.width && Objects.equals(id, imageSize.id) && Objects.equals(ratio, imageSize.ratio);
		}

		@Override
		public int hashCode() {
			return Objects.hash(id, width, ratio);
		}

		@Override
		public String toString() {
			return "ImageSize{" +
					"id='" + id + '\'' +
					", width=" + width +
					", ratio=" + ratio +
					'}';
		}
	}
}

