/*
 * AbstractGenerator.java
 * Copyright (C) 2000-2002 Massimiliano Bigatti
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

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Vector;

/**
 * This class represents any of the Abstract classes generated by Jacobgen
 * 
 * @version $Id$
 * 
 */
public abstract class AbstractGenerator {
	protected final String classHeader = "/**\n"
			+ " * JacobGen generated file --- do not edit\n" + " *\n"
			+ " * (http://www.sourceforge.net/projects/jacob-project" + " */\n";

	protected String filename;
	protected String typelibName;
	protected String className;
	protected String baseClass;
	protected Vector<FieldItem> classFields;
	protected Vector<MethodItem> classMethods;
	protected String destinationPackage;
	protected Writer w;
	protected String guid;

	protected boolean simpleEnums = true;

	protected AbstractGenerator(String filename, String typelibName,
			String destinationPackage, String className, String baseClass,
			Vector<FieldItem> classFields, Vector<MethodItem> classMethods,
			String guid) {

		this.filename = filename;
		this.typelibName = typelibName;
		this.className = className;
		this.baseClass = baseClass;
		this.classFields = classFields;
		this.classMethods = classMethods;
		this.destinationPackage = destinationPackage;
		this.guid = guid;
	}

	public void generate() throws IOException {
		w = new FileWriter(filename);
		w.write(classHeader);

		writePackageDeclaration();
		writeImports();
		writeClassDeclaration();
		writeFields();
		writeConstructors();
		writeMethods();
		writeEndings();
		writeClassClosing();

		w.close();
	}

	protected void writeImports() throws IOException {
		w.write("import com.jacob.com.*;\n\n");
	}

	protected void writePackageDeclaration() throws IOException {
		if (destinationPackage != null
				&& destinationPackage.trim().length() > 0) {
			w.write("package " + destinationPackage + ";\n\n");
		}
	}

	protected abstract void writeClassDeclaration() throws IOException;

	protected abstract void writeFields() throws IOException;

	protected abstract void writeConstructors() throws IOException;

	protected abstract void writeMethods() throws IOException;

	protected void writeEndings() throws IOException {
	}

	protected void writeClassClosing() throws IOException {
		w.write("}\n");
	}
}