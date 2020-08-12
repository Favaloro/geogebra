package org.geogebra.desktop.plugin;

import java.util.HashMap;

import org.geogebra.common.jre.plugin.ScriptManagerJre;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.main.App;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.Scriptable;

public class ScriptManagerD extends ScriptManagerJre {

	protected HashMap<Construction, Scriptable> globalScopeMap;

	public ScriptManagerD(App app) {
		super(app);
		globalScopeMap = new HashMap<>();
	}

	public HashMap<Construction, Scriptable> getGlobalScopeMap() {
		return globalScopeMap;
	}

	@Override
	protected void evalJavaScript(String jsFunction) {
		evalJavaScript(app, jsFunction, null);
	}

	@Override
	public void setGlobalScript() {
		Scriptable globalScope = CallJavaScript.evalGlobalScript(app);
		globalScopeMap.put(app.getKernel().getConstruction(), globalScope);
	}

	public void evalJavaScript(App app, String script, String arg) {
		ensureGlobalScript(app);
		CallJavaScript.evalScript(app, script, arg);
	}

	private void ensureGlobalScript(App app) {
		if (globalScopeMap.get(app.getKernel().getConstruction()) == null) {
			setGlobalScript();
		}
	}

	@Override
	protected void callNativeListener(Object nativeRunnable, String[] args) {
		ensureGlobalScript(app);
		if (nativeRunnable instanceof org.mozilla.javascript.NativeFunction) {
			NativeFunction nativeRunnable1 = (NativeFunction) nativeRunnable;
			CallJavaScript.evalFunction(nativeRunnable1, args, app);
		}
	}
}
