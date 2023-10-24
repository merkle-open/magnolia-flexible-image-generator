package com.merkle.oss.magnolia.imaging.flexible.model;

import com.merkle.oss.magnolia.imaging.flexible.util.LocalizedPropertyNameProvider;
import info.magnolia.context.MgnlContext;
import info.magnolia.dam.api.Asset;
import info.magnolia.dam.api.AssetDecorator;
import info.magnolia.dam.jcr.AssetNodeTypes;
import info.magnolia.dam.jcr.DamConstants;
import info.magnolia.jcr.util.PropertyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.lang.invoke.MethodHandles;
import java.util.Locale;
import java.util.Optional;

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
		private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
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
			try {
				final Session damSession = MgnlContext.getJCRSession(DamConstants.WORKSPACE);
				final Node assetNode = damSession.getNode(asset.getPath());
				return Optional.ofNullable(PropertyUtil.getString(assetNode, localizedPropertyNameProvider.get(assetNode, key, locale)));
			} catch (RepositoryException e) {
				LOG.error("failed to get localized string from node locale: " + locale + " key " + key + " asset: " + asset.getPath(), e);
				return Optional.empty();
			}
		}
	}
}
