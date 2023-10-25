package com.merkle.oss.magnolia.imaging.flexible.model.bundle;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

class Bundle {
	@SerializedName("bundle")
	private final String name;
	private final double ratio;
	@Nullable
	private final Boolean crop;
	private final List<ImageSize> imageSizes;
	private final List<ImageSize> customRenditions;

	Bundle(
			final String name,
			final double ratio,
			@Nullable final Boolean crop,
			final List<ImageSize> imageSizes,
			final List<ImageSize> customRenditions
	) {
		this.name = name;
		this.ratio = ratio;
		this.crop = crop;
		this.imageSizes = imageSizes;
		this.customRenditions = customRenditions;
	}

	public String getName() {
		return name;
	}

	public double getRatio() {
		return ratio;
	}

	@Nullable
	public Boolean getCrop() {
		return crop;
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
		return Double.compare(ratio, bundle.ratio) == 0 && Objects.equals(name, bundle.name) && Objects.equals(crop, bundle.crop) && Objects.equals(imageSizes, bundle.imageSizes) && Objects.equals(customRenditions, bundle.customRenditions);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, ratio, crop, imageSizes, customRenditions);
	}

	@Override
	public String toString() {
		return "Bundle{" +
				"name='" + name + '\'' +
				", ratio=" + ratio +
				", crop=" + crop +
				", imageSizes=" + imageSizes +
				", customRenditions=" + customRenditions +
				'}';
	}

	static class ImageSize {
		@SerializedName(value = "id", alternate = {"media", "name"})
		private final String id;
		private final int width;

		ImageSize(
				String id,
				int width
		) {
			this.width = width;
			this.id = id;
		}

		public String getId() {
			return id;
		}

		public int getWidth() {
			return width;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			ImageSize imageSize = (ImageSize) o;
			return width == imageSize.width && Objects.equals(id, imageSize.id);
		}

		@Override
		public int hashCode() {
			return Objects.hash(id, width);
		}

		@Override
		public String toString() {
			return "ImageSize{" +
					"id='" + id + '\'' +
					", width=" + width +
					'}';
		}
	}
}

