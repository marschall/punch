import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

import org.junit.Test;

import com.github.marschall.punch.ParallelTaskContainer;
import com.github.marschall.punch.SerialTaskContainer;


public class PunchTest {

  @Test
  public void sample() {
    ForkJoinPool pool = new ForkJoinPool();
    pool.invoke(buildTopLevelTask());
    pool.shutdown();
  }
  
  private RecursiveAction buildTopLevelTask() {
    Collection<ForkJoinTask<?>> tasks = new ArrayList<>(9);
    tasks.addAll(buildSerialTasks(0));
    tasks.add(buildParallelTasks());
    tasks.addAll(buildSerialTasks(5));
    return new SerialTaskContainer(tasks);
  }

  private Collection<ForkJoinTask<?>> buildSerialTasks(int start) {
    int numberOfTasks = 4;
    Collection<ForkJoinTask<?>> tasks = new ArrayList<>(numberOfTasks);
    for (int i = 0; i < numberOfTasks; i++) {
      tasks.add(new StringTask("serial-" + (i + 1 + start)));
    }
    return tasks;
  }
  
  private RecursiveAction buildParallelTasks() {
    int numberOfTasks = 10;
    Collection<ForkJoinTask<?>> tasks = new ArrayList<>(numberOfTasks);
    for (int i = 0; i < numberOfTasks; i++) {
      tasks.add(new StringTask("parallel-" + (i + 1)));
    }
    return new ParallelTaskContainer(tasks);
  }
  
  static final class StringTask extends RecursiveAction {

    private final String s;
    
    StringTask(String s) {
      this.s = s;
    }

    @Override
    protected void compute() {
      System.out.println(this.s);
    }
    
  }

}
