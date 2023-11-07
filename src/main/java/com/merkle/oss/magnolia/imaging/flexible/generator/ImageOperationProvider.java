package com.merkle.oss.magnolia.imaging.flexible.generator;

import com.merkle.oss.magnolia.imaging.flexible.generator.operation.FromFlexibleParameter;
import com.merkle.oss.magnolia.imaging.flexible.model.DynamicImageParameter;
import com.merkle.oss.magnolia.imaging.flexible.model.FlexibleParameter;
import com.merkle.oss.magnolia.imaging.flexible.model.bundle.RatioParser;
import info.magnolia.imaging.ParameterProvider;
import info.magnolia.imaging.operations.ImageOperationChain;
import info.magnolia.imaging.operations.cropresize.AutoCropAndResize;
import info.magnolia.imaging.operations.cropresize.BoundedResize;
import info.magnolia.imaging.operations.cropresize.resizers.MultiStepResizer;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class ImageOperationProvider {
	private final RatioParser ratioParser;

	@Inject
	public ImageOperationProvider(final RatioParser ratioParser) {
		this.ratioParser = ratioParser;
	}

	public ImageOperationChain<ParameterProvider<FlexibleParameter>> get(final FlexibleParameter parameter) {
		final ImageOperationChain<ParameterProvider<FlexibleParameter>> chain = new ImageOperationChain<>();
		chain.addOperation(new FromFlexibleParameter());

		if (parameter.getDynamicImageParameter().map(DynamicImageParameter::isCrop).orElse(true)) {
			final AutoCropAndResize resize = new AutoCropAndResize();
			resize.setResizer(new MultiStepResizer());
			resize.setTargetWidth(parameter.getWidth());
			calculateHeight(parameter).ifPresent(resize::setTargetHeight);
			chain.addOperation(resize);
		} else {
			final BoundedResize resize = new BoundedResize();
			resize.setResizer(new MultiStepResizer());
			resize.setMaxWidth(parameter.getWidth());
			resize.setMaxHeight(calculateHeight(parameter).orElse(Integer.MAX_VALUE));
			chain.addOperation(resize);
		}
		return chain;
	}

	protected Optional<Integer> calculateHeight(final FlexibleParameter parameter) {
		return parameter.getRatio().flatMap(ratioParser::parse).map(ratio ->
				calculateHeight(parameter.getWidth(), ratio)
		);
	}

	private int calculateHeight(final int width, final double ratio) {
		return BigDecimal.valueOf(width / ratio).setScale(0, RoundingMode.UP).intValue();
	}
}
