package com.merkle.oss.magnolia.imaging.flexible.model;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class DynamicImageParameter {
	private final boolean crop;

	public DynamicImageParameter(final boolean crop) {
		this.crop = crop;
	}

	public boolean isCrop() {
		return crop;
	}

	public Map<String, String> toMap() {
		return Map.of(Factory.CROP_PARAM, String.valueOf(isCrop()));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DynamicImageParameter that = (DynamicImageParameter) o;
		return crop == that.crop;
	}

	@Override
	public int hashCode() {
		return Objects.hash(crop);
	}

	@Override
	public String toString() {
		return "DynamicImageParameter{" +
				"crop=" + crop +
				'}';
	}

	public static class Factory {
		public static final String CROP_PARAM = "crop";

		public Optional<? extends DynamicImageParameter> create(final Function<String, Optional<String>> parameterProvider) {
			return parameterProvider.apply(CROP_PARAM).map(Boolean::parseBoolean).map(DynamicImageParameter::new);
		}
	}
}
