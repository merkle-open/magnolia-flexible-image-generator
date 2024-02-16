package com.merkle.oss.magnolia.imaging.flexible.generator.operation;

import com.merkle.oss.magnolia.imaging.flexible.model.FlexibleParameter;
import info.magnolia.imaging.ImagingException;
import info.magnolia.imaging.ParameterProvider;
import info.magnolia.imaging.operations.load.AbstractLoader;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class FromFlexibleParameter extends AbstractLoader<ParameterProvider<FlexibleParameter>> {
	private final AssetUtil assetUtil;

	@Inject
	public FromFlexibleParameter(final AssetUtil assetUtil) {
		this.assetUtil = assetUtil;
	}

	@Override
	protected BufferedImage loadSource(final ParameterProvider<FlexibleParameter> param) throws ImagingException {
		try {
			final FlexibleParameter parameter = param.getParameter();
			if (!assetUtil.isImage(parameter)) {
				throw new ImagingException(getClass() + " doesn't support " + parameter);
			}
			return doReadAndClose(toInputStream(param));
		} catch (IOException e) {
			throw new ImagingException("Can't read image stream from flexibleParam.");
		}
	}

	@Override
	protected InputStream toInputStream(final ParameterProvider<FlexibleParameter> param) {
		return param.getParameter().getContentStream();
	}
}
