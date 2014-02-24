/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2013  FeatureIDE team, University of Magdeburg, Germany
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
 * See http://www.fosd.de/featureide/ for further information.
 */
package de.ovgu.featureide.ui.mpl.views.outline;

import java.util.Arrays;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.ovgu.featureide.core.CorePlugin;
import de.ovgu.featureide.core.IFeatureProject;
import de.ovgu.featureide.core.mpl.InterfaceProject;
import de.ovgu.featureide.core.mpl.MPLPlugin;
import de.ovgu.featureide.core.mpl.signature.ProjectStructure;
import de.ovgu.featureide.core.mpl.signature.abstr.AbstractClassFragment;
import de.ovgu.featureide.core.mpl.signature.abstr.AbstractMethodSignature;
import de.ovgu.featureide.core.mpl.signature.abstr.AbstractSignature;
import de.ovgu.featureide.core.mpl.signature.abstr.AbstractSignature.FeatureData;
import de.ovgu.featureide.core.mpl.signature.abstr.ClassFragmentComparator;
import de.ovgu.featureide.core.mpl.signature.abstr.SignatureComparator;
import de.ovgu.featureide.fm.core.Feature;

/**
 * Provides the content for the collaboration outline.
 * 
 * @author Reimar Schr�ter
 * @author Sebastian Krieter
 */
public class ContextOutlineTreeContentProvider implements ITreeContentProvider {
	ProjectStructure projectStructure = null;
	IFeatureProject featureProject = null;

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement == null || !(inputElement instanceof IFile)) {
			return new String[] { "no file found" };
		}

		IFile inputFile = (IFile) inputElement;

		IFeatureProject featureProject = CorePlugin
				.getFeatureProject(inputFile);
		this.featureProject = featureProject;

		if (featureProject != null) {
			if (!MPLPlugin.getDefault().isInterfaceProject(
					featureProject.getProject())) {
				return new String[] { "no interface project" };
			}
			String featureName = featureProject.getFeatureName(inputFile);
			String filename = (inputFile).getName();
			String classname;
			if (filename.endsWith(".java")) {
				classname = filename.substring(0,
						filename.length() - ".java".length());
			} else {
				classname = "";
			}

			projectStructure = MPLPlugin.getDefault()
					.extendedModules_getStruct(featureProject, featureName);
			if (projectStructure != null) {
				AbstractClassFragment[] ar = new AbstractClassFragment[projectStructure
						.getClasses().size()];
				int i = 0;
				for (AbstractClassFragment frag : projectStructure.getClasses()) {
					ar[i++] = frag;
				}
				Arrays.sort(ar, new ClassFragmentComparator(classname));

				return ar;
			} else {
				return new String[] { "loading..." };
			}
		} else {
			return new String[] { "no feature project" };
		}
	}

	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput != null && (newInput instanceof IFile)) {
			IFeatureProject featureProject = CorePlugin
					.getFeatureProject((IFile) newInput);
			if (featureProject != null) {
				String featureName = featureProject
						.getFeatureName((IResource) newInput);
				projectStructure = MPLPlugin.getDefault()
						.extendedModules_getStruct(featureProject, featureName);
			}
		}
	}

	@Override
	public Object[] getChildren(Object parentElement) {

		if (parentElement instanceof AbstractClassFragment) {
			AbstractClassFragment frag = (AbstractClassFragment) parentElement;
			Object[] ret = new Object[frag.getMembers().size()
					+ frag.getInnerClasses().size()];
			int i = 0;
			for (AbstractSignature curMember : frag.getMembers()) {
				ret[i++] = curMember;
			}
			for (AbstractClassFragment curMember : frag.getInnerClasses()
					.values()) {
				ret[i++] = curMember;
			}
			Arrays.sort(ret, new SignatureComparator());

			return ret;
		}
		if (parentElement instanceof AbstractMethodSignature) {
			AbstractMethodSignature sig = (AbstractMethodSignature) parentElement;
			InterfaceProject intp = MPLPlugin.getDefault().getInterfaceProject(
					featureProject.getProject());

			if (intp != null) {
				HashMap<String, Feature> l2 = new HashMap<String, Feature>();

				for (FeatureData featureData : sig.getFeatureData()) {
					Feature feature = featureProject.getFeatureModel().getFeature(intp.getFeatureName(featureData.getId()));
					if (!l2.containsKey(feature.getName())) {
						l2.put(feature.getName(), feature);
					}
				}
				return l2.values().toArray();
			}
		}

		return new Object[] { "No Children" };
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof AbstractClassFragment) {
			AbstractClassFragment frag = (AbstractClassFragment) element;
			return frag.getMemberCount() > 0;
		} else if (element instanceof AbstractMethodSignature) {
			AbstractMethodSignature sig = (AbstractMethodSignature) element;
			return sig.getFeatureData().length > 0;
		}
		return false;
	}
}