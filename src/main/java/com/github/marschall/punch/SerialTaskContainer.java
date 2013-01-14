package com.github.marschall.punch;

import java.util.Collection;
import java.util.concurrent.ForkJoinTask;

public class SerialTaskContainer extends CompositeTask {

  public SerialTaskContainer(Collection<RecoverableTask> tasks) {
    super(tasks);
  }

  @Override
  protected void safeCompute() {
    this.ensureTaskPathSet();
    for (ForkJoinTask<?> task : this.tasks) {
      task.invoke();
    }
  }

}
