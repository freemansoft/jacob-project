package plug.popup.actions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import net.sourceforge.jacob.generator.TLBtoECOREtoCODE;

import org.eclipse.core.internal.resources.File;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class NewAction implements IObjectActionDelegate {

	private URI rawLocationURI;

	/**
	 * Constructor for Action1.
	 */
	public NewAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
//		Shell shell = new Shell();
//		IMenuCreator menuCreator = action.getMenuCreator();
		try {
			java.io.File file = new java.io.File (rawLocationURI);
			String output = file.getParent() + file.separatorChar +  "model/company";
			final TLBtoECOREtoCODE gener = new TLBtoECOREtoCODE("com.sourceforge.jacobGenerated", file.getAbsolutePath(), 	output);
			gener.generate();
		} catch (RuntimeException e) {
			System.out.flush();
			e.printStackTrace();
		} catch (final FileNotFoundException e) {
			System.out.flush();
			e.printStackTrace();
		} catch (final IOException e) {
			System.out.flush();
			e.printStackTrace();
		}
		System.out.println("Done");
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		System.out.println("NewAction.selectionChanged()");
		TreeSelection treeSelection= (TreeSelection)selection;
		Object firstElement2 = treeSelection.getFirstElement();
		System.out.println(firstElement2.getClass());
		File firstElement = (File) firstElement2;
		rawLocationURI = firstElement.getRawLocationURI();
	}

}
