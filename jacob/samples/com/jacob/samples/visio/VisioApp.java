package com.jacob.samples.visio;

import com.jacob.com.*;
import com.jacob.activeX.*;
import java.io.File;

/**
 * Created as part of sourceforge 1386454 to demonstrate returning values in event handlers
 * @author miles@rowansoftware.net
 *
 * This class represents the visio app itself
 */
public class VisioApp extends ActiveXComponent {


    public VisioApp() throws VisioException {
    	super("Visio.Application");
        setVisible(false);
    }

    /**
     * creates a DispatchEvents boject to register o as a listener
     * @param o
     */
    public void addEventListener(VisioEventListener o) {
        DispatchEvents events = new DispatchEvents(this, o);
        if (events == null){
        	System.out.println("You should never get null back when creating a DispatchEvents object");
        }
    }


    public void open(File f) throws VisioException {
        try {
            ActiveXComponent documents = new ActiveXComponent(getProperty("Documents").toDispatch());
            Variant[] args = new Variant[1];
            args[0] = new Variant(f.getPath());
            documents.invoke("Open",args);
        } catch (Exception e) {
            e.printStackTrace();
            throw new VisioException(e);
        }
    }

    public void save() throws VisioException {
        try {
            ActiveXComponent document = new ActiveXComponent(getProperty("ActiveDocument").toDispatch());
            document.invoke("Save");
        } catch (Exception e) {
            e.printStackTrace();
            throw new VisioException(e);
        }
    }

    /**
     * terminates visio 
     */
    public void quit() {
            System.out.println("Received quit()");
            // there can't be any open documents for this to work
            // you'll get a visio error if you don't close them
            ActiveXComponent document = new ActiveXComponent(getProperty("ActiveDocument").toDispatch());
            document.invoke("Close");
            invoke("Quit");
    }

    public void export(File f) throws VisioException {
        try {
            ActiveXComponent document = new ActiveXComponent(getProperty("ActivePage").toDispatch());
            Variant[] args = new Variant[1];
            args[0] = new Variant(f.getPath());
            document.invoke("Export",args);
        } catch (Exception e) {
            throw new VisioException(e);
        }
    }

    public void setVisible(boolean b) throws VisioException {
        try {
            setProperty("Visible",new Variant(b));
        } catch (Exception e) {
            throw new VisioException(e);
        }
    }
}
