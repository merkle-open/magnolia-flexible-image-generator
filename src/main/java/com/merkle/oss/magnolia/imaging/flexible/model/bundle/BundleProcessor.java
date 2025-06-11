package com.merkle.oss.magnolia.imaging.flexible.model.bundle;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;

public class BundleProcessor {
	private final RatioParser ratioParser;

	@Inject
	public BundleProcessor(final RatioParser ratioParser) {
		this.ratioParser = ratioParser;
	}

	ProcessedBundle process(final Bundle bundle) {
		return new ProcessedBundle(
				bundle.getName(),
				processImageSizes(bundle.getName(), bundle.getImageSizes(), bundle.getRatio().orElse(null)),
				processImageSizes(bundle.getName(), bundle.getCustomRenditions(), bundle.getRatio().orElse(null))
		);
	}

	private List<ProcessedBundle.ImageSize> processImageSizes(
			final String bundleName,
			final List<Bundle.ImageSize> imageSizes,
			@Nullable final String bundleRatio
	) {
		return imageSizes.stream()
				.map(imageSize ->
						new ProcessedBundle.ImageSize(
								validateRatioOrThrow(bundleName, imageSize.getRatio().orElse(bundleRatio)),
								imageSize.getWidth(),
								imageSize.getId()
						)
				)
				.collect(Collectors.toList());
	}

	private String validateRatioOrThrow(final String bundleName, @Nullable final String ratio) {
		if(ratio != null) {
			try {
				ratioParser.parse(ratio);
			} catch (Exception e) {
				throw new IllegalArgumentException("invalid ratio "+ratio+" in bundle "+bundleName, e);
			};
		}
		return ratio;
	}
}
