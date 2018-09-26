/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2017  FeatureIDE team, University of Magdeburg, Germany
 *
 * This file is part of FeatureIDE.
 *
 * FeatureIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FeatureIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FeatureIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See http://featureide.cs.ovgu.de/ for further information.
 */
package de.ovgu.featureide.fm.ui.editors.featuremodel.actions;

import static de.ovgu.featureide.fm.core.localization.StringTable.CREATE_SIBLING;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.ui.FMUIPlugin;
import de.ovgu.featureide.fm.ui.editors.IGraphicalFeatureModel;
import de.ovgu.featureide.fm.ui.editors.featuremodel.editparts.FeatureEditPart;
import de.ovgu.featureide.fm.ui.editors.featuremodel.operations.CreateSiblingOperation;

/**
 * Creates a new feature with the currently selected features as siblings
 *
 * @author Sabrina Hugo
 * @author Christian Orsinger
 */
public class CreateSiblingAction extends SingleSelectionAction {

	public static final String ID = "de.ovgu.featureide.createsibling";

	private final IGraphicalFeatureModel featureModel;

	private static ImageDescriptor createImage = PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD);

	public CreateSiblingAction(Object viewer, IGraphicalFeatureModel featureModel) {
		super(CREATE_SIBLING, viewer, ID);
		setImageDescriptor(createImage);
		this.featureModel = featureModel;
	}

	@Override
	public void run() {
		final CreateSiblingOperation op = new CreateSiblingOperation(featureModel, feature);

		try {
			PlatformUI.getWorkbench().getOperationSupport().getOperationHistory().execute(op, null, null);
		} catch (final ExecutionException e) {
			FMUIPlugin.getDefault().logError(e);
		}
	}

	/**
	 * @param selection selected elements
	 * @return true, if only one element of FeatureEditPart or IFeature is selected and this is not the root
	 */
	@Override
	protected boolean isValidSelection(IStructuredSelection selection) {
		if (!super.isValidSelection(selection)) {
			return false;
		}
		final Object selectedObject = selection.getFirstElement();
		IFeature selectedFeature;
		if ((selectedObject instanceof FeatureEditPart) || (selectedObject instanceof IFeature)) {
			if (selectedObject instanceof FeatureEditPart) {
				selectedFeature = ((FeatureEditPart) selectedObject).getModel().getObject();
			} else {
				selectedFeature = (IFeature) selectedObject;
			}
		} else {
			return false;
		}
		return selectedFeature.getStructure().getParent() != null;
	}

	@Override
	protected void updateProperties() {
		setEnabled(true);
	}

}
