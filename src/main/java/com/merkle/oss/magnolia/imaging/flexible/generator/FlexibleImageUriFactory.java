package com.merkle.oss.magnolia.imaging.flexible.generator;

import com.machinezoo.noexception.Exceptions;
import com.merkle.oss.magnolia.imaging.flexible.model.FlexibleParameter;
import info.magnolia.context.MgnlContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

import javax.inject.Inject;
import java.net.URI;
import java.util.stream.Collectors;

public class FlexibleImageUriFactory {
	private final FlexibleParameter.Factory flexibleParameterFactory;

	@Inject
	public FlexibleImageUriFactory(final FlexibleParameter.Factory flexibleParameterFactory) {
		this.flexibleParameterFactory = flexibleParameterFactory;
	}

	// /author/.imaging/flex/jcr:b3ee7444-4830-4454-abbb-20fc35387032/dummy1-1600x900.jpg
	public URI create(final FlexibleParameter parameter) {
		return Exceptions.wrap().get(() -> new URIBuilder()
				.setPathSegments(
						StringUtils.remove(MgnlContext.getContextPath(), "/"),
						".imaging",
						FlexibleImageGenerator.GENERATOR_NAME,
						parameter.getItemKey().asString(),
						parameter.getFileName()
				)
				.setParameters(
						flexibleParameterFactory.toMap(parameter).entrySet().stream().map(entry ->
								new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue()))
						).collect(Collectors.toList())
				)
				.build()
		);
	}
}
