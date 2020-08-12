package org.geogebra.common.plugin;

import java.util.HashMap;

import org.geogebra.common.util.debug.Log;

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
	public static JsReference fromName(String string) {
		if (nameToScript == null) {
			nameToScript = new HashMap<>();
		} else if (nameToScript.containsKey(string)) {
			return nameToScript.get(string);
		}
		JsReference script = new JsReference(string);
		nameToScript.put(string, script);
		return script;
	}

	public static JsReference fromNative(Object nativeRunnable) {
		if (nativeRunnable instanceof String) {
			return fromName((String) nativeRunnable);
		}
		Log.error(nameToScript.size()+" exist");
		for (JsReference ref: nameToScript.values()) {
			if (ref.getNativeRunnable() == nativeRunnable) {
				Log.error("match!");
				return ref;
			}
		}
		JsReference alias = fromName((nameToScript.size()+1)+"");
		alias.nativeRunnable = nativeRunnable;
		return alias;
	}

	public Object getNativeRunnable() {
		return nativeRunnable;
	}

	public String getText() {
		return text;
	}
}
