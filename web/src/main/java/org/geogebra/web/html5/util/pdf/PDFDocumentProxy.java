package org.geogebra.web.html5.util.pdf;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class PDFDocumentProxy {
	@JsProperty
	public native String getNumPages();
}
