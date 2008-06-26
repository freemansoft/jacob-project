package plug.popup.actions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import net.sourceforge.jacob.generator.TLBtoECOREtoCODE;
import net.sourceforge.jacob.generator.TestGenerator;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import sun.rmi.runtime.Log;

public class NewAction implements IObjectActionDelegate {

	private File selectedFile;

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
	@SuppressWarnings("restriction")
	public void run(IAction action) {
		try {
			final String output = selectedFile.getLocation().toFile().getAbsoluteFile().getParent() + java.io.File.separatorChar +  TestGenerator.MODEL_COMPANY;
			final TLBtoECOREtoCODE gener = new TLBtoECOREtoCODE("com.sourceforge.jacobGenerated",
					selectedFile.getLocation().toFile().getAbsolutePath(), 
					output);
			gener.generate();
			selectedFile.getProject().getFolder(TestGenerator.MODEL).refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (RuntimeException e) {
			System.out.flush();
			e.printStackTrace();
		} catch (final FileNotFoundException e) {
			System.out.flush();
			e.printStackTrace();
		} catch (final IOException e) {
			System.out.flush();
			e.printStackTrace();
		} catch (CoreException e) {
			System.out.flush();
			e.printStackTrace();
		}
		System.out.println("Done");
		
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
//		TreeSelection treeSelection= (TreeSelection)selection;
		StructuredSelection ss = (StructuredSelection) selection;
		Object firstElement2 = ss.getFirstElement();
		selectedFile = (File) firstElement2;
	}

}
