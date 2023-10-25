package com.merkle.oss.magnolia.imaging.flexible.generator;

import com.merkle.oss.magnolia.imaging.flexible.model.FlexibleParameter;
import com.merkle.oss.magnolia.imaging.flexible.model.bundle.ProcessedBundlesProvider;
import info.magnolia.dam.templating.functions.DamTemplatingFunctions;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class FlexibleImageUriParser {
	/*
	 * /<context>/.imaging/flex/assetItemKey/fileName
	 * e.g. http://localhost:8080/author/.imaging/flex/jcr:b3ee7444-4830-4454-abbb-20fc35387032/dummy1-1600x900.jpg
	 */
	private static final Pattern URI_PATTERN = Pattern.compile("^(/[^/]+|)/.imaging/flex/([^/]+)/[^/]+$");

	private final DamTemplatingFunctions damTemplatingFunctions;
	private final ProcessedBundlesProvider bundlesProvider;
	private final FlexibleParameter.Factory flexibleParameterFactory;

	@Inject
	public FlexibleImageUriParser(
			final DamTemplatingFunctions damTemplatingFunctions,
			final ProcessedBundlesProvider bundlesProvider,
			final FlexibleParameter.Factory flexibleParameterFactory
	) {
		this.damTemplatingFunctions = damTemplatingFunctions;
		this.bundlesProvider = bundlesProvider;
		this.flexibleParameterFactory = flexibleParameterFactory;
	}

	//http://localhost:8080/author/.imaging/flex/jcr:b3ee7444-4830-4454-abbb-20fc35387032/dummy1-1600x900.jpg
	public Optional<FlexibleParameter> parse(final HttpServletRequest request) {
		final Matcher matcher = URI_PATTERN.matcher(request.getRequestURI());
		if (matcher.find()) {
			final String assetItemKey = matcher.group(2);
			return Optional
					.ofNullable(damTemplatingFunctions.getAsset(assetItemKey))
					.flatMap(asset ->
							flexibleParameterFactory.create(asset, key -> Optional.ofNullable(request.getParameter(key)))
					)
					.filter(parameter ->
							isSizeValid(parameter.getWidth(), parameter.getHeight())
					);
		}
		return Optional.empty();
	}

	private boolean isSizeValid(final int width, final int height) {
		return bundlesProvider.get().stream()
				.flatMap(bundle ->
						Stream.concat(
								bundle.getImageSizes().stream(),
								bundle.getCustomRenditions().stream()
						)
				)
				.anyMatch(size ->
						Objects.equals(size.getWidth(), width) && Objects.equals(size.getHeight(), height)
				);
	}
}
