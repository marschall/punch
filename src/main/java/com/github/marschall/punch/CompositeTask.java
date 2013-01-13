package com.github.marschall.punch;

import java.util.Collection;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

abstract class CompositeTask extends RecursiveAction {

  final Collection<ForkJoinTask<?>> tasks;

  public CompositeTask(Collection<ForkJoinTask<?>> tasks) {
    this.tasks = tasks;
  }

}