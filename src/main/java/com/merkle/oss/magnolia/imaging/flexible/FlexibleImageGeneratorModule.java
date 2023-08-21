package com.merkle.oss.magnolia.imaging.flexible;

import com.google.inject.Provider;
import com.merkle.oss.magnolia.imaging.flexible.bundle.BundlesProvider;
import info.magnolia.module.ModuleLifecycle;
import info.magnolia.module.ModuleLifecycleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class FlexibleImageGeneratorModule implements ModuleLifecycle {
	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private final Provider<BundlesProvider> bundlesProviderProvider;
	private String bundlesConfigPath;

	public FlexibleImageGeneratorModule(final Provider<BundlesProvider> bundlesProviderProvider){
		this.bundlesProviderProvider = bundlesProviderProvider;
	}

	@Override
	public void start(final ModuleLifecycleContext moduleLifecycleContext) {
		LOG.debug("Starting FlexibleImageGenerator Module");
		bundlesProviderProvider.get().flush();
	}

	@Override
	public void stop(final ModuleLifecycleContext moduleLifecycleContext) {
		LOG.debug("Stopping FlexibleImageGenerator Module");
	}

	public String getBundlesConfigPath() {
		return bundlesConfigPath;
	}

	public void setBundlesConfigPath(final String bundlesConfigPath) {
		this.bundlesConfigPath = bundlesConfigPath;
	}
}
