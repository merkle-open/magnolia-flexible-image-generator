package com.merkle.oss.magnolia.imaging.flexible.generator.uri;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import info.magnolia.dam.api.Asset;
import info.magnolia.dam.templating.functions.DamTemplatingFunctions;
import info.magnolia.test.TestMagnoliaConfigurationProperties;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.merkle.oss.magnolia.imaging.flexible.model.AssetRatioProvider;
import com.merkle.oss.magnolia.imaging.flexible.model.DynamicImageParameter;
import com.merkle.oss.magnolia.imaging.flexible.model.FlexibleParameter;
import com.merkle.oss.magnolia.imaging.flexible.model.bundle.ProcessedBundle;
import com.merkle.oss.magnolia.imaging.flexible.model.bundle.ProcessedBundlesProvider;

import jakarta.servlet.http.HttpServletRequest;

class HashedFlexibleImageUriParserTest {
	private final Asset asset = mock(Asset.class);
	private FlexibleImageUriParser flexibleImageUriParser;

	private DamTemplatingFunctions damTemplatingFunctions;

	@BeforeEach
	void setUp() throws IOException {
		damTemplatingFunctions = mock(DamTemplatingFunctions.class);
		doReturn(asset).when(damTemplatingFunctions).getAsset("jcr:b3ee7444-4830-4454-abbb-20fc35387032");

		final ProcessedBundlesProvider processedBundlesProvider = mock(ProcessedBundlesProvider.class);
		final ProcessedBundle.ImageSize imageSize = mock(ProcessedBundle.ImageSize.class);
		doReturn(100).when(imageSize).getWidth();
		doReturn(Optional.of("16:9")).when(imageSize).getRatio();
		final ProcessedBundle processedBundle = mock(ProcessedBundle.class);
		doReturn(List.of(imageSize)).when(processedBundle).getImageSizes();
		doReturn(Collections.emptyList()).when(processedBundle).getCustomRenditions();
		doReturn(List.of(processedBundle)).when(processedBundlesProvider).get();


		flexibleImageUriParser = new HashedFlexibleImageUriParser(
				damTemplatingFunctions,
				processedBundlesProvider,
				new FlexibleParameter.Factory(new DynamicImageParameter.Factory()),
				new ImageDigest(new TestMagnoliaConfigurationProperties(ImageDigest.SALT_PROPERTY_KEY, "someSalt")),
				new AssetRatioProvider()
		);
	}

	@Test
	void parse_valid() {
		assertEquals(
				Optional.of(new FlexibleParameter(new DynamicImageParameter(true), "16:9", 100, "1733184000000", asset)),
				flexibleImageUriParser.parse("/author/.imaging/flex/jcr:b3ee7444-4830-4454-abbb-20fc35387032/crop/true/hash/fd3dbba63701ecfa3ddbe850741b08b1/ratio/16:9/width/100/version/1733184000000/someImage.jpg")
		);
	}

	@Test
	void parse_invalid_hash() {
		assertEquals(
				Optional.empty(),
				flexibleImageUriParser.parse("/author/.imaging/flex/jcr:b3ee7444-4830-4454-abbb-20fc35387032/crop/true/hash/INVALID_HASH/ratio/16:9/width/100/version/1733184000000/someImage.jpg")
		);
		verify(damTemplatingFunctions, never()).getAsset(anyString());
	}

	@Test
	void parse_invalid_param() {
		assertEquals(
				Optional.empty(),
				flexibleImageUriParser.parse("/author/.imaging/flex/jcr:b3ee7444-4830-4454-abbb-20fc35387032/crop/INVALID/hash/1952df994c77d7b92999fef87833207f/ratio/16:9/width/100/version/1733184000000/someImage.jpg")
		);
	}
}
