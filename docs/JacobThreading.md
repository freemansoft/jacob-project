# COM Apartments in JACOB

## introduction

The COM model for Threading differs from the Java model. In COM, each component can declare whether or not it support multi-threading. You can find some basic information about COM threading at:

*   [http://www.execpc.com/~gopalan/com/com_threading.html](http://www.execpc.com/~gopalan/com/com_threading.html)
*   [www.microsoft.com/msj/0297/apartment/apartment.htm](www.microsoft.com/msj/0297/apartment/apartment.htm)
*   [http://www.cswl.com/whiteppr/white/multithreading.html](http://www.cswl.com/whiteppr/white/multithreading.html)

The term **Single Threaded Apartment (STA)** refers to a thread where all COM objects created in that thread are single-threaded. This can manifest itself in two ways:  
Either all calls into that component are made from the same thread that created the component  
OR any call that is made from another thread gets serialized by COM. This serialization of calls is done by using a Windows message loop and posting messages to a hidden window (I'm not kidding). The way COM achieves this is by requiring any other thread to make calls through a local Proxy object rather than the original object (more on this when we discuss the JACOB DispatchProxy class).What does this mean for a Java application? If you are using a component that declares itself as **ThreadingModel "Apartment"** (you can find this out by looking in the registry under its CLSID), and you plan to create, use and destroy this component in one thread - then you are following the rules of an STA and you can declare the thread as an STA thread.On the other hand, if you need to make method calls from another thread (e.g. in a servlet) then you have a few choices. Either you create the component in its own STA, by extending `com.jacob.com.STA`, and use the `com.jacob.com.DispatchProxy` class to pass the Dispatch pointer between threads, or you can declare your thread as an MTA thread. In that case, COM will make the cross-thread calls into the STA that is running your component. If you create an Apartment threaded component in the MTA, COM will automatically create an STA for you and put your component in there, and then marshall all the calls.This brings us to the notion of a **Main STA**. COM requires that if there is any Apartment threaded component in your application, then the first STA created is tagged as the **Main STA**. COM uses the Main STA to create all the Apartment threaded components that are created from an MTA thread. The problem is that if you have already created an STA, then COM will pick that as the Main STA, and if you ever exit that thread - the whole application will exit.

## COM Threads in JACOB Prior to Version 1.7

Up until version 1.7 of JACOB, there was only one model available in JACOB:

*   Before version 1.6: All threads were automatically initialized as STAs.
*   In version 1.6: All threads were automatically initialized as MTAs.

The reason for the change in default was that tagging a Java thread as an STA can cause problems. Any Java Swing application, as well as servlets and applets need to be able to make calls from multiple threads. If you try to make COM method calls across STA threads - it will fail!In most cases, the default chosen by JACOB 1.6 (MTA) works fine, however there are some notable exceptions that have caused people grief. One such exception is in the case of MAPI. It turns out that if you try to create a MAPI object from an MTA thread - it simply fails and exits. This has caused some people to recompile JACOB 1.6 back with the STA default.There is another problem with MTA threads: when you are using Apartment threaded components, we already noted that COM will create the components in the Main STA. If one doesn't exist, COM will create it. However, this means that **all** Apartment threaded components will be created in the **same STA**. This creates a bottleneck, and a dependency between unrelated components. Also, if that STA exits, then all components are destroyed and the application will likely crash.

## COM Threads in JACOB Version 1.7

In Version 1.7 we have added finer grained control to allow the Java programmer to control how COM creates its components. Unfortunately, this means that you need to have a pretty good understanding of the dark and mystical subject of COM Apartments. There are a few different cases you need to consider:

### Default

If you simply run code that was created in Version 1.6 and ignore the COM threading issue, then you will get the same behavior as in 1.6: Each java thread will be an MTA thread, and all Apartment threaded components will be created by COM in its own Main STA. This typically works for most applications (exceptions noted above).

### Create Your Own Apartment

To declare an MTA thread use the following template:  
```
      ComThread.InitMTA();
      ...
      ...
      ComThread.Release();
```
If you want JACOB to create its own Main STA (rather than having COM choose an STA for you), then you should use:  

```
  Thread 1:
  ComThread.InitMTA(true); // a true tells JACOB to create a Main STA
  ...
  ...
  ComThread.Release();
  ...
  Thread 2:
  ComThread.InitMTA(); 
  ...
  ...
  ComThread.Release();
  ...
  ...
  ComThread.quitMainSTA();
```

In this case, you can also create the Main STA explicitly:  

```
  ComThread.startMainSTA();
  ...
  ...
  Thread 1:
  ComThread.InitMTA();
  ...
  ...
  ComThread.Release();
  ...
  Thread 2:
  ComThread.InitMTA(); 
  ...
  ...
  ComThread.Release();
  ...
  ...
  ComThread.quitMainSTA();
```


In the latter case, all Apartment threaded components will be created in JACOB's main STA. This still has all the problems of components sharing the same Main STA and creating a bottleneck. To avoid that, you can also create STA threads yourself:  


```
  ComThread.startMainSTA();
  ...
  ...
  Thread 1:
  ComThread.InitSTA();
  ...
  ...
  ComThread.Release();
  ...
  Thread 2:
  ComThread.InitMTA(); 
  ...
  ...
  ComThread.Release();
  ...
  ...
  ComThread.quitMainSTA();
```

In this example, thread 1 is an STA and thread 2 is an MTA. You could omit the call to ComThread.startMainSTA(), but if you do, then COM will make the first STA your main one, and then if you exit that thread, the application will crash.Actually, Thread 1 is _almost_ an STA. It's lacking a windows message loop. So, this type of STA is fine as long as you are creating a component and using it in the same thread, and not makind event callbacks.

### JACOB's STA Class

If you want to create an true STA where you can create a component and then let other threads call methods on it, then you need a windows message loop. JACOB provides a class called: `com.jacob.com.STA` which does exactly this.

```
  public class com.jacob.com.STA extends java.lang.Thread 
  {
      public com.jacob.com.STA();
      public boolean OnInit(); // you override this
      public void OnQuit(); // you override this
      public void quit();  // you can call this from ANY thread
  }
```

The STA class extends `java.lang.Thread` and it provides you with two methods that you can override: `OnInit` and `OnQuit`. These methods are called from the thread's `run` method so they will execute in the new thread. These methods allow you to create COM components (Dispatch objects) and release them. To create an STA, you subclass it and override the OnInit.The `quit` method is the **only** other method that can be called from any thread. This method uses the Win32 function `PostThreadMessage` to force the STA's windows message loop to exit, thereby terminating the thread.You will then need to make calls into the component that is running in the STA thread. If you simply try to make calls from another thread on a Dispatch object created in the STA thread, you will get a COM Exception. For more details see: [Don Box 'Effective COM' Rule 29](http://www.develop.com/effectivecom): Don't Access raw interface pointers across apartment boundaries.

### The DispatchProxy Class

Since you cannot call methods directly on a Dispatch object created in another STA JACOB provides a method for the class that created the Dispatch object to marshal it to your thread. This is done via the `com.jacob.com.DispatchProxy` class.
```
    public class DispatchProxy extends JacobObject {
        public DispatchProxy(Dispatch);
        public Dispatch toDispatch();

        public native void release();
        public void finalize();
    }
```
This class works as follows: the thread that created the Dispatch object constructs an instance of DispatchProxy(Dispatch) with the Dispatch as a parameter. This instance can then be accessed from another thread, which will invoke its `toDispatch` method proxy as if it were local to your thread. COM will do the inter-thread marshalling transparently.The following example is part of samples/test/ScriptTest2.java in the JACOB distribution. It shows how you can create the ScriptControl in one STA thread and make method calls on it from another:

```
  import com.jacob.com.*;
  import com.jacob.activeX.*;

  class ScriptTest2 extends STA
  {
    public static ActiveXComponent sC;
    public static Dispatch sControl = null;
    public static DispatchProxy sCon = null;

    public boolean OnInit()
    {
      try
      {
        System.out.println("OnInit");
        System.out.println(Thread.currentThread());
        String lang = "VBScript";

        sC = new ActiveXComponent("ScriptControl");
        sControl = (Dispatch)sC.getObject();

        // sCon can be called from another thread
        sCon = new DispatchProxy(sControl);

        Dispatch.put(sControl, "Language", lang);
        return true;
      }
      catch (Exception e)
      {
        e.printStackTrace();
        return false;
      }
    }

    public void OnQuit()
    {
      System.out.println("OnQuit");
    }

    public static void main(String args[]) throws Exception
    {
      try {
        ComThread.InitSTA();
        ScriptTest2 script = new ScriptTest2();
        Thread.sleep(1000);

        // get a thread-local Dispatch from sCon
        Dispatch sc = sCon.toDispatch();

        // call a method on the thread-local Dispatch obtained
        // from the DispatchProxy. If you try to make the same
        // method call on the sControl object - you will get a
        // ComException.
        Variant result = Dispatch.call(sc, "Eval", args[0]);
        System.out.println("eval("+args[0]+") = "+ result);
        script.quit();
        System.out.println("called quit");
      } catch (ComException e) {
        e.printStackTrace();
      }
      finally
      {
        ComThread.Release();
      }
    }
  }
```

You can try to modify the `Dispatch.call` invocation in the main thread to use `sControl` directly, and you will see that it fails. Notice that once we construct the ScriptTest2 object in the main thread, we sleep for a second to allow the other thread time to initialize itself.The STA thread calls `sCon = new DispatchProxy(sControl);` to save a global reference to the DispatchProxy that represents the `sControl` object. The main thread then calls: `Dispatch sc = sCon.toDispatch();` to get a local Dispatch proxy out of the DispatchProxy object.At most **one(!)** thread can call toDispatch(), and the call can be made only once. This is because a IStream object is used to pass the proxy, and it is only written once and closed when you read it. If you need multiple threads to access a Dispatch pointer, then create that many DispatchProxy objects. For more details please refer to the Don Box reference above.

### Recommended Procedure

*   It is recommended that you always allow JACOB to manage the main STA rather than letting COM create one on its own or tag one of yours.
*   Declare an STA thread using ComThread.InitSTA() if all your method calls for that component are going to come from the same thread.
*   If you want an STA thread that allows other threads to call into it, use the `com.jacob.com.STA` class as outlined above.
*   If you have a COM component that declares its ThreadingModel as "Free" or "Both", then use the MTA.
*   In most cases, if you need to make method calls from multiple threads, you can simply use MTA threads, and allow COM to create the components in the Main STA. You should only create your own STA's and DispatchProxy if you understand COM well enough to know when the MTA solution will fail or have other shortcomings.

There are 3 examples in the samples/test directory that demonstrate these cases:

*   ScriptTest.java - creates an STA for the ScriptControl component and runs all its method calls from that STA.
*   ScriptTest2.java - creates a separate STA thread, and makes method calls into the component from another thread using DispatchProxy.
*   ScriptTest3.java - creates a separate MTA thread, and makes method calls into the component from another MTA thread. This is simpler than ScriptTest2 for most applications.

### Default Threading Model

If you create a new thread, and don't call `ComThread.InitSTA()` or `ComThread.InitMTA()` on it, then the first time your java code creates a JacobObject, it will try to register itself with the ROT, and when it sees that the current thread is not initialized, it will initialize it as MTA. This means that the code to do this is no longer inside the native jni code - it is now in the `com.jacob.com.ROT` class. For more details on the ROT, see the [Object Lifetime](JacobComLifetime.html) document.