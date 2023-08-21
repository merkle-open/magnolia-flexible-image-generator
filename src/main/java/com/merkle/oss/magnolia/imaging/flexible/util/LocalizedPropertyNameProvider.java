package com.merkle.oss.magnolia.imaging.flexible.util;

import javax.jcr.Node;
import java.util.Locale;

public interface LocalizedPropertyNameProvider {
	String get(Node assetNode, String propertyName, Locale locale);

}
