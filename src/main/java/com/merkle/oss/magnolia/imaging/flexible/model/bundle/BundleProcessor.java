package com.merkle.oss.magnolia.imaging.flexible.model.bundle;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

public class BundleProcessor {

	ProcessedBundle process(final Bundle bundle) {
		return new ProcessedBundle(
				bundle.getName(),
				processImageSizes(bundle.getImageSizes(), bundle.getRatio()),
				processImageSizes(bundle.getCustomRenditions(), bundle.getRatio())
		);
	}

	private List<ProcessedBundle.ImageSize> processImageSizes(final List<Bundle.ImageSize> imageSizes, final double ratio) {
		return imageSizes.stream()
				.map(imageSize ->
						new ProcessedBundle.ImageSize(
								imageSize.getWidth(),
								calculateHeight(imageSize.getWidth(), ratio),
								imageSize.getId()
						)
				)
				.collect(Collectors.toList());
	}

	private int calculateHeight(final int width, final double ratio) {
		return BigDecimal.valueOf(width / ratio).setScale(0, RoundingMode.UP).intValue();
	}
}
