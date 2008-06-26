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

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

public class AdapterClassPropertyPut extends BaseAdapterClass {
	final static private Logger log = Logger.getLogger(AdapterClassPropertyPut.class);

	public AdapterClassPropertyPut(final EPackage companyPackage, final EClass classModel) {
		super("INVOKE_PROPERTYPUT;", companyPackage, classModel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.jacob.generator.adapters.BaseAdapter#isAccepted(java.lang.String)
	 */
	@Override
	public boolean isAccepted(final String line) {
		if (super.isAccepted(line))
			return true;
		else
			return line.startsWith("INVOKE_PROPERTYPUTREF;");
	}

	@Override
	public void process(final String textLine) {
		final StringTokenizer st = new StringTokenizer(textLine, ";");
		final String value = st.nextToken().trim();
		final String type = AdapterUtilities.changeUnderscoredName(st.nextToken().trim());
		String name = st.nextToken().trim();
		log.debug(classModel.getName() + "\t" + name + "\t" + type);
		if (AdapterUtilities.getIgnoreAttributeList().contains(name)) {// FIXME
			log.warn("Can not add '" + name + "' to" + classModel.getName());
			return;
		}
		name = AdapterUtilities.changeUnderscoredName(name);

		EStructuralFeature structuralFeature = classModel.getEStructuralFeature(name);
		if (null == structuralFeature) {
			log.fatal("Attribute\t" + name);
			final EClassifier classifier = AdapterUtilities.getDispatch(companyPackage);
//			final EClassifier classifier = AdapterUtilities.getClassifier(type, companyPackage);
			structuralFeature = AdapterUtilities.createEAttribute(name, classifier);
			structuralFeature.setChangeable(false);
			structuralFeature.setVolatile(true);
			structuralFeature.setDerived(true);
			classModel.getEStructuralFeatures().add(structuralFeature);
		}
		structuralFeature.setChangeable(true);
	}
}
