package com.merkle.oss.magnolia.imaging.flexible.util;

import java.util.Locale;

import javax.jcr.Node;

public interface LocalizedPropertyNameProvider {
	String get(Node assetNode, String propertyName, Locale locale);

}
