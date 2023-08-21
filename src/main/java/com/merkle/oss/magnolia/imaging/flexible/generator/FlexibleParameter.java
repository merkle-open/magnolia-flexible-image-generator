package com.merkle.oss.magnolia.imaging.flexible.generator;

import info.magnolia.jcr.wrapper.DelegateNodeWrapper;

import javax.annotation.Nullable;
import javax.jcr.Node;
import java.util.Objects;
import java.util.Optional;

public class FlexibleParameter extends DelegateNodeWrapper {
	@Nullable
	private final Integer width;
	@Nullable
	private final Integer height;
	private final boolean crop;

	public FlexibleParameter(
			@Nullable final Integer width,
			@Nullable final Integer height,
			final boolean crop,
			final Node node
	) {
		super(node);
		this.width = width;
		this.height = height;
		this.crop = crop;
	}

	public Optional<Integer> getWidth() {
		return Optional.ofNullable(width);
	}

	public Optional<Integer> getHeight() {
		return Optional.ofNullable(height);
	}

	public boolean isCrop() {
		return this.crop;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		FlexibleParameter that = (FlexibleParameter) o;
		return crop == that.crop && Objects.equals(width, that.width) && Objects.equals(height, that.height);
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
}