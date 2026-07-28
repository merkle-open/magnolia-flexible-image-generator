package com.merkle.oss.magnolia.imaging.flexible.model;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import jakarta.annotation.Nullable;
import jakarta.inject.Inject;

public class CombinedImageModelFactory implements ImageModel.Factory {
	private final Collection<ImageModel.Factory> factories;

	@Inject
	public CombinedImageModelFactory(final Set<ImageModel.Factory> imageModelFactories) {
		this.factories = imageModelFactories;
	}

	@Override
	public Optional<ImageModel> create(final Locale locale, final String assetId, final String bundleName, @Nullable final DynamicImageParameter dynamicImageParameter) {
		return factories
				.stream()
				.map(factory -> factory.create(locale, assetId, bundleName, dynamicImageParameter))
				.flatMap(Optional::stream)
				.findFirst();
	}
}
