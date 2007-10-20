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
 * Applet test case
 */
public class AppTest extends Applet implements ActionListener {
	/**
	 * unique identifier added by Eclipse because this class is serializable
	 */
	private static final long serialVersionUID = -6676420357823607065L;

	TextField in;

	TextField out;

	Button calc;

	ActiveXComponent sC = null;

	/**
	 * startup method
	 */
	public void init() {
		setLayout(new FlowLayout());
		add(in = new TextField("1+1", 16));
		add(out = new TextField("?", 16));
		add(calc = new Button("Calculate"));
		calc.addActionListener(this);

	}

	/**
	 * action method that receives button actions
	 * 
	 * @param ev
	 *            the event
	 */
	public void actionPerformed(ActionEvent ev) {
		if (sC == null) {
			String lang = "VBScript";
			sC = new ActiveXComponent("ScriptControl");
			Dispatch.put(sC, "Language", lang);
		}
		Variant v = Dispatch.call(sC, "Eval", in.getText());
		out.setText(v.toString());
	}
}
