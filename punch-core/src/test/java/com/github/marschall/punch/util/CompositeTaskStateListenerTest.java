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
