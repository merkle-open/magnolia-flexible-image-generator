package com.merkle.oss.magnolia.imaging.flexible.generator.operation;

import com.merkle.oss.magnolia.imaging.flexible.model.FlexibleParameter;
import info.magnolia.imaging.ImagingException;
import info.magnolia.imaging.ParameterProvider;
import info.magnolia.imaging.operations.ImageOperation;
import info.magnolia.imaging.operations.cropresize.AutoCropAndResize;
import info.magnolia.imaging.operations.cropresize.resizers.MultiStepResizer;

import javax.inject.Inject;
import java.awt.image.BufferedImage;

public class FlexibleAutoCropAndResize implements ImageOperation<ParameterProvider<FlexibleParameter>> {
	private final AssetUtil assetUtil;
	private final HeightCalculator heightCalculator;

	@Inject
	public FlexibleAutoCropAndResize(
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
		final AutoCropAndResize autoCropAndResize = new AutoCropAndResize();
		autoCropAndResize.setResizer(new MultiStepResizer());
		autoCropAndResize.setTargetWidth(parameter.getWidth());
		heightCalculator.calculateHeight(parameter).ifPresent(autoCropAndResize::setTargetHeight);
		return autoCropAndResize.apply(source, params);
	}

	public boolean isSupported(final FlexibleParameter parameter) {
		try {
			return !assetUtil.isAnimated(parameter);
		} catch (Exception e) {
			return false;
		}
	}
}
