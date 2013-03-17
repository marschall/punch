package com.github.marschall.punch.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TaskPathTest {

  @Test
  public void stringConversion() {
    assertEquals("0", TaskPath.root(0).toString());

    assertEquals("15/0", TaskPath.root(15).add(0).toString());

    assertEquals("10/0/1", TaskPath.root(10).add(0).add(1).toString());
  }

  @Test
  public void fromStringConversion() {
    assertEquals(TaskPath.root(10), TaskPath.fromString("10"));

    assertEquals(TaskPath.root(0).add(0), TaskPath.fromString("0/0"));

    assertEquals(TaskPath.root(15).add(0).add(1), TaskPath.fromString("15/0/1"));
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
    assertEquals(TaskPath.root(0), TaskPath.root(0));

    assertNotEquals(TaskPath.root(0), TaskPath.root(0).add(1));
    assertNotEquals(TaskPath.root(0).add(1), TaskPath.root(0));

    assertEquals(TaskPath.root(0).add(1), TaskPath.root(0).add(1));
  }

  @Test
  public void hash() {
    assertEquals(TaskPath.root(0).hashCode(), TaskPath.root(0).hashCode());

    assertNotEquals(TaskPath.root(0).hashCode(), TaskPath.root(0).add(1).hashCode());
    assertNotEquals(TaskPath.root(0).add(1).hashCode(), TaskPath.root(0).hashCode());

    assertEquals(TaskPath.root(0).add(1).hashCode(), TaskPath.root(0).add(1).hashCode());
  }

}
