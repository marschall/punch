package com.github.marschall.punch.core;

import java.util.Collection;

abstract class CompositeTask extends RecoverableTask {

  final Collection<RecoverableTask> tasks;

  public CompositeTask(Collection<RecoverableTask> tasks) {
    this.tasks = tasks;
  }

  @Override
  void setTaskPath(TaskPath taskPath) {
    super.setTaskPath(taskPath);
    int i = 0;
    for (RecoverableTask task : this.tasks) {
      task.setTaskPath(taskPath.add(i));
      i += 1;
    }
  }

  @Override
  public void recover(RecoveryService recoveryService) {
    for (RecoverableTask task : this.tasks) {
      task.recover(recoveryService);
    }
  }

}
