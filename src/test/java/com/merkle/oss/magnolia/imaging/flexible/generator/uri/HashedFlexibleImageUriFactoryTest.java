package com.merkle.oss.magnolia.imaging.flexible.generator.uri;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import info.magnolia.context.MgnlContext;
import info.magnolia.dam.api.Asset;
import info.magnolia.dam.api.ItemKey;
import info.magnolia.test.TestMagnoliaConfigurationProperties;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.merkle.oss.magnolia.imaging.flexible.model.DynamicImageParameter;
import com.merkle.oss.magnolia.imaging.flexible.model.FlexibleParameter;

class HashedFlexibleImageUriFactoryTest {
	private HashedFlexibleImageUriFactory uriFactory;

	@BeforeEach
	void setUp() throws IOException {
		uriFactory = new HashedFlexibleImageUriFactory(
				new ImageDigest(new TestMagnoliaConfigurationProperties(ImageDigest.SALT_PROPERTY_KEY, "someSalt"))
		);
	}

	@Test
	void create() throws URISyntaxException {
		final Asset asset = mock(Asset.class);

		doReturn(ItemKey.from("jcr:b3ee7444-4830-4454-abbb-20fc35387032")).when(asset).getItemKey();
		doReturn("someImage.jpg").when(asset).getFileName();
		final FlexibleParameter parameter = new FlexibleParameter(new DynamicImageParameter(true), "16:9", 100,  "1733184000000", asset);

		try (MockedStatic<MgnlContext> mgnlContext = Mockito.mockStatic(MgnlContext.class)) {
			mgnlContext.when(MgnlContext::getContextPath).thenReturn("/author");

			assertEquals(
					new URI("/author/.imaging/flex/jcr:b3ee7444-4830-4454-abbb-20fc35387032/crop/true/hash/466cea7f608f4b1b59fee12ff43f7e68/ratio/16:9/version/1733184000000/width/100/someImage.jpg"),
					uriFactory.create(parameter)
			);
		}
	}
}