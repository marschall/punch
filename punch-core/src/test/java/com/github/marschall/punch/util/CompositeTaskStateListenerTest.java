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

import java.util.Collections;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.marschall.punch.core.TaskPath;
import com.github.marschall.punch.core.TaskStateListener;

public class CompositeTaskStateListenerTest {

  private TaskPath taskPath;
  private TaskStateListener mock;
  private TaskStateListener listener;

  @Before
  public void setUp() {
    this.taskPath = TaskPath.fromString("0/1/2");
    this.mock = EasyMock.createStrictMock(TaskStateListener.class);
    this.listener = new CompositeTaskStateListener(Collections.singletonList(this.mock));
  }

  @After
  public void verify() {
    EasyMock.verify(this.mock);
  }

  @Test
  public void taskStarted() {
    this.mock.taskStarted(this.taskPath);
    EasyMock.replay(this.mock);

    this.listener.taskStarted(this.taskPath);
  }

  @Test
  public void taskFinished() {
    this.mock.taskFinished(this.taskPath);
    EasyMock.replay(this.mock);

    this.listener.taskFinished(this.taskPath);
  }

  @Test
  public void taskFailed() {
    this.mock.taskFailed(this.taskPath);
    EasyMock.replay(this.mock);

    this.listener.taskFailed(this.taskPath);
  }

}
