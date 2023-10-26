package com.merkle.oss.magnolia.imaging.flexible.model.bundle;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BundlesParserTest {

	@Test
	void parse() {
		final String absolutePath = this.getClass().getResource("bundlesConfig.json").getPath();
		final String resourcesRoot = getClass().getClassLoader().getResource("").getPath();
		final String relativePath = absolutePath.replaceFirst(resourcesRoot, "");

		assertEquals(
				List.of(
						new Bundle("1x1", 1.0,
								List.of(
										new Bundle.ImageSize("560w", 560),
										new Bundle.ImageSize("1000w", 1000),
										new Bundle.ImageSize("2000w", 2000)
								),
								List.of(
										new Bundle.ImageSize("fallbackImage", 560),
										new Bundle.ImageSize("previewImage", 10)
								)
						),
						new Bundle("16x9", 1.77777,
								List.of(
										new Bundle.ImageSize("560w", 560),
										new Bundle.ImageSize("1120w", 1120),
										new Bundle.ImageSize("2208w", 2208)
								),
								List.of(
										new Bundle.ImageSize("fallbackImage", 560),
										new Bundle.ImageSize("previewImage", 16)
								)
						)
				),
				new BundlesParser().parse(relativePath).collect(Collectors.toList())
		);
	}
}