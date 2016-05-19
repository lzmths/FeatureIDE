/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2016  FeatureIDE team, University of Magdeburg, Germany
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
package de.ovgu.featureide.fm.core.base.impl;

import de.ovgu.featureide.fm.core.Preferences;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureProperty;

/**
 * 
 * @author Sebastian Krieter
 * 
 */
public class ExtendedFeatureProperty extends FeatureProperty {

	public ExtendedFeatureProperty(IFeature correspondingFeature) {
		super(correspondingFeature);
	}

	protected ExtendedFeatureProperty(FeatureProperty oldProperty, IFeature correspondingFeature) {
		super(oldProperty, correspondingFeature);
	}

	@Override
	public String getDisplayName() {
		final String name = correspondingFeature.getName();
		switch (Preferences.getDefaultFeatureNameScheme()) {
			case Preferences.SCHEME_SHORT:
				int separatorIndex = name.lastIndexOf(".");
				return name.substring(separatorIndex + 1);
			case Preferences.SCHEME_LONG: 
			default: 
				return name;
		}
	}

	@Override
	public IFeatureProperty clone(IFeature newFeature) {
		return new ExtendedFeatureProperty(this, newFeature);
	}

}