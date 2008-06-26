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

package net.sourceforge.jacob.generator.adapters.classInfo;

import java.util.StringTokenizer;

import net.sourceforge.jacob.generator.adapters.AdapterUtilities;
import net.sourceforge.jacob.generator.adapters.BaseAdapter;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;

public class AdapterClass extends BaseAdapter {
	final static private Logger log = Logger.getLogger(AdapterClass.class);
	private String className;
	private EClass createClass;
	private EPackage implPackage;
	private boolean isInterface;

	public AdapterClass(final EPackage companyPackage) {
		super("CLASS ", companyPackage);
		assert null != companyPackage;
	}

	public EClass getCreateClass() {
		assert null != createClass;
		return createClass;
	}

	@Override
	public boolean isAccepted(final String line) {
		if (!super.isAccepted(line)) {
			return false;
		}
		final StringTokenizer st = new StringTokenizer(line.substring(begining().length()).trim(), ";");
		if (st.countTokens() != 2) {
			return false;
		}
		className = st.nextToken().trim();
		className = AdapterUtilities.changeUnderscoredName(className);
		final String parameters = st.nextToken().trim();
		isInterface = "TKIND_DISPATCH".equals(parameters) || "TKIND_INTERFACE".equals(parameters);
		final boolean isClass = "TKIND_COCLASS".equals(parameters) || isInterface;
		if (isClass) {
			log.debug(className);
			createClass = (EClass) companyPackage.getEClassifier(className);
		} else
			createClass = null;
		return isClass;
	}

	@Override
	public void process(final String textLine) {
		createClass = null;
		createClass = EcoreFactory.eINSTANCE.createEClass();
		createClass.setInterface(isInterface);
		createClass.setAbstract(isInterface);
		createClass.setName(className);
		log.debug(className + "\t Yes its an interface: " + isInterface);
		if (isInterface) {
			companyPackage.getEClassifiers().add(createClass);
			final EClass createClass2 = EcoreFactory.eINSTANCE.createEClass();
			createClass2.setInterface(false);
			createClass2.setAbstract(false);
			createClass2.getESuperTypes().add(createClass);
			createClass2.setName(className);

			final EStructuralFeature createAttribute2 = AdapterUtilities.createAttribute("Dispatch", "Dispatch", companyPackage, createClass2);
			createAttribute2.setDerived(false);
			createAttribute2.setChangeable(true);
			createAttribute2.setVolatile(false);
			if (companyPackage.getESubpackages().size() == 0) {
				implPackage = AdapterUtilities.createSubPackage(companyPackage, "jacobimpl");
			}
			implPackage.getEClassifiers().add(createClass2);
		} else {
			final EStructuralFeature createAttribute = AdapterUtilities.createAttribute("Dispatch", "Dispatch", companyPackage, createClass);
			createAttribute.setDerived(false);
			createAttribute.setChangeable(true);
			createAttribute.setVolatile(false);
			companyPackage.getEClassifiers().add(createClass);
		}
	}
}
