package com.github.marschall.punch.util;

import com.github.marschall.punch.core.TaskPath;
import com.github.marschall.punch.core.TaskStateListener;

public enum NullListener implements TaskStateListener {

  INSTANCE;

  @Override
  public void taskStarted(TaskPath path) {
  }

  @Override
  public void taskFinished(TaskPath path) {
  }

  @Override
  public void taskFailed(TaskPath path) {
  }

}