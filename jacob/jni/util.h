#include <objbase.h>
extern "C" {
  VARIANT *extractVariant(JNIEnv *env, jobject arg);
  void ThrowComFail(JNIEnv *env, const char* desc, jint hr);
  IDispatch *extractDispatch(JNIEnv *env, jobject arg);
  SAFEARRAY *extractSA(JNIEnv *env, jobject arg);
  void setSA(JNIEnv *env, jobject arg, SAFEARRAY *sa, int copy);
  SAFEARRAY *copySA(SAFEARRAY *psa);
}
