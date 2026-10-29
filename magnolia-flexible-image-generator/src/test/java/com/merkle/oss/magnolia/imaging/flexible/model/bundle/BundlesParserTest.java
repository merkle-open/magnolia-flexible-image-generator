package com.merkle.oss.magnolia.imaging.flexible.model.bundle;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BundlesParserTest {
	private String relativePath;

	@BeforeEach
	void setUp() {
		final String absolutePath = this.getClass().getResource("bundlesConfig.json").getPath();
		final String resourcesRoot = getClass().getClassLoader().getResource("").getPath();
		this.relativePath = absolutePath.replaceFirst(resourcesRoot, "");
	}

	@Test
	void parse() {
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
						),
						new Bundle("no-custom-renditions", null,
								List.of(
										new Bundle.ImageSize("560w", 560, "1:1"),
										new Bundle.ImageSize("1120w", 1120, null),
										new Bundle.ImageSize("2208w", 2208, null)
								),
								null
						),
						new Bundle("no-image-sizes", null,
								null,
								List.of(
										new Bundle.ImageSize("fallbackImage", 560, null),
										new Bundle.ImageSize("previewImage", 16, null)
								)
						)
				),
				new BundlesParser().parse(relativePath).collect(Collectors.toSet())
		);
	}

	@Test
	void list_getterNeverNull() {
		new BundlesParser().parse(relativePath).forEach(bundle -> {
			assertNotNull(bundle.getImageSizes());
			assertNotNull(bundle.getCustomRenditions());
		});
	}
}
