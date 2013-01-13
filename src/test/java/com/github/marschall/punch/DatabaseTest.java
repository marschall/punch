package com.github.marschall.punch;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import static org.junit.Assert.assertEquals;

public class DatabaseTest {

  private JdbcTemplate jdbcTemplate;
  private PunchPool pool;
  private EmbeddedDatabase db;

  @Before
  public void before() {
    this.db = new EmbeddedDatabaseBuilder()
    .setName("punch-db")
    .setType(EmbeddedDatabaseType.H2)
    .addScript("punch-db.sql")
    .build();

    this.jdbcTemplate = new JdbcTemplate(this.db);
    this.pool = new PunchPool(new PersistingTaskStateListener(this.jdbcTemplate));
  }

  @After
  public void after() throws InterruptedException {
    this.pool.awaitTermination(1, TimeUnit.HOURS);
    this.db.shutdown();
  }

  @Test
  public void testSuccess() {
    this.pool.invoke(JobTrees.buildFileOutRoot());

    int totalTaskCount = this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM t_task_state");
    int finishedCount = this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM t_task_state WHERE task_state = 'FINISHED'");

    assertEquals(17, totalTaskCount);
    assertEquals(17, finishedCount);
  }

  static class PersistingTaskStateListener implements TaskStateListener {

    private static final String INSERT_TASK_SQL = "INSERT INTO t_task_state(task_path, task_state) VALUES (?, ?)";
    private static final String UPDATE_TASK_SQL = "UPDATE t_task_state SET task_state = ? WHERE task_path = ?";

    private final JdbcTemplate jdbcTemplate;

    public PersistingTaskStateListener(JdbcTemplate jdbcTemplate) {
      this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void taskStarted(TaskPath path) {
      this.jdbcTemplate.update(INSERT_TASK_SQL, path.toString(), "RUNNING");
    }

    @Override
    public void taskFinished(TaskPath path) {
      this.jdbcTemplate.update(UPDATE_TASK_SQL, "FINISHED", path.toString());
    }

    @Override
    public void taskFailed(TaskPath path) {
      // nop
    }
  }

  static class TaskState {
    TaskPath taskPath;
    String taskStatus;

    TaskState(String taskPath, String taskStatus) {
      this.taskPath = TaskPath.fromString(taskPath);
      this.taskStatus = taskStatus;
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
