package com.merkle.oss.magnolia.imaging.flexible.model.bundle;

import java.util.List;
import java.util.Objects;

public class ProcessedBundle {
	private final String name;
	private final List<ImageSize> imageSizes;
	private final List<ImageSize> customRenditions;

	ProcessedBundle(
			final String name,
			final List<ImageSize> imageSizes,
			final List<ImageSize> customRenditions
	) {
		this.name = name;
		this.imageSizes = imageSizes;
		this.customRenditions = customRenditions;
	}

	public String getName() {
		return name;
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
		ProcessedBundle that = (ProcessedBundle) o;
		return Objects.equals(name, that.name) && Objects.equals(imageSizes, that.imageSizes) && Objects.equals(customRenditions, that.customRenditions);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, imageSizes, customRenditions);
	}

	@Override
	public String toString() {
		return "ProcessedBundle{" +
				"name='" + name + '\'' +
				", imageSizes=" + imageSizes +
				", customRenditions=" + customRenditions +
				'}';
	}

	public static class ImageSize {
		private final int width;
		private final int height;
		private final String id;
		private final boolean crop;

		ImageSize(
				final int width,
				final int height,
				final String id,
				final boolean crop
		){
			this.width = width;
			this.height = height;
			this.id = id;
			this.crop = crop;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		public String getId() {
			return id;
		}

		public boolean isCrop() {
			return crop;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			ImageSize imageSize = (ImageSize) o;
			return width == imageSize.width && height == imageSize.height && crop == imageSize.crop && Objects.equals(id, imageSize.id);
		}

		@Override
		public int hashCode() {
			return Objects.hash(width, height, id, crop);
		}

		@Override
		public String toString() {
			return "ImageSize{" +
					"width=" + width +
					", height=" + height +
					", id='" + id + '\'' +
					", crop=" + crop +
					'}';
		}
	}
}