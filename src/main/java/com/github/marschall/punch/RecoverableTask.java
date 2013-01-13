package com.github.marschall.punch;

import java.util.concurrent.RecursiveAction;

abstract class RecoverableTask extends RecursiveAction {
  
  volatile TaskPath taskPath;
  
  void setTaskPath(TaskPath taskPath) {
    this.taskPath = taskPath;
  }
  
  abstract void recover(RecoveryService recoveryService);
  
}
