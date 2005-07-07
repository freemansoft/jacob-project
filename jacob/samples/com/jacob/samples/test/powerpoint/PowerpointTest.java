package com.jacob.samples.test.powerpoint;

 /* 
  * $Id$ 
  */ 
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;

/**
 * 
 * power point test program posted to sourceforge to demonstrate memory problem.
 * The submitter stated they had the problem on windows 2000 with office 2000
 * I have been unable to duplicate on windows XP with office 2003.
 * I am comitting this to the tree just in case we need to come back to it.
 */
public class PowerpointTest extends Thread {      
	private static final int NUM_THREADS = 5;
    protected static final int NUM_ITERATIONS = 50;    
          
    private static String POWERPOINT_TEST_PATH = 
        "D:\\jacob\\samples\\com\\jacob\\samples\\test\\powerpoint\\test";
        //"c:\\PowerpointTest\test"; 

    private int threadID;
    private Dispatch comPowerpoint;
    
    public PowerpointTest(int threadID, Dispatch comPowerpoint) {
        super("TestThread "+threadID);
        this.threadID = threadID;
        this.comPowerpoint = comPowerpoint;
    }
    
    public void run() {
        System.out.println("Thread \""+Thread.currentThread().getName()+"\" started");
        System.out.flush();
        ComThread.InitMTA();
        try {                               
            for (int i=0; i<NUM_ITERATIONS; i++) {
                if (i % 10 == 0) {
                    System.out.println(Thread.currentThread().getName()+": Iteration "+i);
                    System.out.flush();
                }
                Dispatch comPresentations = Dispatch.get(comPowerpoint,"Presentations").toDispatch();
                Dispatch comPresentation = Dispatch.call(comPresentations, 
    					"Open",
    					POWERPOINT_TEST_PATH+threadID+".ppt",
    					new Integer(0), 
    					new Integer(0), 
    					new Integer(0)).toDispatch();
                Dispatch.call(comPresentation, "Close");                   
            }
        } catch (Exception e) {
            System.err.println("Error in Thread \""+Thread.currentThread().getName()+"\":");
            e.printStackTrace();
        } finally {
            ComThread.Release();  
            System.out.println("Thread \""+Thread.currentThread().getName()+"\" finished");
            System.out.flush();
        }                
    }
    
    /**
     * main program that lets us run this as a test
     * @param args
     */
    public static void main(String[] args) {                  
        ComThread.InitMTA();
                
        ActiveXComponent component = new ActiveXComponent("Powerpoint.Application");
        Dispatch comPowerpoint = component.getObject();        	
        
        try {	                       	        
	        PowerpointTest[] threads = new PowerpointTest[NUM_THREADS];
		    for (int i=0; i<NUM_THREADS; i++) {
		        threads[i] = new PowerpointTest(i+1, comPowerpoint);
		        threads[i].start();		       
		    }
		    
		    boolean allThreadsFinished = false;
		    while (!allThreadsFinished) {	        
		        allThreadsFinished = true;
			    for (int i=0; i<NUM_THREADS; i++) {
			        if (threads[i].isAlive()) {
			            allThreadsFinished = false;
			            break;
			        }
			    }
			    if (!allThreadsFinished) {
				    try {
				        Thread.sleep(100);
				    } catch (InterruptedException e) {
				        // no op
				    }
			    }
		    }
		    
		    Dispatch.call(comPowerpoint,"Quit");	    
        } finally {
            ComThread.Release();
        }
	}    
}