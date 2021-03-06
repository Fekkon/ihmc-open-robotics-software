/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class us_ihmc_robotics_linearAlgebra_commonOps_NativeCommonOpsWrapper */

#ifndef _Included_us_ihmc_robotics_linearAlgebra_commonOps_NativeCommonOpsWrapper
#define _Included_us_ihmc_robotics_linearAlgebra_commonOps_NativeCommonOpsWrapper
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     us_ihmc_robotics_linearAlgebra_commonOps_NativeCommonOpsWrapper
 * Method:    mult
 * Signature: ([D[D[DIII)V
 */
JNIEXPORT void JNICALL Java_us_ihmc_robotics_linearAlgebra_commonOps_NativeCommonOpsWrapper_mult
  (JNIEnv *, jobject, jdoubleArray, jdoubleArray, jdoubleArray, jint, jint, jint);

/*
 * Class:     us_ihmc_robotics_linearAlgebra_commonOps_NativeCommonOpsWrapper
 * Method:    multQuad
 * Signature: ([D[D[DII)V
 */
JNIEXPORT void JNICALL Java_us_ihmc_robotics_linearAlgebra_commonOps_NativeCommonOpsWrapper_multQuad
  (JNIEnv *, jobject, jdoubleArray, jdoubleArray, jdoubleArray, jint, jint);

/*
 * Class:     us_ihmc_robotics_linearAlgebra_commonOps_NativeCommonOpsWrapper
 * Method:    invert
 * Signature: ([D[DI)V
 */
JNIEXPORT void JNICALL Java_us_ihmc_robotics_linearAlgebra_commonOps_NativeCommonOpsWrapper_invert
  (JNIEnv *, jobject, jdoubleArray, jdoubleArray, jint);

/*
 * Class:     us_ihmc_robotics_linearAlgebra_commonOps_NativeCommonOpsWrapper
 * Method:    solve
 * Signature: ([D[D[DI)V
 */
JNIEXPORT void JNICALL Java_us_ihmc_robotics_linearAlgebra_commonOps_NativeCommonOpsWrapper_solve
  (JNIEnv *, jobject, jdoubleArray, jdoubleArray, jdoubleArray, jint);

/*
 * Class:     us_ihmc_robotics_linearAlgebra_commonOps_NativeCommonOpsWrapper
 * Method:    solveCheck
 * Signature: ([D[D[DI)Z
 */
JNIEXPORT jboolean JNICALL Java_us_ihmc_robotics_linearAlgebra_commonOps_NativeCommonOpsWrapper_solveCheck
  (JNIEnv *, jobject, jdoubleArray, jdoubleArray, jdoubleArray, jint);

/*
 * Class:     us_ihmc_robotics_linearAlgebra_commonOps_NativeCommonOpsWrapper
 * Method:    solveRobust
 * Signature: ([D[D[DII)V
 */
JNIEXPORT void JNICALL Java_us_ihmc_robotics_linearAlgebra_commonOps_NativeCommonOpsWrapper_solveRobust
  (JNIEnv *, jobject, jdoubleArray, jdoubleArray, jdoubleArray, jint, jint);

/*
 * Class:     us_ihmc_robotics_linearAlgebra_commonOps_NativeCommonOpsWrapper
 * Method:    solveDamped
 * Signature: ([D[D[DIID)V
 */
JNIEXPORT void JNICALL Java_us_ihmc_robotics_linearAlgebra_commonOps_NativeCommonOpsWrapper_solveDamped
  (JNIEnv *, jobject, jdoubleArray, jdoubleArray, jdoubleArray, jint, jint, jdouble);

/*
 * Class:     us_ihmc_robotics_linearAlgebra_commonOps_NativeCommonOpsWrapper
 * Method:    projectOnNullspace
 * Signature: ([D[D[DIIID)V
 */
JNIEXPORT void JNICALL Java_us_ihmc_robotics_linearAlgebra_commonOps_NativeCommonOpsWrapper_projectOnNullspace
  (JNIEnv *, jobject, jdoubleArray, jdoubleArray, jdoubleArray, jint, jint, jint, jdouble);

#ifdef __cplusplus
}
#endif
#endif
