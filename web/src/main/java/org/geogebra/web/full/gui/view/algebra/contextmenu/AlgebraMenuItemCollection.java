package org.geogebra.web.full.gui.view.algebra.contextmenu;

import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.html5.main.AppW;

/**
 * AV context menu actions for graphing / classic / geometry
 *
 * @author Zbynek
 */
public class AlgebraMenuItemCollection extends GeoElementMenuItemCollection {

	/**
	 * @param algebraView
	 *            algebra view
	 */
	public AlgebraMenuItemCollection(AlgebraViewW algebraView) {
		AppW app = algebraView.getApp();
		if (!app.getConfig().hasAutomaticLabels()) {
			addLabelingActions();
		}
		if (app.getConfig().hasTableView(app)) {
			addActions(new TableOfValuesAction());
		}
		addActions(new SpecialPointsAction());
		addActions(new DuplicateAction(algebraView), new DeleteAction(), new SettingsAction());
	}

}
