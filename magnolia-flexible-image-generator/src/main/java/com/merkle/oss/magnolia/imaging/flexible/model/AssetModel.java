package com.merkle.oss.magnolia.imaging.flexible.model;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import jakarta.annotation.Nullable;


public class AssetModel {
    private final String id;
    private final String name;
    private final String alt;
    private final String title;
    private final String description;
    private final String src;
    private final String version;
    @Nullable
    private final String ratio;
    @Nullable
    private final String mimeType;

    public AssetModel(
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
        this.id = id;
        this.name = name;
        this.alt = alt;
        this.title = title;
        this.description = description;
        this.src = src;
        this.mimeType = mimeType;
        this.version = version;
        this.ratio = ratio;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAlt() {
        return alt;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSrc() {
        return src;
    }

    public String getVersion() {
        return version;
    }

    public Optional<String> getRatio() {
        return Optional.ofNullable(ratio);
    }

    public Optional<String> getMimeType() {
        return Optional.ofNullable(mimeType);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AssetModel that)) {
            return false;
        }
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(alt, that.alt) && Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(src, that.src) && Objects.equals(version, that.version) && Objects.equals(ratio, that.ratio) && Objects.equals(mimeType, that.mimeType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, alt, title, description, src, version, ratio, mimeType);
    }

    @Override
    public String toString() {
        return "AssetModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", alt='" + alt + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", src='" + src + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", version='" + version + '\'' +
                ", ratio='" + ratio + '\'' +
                '}';
    }

    public interface Factory {
        /**
         * @return an assetModel if the assedId matches (e.g. "jcr:..." for dam) or empty
         */
        Optional<? extends AssetModel> create(Locale locale, String assetId);
    }
}
