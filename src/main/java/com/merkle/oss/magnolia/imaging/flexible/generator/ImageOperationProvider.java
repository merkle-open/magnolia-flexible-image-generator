package com.merkle.oss.magnolia.imaging.flexible.generator;

import com.merkle.oss.magnolia.imaging.flexible.generator.operation.FromFlexibleParameter;
import com.merkle.oss.magnolia.imaging.flexible.model.FlexibleParameter;
import info.magnolia.imaging.ParameterProvider;
import info.magnolia.imaging.operations.ImageOperation;
import info.magnolia.imaging.operations.ImageOperationChain;
import info.magnolia.imaging.operations.cropresize.AutoCropAndResize;
import info.magnolia.imaging.operations.cropresize.BoundedResize;
import info.magnolia.imaging.operations.cropresize.resizers.MultiStepResizer;

public class ImageOperationProvider {

	public ImageOperation<ParameterProvider<FlexibleParameter>> get(final FlexibleParameter parameter) {
		final ImageOperationChain<ParameterProvider<FlexibleParameter>> chain = new ImageOperationChain<>();
		chain.addOperation(new FromFlexibleParameter());
		if (parameter.isCrop()) {
			final AutoCropAndResize resize = new AutoCropAndResize();
			resize.setResizer(new MultiStepResizer());
			resize.setTargetWidth(parameter.getWidth());
			resize.setTargetHeight(parameter.getHeight());
			chain.addOperation(resize);
		} else {
			final BoundedResize resize = new BoundedResize();
			resize.setResizer(new MultiStepResizer());
			resize.setMaxWidth(parameter.getWidth());
			resize.setMaxHeight(parameter.getHeight());
			chain.addOperation(resize);
		}
		return chain;
	}
}
