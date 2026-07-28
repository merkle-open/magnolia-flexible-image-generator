package com.merkle.oss.magnolia.imaging.flexible.configuration;

import info.magnolia.objectfactory.guice.AbstractGuiceComponentConfigurer;

import com.google.inject.multibindings.Multibinder;
import com.merkle.oss.magnolia.imaging.flexible.model.ImageModel;
import com.merkle.oss.magnolia.imaging.flexible.model.MagnoliaDamImageModelFactory;

public class FlexibleImageGeneratorGuiceComponentConfigurer extends AbstractGuiceComponentConfigurer {

	@Override
	protected void configure() {
		super.configure();
		final Multibinder<ImageModel.Factory> imageFactoryMultibinder = Multibinder.newSetBinder(binder(), ImageModel.Factory.class);
		imageFactoryMultibinder.addBinding().to(MagnoliaDamImageModelFactory.class);
	}
}
