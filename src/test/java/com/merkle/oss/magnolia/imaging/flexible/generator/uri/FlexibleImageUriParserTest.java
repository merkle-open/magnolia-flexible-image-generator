package com.merkle.oss.magnolia.imaging.flexible.generator.uri;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import info.magnolia.dam.api.Asset;
import info.magnolia.dam.templating.functions.DamTemplatingFunctions;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.merkle.oss.magnolia.imaging.flexible.model.AssetRatioProvider;
import com.merkle.oss.magnolia.imaging.flexible.model.DynamicImageParameter;
import com.merkle.oss.magnolia.imaging.flexible.model.FlexibleParameter;
import com.merkle.oss.magnolia.imaging.flexible.model.bundle.ProcessedBundle;
import com.merkle.oss.magnolia.imaging.flexible.model.bundle.ProcessedBundlesProvider;

class FlexibleImageUriParserTest {
	private final Asset asset = mock(Asset.class);
	private FlexibleImageUriParser flexibleImageUriParser;

	@BeforeEach
	void setUp() {
		final DamTemplatingFunctions damTemplatingFunctions = mock(DamTemplatingFunctions.class);
		doReturn(asset).when(damTemplatingFunctions).getAsset("jcr:b3ee7444-4830-4454-abbb-20fc35387032");

		final ProcessedBundlesProvider processedBundlesProvider = mock(ProcessedBundlesProvider.class);
		final ProcessedBundle.ImageSize imageSize = mock(ProcessedBundle.ImageSize.class);
		doReturn(560).when(imageSize).getWidth();
		doReturn(Optional.of("16:9")).when(imageSize).getRatio();
		final ProcessedBundle processedBundle = mock(ProcessedBundle.class);
		doReturn(List.of(imageSize)).when(processedBundle).getImageSizes();
		doReturn(Collections.emptyList()).when(processedBundle).getCustomRenditions();
		doReturn(List.of(processedBundle)).when(processedBundlesProvider).get();

		flexibleImageUriParser = new FlexibleImageUriParser(damTemplatingFunctions, processedBundlesProvider, new FlexibleParameter.Factory(new DynamicImageParameter.Factory()), new AssetRatioProvider());
	}

	@Test
	void parse_valid() {
		assertEquals(
				Optional.of(new FlexibleParameter(new DynamicImageParameter(true), "16:9", 560, "1733184000000", asset)),
				flexibleImageUriParser.parse(createRequest("/author/.imaging/flex/jcr:b3ee7444-4830-4454-abbb-20fc35387032/crop/true/ratio/16:9/width/560/version/1733184000000/dummy1-1600x900.jpg"))
		);
	}

	@Test
	void parse_invalid() {
		assertEquals(
				Optional.empty(),
				flexibleImageUriParser.parse(createRequest("/author/.imaging/flex/jcr:b3ee7444-4830-4454-abbb-20fc35387032/crop/true/ratio/16:10/width/560/dummy1-1600x900.jpg"))
		);
	}

	private HttpServletRequest createRequest(final String uri) {
		final HttpServletRequest request = mock(HttpServletRequest.class);
		doReturn(uri).when(request).getRequestURI();
		return request;
	}
}
