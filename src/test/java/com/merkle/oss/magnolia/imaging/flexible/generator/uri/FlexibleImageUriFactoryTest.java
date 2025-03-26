package com.merkle.oss.magnolia.imaging.flexible.generator.uri;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import info.magnolia.dam.api.Asset;
import info.magnolia.dam.api.ItemKey;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletContext;

import org.junit.jupiter.api.Test;

import com.merkle.oss.magnolia.imaging.flexible.model.DynamicImageParameter;
import com.merkle.oss.magnolia.imaging.flexible.model.FlexibleParameter;

class FlexibleImageUriFactoryTest {

	@Test
	void create() throws URISyntaxException {
		final Asset asset = mock(Asset.class);
		doReturn(ItemKey.from("jcr:b3ee7444-4830-4454-abbb-20fc35387032")).when(asset).getItemKey();
		doReturn("someImage.jpg").when(asset).getFileName();
		final FlexibleParameter parameter = new FlexibleParameter(new DynamicImageParameter(true), "16:9", 100, "1733184000000", asset);

		final ServletContext servletContext = mock(ServletContext.class);
		doReturn("/author").when(servletContext).getContextPath();

		assertEquals(
				new URI("/author/.imaging/flex/jcr:b3ee7444-4830-4454-abbb-20fc35387032/crop/true/ratio/16:9/version/1733184000000/width/100/someImage.jpg"),
				new FlexibleImageUriFactory(() -> servletContext).create(parameter)
		);
	}

	@Test
	void createNoContext() throws URISyntaxException {
		final Asset asset = mock(Asset.class);
		doReturn(ItemKey.from("jcr:b3ee7444-4830-4454-abbb-20fc35387032")).when(asset).getItemKey();
		doReturn("someImage.jpg").when(asset).getFileName();
		final FlexibleParameter parameter = new FlexibleParameter(new DynamicImageParameter(true), "16:9", 100,  "1733184000000", asset);

		final ServletContext servletContext = mock(ServletContext.class);
		doReturn("/").when(servletContext).getContextPath();

		assertEquals(
				new URI("/.imaging/flex/jcr:b3ee7444-4830-4454-abbb-20fc35387032/crop/true/ratio/16:9/version/1733184000000/width/100/someImage.jpg"),
				new FlexibleImageUriFactory(() -> servletContext).create(parameter)
		);
	}
}
