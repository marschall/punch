package com.github.marschall.punch.core;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public abstract class RecoverableTask extends RecursiveAction {

  volatile TaskPath taskPath;
  private volatile boolean finished;

  public RecoverableTask() {
    this.finished = false;
  }

  void setTaskPath(TaskPath taskPath) {
    this.taskPath = taskPath;
  }

  @Override
  protected final void compute() {
    if (!this.finished) {
      this.ensureTaskPathSet();
      ForkJoinPool pool = getPool();
      if (pool instanceof PunchPool) {
        // pool is not null and not a regular ForkJoinPool
        PunchPool punchPool = (PunchPool) pool;
        recover(punchPool.recoveryService);
        computeAndNotifyListener(punchPool.listener);
      } else {
        safeCompute();
      }
      this.finished = true;
    }
  }

  void ensureTaskPathSet() {
    // check-then act is thread safe here because it's executed before
    // the first top level task
    if (this.taskPath == null) {
      this.setTaskPath(TaskPath.root());
    }
  }

  void computeAndNotifyListener(TaskStateListener listener) {
    listener.taskStarted(this.taskPath);
    try {
      safeCompute();
    } catch (Throwable t) {
      listener.taskFailed(this.taskPath);
      throw t;
    }
    listener.taskFinished(this.taskPath);
  }

  void recover(RecoveryService recoveryService) {
    this.finished = recoveryService.isFinished(this.taskPath);
  }

  abstract void safeCompute();

}
