/**
 * 
 */
package com.jacob.com;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Date;

/**
 * A utility class used to convert between Java objects and Variants
 */
public final class VariantUtilities {
	private VariantUtilities() {
		// utility class with only static methods don't need constructors
	}

	/**
	 * Populates a variant object from a java object. This method attempts to
	 * figure out the appropriate Variant type
	 * 
	 * @param targetVariant
	 * @param pValueObject
	 * @param fByRef
	 */
	protected static void populateVariant(Variant targetVariant,
			Object pValueObject, boolean fByRef) {
		if (pValueObject == null) {
			targetVariant.putEmpty();
		} else if (pValueObject instanceof Integer) {
			if (fByRef) {
				targetVariant.putIntRef(((Integer) pValueObject).intValue());
			} else {
				targetVariant.putInt(((Integer) pValueObject).intValue());
			}
		} else if (pValueObject instanceof Short) {
			if (fByRef) {
				targetVariant.putShortRef(((Short) pValueObject).shortValue());
			} else {
				targetVariant.putShort(((Short) pValueObject).shortValue());
			}
		} else if (pValueObject instanceof String) {
			if (fByRef) {
				targetVariant.putStringRef((String) pValueObject);
			} else {
				targetVariant.putString((String) pValueObject);
			}
		} else if (pValueObject instanceof Boolean) {
			if (fByRef) {
				targetVariant.putBooleanRef(((Boolean) pValueObject)
						.booleanValue());
			} else {
				targetVariant.putBoolean(((Boolean) pValueObject)
						.booleanValue());
			}
		} else if (pValueObject instanceof Double) {
			if (fByRef) {
				targetVariant.putDoubleRef(((Double) pValueObject)
						.doubleValue());
			} else {
				targetVariant.putDouble(((Double) pValueObject).doubleValue());
			}
		} else if (pValueObject instanceof Float) {
			if (fByRef) {
				targetVariant.putFloatRef(((Float) pValueObject).floatValue());
			} else {
				targetVariant.putFloat(((Float) pValueObject).floatValue());
			}
		} else if (pValueObject instanceof BigDecimal) {
			if (fByRef) {
				targetVariant.putDecimalRef(((BigDecimal) pValueObject));
			} else {
				targetVariant.putDecimal(((BigDecimal) pValueObject));
			}
		} else if (pValueObject instanceof Byte) {
			if (fByRef) {
				targetVariant.putByteRef(((Byte) pValueObject).byteValue());
			} else {
				targetVariant.putByte(((Byte) pValueObject).byteValue());
			}
		} else if (pValueObject instanceof Date) {
			if (fByRef) {
				targetVariant.putDateRef((Date) pValueObject);
			} else {
				targetVariant.putDate((Date) pValueObject);
			}
		} else if (pValueObject instanceof Long) {
			if (fByRef) {
				targetVariant.putCurrencyRef(((Long) pValueObject).longValue());
			} else {
				targetVariant.putCurrency(((Long) pValueObject).longValue());
			}
		} else if (pValueObject instanceof SafeArray) {
			if (fByRef) {
				targetVariant.putSafeArrayRef((SafeArray) pValueObject);
			} else {
				targetVariant.putSafeArray((SafeArray) pValueObject);
			}
		} else if (pValueObject instanceof Dispatch) {
			if (fByRef) {
				targetVariant.putDispatchRef((Dispatch) pValueObject);
			} else {
				targetVariant.putDispatch((Dispatch) pValueObject);
			}
		} else if (pValueObject instanceof Variant) {
			// newly added 1.12-pre6 to support VT_VARIANT
			targetVariant.putVariant(pValueObject);
		} else {
			// should really throw an illegal argument exception if its an
			// invalid type
			if (fByRef) {
				targetVariant.putObjectRef(pValueObject);
			} else {
				targetVariant.putObject(pValueObject);
			}
		}
	}

	/**
	 * Map arguments based on msdn documentation. This method relies on the
	 * variant constructor except for arrays.
	 * 
	 * @param objectToBeMadeIntoVariant
	 * @return Variant that represents the object
	 */
	@SuppressWarnings("unchecked")
	protected static Variant objectToVariant(Object objectToBeMadeIntoVariant) {
		if (objectToBeMadeIntoVariant == null) {
			return new Variant();
		} else if (objectToBeMadeIntoVariant instanceof Variant) {
			// if a variant was passed in then be a slacker and just return it
			return (Variant) objectToBeMadeIntoVariant;
		} else if (objectToBeMadeIntoVariant.getClass().isArray()) {
			// automatically convert arrays using reflection
			SafeArray sa = null;
			int len1 = Array.getLength(objectToBeMadeIntoVariant);
			Object first = Array.get(objectToBeMadeIntoVariant, 0);
			if (first.getClass().isArray()) {
				int max = 0;
				for (int i = 0; i < len1; i++) {
					Object e1 = Array.get(objectToBeMadeIntoVariant, i);
					int len2 = Array.getLength(e1);
					if (max < len2) {
						max = len2;
					}
				}
				sa = new SafeArray(Variant.VariantVariant, len1, max);
				for (int i = 0; i < len1; i++) {
					Object e1 = Array.get(objectToBeMadeIntoVariant, i);
					for (int j = 0; j < Array.getLength(e1); j++) {
						sa.setVariant(i, j, objectToVariant(Array.get(e1, j)));
					}
				}
			} else {
				sa = new SafeArray(Variant.VariantVariant, len1);
				for (int i = 0; i < len1; i++) {
					sa.setVariant(i, objectToVariant(Array.get(
							objectToBeMadeIntoVariant, i)));
				}
			}
			Variant returnVariant = new Variant();
			populateVariant(returnVariant, sa, false);
			return returnVariant;
		} else {
			// rely on populateVariant to throw an exception if its an
			// invalid type
			Variant returnVariant = new Variant();
			populateVariant(returnVariant, objectToBeMadeIntoVariant, false);
			return returnVariant;
		}
	}

	/**
	 * converts an array of objects into an array of Variants by repeatedly
	 * calling obj2Variant(Object)
	 * 
	 * @param arrayOfObjectsToBeConverted
	 * @return Variant[]
	 */
	protected static Variant[] objectsToVariants(
			Object[] arrayOfObjectsToBeConverted) {
		Variant vArg[] = new Variant[arrayOfObjectsToBeConverted.length];
		for (int i = 0; i < arrayOfObjectsToBeConverted.length; i++) {
			vArg[i] = objectToVariant(arrayOfObjectsToBeConverted[i]);
		}
		return vArg;
	}

	/**
	 * Convert a JACOB Variant value to a Java object (type conversions).
	 * provided in Sourceforge feature request 959381. A fix was done to handle
	 * byRef bug report 1607878.
	 * <p>
	 * Unlike other toXXX() methods, it does not do a type conversion except for
	 * special data types (it shouldn't do any!)
	 * <p>
	 * Converts Variant.VariantArray types to SafeArrays
	 * 
	 * @return Corresponding Java object of the type matching the Variant type.
	 * @throws IllegalStateException
	 *             if no underlying windows data structure
	 * @throws NotImplementedException
	 *             if unsupported conversion is requested
	 * @throws JacobException
	 *             if the calculated result was a JacobObject usually as a
	 *             result of error
	 */
	protected static Object variantToObject(Variant sourceData) {
		Object result = null;

		short type = sourceData.getvt(); // variant type

		if ((type & Variant.VariantArray) == Variant.VariantArray) { // array
			// returned?
			SafeArray array = null;
			type = (short) (type - Variant.VariantArray);
			array = sourceData.toSafeArray(false);
			// result = toJava(array);
			result = array;
		} else { // non-array object returned
			switch (type) {
			case Variant.VariantEmpty: // 0
			case Variant.VariantNull: // 1
				break;
			case Variant.VariantShort: // 2
				result = new Short(sourceData.getShort());
				break;
			case Variant.VariantShort | Variant.VariantByref: // 2
				result = new Short(sourceData.getShortRef());
				break;
			case Variant.VariantInt: // 3
				result = new Integer(sourceData.getInt());
				break;
			case Variant.VariantInt | Variant.VariantByref: // 3
				result = new Integer(sourceData.getIntRef());
				break;
			case Variant.VariantFloat: // 4
				result = new Float(sourceData.getFloat());
				break;
			case Variant.VariantFloat | Variant.VariantByref: // 4
				result = new Float(sourceData.getFloatRef());
				break;
			case Variant.VariantDouble: // 5
				result = new Double(sourceData.getDouble());
				break;
			case Variant.VariantDouble | Variant.VariantByref: // 5
				result = new Double(sourceData.getDoubleRef());
				break;
			case Variant.VariantCurrency: // 6
				result = new Long(sourceData.getCurrency());
				break;
			case Variant.VariantCurrency | Variant.VariantByref: // 6
				result = new Long(sourceData.getCurrencyRef());
				break;
			case Variant.VariantDate: // 7
				result = sourceData.getJavaDate();
				break;
			case Variant.VariantDate | Variant.VariantByref: // 7
				result = sourceData.getJavaDateRef();
				break;
			case Variant.VariantString: // 8
				result = sourceData.getString();
				break;
			case Variant.VariantString | Variant.VariantByref: // 8
				result = sourceData.getStringRef();
				break;
			case Variant.VariantDispatch: // 9
				result = sourceData.getDispatch();
				break;
			case Variant.VariantDispatch | Variant.VariantByref: // 9
				result = sourceData.getDispatchRef(); // Can dispatches even
				// be byRef?
				break;
			case Variant.VariantError: // 10
				result = new NotImplementedException(
						"toJavaObject() Not implemented for VariantError");
				break;
			case Variant.VariantBoolean: // 11
				result = new Boolean(sourceData.getBoolean());
				break;
			case Variant.VariantBoolean | Variant.VariantByref: // 11
				result = new Boolean(sourceData.getBooleanRef());
				break;
			case Variant.VariantVariant: // 12 they are always by ref
				result = new NotImplementedException(
						"toJavaObject() Not implemented for VariantVariant without ByRef");
				break;
			case Variant.VariantVariant | Variant.VariantByref: // 12
				result = sourceData.getVariant();
				break;
			case Variant.VariantObject: // 13
				result = new NotImplementedException(
						"toJavaObject() Not implemented for VariantObject");
				break;
			case Variant.VariantDecimal: // 14
				result = sourceData.getDecimal();
				break;
			case Variant.VariantDecimal | Variant.VariantByref: // 14
				result = sourceData.getDecimalRef();
				break;
			case Variant.VariantByte: // 17
				result = new Byte(sourceData.getByte());
				break;
			case Variant.VariantByte | Variant.VariantByref: // 17
				result = new Byte(sourceData.getByteRef());
				break;
			case Variant.VariantTypeMask: // 4095
				result = new NotImplementedException(
						"toJavaObject() Not implemented for VariantTypeMask");
				break;
			case Variant.VariantArray: // 8192
				result = new NotImplementedException(
						"toJavaObject() Not implemented for VariantArray");
				break;
			case Variant.VariantByref: // 16384
				result = new NotImplementedException(
						"toJavaObject() Not implemented for VariantByref");
				break;
			default:
				result = new NotImplementedException("Unknown return type: "
						+ type);
				// there was a "return result" here that caused defect 1602118
				// so it was removed
				break;
			}// switch (type)

			if (result instanceof JacobException) {
				throw (JacobException) result;
			}
		}

		return result;
	}// toJava()
}
