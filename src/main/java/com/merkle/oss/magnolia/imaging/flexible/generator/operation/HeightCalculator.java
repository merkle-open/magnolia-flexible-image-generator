package com.merkle.oss.magnolia.imaging.flexible.generator.operation;

import com.merkle.oss.magnolia.imaging.flexible.model.FlexibleParameter;
import com.merkle.oss.magnolia.imaging.flexible.model.bundle.RatioParser;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class HeightCalculator {
	private final RatioParser ratioParser;

	@Inject
	public HeightCalculator(final RatioParser ratioParser) {
		this.ratioParser = ratioParser;
	}

	public Optional<Integer> calculateHeight(final FlexibleParameter parameter) {
		return parameter.getRatio().flatMap(ratioParser::parse).map(ratio ->
				calculateHeight(parameter.getWidth(), ratio)
		);
	}

	private int calculateHeight(final int width, final double ratio) {
		return BigDecimal.valueOf(width / ratio).setScale(0, RoundingMode.UP).intValue();
	}
}
