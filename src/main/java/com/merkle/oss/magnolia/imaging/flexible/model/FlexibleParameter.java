package com.merkle.oss.magnolia.imaging.flexible.model;

import info.magnolia.dam.api.Asset;
import info.magnolia.dam.api.AssetDecorator;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FlexibleParameter extends AssetDecorator {
	@Nullable
	private final DynamicImageParameter dynamicImageParameter;
	private final int width;
	private final int height;

	public FlexibleParameter(
			@Nullable final DynamicImageParameter dynamicImageParameter,
			final int width,
			final int height,
			final Asset asset
	) {
		super(asset);
		this.width = width;
		this.height = height;
		this.dynamicImageParameter = dynamicImageParameter;
	}

	public Optional<DynamicImageParameter> getDynamicImageParameter() {
		return Optional.ofNullable(dynamicImageParameter);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Map<String, String> toMap() {
		return Stream.concat(
				getDynamicImageParameter().stream().map(DynamicImageParameter::toMap).map(Map::entrySet).flatMap(Collection::stream),
				Map.of(
						Factory.WIDTH_PARAM, String.valueOf(getWidth()),
						Factory.HEIGHT_PARAM, String.valueOf(getHeight())
				).entrySet().stream()
		).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (dynamicImageParam, flexibleParam) -> flexibleParam));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		FlexibleParameter that = (FlexibleParameter) o;
		return width == that.width && height == that.height && Objects.equals(dynamicImageParameter, that.dynamicImageParameter);
	}

	@Override
	public int hashCode() {
		return Objects.hash(dynamicImageParameter, width, height);
	}

	@Override
	public String toString() {
		return "FlexibleParameter{" +
				"imageParameter=" + dynamicImageParameter +
				", width=" + width +
				", height=" + height +
				'}';
	}

	public static class Factory {
		public static final String WIDTH_PARAM = "width";
		public static final String HEIGHT_PARAM = "height";

		private final DynamicImageParameter.Factory dynamicImageParameterFactory;

		@Inject
		public Factory(final DynamicImageParameter.Factory dynamicImageParameterFactory) {
			this.dynamicImageParameterFactory = dynamicImageParameterFactory;
		}

		public Optional<FlexibleParameter> create(final Asset asset, final Function<String, Optional<String>> parameterProvider) {
			return parameterProvider.apply(WIDTH_PARAM).map(Integer::parseInt).flatMap(width ->
					parameterProvider.apply(HEIGHT_PARAM).map(Integer::parseInt).map(height ->
							new FlexibleParameter(
									dynamicImageParameterFactory.create(parameterProvider).orElse(null),
									width,
									height,
									asset
							)
					)
			);
		}
	}
}