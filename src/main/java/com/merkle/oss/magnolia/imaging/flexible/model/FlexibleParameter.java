package com.merkle.oss.magnolia.imaging.flexible.model;

import info.magnolia.dam.api.Asset;
import info.magnolia.dam.api.AssetDecorator;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;
import javax.inject.Inject;

public class FlexibleParameter extends AssetDecorator {
	@Nullable
	private final DynamicImageParameter dynamicImageParameter;
    private final String version;
    @Nullable
	private final String ratio;
	private final int width;

	public FlexibleParameter(
			@Nullable final DynamicImageParameter dynamicImageParameter,
			@Nullable final String ratio,
			final int width,
			final String version,
			final Asset asset
	) {
		super(asset);
		this.ratio = ratio;
		this.width = width;
		this.dynamicImageParameter = dynamicImageParameter;
        this.version = version;
    }

	public Optional<DynamicImageParameter> getDynamicImageParameter() {
		return Optional.ofNullable(dynamicImageParameter);
	}

	public int getWidth() {
		return width;
	}

	public String getVersion() {
		return version;
	}

	public Optional<String> getRatio() {
		return Optional.ofNullable(ratio);
	}

	public Map<String, String> toMap() {
		return Stream.of(
				getDynamicImageParameter().stream().map(DynamicImageParameter::toMap).map(Map::entrySet).flatMap(Collection::stream),
				getRatio().stream().map(ratio -> Map.entry(Factory.RATIO_PARAM, ratio)),
				Map.of(
						Factory.WIDTH_PARAM, String.valueOf(getWidth()),
						Factory.VERSION_PARAM, version
				).entrySet().stream()
		).flatMap(Function.identity()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (dynamicImageParam, flexibleParam) -> flexibleParam));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		FlexibleParameter that = (FlexibleParameter) o;
		return width == that.width && Objects.equals(dynamicImageParameter, that.dynamicImageParameter) && Objects.equals(version, that.version) && Objects.equals(ratio, that.ratio);
	}

	@Override
	public int hashCode() {
		return Objects.hash(dynamicImageParameter, version, ratio, width);
	}

	@Override
	public String toString() {
		return "FlexibleParameter{" +
				"dynamicImageParameter=" + dynamicImageParameter +
				", version='" + version + '\'' +
				", ratio='" + ratio + '\'' +
				", width=" + width +
				"} " + super.toString();
	}

	public static class Factory {
		public static final String WIDTH_PARAM = "width";
		public static final String RATIO_PARAM = "ratio";
		public static final String VERSION_PARAM = "version";

		private final DynamicImageParameter.Factory dynamicImageParameterFactory;

		@Inject
		public Factory(final DynamicImageParameter.Factory dynamicImageParameterFactory) {
			this.dynamicImageParameterFactory = dynamicImageParameterFactory;
		}

		public Optional<FlexibleParameter> create(final Asset asset, final Function<String, Optional<String>> parameterProvider) {
			return parameterProvider.apply(WIDTH_PARAM).map(Integer::parseInt).flatMap(width ->
					parameterProvider.apply(VERSION_PARAM).map(version ->
						new FlexibleParameter(
								dynamicImageParameterFactory.create(parameterProvider).orElse(null),
								parameterProvider.apply(RATIO_PARAM).orElse(null),
								width,
								version,
								asset
						)
					)
			);
		}
	}
}