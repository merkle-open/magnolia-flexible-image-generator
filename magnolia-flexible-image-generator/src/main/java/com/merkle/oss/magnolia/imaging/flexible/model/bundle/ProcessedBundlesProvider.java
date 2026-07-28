package com.merkle.oss.magnolia.imaging.flexible.model.bundle;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.merkle.oss.magnolia.imaging.flexible.FlexibleImageGeneratorModule;

import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;

@Singleton
public class ProcessedBundlesProvider implements Provider<List<ProcessedBundle>> {
	private final Provider<FlexibleImageGeneratorModule> flexibleImageGeneratorModuleProvider;
	private final BundlesParser bundlesParser;
	private final BundleProcessor bundleProcessor;
	@Nullable
	private List<ProcessedBundle> bundles = null;

	@Inject
	public ProcessedBundlesProvider(
			final Provider<FlexibleImageGeneratorModule> flexibleImageGeneratorModuleProvider,
			final BundlesParser bundlesParser,
			final BundleProcessor bundleProcessor
	) {
		this.flexibleImageGeneratorModuleProvider = flexibleImageGeneratorModuleProvider;
		this.bundlesParser = bundlesParser;
		this.bundleProcessor = bundleProcessor;
	}

	@Override
	public List<ProcessedBundle> get() {
		if(bundles == null) {
			bundles = bundlesParser
					.parse(flexibleImageGeneratorModuleProvider.get().getBundlesConfigPath())
					.map(bundleProcessor::process)
					.collect(Collectors.toList());
		}
		return bundles;
	}

	public Optional<ProcessedBundle> get(final String bundleName) {
		return get().stream()
				.filter(bundle -> bundleName.equals(bundle.getName()))
				.findFirst();
	}

	public void flush() {
		bundles = null;
	}
}
