package com.github.marschall.punch.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TaskPathTest {

  @Test
  public void stringConversion() {
    assertEquals("0", TaskPath.root().toString());

    assertEquals("0/0", TaskPath.root().add(0).toString());

    assertEquals("0/0/1", TaskPath.root().add(0).add(1).toString());
  }

  @Test
  public void fromStringConversion() {
    assertEquals(TaskPath.root(), TaskPath.fromString("0"));

    assertEquals(TaskPath.root().add(0), TaskPath.fromString("0/0"));

    assertEquals(TaskPath.root().add(0).add(1), TaskPath.fromString("0/0/1"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void fromStringEmpty() {
    TaskPath.fromString("");
  }

  @Test(expected = NullPointerException.class)
  public void fromStringNull() {
    TaskPath.fromString(null);
  }

  @Test
  public void equality() {
    assertEquals(TaskPath.root(), TaskPath.root());

    assertNotEquals(TaskPath.root(), TaskPath.root().add(1));
    assertNotEquals(TaskPath.root().add(1), TaskPath.root());

    assertEquals(TaskPath.root().add(1), TaskPath.root().add(1));
  }

  @Test
  public void hash() {
    assertEquals(TaskPath.root().hashCode(), TaskPath.root().hashCode());

    assertNotEquals(TaskPath.root().hashCode(), TaskPath.root().add(1).hashCode());
    assertNotEquals(TaskPath.root().add(1).hashCode(), TaskPath.root().hashCode());

    assertEquals(TaskPath.root().add(1).hashCode(), TaskPath.root().add(1).hashCode());
  }

}
