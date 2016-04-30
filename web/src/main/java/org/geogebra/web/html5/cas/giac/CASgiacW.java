package org.geogebra.web.html5.cas.giac;

import org.geogebra.common.cas.CASparser;
import org.geogebra.common.cas.CasParserTools;
import org.geogebra.common.cas.Evaluate;
import org.geogebra.common.cas.giac.CASgiac;
import org.geogebra.common.kernel.AsynchronousCommand;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.js.JavaScriptInjector;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;

/**
 * Web implementation of Giac CAS
 * 
 * @author Michael Borcherds, based on Reduce version
 *
 */
public class CASgiacW extends CASgiac implements Evaluate {

	/** kernel */
	Kernel kernel;
	/** flag indicating that JS file was loaded */
	boolean jsLoaded = false;
	private Evaluate giac;

	/**
	 * Creates new CAS
	 * 
	 * @param casParser
	 *            parser
	 * @param parserTools
	 *            scientific notation convertor
	 * @param kernel
	 *            kernel
	 */
	public CASgiacW(CASparser casParser, CasParserTools parserTools,
	        Kernel kernel) {
		super(casParser);
		this.parserTools = parserTools;
		this.kernel = kernel;

		App.setCASVersionString("Giac/JS");
		Log.debug("starting CAS");
		if (Browser.externalCAS()) {
			Log.debug("switching to external");
			// CASgiacW.this.kernel.getApplication().getGgbApi().initCAS();
			this.jsLoaded = true;
		} else if (Browser.supportsJsCas()) {
			initialize();
		}

	}

	@Override
	public String evaluateCAS(String exp) {
		if (!jsLoaded) {
			return "?";
		}
		try {
			// replace Unicode when sending to JavaScript
			// (encoding problem)
			String processedExp = casParser.replaceIndices(exp, true);
			String ret = evaluateRaw(processedExp);

			return postProcess(ret);

			// } catch (TimeoutException toe) {
			// throw new Error(toe.getMessage());
		} catch (Throwable e) {
			Log.debug("evaluateGiac: " + e.getMessage());
			return "?";
		}
	}

	@Override
	public synchronized String evaluate(String s, long timeoutMilliseconds) {
		if (!jsLoaded) {
			return "?";
		}
		
		// Make sure that indices are replaced in the same way as usual:
		s = casParser.replaceIndices(s, true);

		if (Browser.externalCAS()) {
			// native Giac so need same initString and fix as desktop
			nativeEvaluateRaw(initString, Log.logger != null);

			// fix for problem with eg SolveODE[y''=0,{(0,1), (1,3)}]
			// sending all at once doesn't work from
			// http://dev.geogebra.org/trac/changeset/42719
			String[] sf = specialFunctions.split(";;");
			for (int i = 0; i < sf.length; i++) {
				nativeEvaluateRaw(sf[i], false);
			}
		} else {
			// #5439
			// restart Giac before each call
			nativeEvaluateRaw(initStringWeb, Log.logger != null);
			
			nativeEvaluateRaw(specialFunctions, false);
		}

		nativeEvaluateRaw("timeout " + (timeoutMilliseconds / 1000), false);

		// make sure we don't always get the same value!
		int seed = rand.nextInt(Integer.MAX_VALUE);
		nativeEvaluateRaw("srand(" + seed + ")", false);

		String exp;
		GLookAndFeelI laf = ((AppW) kernel.getApplication()).getLAF();
		if (laf != null && !laf.isSmart()) {
			// evalfa makes sure rootof() converted to decimal
			// eg @rootof({{-4,10,-440,2025},{1,0,10,-200,375}})
			exp = wrapInevalfa(s);
		} else {
			exp = s;
		}

		String ret = nativeEvaluateRaw(exp, true);

		return ret;
	}

	private native String nativeEvaluateRaw(String s, boolean showOutput) /*-{
		if (typeof $wnd.evalGeoGebraCASExternal === 'function') {
			return $wnd.evalGeoGebraCASExternal(s);
		}
		if (typeof Float64Array === 'undefined') {
			$wnd.console.log("Typed arrays not supported, Giac won't work");
			return "?";
		}

		if (showOutput) {
			$wnd.console.log("js giac  input:" + s);
		}

		caseval = $wnd.__ggb__giac.cwrap('caseval', 'string', [ 'string' ]);

		var ret = caseval(s);

		if (showOutput) {
			$wnd.console.log("js giac output:" + ret);
		}

		return ret
	}-*/;

	public void initialize() {
		GWT.runAsync(new RunAsyncCallback() {
			public void onSuccess() {
				Log.debug("giac.js loading success");
				JavaScriptInjector.inject(CASResources.INSTANCE.giacJs());
				CASgiacW.this.jsLoaded = true;
				CASgiacW.this.kernel.getApplication().getGgbApi().initCAS();
			}

			public void onFailure(Throwable reason) {
				Log.debug("giac.js loading failure");
			}
		});
	}

	public void evaluateGeoGebraCASAsync(AsynchronousCommand c) {
		// TODO Auto-generated method stub

	}


	public void clearResult() {
		// not needed

	}

}
