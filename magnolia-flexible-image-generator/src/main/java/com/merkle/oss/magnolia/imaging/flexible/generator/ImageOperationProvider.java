package com.merkle.oss.magnolia.imaging.flexible.generator;

import info.magnolia.imaging.ParameterProvider;
import info.magnolia.imaging.operations.ImageOperationChain;

import com.merkle.oss.magnolia.imaging.flexible.model.FlexibleParameter;

public interface ImageOperationProvider {
	ImageOperationChain<ParameterProvider<FlexibleParameter>> get(FlexibleParameter parameter);
}
