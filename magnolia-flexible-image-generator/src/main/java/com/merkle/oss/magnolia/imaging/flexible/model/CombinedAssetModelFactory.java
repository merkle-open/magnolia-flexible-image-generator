package com.merkle.oss.magnolia.imaging.flexible.model;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import jakarta.inject.Inject;

public class CombinedAssetModelFactory implements AssetModel.Factory {
	private final Collection<AssetModel.Factory> factories;

	@Inject
	public CombinedAssetModelFactory(final Set<AssetModel.Factory> imageModelFactories) {
		this.factories = imageModelFactories;
	}

	@Override
	public Optional<? extends AssetModel> create(final Locale locale, final String assetId) {
		return factories
				.stream()
				.map(factory -> factory.create(locale, assetId))
				.flatMap(Optional::stream)
				.findFirst();
	}
}
