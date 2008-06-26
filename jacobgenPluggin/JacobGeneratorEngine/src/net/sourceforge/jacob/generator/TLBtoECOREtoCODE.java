/*
  Copyright (C) 2007  Robert Searle  
  
  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 2 of the
  License, or (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA 
 */

package net.sourceforge.jacob.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import net.sourceforge.jacob.generator.adapters.AdapterEnumerator;
import net.sourceforge.jacob.generator.adapters.AdapterEnumeratorValues;
import net.sourceforge.jacob.generator.adapters.AdapterUtilities;
import net.sourceforge.jacob.generator.adapters.BaseAdapter;
import net.sourceforge.jacob.generator.adapters.classInfo.AdapterClass;
import net.sourceforge.jacob.generator.adapters.classInfo.AdapterClassExtends;
import net.sourceforge.jacob.generator.adapters.classInfo.AdapterClassGUID;
import net.sourceforge.jacob.generator.adapters.classInfo.AdapterClassMethod;
import net.sourceforge.jacob.generator.adapters.classInfo.AdapterClassPropertyGet;
import net.sourceforge.jacob.generator.adapters.classInfo.AdapterClassPropertyPut;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import com.jacob.jacobgen.TypeLibInspector;

public class TLBtoECOREtoCODE {
	private final String fileLocation ;

	private final static Logger log = Logger.getLogger(TLBtoECOREtoCODE.class);

	private AdapterClass classAdapter;

	private AdapterEnumerator classEnumeration;
	private EPackage companyPackage = null;
	private final java.util.List<BaseAdapter> firstPassCommands = new ArrayList<BaseAdapter>();
	private final EPackage superPackage = EcoreFactory.eINSTANCE.createEPackage();
	private final String typelibFilename;

	public TLBtoECOREtoCODE(final String destinationPackage, final String typelibFilename, final String fileLocation) {
		this.typelibFilename = typelibFilename;
		this.fileLocation = fileLocation;

		// create a package that represents company
		final StringTokenizer packageTokenizer = new StringTokenizer(destinationPackage.trim(), ".");
		final String superPackageName = packageTokenizer.nextToken();

		superPackage.setName(superPackageName);
		superPackage.setNsPrefix(superPackageName + "NS");
		superPackage.setNsURI("http://generator.jacob.sourceforge.net/" + superPackageName);

		if (packageTokenizer.countTokens() > 1) {
			EPackage sup = superPackage;
			EPackage sub = superPackage;
			while (packageTokenizer.hasMoreTokens()) {
				sup = sub;
				final String subPackageName = packageTokenizer.nextToken();
				sub = AdapterUtilities.createSubPackage(sup, subPackageName);
			}
			companyPackage = sub;
		} else {
			companyPackage = superPackage;
		}
	}

	private void addAttributesParameters(final LineNumberReader lnr) throws IOException {
		String firstLine = lnr.readLine();// skip TYPELIB
		firstLine = lnr.readLine();
		EClass classModel = null;
		EEnum enumModel = null;
		while (firstLine != null) {
			firstLine = firstLine.trim();
			log.debug(firstLine);
			boolean accepted = false;
			if (classAdapter.isAccepted(firstLine)) {
				classModel = null;
				enumModel = null;
				accepted = true;
				classModel = classAdapter.getCreateClass();
			} else if (classEnumeration.isAccepted(firstLine)) {
				classModel = null;
				enumModel = null;
				accepted = true;
				enumModel = classEnumeration.getCreateEEnum();
			} else {
				for (final BaseAdapter typesAdapter : firstPassCommands) {
					accepted = typesAdapter.isAccepted(firstLine);
					if (accepted) {
						classModel = null;
						enumModel = null;
						break;
					}
				}
				if ((null != enumModel) && (false == accepted)) {
					// FIXME does an Enum have a GUID???
					accepted = firstLine.startsWith("GUID;{");

				}
				if ((null != classModel) && (false == accepted)) { // method or
					// attribute
					final List<BaseAdapter> secondPassCommands = new ArrayList<BaseAdapter>();
					secondPassCommands.clear();
					secondPassCommands.add(new AdapterClassMethod(companyPackage, classModel));
					secondPassCommands.add(new AdapterClassPropertyGet(companyPackage, classModel));
					secondPassCommands.add(new AdapterClassPropertyPut(companyPackage, classModel));
					secondPassCommands.add(new AdapterClassExtends(companyPackage, classModel));
					secondPassCommands.add(new AdapterClassGUID(companyPackage, classModel));
					for (final BaseAdapter command : secondPassCommands) {
						accepted = command.isAccepted(firstLine);
						if (accepted) {
							command.process(firstLine);
							break;
						}
					}
				}

			}
			if (false == accepted) {
				log.fatal("Problem with \t'" + firstLine + "'");
				System.exit(-1);
			}
			firstLine = lnr.readLine();
		}
	}

	/**
	 * @see http://www.devx.com/Java/Article/29093/1954?pf=true
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void generate() throws FileNotFoundException, IOException {
		// readFile(typelibFilename);
		final TypeLibInspector lib = new TypeLibInspector();
		assert null != typelibFilename;
		final byte[] queryInterface = lib.queryInterface(typelibFilename);
		System.out.flush();
		System.err.flush();
		final String st = new String(queryInterface);
		final String typeLib = new LineNumberReader(new StringReader(st)).readLine().substring("TYPELIB ".length()).trim();

		{
			final String subPackageName = typeLib;
			companyPackage = AdapterUtilities.createSubPackage(companyPackage, subPackageName);
		}

		packageAdapatersFactory();
		structureOnly(new LineNumberReader(new StringReader(st)));

		addAttributesParameters(new LineNumberReader(new StringReader(st)));
		save(superPackage);

		final File xmiFile = new File(fileLocation + ".xmi");
		final File ecoreFile = new File(fileLocation + ".ecore");
		if (ecoreFile.exists())
			ecoreFile.delete();
		xmiFile.renameTo(ecoreFile);
	}

	private void packageAdapatersFactory() {
		firstPassCommands.clear();
		classAdapter = new AdapterClass(companyPackage);
		firstPassCommands.add(classAdapter);
		classEnumeration = new AdapterEnumerator(companyPackage);
		firstPassCommands.add(classEnumeration);
		firstPassCommands.add(new AdapterEnumeratorValues(companyPackage, classEnumeration));
	}

	private void save(final EObject company) throws IOException {
		// create resource set and resource
		final ResourceSet resourceSet = new ResourceSetImpl();

		// Register XML resource factory
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());

		final Resource resource = resourceSet.createResource(URI.createFileURI(fileLocation + ".xmi"));
		// add the root object to the resource
		resource.getContents().add(company);
		// serialize resource – you can specify also serialization
		// options which defined on org.eclipse.emf.ecore.xmi.XMIResource
		resource.save(null);
	}

	private void structureOnly(final LineNumberReader lnr) throws IOException {
		String firstLine;
		do {
			firstLine = lnr.readLine();
			boolean accepted = false;
			for (final BaseAdapter command : firstPassCommands) {
				accepted = command.isAccepted(firstLine);
				if (accepted) {
					command.process(firstLine);
					break;
				}
			}
		} while (firstLine != null);
	}
}
