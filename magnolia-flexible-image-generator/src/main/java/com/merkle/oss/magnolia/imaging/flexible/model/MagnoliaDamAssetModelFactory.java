package com.merkle.oss.magnolia.imaging.flexible.model;

import info.magnolia.dam.api.Asset;
import info.magnolia.dam.api.AssetProviderRegistry;
import info.magnolia.dam.api.ItemKey;
import info.magnolia.dam.templating.functions.DamTemplatingFunctions;

import java.time.Instant;
import java.util.Calendar;
import java.util.Locale;
import java.util.Optional;

import org.jspecify.annotations.Nullable;

import jakarta.inject.Inject;

public class MagnoliaDamAssetModelFactory implements AssetModel.Factory {
    private static final String FALLBACK_VERSION = "1";

    private final DamTemplatingFunctions damTemplatingFunctions;
    private final AssetProviderRegistry assetProviderRegistry;
    private final LocalizedAsset.Factory localizedAssetFactory;
    private final AssetRatioProvider assetRatioProvider;

    @Inject
    public MagnoliaDamAssetModelFactory(
            final DamTemplatingFunctions damTemplatingFunctions,
            final AssetProviderRegistry assetProviderRegistry,
            final LocalizedAsset.Factory localizedAssetFactory,
            final AssetRatioProvider assetRatioProvider
    ) {
        this.damTemplatingFunctions = damTemplatingFunctions;
        this.assetProviderRegistry = assetProviderRegistry;
        this.localizedAssetFactory = localizedAssetFactory;
        this.assetRatioProvider = assetRatioProvider;
    }

    @Override
    public Optional<MagnoliaAssetModel> create(final Locale locale, final String assetId) {
        return Optional
                .of(assetId)
                .filter(this::isValid)
                .map(damTemplatingFunctions::getAsset)
                .map(asset -> localizedAssetFactory.create(locale, asset))
                .map(this::create);
    }

    private boolean isValid(final String assetId) {
        return ItemKey.isValid(assetId) && hasAssetProviderFor(ItemKey.from(assetId));
    }

    private boolean hasAssetProviderFor(final ItemKey itemKey) {
        try {
            assetProviderRegistry.getProviderFor(itemKey);
            return true;
        } catch (AssetProviderRegistry.NoSuchAssetProviderException e) {
            return false;
        }
    }

    private MagnoliaAssetModel create(final LocalizedAsset asset) {
        return new MagnoliaAssetModel(
                asset,
                asset.getItemKey().asString(),
                asset.getName(),
                asset.getCaption(),
                asset.getTitle(),
                asset.getDescription(),
                asset.getLink(),
                assetRatioProvider.get(asset).orElse(null),
                getModificationTime(asset).map(Instant::getEpochSecond).map(String::valueOf).orElse(FALLBACK_VERSION),
                asset.getMimeType()
        );
    }

    private Optional<Instant> getModificationTime(final Asset asset) {
        return Optional
                .ofNullable(asset.getLastModified())
                .or(() -> Optional.ofNullable(asset.getCreated()))
                .map(Calendar::toInstant);
    }

    public static class MagnoliaAssetModel extends AssetModel {
        private final transient Asset asset;

        public MagnoliaAssetModel(
                final Asset asset,
                final String id,
                final String name,
                final String alt,
                final String title,
                final String description,
                final String src,
                final String version,
                @Nullable final String ratio,
                @Nullable final String mimeType
        ) {
            super(id, name, alt, title, description, src, ratio, version, mimeType);
            this.asset = asset;
        }

        public Asset getAsset() {
            return asset;
        }
    }
}
