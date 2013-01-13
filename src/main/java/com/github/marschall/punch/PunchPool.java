package com.github.marschall.punch;

import java.util.concurrent.ForkJoinPool;


class PunchPool extends ForkJoinPool {

  final TaskStateListener listener;

  PunchPool(TaskStateListener listener) {
    if (listener == null) {
      throw new IllegalArgumentException("A task state listener is mandatory.");
    }
    this.listener = listener;
  }
}

