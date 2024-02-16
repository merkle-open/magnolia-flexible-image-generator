package com.merkle.oss.magnolia.imaging.flexible.generator.operation;

import info.magnolia.dam.api.Asset;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

public class AssetUtil {

	public boolean isAnimated(final Asset asset) throws IOException {
		try (ImageInputStream iis = ImageIO.createImageInputStream(asset.getContentStream())) {
			final ImageReader imageReader = ImageIO.getImageReadersByMIMEType(asset.getMimeType()).next();
			imageReader.setInput(iis);

			final int numImages = imageReader.getNumImages(true);
			return numImages > 1;
		}
	}

	public boolean isImage(final Asset asset) {
		return ImageIO.getImageReadersByMIMEType(asset.getMimeType()).hasNext();
	}
}
