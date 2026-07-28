package com.merkle.oss.magnolia.imaging.flexible.model;

import info.magnolia.dam.api.Asset;
import info.magnolia.dam.api.AssetDecorator;
import info.magnolia.dam.jcr.AbstractJcrItem;
import info.magnolia.dam.jcr.AssetNodeTypes;
import info.magnolia.jcr.util.PropertyUtil;

import java.util.Locale;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.merkle.oss.magnolia.imaging.flexible.util.LocalizedPropertyNameProvider;

import jakarta.inject.Inject;

public class LocalizedAsset extends AssetDecorator {
	private final String title;
	private final String description;
	private final String caption;

	public LocalizedAsset(
			final Asset decorated,
			final String title,
			final String description,
			final String caption
	) {
		super(decorated);
		this.title = title;
		this.description = description;
		this.caption = caption;
	}

	@Override
	public String getCaption() {
		return caption;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public static class Factory {
		private final LocalizedPropertyNameProvider localizedPropertyNameProvider;

		@Inject
		public Factory(final LocalizedPropertyNameProvider localizedPropertyNameProvider) {
			this.localizedPropertyNameProvider = localizedPropertyNameProvider;
		}

		public LocalizedAsset create(final Locale locale, final Asset asset) {
			return new LocalizedAsset(
					asset,
					getTitle(locale, asset),
					getDescription(locale, asset),
					getCaption(locale, asset)
			);
		}

		private String getCaption(final Locale locale, final Asset asset) {
			return getLocalizedStringFromNode(asset, locale, AssetNodeTypes.Asset.CAPTION).or(() ->
					Optional.of(asset.getCaption()).filter(StringUtils::isNotEmpty)
			).orElseGet(asset::getName);
		}

		private String getTitle(final Locale locale, final Asset asset) {
			return getLocalizedStringFromNode(asset, locale, AssetNodeTypes.Asset.TITLE).or(() ->
					Optional.of(asset.getTitle()).filter(StringUtils::isNotEmpty)
			).orElseGet(asset::getName);
		}

		private String getDescription(final Locale locale, final Asset asset) {
			return getLocalizedStringFromNode(asset, locale, AssetNodeTypes.Asset.DESCRIPTION).orElseGet(asset::getDescription);
		}

		private Optional<String> getLocalizedStringFromNode(final Asset asset, final Locale locale, final String key) {
			return Optional
					.of(asset)
					.filter(AbstractJcrItem.class::isInstance)
					.map(AbstractJcrItem.class::cast)
					.map(AbstractJcrItem::getNode)
					.map(assetNode ->
							PropertyUtil.getString(assetNode, localizedPropertyNameProvider.get(assetNode, key, locale))
					);
		}
	}
}
