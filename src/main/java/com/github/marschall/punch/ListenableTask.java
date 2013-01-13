package com.github.marschall.punch;

import com.github.marschall.punch.PunchPool.PunchWorkerThread;



abstract class ListenableTask extends RecoverableTask {
  private volatile boolean finished;

  ListenableTask() {
    this.finished = false;
  }

  @Override
  protected void compute() {
    PunchWorkerThread punchWorker = (PunchWorkerThread) Thread.currentThread();
    if (!this.finished) {
      punchWorker.listener.taskStarted(this.taskPath);
      try {
        safeCompute();
      } catch (Throwable t) {
        punchWorker.listener.taskFailed(this.taskPath);
        throw t;
      }
      punchWorker.listener.taskFinished(this.taskPath);
      this.finished = true;
    }
  }

  @Override
  void recover(RecoveryService recoveryService) {
    this.finished = recoveryService.isFinished(this.taskPath);
  }

  abstract void safeCompute();

}
