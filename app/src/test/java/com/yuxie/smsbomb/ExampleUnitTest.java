package com.yuxie.smsbomb;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test() {
        String str = "{\"phone\":\"target_phone\",\"countryCode\":\"86\",\"type\":\"authenticode\"}";
        String replaceAll = str.replaceAll("target_phone", "15622145861");
        System.out.println("str:" + str);
        System.out.println("replaceAll:" + replaceAll);
    }
}