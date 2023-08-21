package com.merkle.oss.magnolia.imaging.flexible.bundle;

import com.merkle.oss.magnolia.imaging.flexible.FlexibleImageGeneratorModule;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

@Singleton
public class BundlesProvider implements Provider<List<Bundle>> {
	private final Provider<FlexibleImageGeneratorModule> flexibleImageGeneratorModuleProvider;
	private final BundlesParser bundlesParser;
	@Nullable
	private List<Bundle> bundles = null;

	@Inject
	public BundlesProvider(
			final Provider<FlexibleImageGeneratorModule> flexibleImageGeneratorModuleProvider,
			final BundlesParser bundlesParser
	) {
		this.flexibleImageGeneratorModuleProvider = flexibleImageGeneratorModuleProvider;
		this.bundlesParser = bundlesParser;
	}

	@Override
	public List<Bundle> get() {
		if(bundles == null) {
			bundles = bundlesParser.parse(flexibleImageGeneratorModuleProvider.get().getBundlesConfigPath());
		}
		return bundles;
	}

	public Optional<Bundle> get(final String bundleName) {
		return get().stream()
				.filter(bundle -> bundleName.equals(bundle.getName()))
				.findFirst();
	}

	public void flush() {
		bundles = null;
	}
}
