package com.merkle.oss.magnolia.imaging.flexible.generator;

import com.machinezoo.noexception.Exceptions;
import com.merkle.oss.magnolia.imaging.flexible.model.FlexibleParameter;
import info.magnolia.context.MgnlContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FlexibleImageUriFactory {
	/*
	 * /<context>/.imaging/flex/assetItemKey/param1Key/param1Value/.../fileName
	 * e.g. /author/.imaging/flex/jcr:b3ee7444-4830-4454-abbb-20fc35387032/crop/true/height/316/width/560/dummy1-1600x900.jpg
	 */
	public URI create(final FlexibleParameter parameter) {
		return Exceptions.wrap().get(() ->
				new URIBuilder().setPathSegments(
						Stream.of(
								Stream.of(
										StringUtils.remove(MgnlContext.getContextPath(), "/"),
										".imaging",
										FlexibleImageGenerator.GENERATOR_NAME,
										parameter.getItemKey().asString()
								),
								parameter.toMap().entrySet().stream().sorted(Map.Entry.comparingByKey()).flatMap(p -> Stream.of(p.getKey(), p.getValue())),
								Stream.of(parameter.getFileName())
						).flatMap(Function.identity()).collect(Collectors.toList())
				).build()
		);
	}
}
