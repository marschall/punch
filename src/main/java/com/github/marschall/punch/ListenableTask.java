package com.github.marschall.punch;

import java.util.concurrent.ForkJoinPool;




abstract class ListenableTask extends RecoverableTask {
  private volatile boolean finished;

  ListenableTask() {
    this.finished = false;
  }

  @Override
  protected final void compute() {
    if (!this.finished) {
      ForkJoinPool pool = getPool();
      if (pool instanceof PunchPool) {
        // pool is not null and not a regular ForkJoinPool
        computeAndNotifyListener(((PunchPool) pool).listener);
      } else {
        safeCompute();
      }
      this.finished = true;
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

  @Override
  void recover(RecoveryService recoveryService) {
    this.finished = recoveryService.isFinished(this.taskPath);
  }

  abstract void safeCompute();

}
