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

import junit.framework.TestCase;

import com.Word.Application;
import com.Word.Document;
import com.Word.jacobimpl.Documents;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class TestWord11  extends TestCase {
	private ActiveXComponent activeXcomponent;
	private Application wordApp;
	private Document document;

	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		activeXcomponent = new ActiveXComponent("Word.Application");
		wordApp = new Application();
		wordApp.setDispatch(activeXcomponent);
	}

	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (document != null){
			document.Close(new Variant(false), null, null);
			document = null;
		}
		wordApp.Quit();
		activeXcomponent.safeRelease();
	}

	
	public void testLoad() {
		wordApp.setVisible(true);
		assertEquals("11.0", wordApp.getVersion());
	}
	
	public void testNewDocument() {
		testLoad();
		wordApp.Activate();
		Documents doc = new Documents();
		Dispatch documents = (Dispatch) wordApp.getDocuments();
		doc.setDispatch(documents);
		Document add = ((com.Word.Documents)doc).Add(Variant.VT_MISSING, Variant.VT_MISSING, Variant.VT_MISSING, Variant.VT_MISSING);
		add.PrintPreview();
		add.ClosePrintPreview();
		add.Close();
	}
	
}
