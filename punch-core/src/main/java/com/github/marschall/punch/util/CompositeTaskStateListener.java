/*
 * Copyright (C) 2013 by Netcetera AG.
 * All rights reserved.
 *
 * The copyright to the computer program(s) herein is the property of Netcetera AG, Switzerland.
 * The program(s) may be used and/or copied only with the written permission of Netcetera AG or
 * in accordance with the terms and conditions stipulated in the agreement/contract under which
 * the program(s) have been supplied.
 */
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
