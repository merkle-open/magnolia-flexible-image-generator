package com.merkle.oss.magnolia.imaging.flexible.generator;

import info.magnolia.imaging.DefaultImageGenerator;
import info.magnolia.imaging.ImagingException;
import info.magnolia.imaging.OutputFormat;
import info.magnolia.imaging.ParameterProvider;
import info.magnolia.imaging.operations.ImageOperationChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.lang.invoke.MethodHandles;

public class FlexibleImageGenerator extends ImageOperationChain<ParameterProvider<FlexibleParameter>> {
	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	public static final String GENERATOR_NAME = "flex";

	private final DefaultImageGenerator defaultImageGenerator;
	private final ImageOperationProvider imageOperationProvider;

	@Inject
	public FlexibleImageGenerator(
			final DefaultImageGenerator defaultImageGenerator,
			final ImageOperationProvider imageOperationProvider
	) {
		this.defaultImageGenerator = defaultImageGenerator;
		this.imageOperationProvider = imageOperationProvider;
	}

	@Override
	public BufferedImage generate(final ParameterProvider<FlexibleParameter> parameterProvider) throws ImagingException {
		try {
			final FlexibleParameter parameter = parameterProvider.getParameter();
			return imageOperationProvider.get(parameter).apply(null, () -> parameter);
		} catch (Exception e) {
			final String msg = "Failed to generate image for " + parameterProvider;
			LOG.error(msg, e);
			throw new ImagingException(msg, e);
		}
	}

	@Override
	public OutputFormat getOutputFormat(final ParameterProvider<FlexibleParameter> parameters) {
		return defaultImageGenerator.getOutputFormat(parameters::getParameter);
	}
}
