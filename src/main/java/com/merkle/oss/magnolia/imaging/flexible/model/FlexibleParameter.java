package com.merkle.oss.magnolia.imaging.flexible.model;

import info.magnolia.dam.api.Asset;
import info.magnolia.dam.api.AssetDecorator;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class FlexibleParameter extends AssetDecorator {
	private final int width;
	private final int height;
	private final boolean crop;

	public FlexibleParameter(
			final int width,
			final int height,
			final boolean crop,
			final Asset asset
	) {
		super(asset);
		this.width = width;
		this.height = height;
		this.crop = crop;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean isCrop() {
		return this.crop;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		FlexibleParameter that = (FlexibleParameter) o;
		return width == that.width && height == that.height && crop == that.crop;
	}

	@Override
	public int hashCode() {
		return Objects.hash(width, height, crop);
	}

	@Override
	public String toString() {
		return "FlexibleParameter{" +
				"width=" + width +
				", height=" + height +
				", crop=" + crop +
				'}';
	}

	public static class Factory {
		public static final String WIDTH_PARAM = "width";
		public static final String HEIGHT_PARAM = "height";
		public static final String CROP_PARAM = "crop";

		public Optional<FlexibleParameter> create(final Asset asset, final Function<String, Optional<String>> parameterProvider) {
			return parameterProvider.apply(WIDTH_PARAM).map(Integer::parseInt).flatMap(width ->
					parameterProvider.apply(HEIGHT_PARAM).map(Integer::parseInt).flatMap(height ->
							parameterProvider.apply(CROP_PARAM).map(Boolean::parseBoolean).map(crop ->
									new FlexibleParameter(width, height, crop, asset)
							)
					)
			);
		}

		public Map<String, String> toMap(final FlexibleParameter parameter) {
			return Map.of(
					WIDTH_PARAM, String.valueOf(parameter.getWidth()),
					HEIGHT_PARAM, String.valueOf(parameter.getHeight()),
					CROP_PARAM, String.valueOf(parameter.isCrop())
			);
		}
	}
}