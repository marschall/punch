package com.github.marschall.punch;

import java.util.concurrent.ForkJoinPool;


public final class PunchPool extends ForkJoinPool {

  final TaskStateListener listener;

  public PunchPool(TaskStateListener listener) {
    if (listener == null) {
      throw new NullPointerException("A task state listener is mandatory.");
    }
    this.listener = listener;
  }
}

