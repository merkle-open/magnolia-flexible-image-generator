package com.merkle.oss.magnolia.imaging.flexible.model.bundle;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import jakarta.annotation.Nullable;

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
		@Nullable
		private final String ratio;
		private final int width;
		private final String id;

		ImageSize(
				@Nullable final String ratio,
				final int width,
				final String id
		){
			this.ratio = ratio;
			this.width = width;
			this.id = id;
		}

		public Optional<String> getRatio() {
			return Optional.ofNullable(ratio);
		}

		public int getWidth() {
			return width;
		}

		public String getId() {
			return id;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			ImageSize imageSize = (ImageSize) o;
			return width == imageSize.width && Objects.equals(ratio, imageSize.ratio) && Objects.equals(id, imageSize.id);
		}

		@Override
		public int hashCode() {
			return Objects.hash(ratio, width, id);
		}

		@Override
		public String toString() {
			return "ImageSize{" +
					"ratio='" + ratio + '\'' +
					", width=" + width +
					", id='" + id + '\'' +
					'}';
		}
	}
}
