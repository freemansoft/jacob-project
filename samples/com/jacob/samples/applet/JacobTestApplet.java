package com.jacob.samples.applet;

import java.applet.Applet;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 * Example applet to demonstrate:
 * 1. The use of jacob from an applet
 * 2. To show how to distribute the jacob native lib with the applet (also works with webstart)
 * 
 * Comment on 2.:
 * The way shown here is quite straight forward and it is not necessary to use 
 * a mechanism like the "DLLFromJARClassLoader" or something similar...
 * 
 *  @author ttreeck, www.nepatec.de
 * 
 */

public class JacobTestApplet extends Applet implements ActionListener {

	private static final long serialVersionUID = 4492492907986849158L;

	TextField in;
	TextField out;
	Button calc;
	ActiveXComponent sC = null;

	/**
	 * startup method
	 */
	@Override
	public void init() {
		setLayout(new FlowLayout());
		add(this.in = new TextField("1+1", 16));
		add(this.out = new TextField("?", 16));
		add(this.calc = new Button("Calculate"));
		this.calc.addActionListener(this);

	}

	/**
	 * Returns information about this applet. 
	 * According to the java spec:
	 * "An applet should override this method to return a String containing information about the author, version, and copyright of the applet." 
	 * 
	 * @return information about the applet. 
	 */
	@Override
	public String getAppletInfo() {
		return "Jacob Test Applet. Written by ttreeck, nepatec GmbH & Co. KG.\nhttp://www.nepatec.de";
	}
	
	/**
	 * Returns information about the parameters that are understood by this applet.
	 * According to the java spec:
	 * "An applet should override this method to return an array of Strings describing these parameters."
	 * 
	 * @return array with a set of three Strings containing the name, the type, and a description. 
	 */

	@Override
	public String[][] getParameterInfo(){
        return new String[][]{};
	}

	/**
	 * action method that receives button actions
	 * 
	 * @param ev the event
	 */
	public void actionPerformed(ActionEvent ev) {
		if (this.sC == null) {
			String lang = "VBScript";
			this.sC = new ActiveXComponent("ScriptControl");
			Dispatch.put(this.sC, "Language", lang);
		}
		Variant v = Dispatch.call(this.sC, "Eval", this.in.getText());
		this.out.setText(v.toString());
	}
}