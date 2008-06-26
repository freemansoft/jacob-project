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
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

public class AdapterClassPropertyGet extends BaseAdapterClass {
	final static private Logger log = Logger.getLogger(AdapterClassPropertyGet.class);

	public AdapterClassPropertyGet(final EPackage companyPackage, final EClass classModel) {
		super("INVOKE_PROPERTYGET;", companyPackage, classModel);
	}

	@Override
	public void process(final String textLine) {
		final StringTokenizer st = new StringTokenizer(textLine.substring(begining().length()).trim(), ";");
		final String type = AdapterUtilities.changeUnderscoredName(st.nextToken().trim());
		String name = st.nextToken().trim();
		final String inOut = st.nextToken().trim();
		log.debug(classModel.getName() + "\t" + name + "\t" + type + "\t" + inOut);
		if (inOut.startsWith("[")) {
			log.warn(inOut);
		}

		if (AdapterUtilities.getIgnoreAttributeList().contains(name)) {// FIXME
			log.warn("Can not add '" + name + "' to" + classModel.getName());
			return;
		}
		name = AdapterUtilities.changeUnderscoredName(name);

		final EStructuralFeature structuralFeature = classModel.getEStructuralFeature(name);

		if (!"[]".equals(inOut)) {
			// Could already exist because of put
			if (null == structuralFeature) {
				final EStructuralFeature createAttribute = AdapterUtilities.createAttribute(type, name, companyPackage, classModel);
			}
			AdapterUtilities.createMethod(textLine, type, "get" + name, inOut, companyPackage, classModel);
		} else {
			AdapterUtilities.createAttribute(type, name, companyPackage, classModel);
		}
	}

}
