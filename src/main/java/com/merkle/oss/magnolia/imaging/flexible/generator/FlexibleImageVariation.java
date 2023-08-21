package com.merkle.oss.magnolia.imaging.flexible.generator;

import com.merkle.oss.magnolia.imaging.flexible.bundle.ImageSize;
import info.magnolia.context.MgnlContext;
import info.magnolia.dam.api.Asset;
import info.magnolia.dam.jcr.DamConstants;
import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

public class FlexibleImageVariation {
	public static final Charset CHARSET = StandardCharsets.UTF_8;
	public static final String WIDTH = "width";
	public static final String HEIGHT = "height";
	public static final String SIZE = "size";
	public static final String CROP = "crop";

	public String createLink(final ImageSize imageSize, final Asset asset) {
		final String imageSizePart = getImageSizePart(imageSize);
		return MgnlContext.getContextPath()
				+ "/.imaging"
				+ "/" + FlexibleImageGenerator.GENERATOR_NAME
				+ "/" + imageSizePart
				+ "/" + encode(Paths.get(DamConstants.WORKSPACE, asset.getPath())).toString();
	}

	private Path encode(final Path path) {
		return StreamSupport
				.stream(
						Spliterators.spliteratorUnknownSize(path.iterator(), Spliterator.ORDERED),
						false
				)
				.map(Path::toString)
				.map(this::encode)
				.map(Paths::get)
				.reduce(Path.of(""), Path::resolve);
	}

	private String encode(final String string) {
		return URLEncoder.encode(string, CHARSET);
	}

	private String getImageSizePart(final ImageSize imageSize) {
		if (imageSize.getWidth() != null && imageSize.getHeight() == null) {
			return WIDTH + "/" + imageSize.getWidth();
		}
		if (imageSize.getWidth() == null && imageSize.getHeight() != null) {
			return HEIGHT + "/" + imageSize.getHeight();
		}
		if (imageSize.getWidth() != null && imageSize.getHeight() != null) {
			if (imageSize.isCrop() != null && imageSize.isCrop()) {
				return CROP + "/" + imageSize.getWidth() + "x" + imageSize.getHeight();
			}
			return SIZE + "/" + imageSize.getWidth() + "x" + imageSize.getHeight();
		}
		return StringUtils.EMPTY;
	}
}
