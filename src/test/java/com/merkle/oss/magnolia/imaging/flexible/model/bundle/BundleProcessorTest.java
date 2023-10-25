package com.merkle.oss.magnolia.imaging.flexible.model.bundle;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BundleProcessorTest {

	@Test
	void process() {
		assertEquals(
				new ProcessedBundle(
						"16x9",
						List.of(
								new ProcessedBundle.ImageSize(560, 316, "560w", false),
								new ProcessedBundle.ImageSize(1120, 631, "1120w", false),
								new ProcessedBundle.ImageSize(2208, 1243, "2208w", false)
						),
						List.of(
								new ProcessedBundle.ImageSize(560, 316, "fallbackImage", false),
								new ProcessedBundle.ImageSize(16, 10, "previewImage", false)
						)
				),
				new BundleProcessor().process(new Bundle("16x9", 1.77777, false,
						List.of(
								new Bundle.ImageSize("560w", 560),
								new Bundle.ImageSize("1120w", 1120),
								new Bundle.ImageSize("2208w", 2208)
						),
						List.of(
								new Bundle.ImageSize("fallbackImage", 560),
								new Bundle.ImageSize("previewImage", 16)
						)
				))
		);
	}
}