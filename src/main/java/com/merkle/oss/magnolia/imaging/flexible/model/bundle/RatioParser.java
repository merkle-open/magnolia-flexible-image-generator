package com.merkle.oss.magnolia.imaging.flexible.model.bundle;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RatioParser {
	private static final Pattern RATIO = Pattern.compile("^(\\d+):(\\d+)$");

	public Optional<Double> parse(final String ratio) {
		final Matcher matcher = RATIO.matcher(ratio);
		if(matcher.matches()) {
			final double dividend = Double.parseDouble(matcher.group(1));
			final double divisor = Double.parseDouble(matcher.group(2));
			return Optional.of(dividend/divisor);
		}
		return Optional.empty();
	}
}
