package com.kuhrusty.morbadscorepad;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UtilTest {
    @Test
    public void testCompare() throws Exception {
        assertTrue(Util.compare("foo", "bar") > 1);
        assertTrue(Util.compare("bar", "foo") < 1);
        assertEquals(0, Util.compare("foo", "foo"));
        assertEquals(-1, Util.compare(null, "foo"));
        assertEquals(1, Util.compare("foo", null));
        assertEquals(0, Util.compare(null, null));
    }

    @Test
    public void testOxfordComma() throws Exception {
        assertEquals("foo", Util.oxfordComma("or", "foo"));
        assertEquals("foo or bar", Util.oxfordComma("or", "foo", "bar"));
        assertEquals("foo, bar, or baz", Util.oxfordComma("or", "foo", "bar", "baz"));
        assertEquals("foo, bar, baz, or blurfl", Util.oxfordComma("or", "foo", "bar", "baz", "blurfl"));

        assertEquals("foo", Util.oxfordComma(null, "foo"));
        assertEquals("foo, bar", Util.oxfordComma(null, "foo", "bar"));
        assertEquals("foo, bar, baz", Util.oxfordComma(null, "foo", "bar", "baz"));
        assertEquals("foo, bar, baz, blurfl", Util.oxfordComma(null, "foo", "bar", "baz", "blurfl"));
    }
}