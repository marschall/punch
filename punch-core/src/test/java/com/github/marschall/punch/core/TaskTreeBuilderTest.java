package com.github.marschall.punch.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.junit.Test;

import com.github.marschall.punch.core.TaskTreeBuilder.NodeType;

import static com.github.marschall.punch.core.TaskTreeBuilder.parallel;
import static com.github.marschall.punch.core.TaskTreeBuilder.serial;
import static com.github.marschall.punch.core.TaskTreeBuilder.task;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * JUnit tests for {@link TaskTreeBuilder}.
 */
public class TaskTreeBuilderTest {

  @Test
  public void constructorInnerNodeCorrect() {
    TaskTreeBuilder builder = new TaskTreeBuilder(NodeType.PARALLEL, Collections.<TaskTreeBuilder>emptyList(), null);
    // prevent "unused variable" warnings
    builder.build();
  }

  @Test
  public void constructorTaskCorrect() {
    DoNothingTask task = new DoNothingTask();
    TaskTreeBuilder builder = new TaskTreeBuilder(NodeType.SINGLE, Collections.<TaskTreeBuilder>emptyList(), task);
    assertEquals(task, builder.build());
  }

  @Test(expected = IllegalArgumentException.class)
  public void constructorInnerNodeAndTask() {
    TaskTreeBuilder builder = new TaskTreeBuilder(NodeType.PARALLEL, Collections.<TaskTreeBuilder>emptyList(), new DoNothingTask());
    // prevent "unused variable warnings"
    builder.build();
  }

  @Test(expected = NullPointerException.class)
  public void constructorTaskNodeWithoutTask() {
    TaskTreeBuilder builder = new TaskTreeBuilder(NodeType.SINGLE, Collections.<TaskTreeBuilder>emptyList(), null);
    // prevent "unused variable warnings"
    builder.build();
  }

  @Test
  public void build() {
    RecoverableTask task1 = new DoNothingTask();
    RecoverableTask task2 = new DoNothingTask();
    RecoverableTask task3 = new DoNothingTask();

    RecoverableTask root = serial(
        parallel(task1, task2),
        task(task3)
    ).build();

    // Check root
    assertTrue(root instanceof SerialTaskContainer);

    // Check first level
    CompositeTask actualRoot = (CompositeTask) root;
    assertEquals(2, actualRoot.tasks.size());
    assertTrue(elementAt(actualRoot.tasks, 0) instanceof ParallelTaskContainer);
    assertEquals(elementAt(actualRoot.tasks, 1), task3);

    // Check second level
    CompositeTask parallelTask = (CompositeTask) elementAt(actualRoot.tasks, 0);
    assertThat(parallelTask.tasks, containsInAnyOrder(task1, task2));
  }

  @Test
  public void buildWithTasks() {
    RecoverableTask task1 = new DoNothingTask();
    RecoverableTask task2 = new DoNothingTask();

    // Serial
    RecoverableTask root = serial(task1, task2).build();
    assertTrue(root instanceof SerialTaskContainer);
    CompositeTask actualRoot = (CompositeTask) root;
    assertThat(actualRoot.tasks, contains(task1, task2));
  }

  private static <T> T elementAt(Collection<T> collection, int index) {
    return new ArrayList<>(collection).get(index);
  }

  class DoNothingTask extends RecoverableTask {

    @Override
    void safeCompute() {
      // nop
    }

  }
}
