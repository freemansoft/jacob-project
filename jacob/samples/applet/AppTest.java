import java.awt.*;
import java.awt.event.*;
import java.applet.*;

import com.jacob.com.*;
import com.jacob.activeX.*;

public class AppTest extends Applet implements ActionListener 
{
  TextField in;
  TextField out;
  Button calc;
  ActiveXComponent sC = null;
  Object sControl = null;

  public void init() 
  {
    setLayout(new FlowLayout());
    add(in = new TextField("1+1", 16));
    add(out = new TextField("?", 16));
    add(calc = new Button("Calculate"));
    calc.addActionListener(this);

  }

  public void actionPerformed(ActionEvent ev) 
  {
    if (sC == null) {
		  String lang = "VBScript";
      sC = new ActiveXComponent("ScriptControl");
      sControl = sC.getObject();
      Dispatch.put(sControl, "Language", lang);
		}
    Variant v = Dispatch.call(sControl, "Eval", in.getText());
    out.setText(v.toString());
  }
}
