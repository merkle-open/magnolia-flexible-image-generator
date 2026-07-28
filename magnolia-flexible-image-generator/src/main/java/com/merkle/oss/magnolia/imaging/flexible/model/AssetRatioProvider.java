package com.merkle.oss.magnolia.imaging.flexible.model;

import info.magnolia.dam.api.Asset;
import info.magnolia.dam.api.metadata.MagnoliaAssetMetadata;

import java.util.Optional;

public class AssetRatioProvider {

	public Optional<String> get(final Asset asset) {
		return Optional
				.ofNullable(asset.getMetadata(MagnoliaAssetMetadata.class))
				.filter(this::isValid)
				.map(metadata -> metadata.getWidth().toString() + ":" + metadata.getHeight().toString());
	}

	private boolean isValid(final MagnoliaAssetMetadata metadata) {
		return metadata.getWidth() != null && metadata.getWidth() > 0L &&
				metadata.getHeight() != null && metadata.getHeight() > 0L;
	}
}
