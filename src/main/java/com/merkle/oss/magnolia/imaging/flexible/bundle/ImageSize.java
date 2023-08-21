package com.merkle.oss.magnolia.imaging.flexible.bundle;

public class ImageSize {

	private Integer width;
	private Integer height;
	private Boolean crop;
	private Double ratio;
	private String media;
	private String orientation;
	private Boolean fallback = false;

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Boolean isCrop() {
		return crop;
	}

	public void setCrop(Boolean crop) {
		this.crop = crop;
	}

	public Double getRatio() {
		return ratio;
	}

	public void setRatio(Double ratio) {
		this.ratio = ratio;
	}

	public String getMedia() {
		return media;
	}

	public void setMedia(String media) {
		this.media = media;
	}

	public String getOrientation() {
		return orientation;
	}

	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	public Boolean getFallback() {
		return fallback;
	}

	public void setFallback(Boolean fallback) {
		this.fallback = fallback;
	}
}
