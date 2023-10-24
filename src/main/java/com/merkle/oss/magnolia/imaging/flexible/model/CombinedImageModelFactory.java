package com.merkle.oss.magnolia.imaging.flexible.model;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

public class CombinedImageModelFactory implements ImageModel.Factory {
	private final Collection<ImageModel.Factory> factories;

	@Inject
	public CombinedImageModelFactory(final Set<ImageModel.Factory> imageModelFactories) {
		this.factories = imageModelFactories;
	}

	@Override
	public Optional<ImageModel> create(final Locale locale, final String assetId, final String bundleName) {
		return factories
				.stream()
				.map(factory -> factory.create(locale, assetId, bundleName))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.findFirst();
	}
}
