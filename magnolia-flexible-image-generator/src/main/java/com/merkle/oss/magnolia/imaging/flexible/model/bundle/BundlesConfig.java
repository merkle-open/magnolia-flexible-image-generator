package com.merkle.oss.magnolia.imaging.flexible.model.bundle;

import java.util.List;
import java.util.Objects;

class BundlesConfig {
	private final List<String> scanPaths;
	private final String bundlesDirName;

	BundlesConfig(
			final List<String> scanPaths,
			final String bundlesDirName
	) {
		this.scanPaths = scanPaths;
		this.bundlesDirName = bundlesDirName;
	}

	public List<String> getScanPaths() {
		return scanPaths;
	}

	public String getBundlesDirName() {
		return bundlesDirName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BundlesConfig that = (BundlesConfig) o;
		return Objects.equals(scanPaths, that.scanPaths) && Objects.equals(bundlesDirName, that.bundlesDirName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(scanPaths, bundlesDirName);
	}

	@Override
	public String toString() {
		return "BundlesConfig{" +
				"scanPaths=" + scanPaths +
				", bundlesDirName='" + bundlesDirName + '\'' +
				'}';
	}
}