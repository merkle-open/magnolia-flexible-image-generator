package com.merkle.oss.magnolia.imaging.flexible.generator;

import com.merkle.oss.magnolia.imaging.flexible.model.DynamicImageParameter;
import com.merkle.oss.magnolia.imaging.flexible.model.FlexibleParameter;
import info.magnolia.dam.api.Asset;
import info.magnolia.dam.api.ItemKey;
import info.magnolia.imaging.ImageGenerator;
import info.magnolia.imaging.ParameterProvider;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.test.mock.jcr.MockNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.jcr.Property;
import javax.jcr.RepositoryException;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class FlexibleParameterCachingStrategyTest {
	private final ImageGenerator<ParameterProvider<FlexibleParameter>> generator = mock(ImageGenerator.class);
	private final Asset asset = mock(Asset.class);
	private FlexibleParameterCachingStrategy flexibleParameterCachingStrategy;
	private Property cachedBinary;
	private Calendar cachedBinaryCalendar;
	private Calendar assetCalendar;

	@BeforeEach
	void setUp() throws RepositoryException {
		doReturn("flex").when(generator).getName();
		doReturn(ItemKey.from("jcr:b3ee7444-4830-4454-abbb-20fc35387032")).when(asset).getItemKey();

		cachedBinary = mock(Property.class);
		final MockNode cachedBinaryParent = new MockNode();
		doReturn(cachedBinaryParent).when(cachedBinary).getParent();
		cachedBinaryCalendar = Calendar.getInstance();
		cachedBinaryParent.setProperty(NodeTypes.LastModified.LAST_MODIFIED, cachedBinaryCalendar);
		assetCalendar = Calendar.getInstance();
		doReturn(assetCalendar).when(asset).getLastModified();

		flexibleParameterCachingStrategy = new FlexibleParameterCachingStrategy();
	}

	@Test
	void getCachePath_noDynamicImageParameter() {
		assertEquals(
				"/flex/jcr%3Ab3ee7444-4830-4454-abbb-20fc35387032/560/316/0",
				flexibleParameterCachingStrategy.getCachePath(generator, () -> new FlexibleParameter(null, 560, 316, asset))
		);
	}

	@Test
	void getCachePath_dynamicImageParameter() {
		assertEquals(
				"/flex/jcr%3Ab3ee7444-4830-4454-abbb-20fc35387032/560/316/1268",
				flexibleParameterCachingStrategy.getCachePath(generator, () -> new FlexibleParameter(new DynamicImageParameter(false), 560, 316, asset))
		);
	}

	@Test
	void shouldRegenerate_assetModifiedAfterCachedBinary_shouldRegenerate() throws RepositoryException {
		cachedBinaryCalendar.set(2023, Calendar.OCTOBER, 26, 14, 30);
		assetCalendar.set(2023, Calendar.OCTOBER, 26, 14, 31);
		assertTrue(flexibleParameterCachingStrategy.shouldRegenerate(cachedBinary, () -> new FlexibleParameter(null, 560, 316, asset)));
	}

	@Test
	void shouldRegenerate_assetModifiedBeforeCachedBinary_shouldNotRegenerate() throws RepositoryException {
		cachedBinaryCalendar.set(2023, Calendar.OCTOBER, 26, 14, 30);
		assetCalendar.set(2023, Calendar.OCTOBER, 26, 14, 29);
		assertFalse(flexibleParameterCachingStrategy.shouldRegenerate(cachedBinary, () -> new FlexibleParameter(null, 560, 316, asset)));
	}
}