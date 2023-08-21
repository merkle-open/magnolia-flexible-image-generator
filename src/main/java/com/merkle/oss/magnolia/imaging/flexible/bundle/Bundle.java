package com.merkle.oss.magnolia.imaging.flexible.bundle;

import com.google.gson.annotations.SerializedName;
import com.merkle.oss.magnolia.imaging.flexible.model.CustomRendition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bundle {

	@SerializedName("bundle")
	private String name;
	private Double ratio;
	private Boolean crop = true;
	private List<ImageSize> imageSizes;
	private List<CustomRendition> customRenditions;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getRatio() {
		return ratio;
	}

	public void setRatio(Double ratio) {
		this.ratio = ratio;
	}

	public Boolean isCrop() {
		return crop;
	}

	public void setCrop(Boolean crop) {
		this.crop = crop;
	}

	public List<ImageSize> getImageSizes() {
		return new ArrayList<>(imageSizes);
	}

	public void setImageSizes(List<ImageSize> imageSizes) {
		this.imageSizes = new ArrayList<>(imageSizes);
	}

	public List<CustomRendition> getCustomRenditions() {
		if (customRenditions == null) {
			return Collections.emptyList();
		}
		return new ArrayList<>(customRenditions);
	}

	public void setCustomRenditions(List<CustomRendition> customRenditions) {
		this.customRenditions = new ArrayList<>(customRenditions);
	}
}
