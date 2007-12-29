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

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import net.sourceforge.jacob.generator.adapters.AdapterUtilities;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

public class AdapterClassMethod extends BaseAdapterClass {
	final static Set<String> ignoreList = new HashSet<String>();
	final static private Logger log = Logger.getLogger(AdapterClassMethod.class);

	static {
		ignoreList.add("QueryInterface");
		ignoreList.add("AddRef");
		ignoreList.add("Release");
		ignoreList.add("GetTypeInfoCount");
		ignoreList.add("GetTypeInfo");
		ignoreList.add("GetIDsOfNames");
		ignoreList.add("Invoke");
		ignoreList.add("");
		ignoreList.add(null);
	};

	public AdapterClassMethod(final EPackage companyPackage, final EClass classModel) {
		super("INVOKE_FUNC;", companyPackage, classModel);
	}

	@Override
	public void process(final String textLine) {
		// log.debug(textLine);
		final StringTokenizer st = new StringTokenizer(textLine.substring(begining().length()).trim(), ";");
		final String returnType = st.nextToken().trim();
		final String functionName = st.nextToken().trim();
		final String parameters = st.nextToken().trim();

		if (!ignoreList.contains(functionName)) {
			AdapterUtilities.createMethod(textLine, returnType, functionName, parameters, companyPackage, classModel);
		}
	}
}
