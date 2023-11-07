package com.merkle.oss.magnolia.imaging.flexible.model.bundle;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BundlesParserTest {

	@Test
	void parse() {
		final String absolutePath = this.getClass().getResource("bundlesConfig.json").getPath();
		final String resourcesRoot = getClass().getClassLoader().getResource("").getPath();
		final String relativePath = absolutePath.replaceFirst(resourcesRoot, "");

		assertEquals(
				Set.of(
						new Bundle("1x1", "1:1",
								List.of(
										new Bundle.ImageSize("560w", 560, null),
										new Bundle.ImageSize("1000w", 1000, null),
										new Bundle.ImageSize("2000w", 2000, null)
								),
								List.of(
										new Bundle.ImageSize("fallbackImage", 560, null),
										new Bundle.ImageSize("previewImage", 10, null)
								)
						),
						new Bundle("16x9", "16:9",
								List.of(
										new Bundle.ImageSize("560w", 560, null),
										new Bundle.ImageSize("1120w", 1120, null),
										new Bundle.ImageSize("2208w", 2208, null)
								),
								List.of(
										new Bundle.ImageSize("fallbackImage", 560, null),
										new Bundle.ImageSize("previewImage", 16, null)
								)
						),
						new Bundle("stage", null,
								List.of(
										new Bundle.ImageSize("560w", 560, "1:1"),
										new Bundle.ImageSize("1120w", 1120, null),
										new Bundle.ImageSize("2208w", 2208, null)
								),
								List.of(
										new Bundle.ImageSize("fallbackImage", 560, null),
										new Bundle.ImageSize("previewImage", 16, null)
								)
						)
				),
				new BundlesParser().parse(relativePath).collect(Collectors.toSet())
		);
	}
}