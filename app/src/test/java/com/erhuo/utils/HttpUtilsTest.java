package com.erhuo.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HttpUtilsTest {
    HttpUtils httpUtils;

    @Before
    public void setUp() throws Exception {
        httpUtils = new HttpUtils();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void isImgUrl() {
        assertEquals(false,HttpUtils.isImgUrl("1/5"));
    }

    @Test
    public void getBitMapFromUrl() {
        assertNotNull(HttpUtils.getBitMapFromUrl(null));
    }
}