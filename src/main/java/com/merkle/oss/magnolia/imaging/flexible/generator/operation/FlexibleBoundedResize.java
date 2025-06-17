package com.merkle.oss.magnolia.imaging.flexible.generator.operation;

import info.magnolia.imaging.ImagingException;
import info.magnolia.imaging.ParameterProvider;
import info.magnolia.imaging.operations.ImageOperation;
import info.magnolia.imaging.operations.cropresize.BoundedResize;
import info.magnolia.imaging.operations.cropresize.resizers.MultiStepResizer;

import java.awt.image.BufferedImage;

import javax.inject.Inject;

import com.merkle.oss.magnolia.imaging.flexible.model.FlexibleParameter;

public class FlexibleBoundedResize implements ImageOperation<ParameterProvider<FlexibleParameter>> {
	private final AssetUtil assetUtil;
	private final HeightCalculator heightCalculator;

	@Inject
	public FlexibleBoundedResize(
			final AssetUtil assetUtil,
			final HeightCalculator heightCalculator
	) {
		this.assetUtil = assetUtil;
		this.heightCalculator = heightCalculator;
	}

	@Override
	public BufferedImage apply(final BufferedImage source, final ParameterProvider<FlexibleParameter> params) throws ImagingException {
		final FlexibleParameter parameter = params.getParameter();
		if (!isSupported(parameter)) {
			throw new ImagingException(getClass() + " doesn't support " + parameter);
		}
		final BoundedResize boundedResize = new BoundedResize();
		boundedResize.setResizer(new MultiStepResizer());
		boundedResize.setMaxWidth(parameter.getWidth());
		boundedResize.setMaxHeight(heightCalculator.calculateHeight(parameter));
		return boundedResize.apply(source, params);
	}

	public boolean isSupported(final FlexibleParameter parameter) {
		try {
			return !assetUtil.isAnimated(parameter);
		} catch (Exception e) {
			return false;
		}
	}
}
