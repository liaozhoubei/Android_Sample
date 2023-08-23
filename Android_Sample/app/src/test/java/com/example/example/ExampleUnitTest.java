package com.example.example;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
        int youNumber= 5;
        String str = String.format("%1$-35s", "a");
        System.out.println("h"+ str+"w");
        String str1 = String.format("%1$35s", "a");
        System.out.println("h"+ str1+"w");

        long l = 258;
        String format = String.format("% -20d", l);
        System.out.println("d"+ format+"d");
    }
}