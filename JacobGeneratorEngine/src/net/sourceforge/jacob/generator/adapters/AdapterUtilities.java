/*
  Copyright (C) 2007  Robert Searle  
  
  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 2 of the
  License, or (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA 
 */

package net.sourceforge.jacob.generator.adapters;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class AdapterUtilities {
	private static EPackage comJacobComPackage;

	final public static Set<String> ignoreAttributeList = new HashSet<String>();

	final static private Logger log = Logger.getLogger(AdapterUtilities.class);
	static final public String UNDERSCORE_REPLACEMENT = "JACOB";
	private static EDataType vtDispatchType = null;

	private static EDataType vtVariantType = null;

	static {
		ignoreAttributeList.add("defaultValue"); // FIXME Is it safe to
		// ignore this?
		ignoreAttributeList.add("DefaultValue"); // FIXME Is it safe to
		// ignore this?
		ignoreAttributeList.add("Name"); // FIXME Is it safe to ignore this?
		ignoreAttributeList.add("_NewEnum"); // FIXME Is it safe to ignore
		// this?
		ignoreAttributeList.add("");
		ignoreAttributeList.add(null);
	}

	public static String changeUnderscoredName(String className) {
		className = className.trim();
		if (className.startsWith("_")) { // FIXME
			className = AdapterUtilities.UNDERSCORE_REPLACEMENT + className;
		}
		return className;
	}

	public static EStructuralFeature createAttribute(final String type, final String name, final EPackage companyPackage, final EClass classModel) {
		final EClassifier classifier;
		classifier = getClassifier(type, companyPackage);
		if (classifier instanceof EClass) {
			// Attribute is a different class
			final EReference createEReference = EcoreFactory.eINSTANCE.createEReference();
			createEReference.setChangeable(false);
			createEReference.setVolatile(true);
			createEReference.setDerived(true);
			createEReference.setName(name);
			createEReference.setEType(classifier);
			classModel.getEStructuralFeatures().add(createEReference);
			return createEReference;
		} else {
			// Basic Attribute.
			final EAttribute createEAttribute = AdapterUtilities.createEAttribute(name, classifier);
			assert (null != classifier);
			classModel.getEStructuralFeatures().add(createEAttribute);
			return createEAttribute;
		}
	}

	public static EAttribute createEAttribute(final String name, final EClassifier classifier) {
		final EAttribute createEAttribute = EcoreFactory.eINSTANCE.createEAttribute();
		createEAttribute.setDerived(true); // no need for a class attribute
		createEAttribute.setTransient(true);
		createEAttribute.setVolatile(true);
		createEAttribute.setChangeable(false);
		createEAttribute.setName(name);
		createEAttribute.setEType(classifier);
		return createEAttribute;
	}

	public static void createMethod(final String textLine, final String returnType, final String functionName, final String parameters, final EPackage companyPackage,
			final EClass classModel) {
		log.debug(classModel.getName() + "\t" + functionName);
		final EOperation method = EcoreFactory.eINSTANCE.createEOperation();
		final EClassifier classifier = AdapterUtilities.getClassifier(returnType, companyPackage);
		method.setEType(classifier);
		method.setName(functionName);
		classModel.getEOperations().add(method);

		final String substring = parameters.substring(1, parameters.length() - 1);
		log.debug("\t" + substring);
		if (substring.length() != 0) {
			final StringTokenizer paramterTokens = new StringTokenizer(substring, ",");
			do {
				final String paramterValues = paramterTokens.nextToken().trim();
				log.debug("\t\t" + paramterValues);
				final StringTokenizer paramterToken = new StringTokenizer(paramterValues, ",} ");

				final String inOut;
				final String type;
				final String name;
				if (paramterToken.countTokens() == 3) {
					inOut = paramterToken.nextToken().trim();
					type = paramterToken.nextToken().trim();
					name = paramterToken.nextToken().trim();
				} else if (paramterToken.countTokens() == 2) {
					inOut = null;
					type = paramterToken.nextToken().trim();
					name = paramterToken.nextToken().trim();
				} else {
					// log.error(paramterValues);
					throw new RuntimeException("Could not process this line" + textLine);
				}

				final EParameter createEParameter = EcoreFactory.eINSTANCE.createEParameter();
				createEParameter.setName(name);

				final EClassifier parameterClassifier = AdapterUtilities.getClassifier(type, companyPackage);
				createEParameter.setEType(parameterClassifier);
				method.getEParameters().add(createEParameter);
			} while (paramterTokens.hasMoreTokens());
		}
	};

	/**
	 * @param packageTokenizer
	 * @param sup
	 * @param subPackageName
	 * @return
	 */
	public static EPackage createSubPackage(final EPackage sup, final String subPackageName) {
		EPackage sub;
		sub = EcoreFactory.eINSTANCE.createEPackage();
		sub.setName(subPackageName);
		sub.setNsPrefix(subPackageName + "NS");
		sub.setNsURI("http://generator.jacob.sourceforge.net/" + subPackageName);
		sup.getESubpackages().add(sub);
		return sub;
	}

	/**
	 * 
	 * @param returnType
	 * @param companyPackage
	 * @return
	 */
	public static EClassifier getClassifier(final String returnType, final EPackage companyPackage) {
		final EClassifier classifier;

		/*
		 * Eclipse Development using the Graphical Editing Framework and the
		 * Eclipse Modeling Framework
		 * 
		 * Declaring datatypes EMF provides datatypes such as EString and EInt,
		 * which represent the basic Java types that you can use for simple
		 * attributes. If you need to use a different Java type, you need to
		 * create an EDataType to represent it. For example, we use EString to
		 * represent attributes such as condition of ConditionalOutputPort and
		 * whileCondition for LoopTask from the WorkflowModel for the sample
		 * application. If we wanted to represent these conditions with a
		 * specific existing Java type instead, we would declare an EDataType
		 * corresponding to that type, as follows: <eClassifiers
		 * xsi:type="ecore:EDataType" name="Condition"
		 * instanceClassName="com.example.Condition"/>
		 */

		if ("VT_VOID".equals(returnType)) {
			classifier = null;
		} else if ("VT_BOOL".equals(returnType)) {
			classifier = EcorePackage.eINSTANCE.getEBoolean();
		} else if ("VT_UI2".equals(returnType) || "VT_UINT".equals(returnType) || "VT_INT".equals(returnType) || "VT_UI4".equals(returnType) || "VT_I4".equals(returnType)
				|| "VT_HRESULT".equals(returnType)) {
			classifier = EcorePackage.eINSTANCE.getEInt();
		} else if ("VT_R4".equals(returnType)) {
			classifier = EcorePackage.eINSTANCE.getEFloat();
		} else if ("VT_R8".equals(returnType)) {
			classifier = EcorePackage.eINSTANCE.getEDouble();
		} else if ("VT_DATE".equals(returnType)) {
			classifier = EcorePackage.eINSTANCE.getEDate();
		} else if ("VT_BSTR".equals(returnType)) {
			classifier = EcorePackage.eINSTANCE.getEString();
		} else if ("GUID".equals(returnType)) {
			classifier = EcorePackage.eINSTANCE.getELong();
		} else if ("VT_UNKNOWN".equals(returnType) || "EXCEPINFO".equals(returnType) || "DISPPARAMS".equals(returnType)) {
			classifier = EcorePackage.eINSTANCE.getEObject();
		} else {
			if ("VT_VARIANT".equals(returnType)) {
				classifier = getVariant(companyPackage);
			} else if ("VT_PTR".equals(returnType)) {
				// FIXME Is a VT_PTR really a object?
				classifier = EcorePackage.eINSTANCE.getEJavaObject();
			} else if ("IDispatch".equals(returnType) || "Dispatch".equals(returnType) || "VT_DISPATCH".equals(returnType)) {
				classifier = getDispatch(companyPackage);
			} else {
				final EClassifier possibleValue = companyPackage.getEClassifier(returnType);
				if (possibleValue == null) {
					log.warn(returnType + "\t" + "Cannot be mapped");
					classifier = EcorePackage.eINSTANCE.getEJavaObject();
				} else
					classifier = possibleValue;
			}
		}
		return classifier;
	}

	private static EPackage getComJacobComPackage(final EPackage companyPackage) {
		if (null == comJacobComPackage) {
			EPackage comPackage = companyPackage;
			while (!comPackage.getName().equals("com")) {
				comPackage = comPackage.getESuperPackage();
			}
			final EPackage comJacobPackage = EcoreFactory.eINSTANCE.createEPackage();
			comJacobPackage.setName("jacob");
			comJacobPackage.setNsPrefix("jacob2" + "NS");
			comJacobPackage.setNsURI("http://generator.jacob.sourceforge.net/" + "jacob2");
			comPackage.getESubpackages().add(comJacobPackage);

			comJacobComPackage = EcoreFactory.eINSTANCE.createEPackage();
			comJacobComPackage.setName("com");
			comJacobComPackage.setNsPrefix("com2" + "NS");
			comJacobComPackage.setNsURI("http://generator.jacob.sourceforge.net/" + "com2");
			comJacobPackage.getESubpackages().add(comJacobComPackage);
		}
		return comJacobComPackage;
	}

	public static EDataType getDispatch(final EPackage companyPackage) {
		if (vtDispatchType == null) {
			vtDispatchType = EcoreFactory.eINSTANCE.createEDataType();
			final Class<Dispatch> dispatchClass = Dispatch.class;
			vtDispatchType.setInstanceClassName(dispatchClass.getCanonicalName());
			vtDispatchType.setInstanceClass(dispatchClass);
			vtDispatchType.setName(dispatchClass.getSimpleName());
			getComJacobComPackage(companyPackage).getEClassifiers().add(vtDispatchType);
		}
		return vtDispatchType;
	}

	static public Set<String> getIgnoreAttributeList() {
		return ignoreAttributeList;
	}

	public static EDataType getVariant(final EPackage companyPackage) {
		if (vtVariantType == null) {
			vtVariantType = EcoreFactory.eINSTANCE.createEDataType();
			final Class<Variant> variantClass = Variant.class;
			vtVariantType.setInstanceClassName(variantClass.getCanonicalName());
			vtVariantType.setInstanceClass(variantClass);
			vtVariantType.setName(variantClass.getSimpleName());

			getComJacobComPackage(companyPackage).getEClassifiers().add(vtVariantType);
		}
		return vtVariantType;
	}

}
