package com.merkle.oss.magnolia.imaging.flexible.generator.uri;

import com.merkle.oss.magnolia.imaging.flexible.model.DynamicImageParameter;
import com.merkle.oss.magnolia.imaging.flexible.model.FlexibleParameter;
import info.magnolia.context.MgnlContext;
import info.magnolia.dam.api.Asset;
import info.magnolia.dam.api.ItemKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FlexibleImageUriFactoryTest {

	private Asset asset;

	@BeforeEach
	void setUp() throws IOException {
		asset = mock(Asset.class);
		final Calendar lastModified = new Calendar.Builder()
				.setDate(2024, 11, 3)
				.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC))
				.build();
		when(asset.getLastModified()).thenReturn(lastModified);
	}

	@Test
	void create() throws URISyntaxException {
		doReturn(ItemKey.from("jcr:b3ee7444-4830-4454-abbb-20fc35387032")).when(asset).getItemKey();
		doReturn("someImage.jpg").when(asset).getFileName();
		final FlexibleParameter parameter = new FlexibleParameter(new DynamicImageParameter(true), "16:9", 100,  asset);

		try (MockedStatic<MgnlContext> mgnlContext = Mockito.mockStatic(MgnlContext.class)) {
			mgnlContext.when(MgnlContext::getContextPath).thenReturn("/author");

			assertEquals(
					new URI("/author/.imaging/flex/jcr:b3ee7444-4830-4454-abbb-20fc35387032/crop/true/ratio/16:9/v/1733184000000/width/100/someImage.jpg"),
					new FlexibleImageUriFactory().create(parameter)
			);
		}
	}

	@Test
	void createNoContext() throws URISyntaxException {
		doReturn(ItemKey.from("jcr:b3ee7444-4830-4454-abbb-20fc35387032")).when(asset).getItemKey();
		doReturn("someImage.jpg").when(asset).getFileName();
		final FlexibleParameter parameter = new FlexibleParameter(new DynamicImageParameter(true), "16:9", 100,  asset);

		try (MockedStatic<MgnlContext> mgnlContext = Mockito.mockStatic(MgnlContext.class)) {
			mgnlContext.when(MgnlContext::getContextPath).thenReturn("/");

			assertEquals(
					new URI("/.imaging/flex/jcr:b3ee7444-4830-4454-abbb-20fc35387032/crop/true/ratio/16:9/v/1733184000000/width/100/someImage.jpg"),
					new FlexibleImageUriFactory().create(parameter)
			);
		}
	}
}