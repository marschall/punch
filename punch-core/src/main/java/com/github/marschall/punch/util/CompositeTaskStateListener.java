package com.github.marschall.punch.util;

import java.util.Collection;

import com.github.marschall.punch.core.TaskPath;
import com.github.marschall.punch.core.TaskStateListener;

public final class CompositeTaskStateListener implements TaskStateListener {

  private final Collection<? extends TaskStateListener> listeners;

  public CompositeTaskStateListener(Collection<? extends TaskStateListener> listeners) {
    // make a copy?
    this.listeners = listeners;
  }

  @Override
  public void taskStarted(TaskPath path) {
    for (TaskStateListener listener : this.listeners) {
      listener.taskStarted(path);
    }
  }

  @Override
  public void taskFinished(TaskPath path) {
    for (TaskStateListener listener : this.listeners) {
      listener.taskFinished(path);
    }
  }

  @Override
  public void taskFailed(TaskPath path) {
    for (TaskStateListener listener : this.listeners) {
      listener.taskFailed(path);
    }
  }

}
