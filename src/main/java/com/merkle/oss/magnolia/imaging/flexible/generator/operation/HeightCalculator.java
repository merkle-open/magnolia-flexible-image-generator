package com.merkle.oss.magnolia.imaging.flexible.generator.operation;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.inject.Inject;

import com.merkle.oss.magnolia.imaging.flexible.model.FlexibleParameter;
import com.merkle.oss.magnolia.imaging.flexible.model.bundle.RatioParser;

public class HeightCalculator {
	private final RatioParser ratioParser;

	@Inject
	public HeightCalculator(final RatioParser ratioParser) {
		this.ratioParser = ratioParser;
	}

	public int calculateHeight(final FlexibleParameter parameter) {
		double ratio = ratioParser.parse(parameter.getRatio());
		return calculateHeight(parameter.getWidth(), ratio);
	}

	private int calculateHeight(final int width, final double ratio) {
		return BigDecimal.valueOf(width / ratio).setScale(0, RoundingMode.UP).intValue();
	}
}
