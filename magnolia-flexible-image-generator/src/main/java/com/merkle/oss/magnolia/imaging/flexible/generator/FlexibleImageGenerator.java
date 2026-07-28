package com.merkle.oss.magnolia.imaging.flexible.generator;

import info.magnolia.dam.api.Asset;
import info.magnolia.imaging.ImagingException;
import info.magnolia.imaging.OutputFormat;
import info.magnolia.imaging.ParameterProvider;
import info.magnolia.imaging.operations.ImageOperationChain;

import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.function.Predicate;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.Strings;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypes;

import com.machinezoo.noexception.Exceptions;
import com.merkle.oss.magnolia.imaging.flexible.model.FlexibleParameter;

import jakarta.annotation.Nullable;
import jakarta.inject.Inject;

public class FlexibleImageGenerator extends ImageOperationChain<ParameterProvider<FlexibleParameter>> {
	public static final String GENERATOR_NAME = "flex";

	private final ImageOperationProvider imageOperationProvider;

	@Inject
	public FlexibleImageGenerator(final ImageOperationProvider imageOperationProvider) {
		this.imageOperationProvider = imageOperationProvider;
	}

	@Override
	public BufferedImage generate(final ParameterProvider<FlexibleParameter> parameterProvider) throws ImagingException {
		final FlexibleParameter parameter = parameterProvider.getParameter();
		try {
			return imageOperationProvider.get(parameter).apply(null, () -> parameter);
		} catch (Exception e) {
			throw new ImagingException("Failed to generate image for " + parameter, e);
		}
	}

	@Override
	public OutputFormat getOutputFormat(final ParameterProvider<FlexibleParameter> params) {
		final FlexibleParameter flexibleParameter = params.getParameter();
		try {
			final OutputFormat format = getOutputFormat().clone();
            @Nullable
			final String extension = getExtension(flexibleParameter).orElse(null);
            Optional.ofNullable(extension).ifPresent(format::setFormatName);

			if (Strings.CI.equals("gif", extension)) {
				format.setCompressionType("LZW");
			} else if (Strings.CI.equals("png", extension)) {
				format.setQuality(estimatePngQuality(flexibleParameter));
			} else {
				format.setCompressionType(null);
			}
			return format;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Can't clone the output format to produce a dynamic format.", e);
		}
	}

    private Optional<String> getExtension(final FlexibleParameter flexibleParameter) {
        return getExtensionFromFilename(flexibleParameter).or(() ->
                getExtensionFromMimeType(flexibleParameter)
        );
    }

    private Optional<String> getExtensionFromFilename(final FlexibleParameter flexibleParameter) {
        return Optional
                .of(FilenameUtils.getExtension(flexibleParameter.getFileName()))
                .filter(Predicate.not(String::isEmpty));
    }

    private Optional<String> getExtensionFromMimeType(final FlexibleParameter flexibleParameter) {
        try {
            final MimeType mimeType = MimeTypes.getDefaultMimeTypes().forName(flexibleParameter.getMimeType());
            return Optional
                    .of(mimeType.getExtension())
                    .map(extension -> extension.replaceFirst("\\.", ""));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

	public int estimatePngQuality(final Asset asset) {
		final long binarySize = asset.getFileSize();
		final BufferedImage read = Exceptions.wrap().get(() -> ImageIO.read(asset.getContentStream()));

		final double uncompressed = read.getHeight() * read.getWidth() * read.getColorModel().getPixelSize();
		final int compress = Math.min((int) ((binarySize * 8.0 / uncompressed) * 100), 100);
		// 0 <= quality <= 9, the higher quality, the better image output, also has bigger size.
		// more info: info.magnolia.imaging.DefaultImageStreamer#write
		final int compressMaxRange = 100;
		final int compressMinRange = 0;
		final int qualityMaxRange = 9;
		final int qualityMinRange = 0;
		return (((compress - compressMinRange) * qualityMaxRange) / compressMaxRange) + qualityMinRange;
	}
}
