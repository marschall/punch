package com.github.marschall.punch;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.embedded.ConnectionProperties;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseConfigurer;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.util.ClassUtils;

/**
 * A fork of Spring's {@link EmbeddedDatabaseBuilder} but customized for H2. The
 * fork was necessary in order to configure database parameters which is not
 * possible with Spring's solution.
 */
public class H2DatabaseBuilder {

  private final EmbeddedDatabaseFactory databaseFactory;
  private final ResourceDatabasePopulator databasePopulator;
  private final ResourceLoader resourceLoader;

  public H2DatabaseBuilder() {
    this.databaseFactory = new EmbeddedDatabaseFactory();
    this.databasePopulator = new ResourceDatabasePopulator();
    try {
      this.databaseFactory.setDatabaseConfigurer(H2EmbeddedDatabaseConfigurer.getInstance());
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException("Driver for test database type [" + EmbeddedDatabaseType.H2 +
          "] is not available in the classpath", e);
    }
    this.databaseFactory.setDatabasePopulator(this.databasePopulator);
    this.resourceLoader = new DefaultResourceLoader();
  }

  /**
   * Set the name of the embedded database.
   * <p>Defaults to "testdb" if not called.
   * @param databaseName the database name
   * @return this, to facilitate method chaining
   */
  public H2DatabaseBuilder setName(String databaseName) {
    this.databaseFactory.setDatabaseName(databaseName);
    return this;
  }

  /**
   * Add a SQL script to execute to populate the database.
   * @param sqlResource the sql resource location
   * @return this, to facilitate method chaining
   */
  public H2DatabaseBuilder addScript(String sqlResource) {
    this.databasePopulator.addScript(this.resourceLoader.getResource(sqlResource));
    return this;
  }

  /**
   * Build the embedded database.
   * @return the embedded database
   */
  public EmbeddedDatabase build() {
    return this.databaseFactory.getDatabase();
  }

  static class H2EmbeddedDatabaseConfigurer implements EmbeddedDatabaseConfigurer {

    private static H2EmbeddedDatabaseConfigurer INSTANCE;
    private final Class<? extends Driver> driverClass;

    /**
     * Get the singleton {@link H2EmbeddedDatabaseConfigurer} instance.
     * @return the configurer
     * @throws ClassNotFoundException if H2 is not on the classpath
     */
    @SuppressWarnings("unchecked")
    public static synchronized H2EmbeddedDatabaseConfigurer getInstance() throws ClassNotFoundException {
      if (INSTANCE == null) {
        INSTANCE = new H2EmbeddedDatabaseConfigurer(
            (Class<? extends Driver>) ClassUtils.forName("org.h2.Driver", H2EmbeddedDatabaseConfigurer.class.getClassLoader()));
      }
      return INSTANCE;
    }

    private H2EmbeddedDatabaseConfigurer(Class<? extends Driver> driverClass) {
      this.driverClass = driverClass;
    }

    @Override
    public void configureConnectionProperties(ConnectionProperties properties, String databaseName) {
      properties.setDriverClass(this.driverClass);
      properties.setUrl(String.format("jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1;MVCC=TRUE", databaseName));
      properties.setUsername("sa");
      properties.setPassword("");
    }

    @Override
    public void shutdown(DataSource dataSource, String databaseName) {
      try {
        Connection connection = dataSource.getConnection();
        Statement stmt = connection.createStatement();
        stmt.execute("SHUTDOWN");
      }
      catch (SQLException ex) {
        // log something
      }
    }

  }
}
