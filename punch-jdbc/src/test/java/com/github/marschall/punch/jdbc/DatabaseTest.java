package com.github.marschall.punch.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.transaction.PlatformTransactionManager;

import com.github.marschall.punch.core.JobTrees;
import com.github.marschall.punch.core.PunchPool;
import com.github.marschall.punch.core.TaskPath;
import com.github.marschall.punch.core.TaskStateListener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DatabaseTest {

  private JdbcTemplate jdbcTemplate;
  private PunchPool pool;
  private EmbeddedDatabase db;

  @Before
  public void before() {
    this.db = new H2DatabaseBuilder()
    .setName("punch-db")
    .addScript("punch-db.sql")
    .build();

    this.jdbcTemplate = new JdbcTemplate(this.db);
    PlatformTransactionManager transactionManager = new DataSourceTransactionManager(this.db);
    TaskStateListener taskStateListener = new PersistingTaskStateListener(this.jdbcTemplate, transactionManager);
    DatabaseRecoveryService recoveryService = new DatabaseRecoveryService(this.jdbcTemplate);
    this.pool = new PunchPool(taskStateListener, recoveryService);
  }

  @After
  public void after() throws InterruptedException {
    this.pool.shutdown();
    assertTrue(this.pool.awaitTermination(1, TimeUnit.SECONDS));
    this.db.shutdown();
  }

  @Test
  public void testSuccess() {
    this.pool.invoke(JobTrees.buildFileOutRoot());

    int totalTaskCount = this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM t_task_state");
    int finishedCount = this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM t_task_state WHERE task_state = 'FINISHED'");

    assertEquals(22, totalTaskCount);
    assertEquals(22, finishedCount);
  }

  static class TaskState {
    TaskPath taskPath;
    String taskStatus;

    TaskState(String taskPath, String taskStatus) {
      this.taskPath = TaskPath.fromString(taskPath);
      this.taskStatus = taskStatus;
    }

    @Override
    public String toString() {
      return String.format("| %10s | %10s |", this.taskPath, this.taskStatus);
    }
  }

  static enum TaskStateRowMapper implements RowMapper<TaskState> {

    INSTANCE;

    @Override
    public TaskState mapRow(ResultSet rs, int rowNum) throws SQLException {
      return new TaskState(rs.getString("TASK_PATH"), rs.getString("TASK_STATE"));
    }

  }

}
