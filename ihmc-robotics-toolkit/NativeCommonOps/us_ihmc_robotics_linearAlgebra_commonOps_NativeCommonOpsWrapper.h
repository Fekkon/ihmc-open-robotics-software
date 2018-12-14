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
 * Method:    computeAB
 * Signature: ([D[D[DIII)V
 */
JNIEXPORT void JNICALL Java_us_ihmc_robotics_linearAlgebra_commonOps_NativeCommonOpsWrapper_computeAB
  (JNIEnv *, jobject, jdoubleArray, jdoubleArray, jdoubleArray, jint, jint, jint);

/*
 * Class:     us_ihmc_robotics_linearAlgebra_commonOps_NativeCommonOpsWrapper
 * Method:    computeAtBA
 * Signature: ([D[D[DII)V
 */
JNIEXPORT void JNICALL Java_us_ihmc_robotics_linearAlgebra_commonOps_NativeCommonOpsWrapper_computeAtBA
  (JNIEnv *, jobject, jdoubleArray, jdoubleArray, jdoubleArray, jint, jint);

/*
 * Class:     us_ihmc_robotics_linearAlgebra_commonOps_NativeCommonOpsWrapper
 * Method:    solve
 * Signature: ([D[D[DI)V
 */
JNIEXPORT void JNICALL Java_us_ihmc_robotics_linearAlgebra_commonOps_NativeCommonOpsWrapper_solve
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
