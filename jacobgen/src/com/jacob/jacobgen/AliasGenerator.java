/*
 * AliasGenerator.java
 * Copyright (C) 2002 Massimiliano Bigatti
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.jacob.jacobgen;

import java.io.IOException;

/**
 * This class creates any of the wrapper classes that map to a DLL object of
 * type TKIND_ALIAS
 * 
 * @version $Id$
 */
class AliasGenerator extends AbstractGenerator {

	protected AliasGenerator(String filename, String typelibName,
			String destinationPackage, String className, String baseClass) {
		super(filename, typelibName, destinationPackage, className, baseClass,
				null, null, null);
	}

	protected void writeClassDeclaration() throws IOException {
		w.write("public interface " + className + " extends " + baseClass
				+ " {\n\n");
	}

	protected void writeFields() throws IOException {
	}

	protected void writeConstructors() throws IOException {
	}

	protected void writeMethods() throws IOException {
	}

}
