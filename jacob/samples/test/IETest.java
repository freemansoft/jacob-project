package samples.test;

import com.jacob.com.*;
import com.jacob.activeX.*;
/*
 * This demonstrates the new event handling code in jacob 1.7
 * This example will open up IE and print out some of the events
 * it listens to as it havigates to web sites.
 * contributed by Niels Olof Bouvin mailto:n.o.bouvin@daimi.au.dk
 * and Henning Jae jehoej@daimi.au.dk
 */

class IETest
{
    public static void main(String[] args)
    {
      ActiveXComponent ie = new ActiveXComponent("clsid:0002DF01-0000-0000-C000-000000000046");
      Dispatch ieo = ie.getObject();
      try {
        ieo.setProperty("Visible", true);
        ieo.setProperty("AddressBar", true);
        System.out.println(ieo.getPropertyAsString("Path"));
        ieo.setProperty("StatusText", "My Status Text");
  
        IEEvents ieE = new IEEvents();
        DispatchEvents de = new DispatchEvents((Dispatch) ieo, ieE,"InternetExplorer.Application.1");
        Variant optional = new Variant();
        optional.noParam();
  
        ieo.setProperty("Navigate", "http://www.danadler.com/jacob");
        try { Thread.sleep(5000); } catch (Exception e) {}
        ieo.call("Navigate", "http://groups.yahoo.com/group/jacob-project");
        try { Thread.sleep(5000); } catch (Exception e) {}
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        ie.call("Quit", new Variant[] {});
      }
    }
}

class IEEvents 
{
    public void BeforeNavigate2(Variant[] args) {
      System.out.println("BeforeNavigate2");
    }

    public void CommandStateChanged(Variant[] args) {
      System.out.println("CommandStateChanged");
    }

    public void DocumentComplete(Variant[] args) {
      System.out.println("DocumentComplete");
    }

    public void DownloadBegin(Variant[] args) {
      System.out.println("DownloadBegin");
    }

    public void DownloadComplete(Variant[] args) {
      System.out.println("DownloadComplete");
    }

    public void NavigateComplete2(Variant[] args) {
      System.out.println("NavigateComplete2");
    }

    public void NewWindow2(Variant[] args) {
      System.out.println("NewWindow2");
    }

    public void OnFullScreen(Variant[] args) {
      System.out.println("OnFullScreen");
    }

    public void OnMenuBar(Variant[] args) {
      System.out.println("OnMenuBar");
    }

    public void OnQuit(Variant[] args) {
      System.out.println("OnQuit");
    }

    public void OnStatusBar(Variant[] args) {
      System.out.println("OnStatusBar");
    }

    public void OnTheaterMode(Variant[] args) {
      System.out.println("OnTheaterMode");
    }

    public void OnToolBar(Variant[] args) {
      System.out.println("OnToolBar");
    }

    public void OnVisible(Variant[] args) {
      System.out.println("OnVisible");
    }

    public void ProgressChange(Variant[] args) {
      System.out.println("ProgressChange");
    }

    public void PropertyChange(Variant[] args) {
      System.out.println("PropertyChange");
    }

    public void StatusTextChange(Variant[] args) {
      System.out.println("StatusTextChange");
    }

    public void TitleChange(Variant[] args) {
      System.out.println("TitleChange");
    }
}
