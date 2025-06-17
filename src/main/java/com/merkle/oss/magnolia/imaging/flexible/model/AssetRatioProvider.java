package com.merkle.oss.magnolia.imaging.flexible.model;

import info.magnolia.dam.api.Asset;
import info.magnolia.dam.api.metadata.MagnoliaAssetMetadata;

import java.util.Optional;

public class AssetRatioProvider {

	public String get(final Asset asset) {
		final MagnoliaAssetMetadata metadata = Optional
				.ofNullable(asset.getMetadata(MagnoliaAssetMetadata.class))
				.filter(this::isValid)
				.orElseThrow(() ->
						new IllegalArgumentException("Asset " + asset.getPath() + " has no width or height!")
				);
		return metadata.getWidth().toString() + ":" + metadata.getHeight().toString();
	}

	private boolean isValid(final MagnoliaAssetMetadata metadata) {
		return metadata.getWidth() != null && metadata.getWidth() > 0L &&
				metadata.getHeight() != null && metadata.getHeight() > 0L;
	}
}
