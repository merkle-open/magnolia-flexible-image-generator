package com.merkle.oss.magnolia.imaging.flexible.generator;

import com.machinezoo.noexception.Exceptions;
import info.magnolia.context.MgnlContext;
import info.magnolia.imaging.ImagingException;
import info.magnolia.imaging.ParameterProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlexibleParameterProvider implements ParameterProvider<FlexibleParameter> {
	/*
	 * /<context>/.imaging/flex/<mode>/<value>/<workspace>/<assetPath>
	 */
	private static final Pattern URI_PATTERN = Pattern.compile("^(/[^/]+|)/.imaging/flex/([^/]+)/([^/]+)/([^/]+)(/.*)$");
	private final String uri;

	public FlexibleParameterProvider(final String uri) {
		this.uri = uri;
	}

	@Override
	public FlexibleParameter getParameter() {
		return Exceptions.wrap().get(() ->
				getParameter(uri)
		);
	}

	private FlexibleParameter getParameter(final String uri) throws ImagingException {
		final Matcher matcher = URI_PATTERN.matcher(uri);
		if (!matcher.find()) {
			throw new ImagingException("Can't create a FlexibleParameterProvider object for " + uri);
		}

		final String mode = matcher.group(2);
		final String value = matcher.group(3);
		final String workspaceName = matcher.group(4);
		final String path = matcher.group(5) + "/" + JcrConstants.JCR_CONTENT;

		final Node node = getNode(workspaceName, path).orElseThrow(() ->
				new ImagingException("Couldn't find asset node for path " + path + " and workspace " + workspaceName)
		);

		switch (mode) {
			case FlexibleImageVariation.WIDTH:
				return new FlexibleParameter(Integer.parseInt(value), null, false, node);
			case FlexibleImageVariation.HEIGHT:
				return new FlexibleParameter(null, Integer.parseInt(value), false, node);
			case FlexibleImageVariation.SIZE:
				final List<String> size = Arrays.asList(StringUtils.split(value, "x"));
				return new FlexibleParameter(Integer.parseInt(size.get(0)), Integer.parseInt(size.get(1)), false, node);
			case FlexibleImageVariation.CROP:
			default:
				final List<String> cropSize = Arrays.asList(StringUtils.split(value, "x"));
				return new FlexibleParameter(Integer.parseInt(cropSize.get(0)), Integer.parseInt(cropSize.get(1)), true, node);
		}
	}

	private Optional<Node> getNode(final String workspace, final String path) {
		try {
			final Session session = MgnlContext.getJCRSession(workspace);
			return Optional.of(session.getNode(path));
		} catch (RepositoryException e) {
			return Optional.empty();
		}
	}

	@Override
	public String toString() {
		return "FlexibleParameterProvider{" +
				"uri='" + uri + '\'' +
				'}';
	}
}
