package org.geogebra.common.plugin;

import java.util.HashMap;

import org.geogebra.common.main.App;

public class JsReference {


	private static HashMap<String, JsReference> nameToScript = new HashMap<>();
	private final String text;
	private Object nativeRunnable;

	public JsReference(String string) {
		this.text= string;
	}

	/**
	 * @param app
	 *            app
	 * @param string
	 *            script name
	 * @return script
	 */
	public static JsReference fromName(App app, String string) {
		if (nameToScript == null) {
			nameToScript = new HashMap<>();
		} else if (nameToScript.containsKey(string)) {
			return nameToScript.get(string);
		}
		JsReference script = new JsReference(string);
		nameToScript.put(string, script);
		return script;
	}

	public static JsReference fromNative(App app, Object nativeRunnable) {
		if (nativeRunnable instanceof String) {
			return fromName(app, (String) nativeRunnable);
		}
		for (JsReference ref: nameToScript.values()) {
			if (ref.getNativeRunnable() == nativeRunnable) {
				return ref;
			}
		}
		JsReference alias = new JsReference((nameToScript.size()+1)+"");
		alias.nativeRunnable = nativeRunnable;
		return alias;
	}

	private Object getNativeRunnable() {
		return nativeRunnable;
	}

	public String getText() {
		return text;
	}
}
