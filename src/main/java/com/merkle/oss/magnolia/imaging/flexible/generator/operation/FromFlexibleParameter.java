package com.merkle.oss.magnolia.imaging.flexible.generator.operation;

import info.magnolia.dam.binary.api.Binary;
import info.magnolia.dam.binary.api.BinaryGetter;
import info.magnolia.imaging.ImagingException;
import info.magnolia.imaging.ParameterProvider;
import info.magnolia.imaging.operations.load.AbstractLoader;

import java.io.InputStream;

import com.merkle.oss.magnolia.imaging.flexible.model.FlexibleParameter;

import jakarta.inject.Inject;

public class FromFlexibleParameter extends AbstractLoader<ParameterProvider<FlexibleParameter>> {
    private final BinaryGetter binaryGetter;

    @Inject
	public FromFlexibleParameter(final BinaryGetter binaryGetter) {
        this.binaryGetter = binaryGetter;
	}

	@Override
	protected InputStream toInputStream(final ParameterProvider<FlexibleParameter> param) throws ImagingException {
        final Binary binary = binaryGetter.getBinary(param.getParameter().getBinaryReference()).orElseThrow(() ->
                new ImagingException("binary not present!")
        );
        return binary.getInputStream();
	}
}
