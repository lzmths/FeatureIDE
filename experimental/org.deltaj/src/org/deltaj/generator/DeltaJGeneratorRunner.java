/*
 * generated by Xtext
 */
package org.deltaj.generator;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.generator.IGenerator;
import org.eclipse.xtext.generator.JavaIoFileSystemAccess;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.Issue;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

public class DeltaJGeneratorRunner {

	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("Aborting: no path to EMF resource provided!");
			return;
		}
		Injector injector = new org.deltaj.DeltaJStandaloneSetupGenerated()
				.createInjectorAndDoEMFRegistration();
		DeltaJGeneratorRunner main = injector
				.getInstance(DeltaJGeneratorRunner.class);
		main.runGenerator(args[0]);
	}

	@Inject
	private Provider<ResourceSet> resourceSetProvider;

	@Inject
	private IResourceValidator validator;

	@Inject
	private IGenerator generator;

	@Inject
	private JavaIoFileSystemAccess fileAccess;

	protected String outputPath;

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public List<Issue> runGenerator(String string) {
		// load the resource
		ResourceSet set = resourceSetProvider.get();
		Resource resource = set.getResource(URI.createURI(string), true);

		// validate the resource
		List<Issue> issues = validator.validate(resource, CheckMode.ALL,
				CancelIndicator.NullImpl);
		if (!issues.isEmpty()) {
			for (Issue issue : issues) {
				System.err.println(issue);
			}
			return issues;
		}

		fileAccess.setOutputPath(outputPath);
		generator.doGenerate(resource, fileAccess);

		System.out.println("Code generation finished.");

		return issues;
	}
	
	/**
	 * Loads all files and run the generator on the last one.
	 * @param strings
	 * @return
	 */
	public List<Issue> runGeneratorOnFiles(String... strings) {
		// load the resource
		ResourceSet set = resourceSetProvider.get();
		Resource resource = null;
		for (String string : strings) {
			resource = set.getResource(URI.createURI(string), true);

			// validate the resource
			List<Issue> issues = validator.validate(resource, CheckMode.ALL,
					CancelIndicator.NullImpl);
			if (!issues.isEmpty()) {
				for (Issue issue : issues) {
					System.err.println(issue);
				}
				return issues;
			}
		}

		fileAccess.setOutputPath(outputPath);
		generator.doGenerate(resource, fileAccess);

		System.out.println("Code generation finished.");

		return Collections.emptyList();
	}

	/**
	 * Cleans all contents of the output folder (all files and directories) but
	 * not the output folder itself
	 */
	public void cleanOutputFolder() {
		String folderToClean = getOutputPath();
		File f = new File(folderToClean);
		if (f.exists()) {
			cleanAllFilesAndDirectories(f, false);
		}
	}

	protected void cleanAllFilesAndDirectories(File parentFolder,
			boolean cleanParentFolder) {
		final File[] contents = parentFolder.listFiles();
		for (int j = 0; contents != null && j < contents.length; j++) {
			final File file = contents[j];
			if (file.isDirectory()) {
				cleanAllFilesAndDirectories(file, true);
			} else {
				file.delete();
			}
		}
		if (cleanParentFolder)
			parentFolder.delete();
	}
}
