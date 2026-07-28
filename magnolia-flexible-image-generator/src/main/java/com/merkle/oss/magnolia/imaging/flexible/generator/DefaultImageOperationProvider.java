package com.merkle.oss.magnolia.imaging.flexible.generator;

import info.magnolia.imaging.ParameterProvider;
import info.magnolia.imaging.operations.ImageOperationChain;

import com.merkle.oss.magnolia.imaging.flexible.generator.operation.FlexibleAutoCropAndResize;
import com.merkle.oss.magnolia.imaging.flexible.generator.operation.FlexibleBoundedResize;
import com.merkle.oss.magnolia.imaging.flexible.generator.operation.FromFlexibleParameter;
import com.merkle.oss.magnolia.imaging.flexible.model.DynamicImageParameter;
import com.merkle.oss.magnolia.imaging.flexible.model.FlexibleParameter;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

public class DefaultImageOperationProvider implements ImageOperationProvider {
	private final Provider<FromFlexibleParameter> fromFlexibleParameter;
	private final Provider<FlexibleAutoCropAndResize> flexibleAutoCropAndResize;
	private final Provider<FlexibleBoundedResize> flexibleBoundedResize;

	@Inject
	public DefaultImageOperationProvider(
			final Provider<FromFlexibleParameter> fromFlexibleParameter,
			final Provider<FlexibleAutoCropAndResize> flexibleAutoCropAndResize,
			final Provider<FlexibleBoundedResize> flexibleBoundedResize
	) {
		this.fromFlexibleParameter = fromFlexibleParameter;
		this.flexibleAutoCropAndResize = flexibleAutoCropAndResize;
		this.flexibleBoundedResize = flexibleBoundedResize;
	}

	@Override
	public ImageOperationChain<ParameterProvider<FlexibleParameter>> get(final FlexibleParameter parameter) {
		final ImageOperationChain<ParameterProvider<FlexibleParameter>> chain = new ImageOperationChain<>();
		chain.addOperation(fromFlexibleParameter.get());

		if (parameter.getDynamicImageParameter().map(DynamicImageParameter::isCrop).orElse(true)) {
			chain.addOperation(flexibleAutoCropAndResize.get());
		} else {
			chain.addOperation(flexibleBoundedResize.get());
		}
		return chain;
	}


}
