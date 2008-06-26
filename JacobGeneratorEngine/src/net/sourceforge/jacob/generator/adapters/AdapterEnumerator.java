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
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;

public class AdapterEnumerator extends BaseAdapter {
	final static private Logger log = Logger.getLogger(AdapterEnumerator.class);
	private EEnum createEEnum;
	private String enumName;

	public AdapterEnumerator(final EPackage companyPackage) {
		super("CLASS ", companyPackage);
	}

	public EEnum getCreateEEnum() {
		assert null != createEEnum;
		return createEEnum;
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
		enumName = AdapterUtilities.changeUnderscoredName(st.nextToken().trim());
		final String parameters = st.nextToken().trim();
		return "TKIND_ENUM".equals(parameters);
	}

	@Override
	public void process(final String textLine) {
		log.debug(enumName);
		createEEnum = EcoreFactory.eINSTANCE.createEEnum();
		createEEnum.setName(enumName);
		companyPackage.getEClassifiers().add(createEEnum);
	}
}
