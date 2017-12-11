package org.geogebra.web.web.gui;

import org.geogebra.web.web.gui.images.SvgPerspectiveResources;

import com.google.gwt.core.shared.GWT;

/**
 * Provides access to PNG image resources
 * 
 * @author Zbynek
 */

public class ImageFactory {
	private static SvgPerspectiveResources pr;


	/**
	 * @return menu images
	 */
	public static SvgPerspectiveResources getPerspectiveResources() {
		if (pr == null) {
			pr = GWT.create(SvgPerspectiveResources.class);
		}
		return pr;

	}

}
