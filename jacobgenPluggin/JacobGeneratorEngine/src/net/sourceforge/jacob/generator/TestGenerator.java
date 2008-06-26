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

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * This is the root class for Jacobgen. It generates Jacob wrapper class for
 * windows DLLs. Run this class with no command line parameters to get a list of
 * the valid command line options
 * 
 * <code>
 * %JRE% -Xint com.jacob.jacobgen.Jacobgen %1 %2 %3 %4 %5
 * </code>
 * 
 * @version $Id$
 * @author Robert Searle
 */
public class TestGenerator {
	final static private Logger log = Logger.getLogger(TestGenerator.class);

	public static void main(final String[] args) {
		org.apache.log4j.BasicConfigurator.configure();

		if (args.length == 0) {
			System.out.println("JacobGen [options] typelibfile\n");
			System.out.println("Options:");
			System.out.println("\t-package:<destination package name>");
			System.out.println();
			System.exit(0);
		} else {
			final TestGenerator g = new TestGenerator(args);
			g.parseOptions(args);
			if (g.typelibFilename == null) {
				System.out.println("Jacobgen you need to specify an input file");
			} else {
				final TLBtoECOREtoCODE gener = new TLBtoECOREtoCODE(g.destinationPackage, g.typelibFilename, 	"model/company");
				try {
					gener.generate();
				} catch (final FileNotFoundException e) {
					System.out.flush();
					e.printStackTrace();
				} catch (final IOException e) {
					System.out.flush();
					e.printStackTrace();
				}
				System.out.println("Done");
			}
			System.exit(0);
		}
	}

	String destinationPackage;

	String typelibFilename;

	public TestGenerator(final String[] args) {
		parseOptions(args);
	}

	private void parseOptions(final String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-package:")) {
				destinationPackage = args[i].substring("-package:".length());
			} else {
				typelibFilename = args[i];
			}
		}
		log.debug("destinationPackage = '" + destinationPackage + "'");
		log.debug("typelibFilename = '" + typelibFilename + "'");
	}
}
