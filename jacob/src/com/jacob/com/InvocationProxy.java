package com.jacob.com;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @version $Id$
 * @author joe
 * 
 * DispatchProxy wraps this class around any event handlers
 * before making the JNI call that sets up the link with EventProxy.
 * This means that EventProxy just calls invoke(String,Variant[])
 * against the instance of this class.  Then this class does 
 * reflection against the event listener to call the actual event methods.
 * All Event methods have the signature
 *  
 * 		<code> void eventMethodName(Variant[])</code>
 *  
 */
public class InvocationProxy {

	/**
	 * the object we will try and forward to.
	 */
	Object mTargetObject = null;
	
	/**
	 * constructs an invocation proxy that fronts for an event listener 
	 * @param pTargetObject
	 */
	protected InvocationProxy(Object pTargetObject){
		super();
		if (JacobObject.isDebugEnabled()){
			JacobObject.debug(
					"InvocationProxy: created for object "+pTargetObject);
		}
		mTargetObject = pTargetObject;
		if (mTargetObject == null){
			throw new IllegalArgumentException("InvocationProxy requires a target");
		}
		// JNI code apparently bypasses this check and could operate against
		// protected classes.  This seems like a security issue...
		// mayb eit was because JNI code isn't in a package?
		if (!java.lang.reflect.Modifier.isPublic(
				pTargetObject.getClass().getModifiers())){
			throw new IllegalArgumentException(
				"InvocationProxy only public classes can receive event notifications");
		}
	}
	
	/**
	 * the method actually invoked by EventProxy.cpp
	 * @param methodName name of method in mTargetObject we will invoke
	 * @param parameters Variant[] that is the single parameter to the method
	 */
	public void invoke(String methodName, Variant targetParameter[]){
		if (mTargetObject == null){ 
			if (JacobObject.isDebugEnabled()){
				JacobObject.debug(
						"InvocationProxy: received notification with no target set");
			}
			return;
		}
		Class targetClass = mTargetObject.getClass();
		if (methodName == null){
			throw new IllegalArgumentException("InvocationProxy: missing method name");
		}
		if (targetParameter == null){
			throw new IllegalArgumentException("InvocationProxy: missing Variant parameters");
		}
		try {
			// should wrap in debug flag
			if (JacobObject.isDebugEnabled()){
				JacobObject.debug("InvocationProxy: trying to invoke "+methodName
						+" on "+mTargetObject);
			}
			Method targetMethod = targetClass.getMethod(methodName,
					new Class[] {Variant[].class});
			if (targetMethod != null){
				// protected classes can't be invoked against even if they
				// let you grab the method.  you could do targetMethod.setAccessible(true);
				// but that should be stopped by the security manager
				targetMethod.invoke(mTargetObject,new Object[] {targetParameter});
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// this happens whenever the listener doesn't implement all the methods
			if (JacobObject.isDebugEnabled()){
				JacobObject.debug("InvocationProxy: listener doesn't implement "
						+ methodName);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			if (JacobObject.isDebugEnabled()){
				JacobObject.debug("InvocationProxy: probably tried to access public method on non public class"
						+ methodName);
			}
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
    /**
     * used by EventProxy.cpp to create variant objects in the right thread
     * @return
     */
    public Variant getVariant(){
    	return new VariantViaEvent();
    }

    /**
	 * helper method used by DispatchEvents to help clean up and reduce references
	 *
	 */
	protected void clearTarget(){
		mTargetObject = null;
	}
}
