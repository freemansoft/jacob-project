/* Header for class EnumVariant
 */
#ifndef _Included_EnumVariant
#define _Included_EnumVariant

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_jacob_com_EnumVariant
 * Method:    Next
 * Signature: ([Lcom/jacob/com/Variant;)I
 */
JNIEXPORT jint JNICALL Java_com_jacob_com_EnumVariant_Next
  (JNIEnv *, jobject, jobjectArray);

/*
 * Class:     com_jacob_com_EnumVariant
 * Method:    Release
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_EnumVariant_release
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_EnumVariant
 * Method:    Reset
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_EnumVariant_Reset
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_EnumVariant
 * Method:    Skip
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_EnumVariant_Skip
  (JNIEnv *, jobject, jint);

#ifdef __cplusplus
}
#endif
#endif
