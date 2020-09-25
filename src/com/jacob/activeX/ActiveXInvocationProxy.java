/*
 * Copyright (c) 1999-2004 Sourceforge JACOB Project.
 * All rights reserved. Originator: Dan Adler (http://danadler.com).
 * Get more information about JACOB at http://sourceforge.net/projects/jacob-project
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.jacob.activeX;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.jacob.com.InvocationProxy;
import com.jacob.com.NotImplementedException;
import com.jacob.com.Variant;

/**
 * RELEASE 1.12 EXPERIMENTAL.
 * <p>
 * This class that lets event handlers receive events with all java objects as
 * parameters. The standard Jacob event methods all accept an array of Variant
 * objects. When using this class, you can set up your event methods as regular
 * java methods with the correct number of parameters of the correct java type.
 * This does NOT work for any event that wishes to accept a call back and modify
 * the calling parameters to tell windows what to do. An example is when an
 * event lets the receiver cancel the action by setting a boolean flag to false.
 * The java objects cannot be modified and their values will not be passed back
 * into the originating Variants even if they could be modified.
 * <p>
 * This class acts as a proxy between the windows event callback mechanism and
 * the Java classes that are looking for events. It assumes that all of the Java
 * classes that are looking for events implement methods with the same names as
 * the windows events and that the implemented methods native java objects of
 * the type and order that match the windows documentation. The methods can
 * return void or a Variant that will be returned to the calling layer. All
 * Event methods that will be recognized by InvocationProxyAllEvents have the
 * signature
 * 
 * <code> void eventMethodName(Object,Object...)</code> or
 * <code> Object eventMethodName(Object,Object...)</code>
 */
public class ActiveXInvocationProxy extends InvocationProxy {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jacob.com.InvocationProxy#invoke(java.lang.String,
	 *      com.jacob.com.Variant[])
	 */
	@SuppressWarnings("unchecked")
	public Variant invoke(String methodName, Variant targetParameters[]) {
		Variant mVariantToBeReturned = null;
		if (mTargetObject == null) {
			// structured programming guidlines say this return should not be up
			// here
			return null;
		}
		Class targetClass = mTargetObject.getClass();
		if (methodName == null) {
			throw new IllegalArgumentException(
					"InvocationProxy: missing method name");
		}
		if (targetParameters == null) {
			throw new IllegalArgumentException(
					"InvocationProxy: missing Variant parameters");
		}
		try {
			Method targetMethod;
			Object parametersAsJavaObjects[] = getParametersAsJavaObjects(targetParameters);
			Class parametersAsJavaClasses[] = getParametersAsJavaClasses(parametersAsJavaObjects);
			targetMethod = targetClass.getMethod(methodName,
					parametersAsJavaClasses);
			if (targetMethod != null) {
				// protected classes can't be invoked against even if they
				// let you grab the method. you could do
				// targetMethod.setAccessible(true);
				// but that should be stopped by the security manager
				Object mReturnedByInvocation = null;
				mReturnedByInvocation = targetMethod.invoke(mTargetObject,
						parametersAsJavaObjects);
				if (mReturnedByInvocation == null) {
					mVariantToBeReturned = null;
				} else if (!(mReturnedByInvocation instanceof Variant)) {
					mVariantToBeReturned = new Variant(mReturnedByInvocation);
				} else {
					mVariantToBeReturned = (Variant) mReturnedByInvocation;
				}
			}
		} catch (SecurityException e) {
			// what causes this exception?
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// this happens whenever the listener doesn't implement all the
			// methods
		} catch (IllegalArgumentException e) {
			// we can throw these inside the catch block so need to re-throw it
			Exception oneWeShouldToss = new IllegalArgumentException(
					"Unable to map parameters for method " + methodName + ": "
							+ e.toString());
			oneWeShouldToss.printStackTrace();
		} catch (IllegalAccessException e) {
			// can't access the method on the target instance for some reason
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// invocation of target method failed
			e.printStackTrace();
		}
		return mVariantToBeReturned;

	}

	/**
	 * creates a method signature compatible array of classes from an array of
	 * parameters
	 * 
	 * @param parametersAsJavaObjects
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Class[] getParametersAsJavaClasses(Object[] parametersAsJavaObjects) {
		if (parametersAsJavaObjects == null) {
			throw new IllegalArgumentException(
					"This only works with an array of parameters");
		}
		int numParameters = parametersAsJavaObjects.length;
		Class parametersAsJavaClasses[] = new Class[numParameters];
		for (int parameterIndex = 0; parameterIndex < numParameters; parameterIndex++) {
			Object oneParameterObject = parametersAsJavaObjects[parameterIndex];
			if (oneParameterObject == null) {
				parametersAsJavaClasses[parameterIndex] = null;
			} else {
				Class oneParameterClass = oneParameterObject.getClass();
				parametersAsJavaClasses[parameterIndex] = oneParameterClass;
			}
		}
		return parametersAsJavaClasses;
	}

	/**
	 * converts an array of Variants to their associated Java types
	 * 
	 * @param targetParameters
	 * @return
	 */
	private Object[] getParametersAsJavaObjects(Variant[] targetParameters) {
		if (targetParameters == null) {
			throw new IllegalArgumentException(
					"This only works with an array of parameters");
		}
		int numParameters = targetParameters.length;
		Object parametersAsJavaObjects[] = new Object[numParameters];
		for (int parameterIndex = 0; parameterIndex < numParameters; parameterIndex++) {
			Variant oneParameterObject = targetParameters[parameterIndex];
			if (oneParameterObject == null) {
				parametersAsJavaObjects[parameterIndex] = null;
			} else {
				try {
					parametersAsJavaObjects[parameterIndex] = oneParameterObject
							.toJavaObject();
				} catch (NotImplementedException nie) {
					throw new IllegalArgumentException(
							"Can't convert parameter " + parameterIndex
									+ " type " + oneParameterObject.getvt()
									+ " to java object: " + nie.getMessage());
				}
			}
		}
		return parametersAsJavaObjects;
	}

}
