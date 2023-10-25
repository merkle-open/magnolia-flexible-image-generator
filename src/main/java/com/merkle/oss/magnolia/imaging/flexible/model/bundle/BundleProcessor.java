package com.merkle.oss.magnolia.imaging.flexible.model.bundle;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BundleProcessor {

	ProcessedBundle process(final Bundle bundle) {
		final boolean crop = Optional.ofNullable(bundle.getCrop()).orElse(true);
		return new ProcessedBundle(
				bundle.getName(),
				processImageSizes(bundle.getImageSizes(), bundle.getRatio(), crop),
				processImageSizes(bundle.getCustomRenditions(), bundle.getRatio(), crop)
		);
	}

	private List<ProcessedBundle.ImageSize> processImageSizes(final List<Bundle.ImageSize> imageSizes, final double ratio, final boolean crop) {
		return imageSizes.stream()
				.map(imageSize ->
						new ProcessedBundle.ImageSize(
								imageSize.getWidth(),
								calculateHeight(imageSize.getWidth(), ratio),
								imageSize.getId(),
								crop
						)
				)
				.collect(Collectors.toList());
	}

	private int calculateHeight(final int width, final double ratio) {
		return BigDecimal.valueOf(width / ratio).setScale(0, RoundingMode.UP).intValue();
	}
}
