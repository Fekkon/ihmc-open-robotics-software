package us.ihmc.convexOptimization.quadraticProgram;

import org.ejml.data.DenseMatrix64F;

import us.ihmc.convexOptimization.QpOASESCWrapper;
import us.ihmc.yoVariables.registry.YoVariableRegistry;
import us.ihmc.yoVariables.variable.YoDouble;
import us.ihmc.yoVariables.variable.YoInteger;
import us.ihmc.tools.exceptions.NoConvergenceException;

public class OASESConstrainedQPSolver extends ConstrainedQPSolver
{

   /**
    *
    * min 0.5*x'Hx + g'x
    *
    * st lbA <= Ax <= ubA
    *     lb <= x <= ub
    *
    * matrices are row-major
    *
    * @param nWSR - number of working set re-calculation
    * @param cputime - maximum cputime, null
    * @param x - initial and return variable to be optimized
    * @return returnCode from C-API
    */
   YoVariableRegistry registry = new YoVariableRegistry(getClass().getSimpleName());
   YoDouble maxCPUTime = new YoDouble("maxCPUTime", registry);
   YoDouble currentCPUTime = new YoDouble("currentCPUTime", registry);
   YoInteger maxWorkingSetChange = new YoInteger("maxWorkingSetchange", registry);
   YoInteger currentWorkingSetChange = new YoInteger("currentWorkingSetchange", registry);

   QpOASESCWrapper qpWrapper;

   public OASESConstrainedQPSolver(YoVariableRegistry parentRegistry)
   {
      this(1, 1, parentRegistry);
   }

   public OASESConstrainedQPSolver(int nvar, int ncon, YoVariableRegistry parentRegistry)
   {
      maxCPUTime.set(Double.POSITIVE_INFINITY);
      maxWorkingSetChange.set(10000);
      qpWrapper = new QpOASESCWrapper();

      if (parentRegistry != null)
      {
         parentRegistry.addChild(this.registry);
      }
   }

   @Override
   public boolean supportBoxConstraints()
   {
      return true;
   }

   @Override
   public int solve(DenseMatrix64F Q, DenseMatrix64F f, DenseMatrix64F Aeq, DenseMatrix64F beq, DenseMatrix64F Ain, DenseMatrix64F bin, DenseMatrix64F x,
         boolean initialize) throws NoConvergenceException
   {
      return solve(Q, f, Aeq, beq, Ain, bin, null, null, x, initialize);
   }

   @Override
   public int solve(DenseMatrix64F Q, DenseMatrix64F f, DenseMatrix64F Aeq, DenseMatrix64F beq, DenseMatrix64F Ain, DenseMatrix64F bin, DenseMatrix64F lb,
         DenseMatrix64F ub, DenseMatrix64F x, boolean initialize) throws NoConvergenceException
   {

      qpWrapper.setMaxCpuTime(maxCPUTime.getDoubleValue());
      qpWrapper.setMaxWorkingSetChanges(maxWorkingSetChange.getIntegerValue());
      qpWrapper.solve(Q, f, Aeq, beq, Ain, bin, lb, ub, x, initialize);
      int iter = qpWrapper.getLastWorkingSetChanges();
      currentCPUTime.set(qpWrapper.getLastCpuTime());
      currentWorkingSetChange.set(qpWrapper.getLastWorkingSetChanges());
      return iter;
   }
}
