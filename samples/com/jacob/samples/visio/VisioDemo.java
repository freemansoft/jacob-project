package com.jacob.samples.visio;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import com.jacob.com.ComThread;

/**
 * Created as part of sourceforge 1386454 to demonstrate returning values in
 * event handlers
 * 
 * @author miles@rowansoftware.net
 *         <p>
 *         This file contains the main() that runs the demo
 *         <p>
 *         Look in the docs area at the Jacob usage document for command line
 *         options.
 */
public class VisioDemo extends JFrame implements ActionListener, WindowListener {

	/**
	 * Totally dummy value to make Eclipse quit complaining
	 */
	private static final long serialVersionUID = 1L;

	JButton chooseButton;
	JButton openButton;
	JPanel buttons;

	ImageIcon theImage;
	JLabel theLabel; // the icon on the page is actually this button's icon

	File selectedFile;
	/** everyone should get this through getVisio() */
	private VisioAppFacade visioProxy = null;

	// put this up here so it remembers where we were on the last choose
	JFileChooser chooser = null;

	public class VisioFileFilter extends FileFilter {
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			} else {
				return (f.getName().toUpperCase().endsWith(".VSD"));
			}
		}

		public String getDescription() {
			return "Visio Drawings";
		}
	}

	public VisioDemo() {
		super("Visio in Swing POC");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		buttons = new JPanel();
		getContentPane().setLayout(new BorderLayout());
		chooseButton = new JButton("Choose file to display");
		openButton = new JButton("Open file chosen file in Visio");
		chooseButton.addActionListener(this);
		openButton.addActionListener(this);
		buttons.add(chooseButton);
		buttons.add(openButton);
		getContentPane().add(buttons, BorderLayout.SOUTH);
		theLabel = new JLabel("");
		getContentPane().add(theLabel, BorderLayout.CENTER);
		addWindowListener(this);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(640, 480);
		this.setVisible(true);
	}

	public static void main(String args[]) throws Exception {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ComThread.InitSTA();
				VisioDemo poc = new VisioDemo();
				ComThread.Release();
				if (poc == null) {
					System.out.println("poc== null? That should never happen!");
				}
			}
		});
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == chooseButton) {
			pickFile();
		} else if (e.getSource() == openButton) {
			try {
				openFile();
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new RuntimeException(ex);
			}
		} else {
			System.out.println("Awesome!");
		}
	}

	private void pickFile() {
		try {
			chooser = new JFileChooser();
			// comment this out if you want it to always go to myDocuments
			chooser
					.setCurrentDirectory(new File(System
							.getProperty("user.dir")));
			chooser.setFileFilter(new VisioFileFilter());
			int returnVal = chooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				selectedFile = chooser.getSelectedFile();
				showSelectedFilePreview();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * use this private method instead of initializing on boot up so that
	 * instance and all listeners are created in this thread (event thread)
	 * rather than root thread
	 * 
	 * @return
	 */
	private VisioAppFacade getVisio() {
		if (visioProxy == null) {
			try {
				visioProxy = VisioAppFacade.getInstance();
			} catch (VisioException ve) {
				System.out.println("ailed to openFile()");
				ve.printStackTrace();
			}
		}
		return visioProxy;
	}

	private void showSelectedFilePreview() throws VisioException {
		if (selectedFile != null) {
			byte[] image = getVisio().createPreview(selectedFile);
			theImage = new ImageIcon(image);
			theLabel.setIcon(theImage);
		}
	}

	private void openFile() throws VisioException {
		try {
			getVisio().editDiagram(selectedFile);
			showSelectedFilePreview();
		} catch (VisioException ve) {
			System.out.println("ailed to openFile()");
			ve.printStackTrace();
		}

	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
		System.out.println("WINDOW CLOSED");
		if (visioProxy != null) {
			visioProxy.quit();
		}
	}

	public void windowClosing(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
		System.out.println("Fooboo");
	}

	public void windowOpened(WindowEvent e) {
	}
}
