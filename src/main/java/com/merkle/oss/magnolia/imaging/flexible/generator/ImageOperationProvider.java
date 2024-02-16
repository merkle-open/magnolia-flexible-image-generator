package com.merkle.oss.magnolia.imaging.flexible.generator;

import com.merkle.oss.magnolia.imaging.flexible.model.FlexibleParameter;
import info.magnolia.imaging.ParameterProvider;
import info.magnolia.imaging.operations.ImageOperationChain;

public interface ImageOperationProvider {
	ImageOperationChain<ParameterProvider<FlexibleParameter>> get(FlexibleParameter parameter);
}
