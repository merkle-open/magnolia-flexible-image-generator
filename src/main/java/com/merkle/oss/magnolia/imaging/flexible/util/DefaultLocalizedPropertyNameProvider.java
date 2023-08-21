package com.merkle.oss.magnolia.imaging.flexible.util;

import info.magnolia.cms.i18n.I18nContentSupport;

import javax.inject.Inject;
import javax.jcr.Node;
import java.util.Locale;
import java.util.Objects;

public class DefaultLocalizedPropertyNameProvider implements LocalizedPropertyNameProvider {
	private final I18nContentSupport i18nContentSupport;

	@Inject
	public DefaultLocalizedPropertyNameProvider(final I18nContentSupport i18nContentSupport) {
		this.i18nContentSupport = i18nContentSupport;
	}

	@Override
	public String get(final Node assetNode, final String propertyName, final Locale locale) {
		boolean isDefault = Objects.equals(locale, this.i18nContentSupport.getDefaultLocale());
		return !isDefault ? String.format("%s_%s", propertyName, locale.toString()) : propertyName;
	}
}
