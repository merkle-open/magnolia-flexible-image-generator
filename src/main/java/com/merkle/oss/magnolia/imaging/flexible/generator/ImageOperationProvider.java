package com.merkle.oss.magnolia.imaging.flexible.generator;

import com.merkle.oss.magnolia.imaging.flexible.generator.operation.FromFlexibleParameter;
import com.merkle.oss.magnolia.imaging.flexible.model.DynamicImageParameter;
import com.merkle.oss.magnolia.imaging.flexible.model.FlexibleParameter;
import info.magnolia.imaging.ParameterProvider;
import info.magnolia.imaging.operations.ImageOperationChain;
import info.magnolia.imaging.operations.cropresize.AutoCropAndResize;
import info.magnolia.imaging.operations.cropresize.BoundedResize;
import info.magnolia.imaging.operations.cropresize.resizers.MultiStepResizer;

public class ImageOperationProvider {

	public ImageOperationChain<ParameterProvider<FlexibleParameter>> get(final FlexibleParameter parameter) {
		final ImageOperationChain<ParameterProvider<FlexibleParameter>> chain = new ImageOperationChain<>();
		chain.addOperation(new FromFlexibleParameter());

		if (parameter.getDynamicImageParameter().map(DynamicImageParameter::isCrop).orElse(true)) {
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
