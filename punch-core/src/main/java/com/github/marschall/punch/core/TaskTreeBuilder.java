package com.github.marschall.punch.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;


public class TaskTreeBuilder {

  private final NodeType nodeType;
  private final Collection<TaskTreeBuilder> children;
  private final RecoverableTask task;

  public static TaskTreeBuilder parallel(TaskTreeBuilder... tasks) {
    return new TaskTreeBuilder(NodeType.PARALLEL, Arrays.asList(tasks), null);
  }

  public static TaskTreeBuilder parallel(RecoverableTask... tasks) {
    return parallel(Arrays.asList(tasks));
  }

  public static TaskTreeBuilder parallel(Collection<RecoverableTask> tasks) {
    return createFromTasks(NodeType.PARALLEL, tasks);
  }

  public static TaskTreeBuilder serial(TaskTreeBuilder... tasks) {
    return new TaskTreeBuilder(NodeType.SERIAL, Arrays.asList(tasks), null);
  }

  public static TaskTreeBuilder serial(RecoverableTask... tasks) {
    return serial(Arrays.asList(tasks));
  }

  public static TaskTreeBuilder serial(Collection<RecoverableTask> tasks) {
    return createFromTasks(NodeType.SERIAL, tasks);
  }

  public static TaskTreeBuilder task(RecoverableTask task) {
    return new TaskTreeBuilder(task);
  }

  private static TaskTreeBuilder createFromTasks(NodeType nodeType, Collection<RecoverableTask> tasks) {
    Collection<TaskTreeBuilder> children = new ArrayList<>(tasks.size());
    for (RecoverableTask task : tasks) {
      children.add(new TaskTreeBuilder(task));
    }
    return new TaskTreeBuilder(nodeType, children, null);
  }

  TaskTreeBuilder(RecoverableTask task) {
    this(NodeType.SINGLE, Collections.<TaskTreeBuilder>emptyList(), task);
  }

  TaskTreeBuilder(NodeType nodeType, Collection<TaskTreeBuilder> children, RecoverableTask task) {
    this.nodeType = nodeType;
    if (nodeType == NodeType.SINGLE && task == null) {
      throw new NullPointerException("Task must not be null for single jobs.");
    } else if (nodeType != NodeType.SINGLE && task != null) {
      throw new IllegalArgumentException("An inner node (parallel/serial) cannot contain a task directly.");
    }
    this.children = children;
    this.task = task;
  }

  public RecoverableTask build() {
    Collection<RecoverableTask> tasks = new ArrayList<>(this.children.size());
    for (TaskTreeBuilder child : this.children) {
      tasks.add(child.build());
    }

    RecoverableTask task;
    switch (this.nodeType) {
    case PARALLEL:
      task = new ParallelTaskContainer(tasks);
      break;
    case SERIAL:
      task = new SerialTaskContainer(tasks);
      break;
    case SINGLE:
      task = this.task;
      break;
    default:
      throw new IllegalStateException("Unsupported node type: " + this.nodeType);
    }
    task.setTaskPath(TaskPath.root());
    return task;
  }

  static enum NodeType { PARALLEL, SERIAL, SINGLE }
}
