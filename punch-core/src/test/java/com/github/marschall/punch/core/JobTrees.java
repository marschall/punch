package com.github.marschall.punch.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.RecursiveAction;

import static com.github.marschall.punch.core.TaskTreeBuilder.parallel;
import static com.github.marschall.punch.core.TaskTreeBuilder.serial;

public final class JobTrees {

  private JobTrees() {
    throw new AssertionError("not instantiable");
  }

  /**
   * <pre>
   * parallel
   *   - sequential
   *     - serial-1
   *     - serial-2
   *   - singleTask-1
   *   - sequential
   *     - serial-3
   *     - serial-4
   * </pre>
   */
  static RecursiveAction buildTree() {
    return parallel(
        buildSerialTasks(1, 2),
        new StringTask("singleTask-1"),
        buildSerialTasks(3, 2)
    ).build();
  }

  static RecoverableTask buildSerialTasks(int start, int numberOfTasks) {
    Collection<RecoverableTask> tasks = new ArrayList<>(numberOfTasks);
    for (int i = start; i < start + numberOfTasks; i++) {
      tasks.add(new StringTask("serial-" + i));
    }
    return new SerialTaskContainer(tasks);
  }

  /**
   * <pre>
   * fileout-root
   *  - tenants (parallel)
   *    - tenant1 (sequential)
   *      - staging (parallel)
   *        - staging-job1
   *        - staging-job2
   *        - ...
   *      - writing-job
   *    - tenant2 (sequential)
   *      ...
   * </pre>
   */
  public static RecoverableTask buildFileOutRoot() {
    return parallel(
        serial(
            buildStagingJobs(10, 1),
            new StringTask("writing-job tenant-1")
        ),
        serial(
            buildStagingJobs(5, 2),
            new StringTask("writing-job tenant-2")
        )
    ).build();
  }

  private static RecoverableTask buildStagingJobs(int numberOfTasks, int tenant) {
    Collection<RecoverableTask> tasks = new ArrayList<>(numberOfTasks);
    for (int i = 0; i < numberOfTasks; i++) {
      tasks.add(new StringTask("staging-job-" + i + " tenant-" + tenant));
    }
    return new ParallelTaskContainer(tasks);
  }

  static final class StringTask extends RecoverableTask {

    private final String s;

    StringTask(String s) {
      this.s = s;
    }

    @Override
    protected void safeCompute() {
      System.out.println(this.s);
    }

  }
}
