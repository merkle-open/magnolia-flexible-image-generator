package com.merkle.oss.magnolia.imaging.flexible.model.bundle;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BundleProcessorTest {

	@Test
	void process() {
		assertEquals(
				new ProcessedBundle(
						"someBundle",
						List.of(
								new ProcessedBundle.ImageSize("1:1", 560, "560w"),
								new ProcessedBundle.ImageSize("16:9", 1120, "1120w"),
								new ProcessedBundle.ImageSize("16:9", 2208, "2208w")
						),
						List.of(
								new ProcessedBundle.ImageSize("16:9", 560, "fallbackImage"),
								new ProcessedBundle.ImageSize("16:9", 16, "previewImage")
						)
				),
				new BundleProcessor(new RatioParser()).process(new Bundle("someBundle", "16:9",
						List.of(
								new Bundle.ImageSize("560w", 560, "1:1"),
								new Bundle.ImageSize("1120w", 1120, null),
								new Bundle.ImageSize("2208w", 2208, null)
						),
						List.of(
								new Bundle.ImageSize("fallbackImage", 560, null),
								new Bundle.ImageSize("previewImage", 16, null)
						)
				))
		);
	}
}