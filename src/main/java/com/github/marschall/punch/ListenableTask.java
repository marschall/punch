package com.github.marschall.punch;


abstract class ListenableTask extends RecoverableTask {

  private final TaskStateListener listener;
  
  private volatile boolean finished;
  
  ListenableTask(TaskStateListener listener) {
    this.listener = listener;
    this.finished = false;
  }
  
  @Override
  protected void compute() {
    if (!this.finished) {
      this.preCompute();
      this.listener.taskStarted(taskPath);
      this.safeCompute();
      this.listener.taskFinished(taskPath);
      this.finished = true;
      this.postCompute();
    }
  }

  @Override
  void recover(RecoveryService recoveryService) {
    this.finished = recoveryService.isFinished(this.taskPath);
  }
  
  abstract void safeCompute();

  protected void postCompute() {
    // eg. close transaction
  }

  protected void preCompute() {
    // eg. open transaction
  }

}
