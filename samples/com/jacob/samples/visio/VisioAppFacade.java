package com.jacob.samples.visio;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created as part of sourceforge 1386454 to demonstrate returning values in
 * event handlers
 * 
 * @author miles@rowansoftware.net
 * 
 * This singleton isolates the demo app from the Visio instance object so that
 * you can't try and send messages to a dead Visio instance after quit() has
 * been called. Direct consumption of VisioApp would mean you could quit but
 * would still have a handle to the no longer connected application proxy
 * 
 */
public class VisioAppFacade {

	private VisioApp app;
	private static VisioAppFacade instance;

	/** extension for image files */
	public static final String IMAGE_EXT = ".jpg";
	/** extension for visio files */
	public static final String VISIO_EXT = ".vsd";
	/** the buffer size when we want to read stuff in */
	public static final int BUFFER_SIZE = 2048;

	/**
	 * Wrapper around Visio
	 * 
	 * @throws VisioException
	 */
	private VisioAppFacade() throws VisioException {
		this.app = new VisioApp();
		app.addEventListener(new VisioEventAdapter(app));
	}

	/**
	 * @return the singleton instance of Visio
	 * @throws VisioException
	 */
	public static VisioAppFacade getInstance() throws VisioException {
		if (instance == null) {
			instance = new VisioAppFacade();
		}
		return instance;
	}

	/**
	 * creates a preview in a temp file and returns the raw data.
	 * 
	 * @param visioData
	 * @return raw preview data
	 * @throws VisioException
	 */
	public byte[] createPreview(byte[] visioData) throws VisioException {
		byte[] preview;
		File tmpFile;
		try {
			tmpFile = getTempVisioFile();
			OutputStream out = new FileOutputStream(tmpFile);
			out.write(visioData);
			out.close();
		} catch (IOException ioe) {
			throw new VisioException(ioe);
		}
		preview = createPreview(tmpFile);
		tmpFile.delete();
		return preview;
	}

	/**
	 * reads a preview from a saved file
	 * 
	 * @param visioFile
	 * @return raw preview data
	 * @throws VisioException
	 */
	public byte[] createPreview(File visioFile) throws VisioException {
		try {
			File imageFile;
			imageFile = getTempImageFile();
			app.open(visioFile);
			app.export(imageFile);
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			FileInputStream fin = new FileInputStream(imageFile);
			copy(fin, bout);
			fin.close();
			imageFile.delete();
			bout.close();
			return bout.toByteArray();
		} catch (IOException ioe) {
			throw new VisioException(ioe);
		}
	}

	private void copy(InputStream in, OutputStream out) throws IOException {
		byte[] buff = new byte[BUFFER_SIZE];
		int read;
		do {
			read = in.read(buff);
			if (read > 0) {
				out.write(buff, 0, read);
			}
		} while (read > 0);
	}

	/**
	 * creates a preview from an input stream
	 * 
	 * @param in
	 * @return byte contents of the preview stream
	 * @throws VisioException
	 */
	public byte[] createPreview(InputStream in) throws VisioException {
		byte[] preview;
		// byte[] buff = new byte[2048];
		// int read = 0;
		OutputStream out;
		File tmpFile;

		try {
			tmpFile = getTempVisioFile();
			out = new FileOutputStream(tmpFile);
			copy(in, out);
			out.close();
		} catch (IOException ioe) {
			throw new VisioException(ioe);
		}

		preview = createPreview(tmpFile);
		tmpFile.delete();
		return preview;
	}

	/**
	 * opens the file in Visio and makes the editor visible
	 * 
	 * @param f
	 *            the reference to the Visio file to be opened
	 * @throws VisioException
	 */
	public void editDiagram(File f) throws VisioException {
		app.open(f);
		app.setVisible(true);
	}

	/**
	 * creates a temporary viso file
	 * 
	 * @return created visio temporary file
	 * @throws IOException
	 */
	private File getTempVisioFile() throws IOException {
		return File.createTempFile("java", VISIO_EXT);
	}

	/**
	 * creates a temporary image file and returns the File object
	 * 
	 * @return the created image file object
	 * @throws IOException
	 */
	private File getTempImageFile() throws IOException {
		return File.createTempFile("java", IMAGE_EXT);
	}

	/** exit visio */
	public void quit() {
		app.quit();
		instance = null;
	}
}
