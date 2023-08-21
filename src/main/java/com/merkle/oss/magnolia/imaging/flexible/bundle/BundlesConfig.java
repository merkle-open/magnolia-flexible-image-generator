package com.merkle.oss.magnolia.imaging.flexible.bundle;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class BundlesConfig {

	private List<String> scanPaths;
	private String bundlesDirName;

	public List<String> getScanPaths() {
		if (scanPaths == null) {
			return new ArrayList<>();
		}
		return new ArrayList<>(scanPaths);
	}

	public void setScanPaths(List<String> scanPaths) {
		if (scanPaths != null) {
			this.scanPaths = new ArrayList<>(scanPaths);
		} else {
			this.scanPaths = new ArrayList<>();
		}
	}

	public String getBundlesDirName() {
		return StringUtils.defaultString(bundlesDirName);
	}

	public void setBundlesDirName(String bundlesDirName) {
		this.bundlesDirName = StringUtils.defaultString(bundlesDirName);
	}
}
