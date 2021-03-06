/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2019  FeatureIDE team, University of Magdeburg, Germany
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
package de.ovgu.featureide.fm.core.configuration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;

import org.junit.Test;

import de.ovgu.featureide.Commons;
import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.io.manager.FeatureModelManager;
import de.ovgu.featureide.fm.core.job.LongRunningWrapper;

/**
 * Test class for the reading configurations.
 *
 * @author Stefan Krueger
 * @author Florian Proksch
 */
public class TConfigurationReader {

	protected static File MODEL_FILE_FOLDER = Commons.getRemoteOrLocalFolder("analyzefeaturemodels/");

	private static final FileFilter filter = new FileFilter() {

		@Override
		public boolean accept(File pathname) {
			return pathname.getName().endsWith(".xml");
		}
	};

	String text = "";
	InputStream a; // = new InputStream(text.getBytes(Charset.availableCharsets().get("UTF-8")));

	private final IFeatureModel FM_test_1 = init("test_5.xml");
	private final FeatureModelFormula formula = new FeatureModelFormula(FM_test_1);

	private final IFeatureModel init(String name) {
		IFeatureModel fm = null;
		final File[] listFiles = MODEL_FILE_FOLDER.listFiles(filter);
		assertNotNull(listFiles);
		for (final File f : listFiles) {
			if (f.getName().equals(name)) {
				fm = FeatureModelManager.load(f.toPath());
				if (fm != null) {
					break;
				}
			}
		}
		return fm;
	}

	private boolean isValid(Configuration configuration) {
		final IConfigurationPropagator propagator = new ConfigurationPropagator(formula, configuration);
		return LongRunningWrapper.runMethod(propagator.isValid());
	}

	private boolean isValidAfterUpdate(Configuration configuration) {
		final IConfigurationPropagator propagator = new ConfigurationPropagator(formula, configuration);
		LongRunningWrapper.runMethod(propagator.update());
		return LongRunningWrapper.runMethod(propagator.isValid());
	}

	@Test
	public void isValidConfiguration() {
		final Configuration c = new Configuration(formula);
		final DefaultFormat r = new DefaultFormat();
		r.read(c, "C#");

		assertFalse(isValid(c));
	}

	@Test
	public void isValidConfiguration2() {
		final Configuration c = new Configuration(formula);
		c.setManual("C#", Selection.SELECTED);
		c.setManual("Python Ruby", Selection.SELECTED);
		c.setManual("Bash   script   ", Selection.UNSELECTED);
		c.setManual("C++", Selection.SELECTED);
		assertFalse(isValidAfterUpdate(c));
	}

	@Test
	public void isValidConfiguration3() {
		final Configuration c = new Configuration(formula);
		c.setManual("C#", Selection.SELECTED);
		c.setManual("Python Ruby", Selection.SELECTED);
		c.setManual("Bash   script   ", Selection.SELECTED);
		c.setManual("C++", Selection.SELECTED);
		assertTrue(isValidAfterUpdate(c));
	}

	@Test
	public void isValidConfiguration4() {
		final Configuration c = new Configuration(formula);
		final DefaultFormat r = new DefaultFormat();
		r.read(c, "C# \njute \n \"Bash   script   \"");
		assertFalse(isValid(c));
	}

	@Test
	public void isValidConfiguration5() {
		final Configuration c = new Configuration(formula);
		final DefaultFormat r = new DefaultFormat();
		r.read(c, "C# \njute \n \"Bash   script   \" \"Python Ruby\"");
		assertTrue(isValid(c));
	}

	@Test
	public void isValidConfiguration6() {
		final Configuration c = new Configuration(formula);
		final DefaultFormat r = new DefaultFormat();
		r.read(c, "C# \njute \n \"Bash   script   \" \n\"Python Ruby\" \n\"C++\"");
		assertTrue(isValid(c));
	}

	@Test
	public void isValidConfiguration7() {
		final Configuration c = new Configuration(formula);
		final DefaultFormat r = new DefaultFormat();
		r.read(c, "C# \njute \n \"Bash   script    \n\"Python Ruby\" \n\"C++\"");
		assertFalse(isValid(c));
	}

	@Test
	public void isValidConfiguration8() {
		final Configuration c = new Configuration(formula);
		final DefaultFormat r = new DefaultFormat();
		r.read(c, "C# \nj ute \n \"Bash   script    \"\n\"Python Ruby\" \n\"C++\"");
		assertFalse(isValid(c));
	}

	@Test
	public void isValidConfiguration9() {
		final Configuration c = new Configuration(formula);
		final DefaultFormat r = new DefaultFormat();
		r.read(c, "C# \njute \n \"Bash   script   \" Python Ruby\" \n\"C++\"");
		assertFalse(isValid(c));
	}

	@Test
	public void isValidConfiguration10() {
		final Configuration c = new Configuration(formula);
		final DefaultFormat r = new DefaultFormat();
		r.read(c, "jute \"Bash   script   \" \"Python C# Ruby\" \"C++\"");
		assertTrue(isValid(c));
	}
}
