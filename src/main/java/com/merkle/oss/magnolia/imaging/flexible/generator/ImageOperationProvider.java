package com.merkle.oss.magnolia.imaging.flexible.generator;

import com.merkle.oss.magnolia.imaging.flexible.model.bundle.ProcessedBundlesProvider;
import info.magnolia.imaging.ParameterProvider;
import info.magnolia.imaging.operations.ImageOperation;
import info.magnolia.imaging.operations.ImageOperationChain;
import info.magnolia.imaging.operations.cropresize.AutoCropAndResize;
import info.magnolia.imaging.operations.cropresize.BoundedResize;
import info.magnolia.imaging.operations.cropresize.resizers.MultiStepResizer;
import info.magnolia.imaging.operations.load.FromBinaryNode;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.jcr.Node;
import java.util.Objects;
import java.util.stream.Stream;

public class ImageOperationProvider {
	private final ProcessedBundlesProvider bundlesProvider;

	@Inject
	public ImageOperationProvider(final ProcessedBundlesProvider bundlesProvider) {
		this.bundlesProvider = bundlesProvider;
	}

	public ImageOperation<ParameterProvider<Node>> get(final FlexibleParameter parameter) {
		final ImageOperationChain<ParameterProvider<Node>> chain = new ImageOperationChain<>();
		chain.addOperation(new FromBinaryNode());
		if (isSizeValid(parameter.getWidth().orElse(null), parameter.getHeight().orElse(null))) {
			if (parameter.isCrop()) {
				final AutoCropAndResize resize = new AutoCropAndResize();
				resize.setResizer(new MultiStepResizer());
				resize.setTargetWidth(parameter.getWidth().orElse(Integer.MAX_VALUE));
				resize.setTargetHeight(parameter.getHeight().orElse(Integer.MAX_VALUE));
				chain.addOperation(resize);
			} else {
				final BoundedResize resize = new BoundedResize();
				resize.setResizer(new MultiStepResizer());
				resize.setMaxWidth(parameter.getWidth().orElse(Integer.MAX_VALUE));
				resize.setMaxHeight(parameter.getHeight().orElse(Integer.MAX_VALUE));
				chain.addOperation(resize);
			}
			return chain;
		}
		throw new IllegalArgumentException("Invalid parameter " + parameter);
	}

	private boolean isSizeValid(@Nullable final Integer width, @Nullable final Integer height) {
		return bundlesProvider.get().stream()
				.flatMap(bundle ->
						Stream.concat(
								bundle.getImageSizes().stream(),
								bundle.getCustomRenditions().stream()
						)
				)
				.anyMatch(size ->
						Objects.equals(size.getWidth(), width) && Objects.equals(size.getHeight(), height)
				);
	}
}
