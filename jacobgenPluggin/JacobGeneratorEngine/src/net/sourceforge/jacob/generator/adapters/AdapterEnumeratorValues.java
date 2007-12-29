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

package net.sourceforge.jacob.generator.adapters;

import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;

public class AdapterEnumeratorValues extends BaseAdapter {
	final static private Logger log = Logger.getLogger(AdapterEnumeratorValues.class);
	private final AdapterEnumerator enumerationClass;

	public AdapterEnumeratorValues(final EPackage companyPackage, final AdapterEnumerator enumerationClass) {
		super("VAR_CONST;", companyPackage);
		this.enumerationClass = enumerationClass;
	}

	@Override
	public void process(final String textLine) {
		/*
		 * VAR_CONST;wbemImpersonationLevelAnonymous;VT_I4;1
		 * VAR_CONST;wbemImpersonationLevelIdentify;VT_I4;2
		 * VAR_CONST;wbemImpersonationLevelImpersonate;VT_I4;3
		 * VAR_CONST;wbemImpersonationLevelDelegate;VT_I4;4
		 */
		final StringTokenizer st = new StringTokenizer(textLine.substring(begining().length()).trim(), ";");
		final String name = AdapterUtilities.changeUnderscoredName(st.nextToken().trim());
		final String type = AdapterUtilities.changeUnderscoredName(st.nextToken().trim());
		final int ordal = Integer.parseInt(st.nextToken().trim());
		final EEnumLiteral createEEnumLiteral = EcoreFactory.eINSTANCE.createEEnumLiteral();
		createEEnumLiteral.setValue(ordal);
		createEEnumLiteral.setName(name);
		createEEnumLiteral.setLiteral(name);

		enumerationClass.getCreateEEnum().getELiterals().add(createEEnumLiteral);
	}

}
