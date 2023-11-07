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
	@Nullable
	private final String ratio;
	private final int width;

	public FlexibleParameter(
			@Nullable final DynamicImageParameter dynamicImageParameter,
			@Nullable final String ratio,
			final int width,
			final Asset asset
	) {
		super(asset);
		this.ratio = ratio;
		this.width = width;
		this.dynamicImageParameter = dynamicImageParameter;
	}

	public Optional<DynamicImageParameter> getDynamicImageParameter() {
		return Optional.ofNullable(dynamicImageParameter);
	}

	public int getWidth() {
		return width;
	}

	public Optional<String> getRatio() {
		return Optional.ofNullable(ratio);
	}

	public Map<String, String> toMap() {
		return Stream.of(
				getDynamicImageParameter().stream().map(DynamicImageParameter::toMap).map(Map::entrySet).flatMap(Collection::stream),
				getRatio().stream().map(ratio -> Map.entry(Factory.RATIO_PARAM, ratio)),
				Map.of(
						Factory.WIDTH_PARAM, String.valueOf(getWidth())
				).entrySet().stream()
		).flatMap(Function.identity()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (dynamicImageParam, flexibleParam) -> flexibleParam));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		FlexibleParameter that = (FlexibleParameter) o;
		return width == that.width && Objects.equals(dynamicImageParameter, that.dynamicImageParameter) && Objects.equals(ratio, that.ratio);
	}

	@Override
	public int hashCode() {
		return Objects.hash(dynamicImageParameter, ratio, width);
	}

	public static class Factory {
		public static final String WIDTH_PARAM = "width";
		public static final String RATIO_PARAM = "ratio";

		private final DynamicImageParameter.Factory dynamicImageParameterFactory;

		@Inject
		public Factory(final DynamicImageParameter.Factory dynamicImageParameterFactory) {
			this.dynamicImageParameterFactory = dynamicImageParameterFactory;
		}

		public Optional<FlexibleParameter> create(final Asset asset, final Function<String, Optional<String>> parameterProvider) {
			return parameterProvider.apply(WIDTH_PARAM).map(Integer::parseInt).map(width ->
					new FlexibleParameter(
							dynamicImageParameterFactory.create(parameterProvider).orElse(null),
							parameterProvider.apply(RATIO_PARAM).orElse(null),
							width,
							asset
					)
			);
		}
	}
}