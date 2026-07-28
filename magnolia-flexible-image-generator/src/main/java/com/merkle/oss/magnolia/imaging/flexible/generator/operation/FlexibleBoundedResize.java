package com.merkle.oss.magnolia.imaging.flexible.generator.operation;

import info.magnolia.imaging.ImagingException;
import info.magnolia.imaging.ParameterProvider;
import info.magnolia.imaging.operations.ImageOperation;
import info.magnolia.imaging.operations.cropresize.BoundedResize;
import info.magnolia.imaging.operations.cropresize.resizers.MultiStepResizer;

import java.awt.image.BufferedImage;

import com.merkle.oss.magnolia.imaging.flexible.model.FlexibleParameter;

import jakarta.inject.Inject;

public class FlexibleBoundedResize implements ImageOperation<ParameterProvider<FlexibleParameter>> {
	private final HeightCalculator heightCalculator;

	@Inject
	public FlexibleBoundedResize(final HeightCalculator heightCalculator) {
		this.heightCalculator = heightCalculator;
	}

	@Override
	public BufferedImage apply(final BufferedImage source, final ParameterProvider<FlexibleParameter> params) throws ImagingException {
		final FlexibleParameter parameter = params.getParameter();
		final BoundedResize boundedResize = new BoundedResize();
		boundedResize.setResizer(new MultiStepResizer());
		boundedResize.setMaxWidth(parameter.getWidth());
		boundedResize.setMaxHeight(heightCalculator.calculateHeight(parameter));
		return boundedResize.apply(source, params);
	}
}
