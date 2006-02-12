package com.jacob.samples.visio;

import java.io.*;

/**
 * Created as part of sourceforge 1386454 to demonstrate returning values in event handlers
 * @author miles@rowansoftware.net
 *
 * This singleton isolates the demo app from the Visio instance object so that
 * you can't try and send messages to a dead Visio instance after quit() has been
 * called.  Direct consumption of VisioApp would mean you could quit but would 
 * still have a handle to the no longer connected application proxy
 * 
 */
public class VisioAppFacade {


    private VisioApp app;
    private static VisioAppFacade instance;

    public static final String IMAGE_EXT = ".jpg";
    public static final String VISIO_EXT = ".vsd";
    public static final int  BUFFER_SIZE = 2048;

    private VisioAppFacade() throws VisioException {
        this.app = new VisioApp();
		app.addEventListener(new VisioEventAdapter(app));
    }

    public static VisioAppFacade getInstance() throws VisioException {
        if (instance == null) {
            instance = new VisioAppFacade();
        } 
        return instance;
    }

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
                out.write(buff,0,read);
            }
        } while (read > 0);
    }

    public byte[] createPreview(InputStream in) throws VisioException {
        byte[] preview;
        //byte[] buff = new byte[2048];
        //int read = 0;
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

    public void editDiagram(File f) throws VisioException {
    	app.open(f);
    	app.setVisible(true);
    }

    private File getTempVisioFile() throws IOException {
        return File.createTempFile("java",VISIO_EXT);
    }

    private File getTempImageFile() throws IOException {
        return File.createTempFile("java",IMAGE_EXT);
    }

    public void quit() {
        app.quit();
        instance = null;
    }
}
