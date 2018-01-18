package com.seckawijoki.jfinal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * Created by 瑶琴频曲羽衣魂 on 2017/12/6 at 18:15.
 */

public class TestTest {
  public static void main(String[] args) {
    new TestTest();
  }
  private TestTest() {
    try {
      Class<TestTest> c = TestTest.class;
      Method[] methods = c.getDeclaredMethods();
      System.out.println("TestTest.TestTest(): methods = " + Arrays.toString(methods));
      TestTest instance = c.newInstance();
      for ( Method method : methods ) {
        int modifiers = method.getModifiers();
        System.out.println("TestTest.TestTest(): modifiers = " + modifiers);
          if ( (modifiers & Modifier.STATIC) == 0 )
          method.invoke(instance, null);
      }
    } catch ( IllegalAccessException e ) {
      e.printStackTrace();
    } catch ( InstantiationException e ) {
      e.printStackTrace();
    } catch ( InvocationTargetException e ) {
      e.printStackTrace();
    }
  }

  private void methodA() {
    System.out.println("TestTest.methodA(): true = " + true);
  }

  private void methodB() {
    System.out.println("TestTest.methodB(): false = " + false);
  }

  private void methodC() {
    System.out.println("TestTest.methodC(): this = " + this);
  }

  private void methodD() {
    System.out.println("TestTest.methodD");
  }
}
