package com.kuhrusty.morbadscorepad;

import android.content.Context;
import android.content.res.AssetManager;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;

/**
 * A couple utility methods for use in tests.
 */
public class TestUtil {

    /**
     * This returns a mock Context whose getAssets().open() will return a file
     * from a test directory.
     */
    public static Context mockContext() {
        return mockContext("src/main/assets");
    }

    /**
     * This returns a mock Context whose getAssets().open() will return a file
     * from the given test directory.
     */
    public static Context mockContext(final String base) {
        Context rv = PowerMockito.mock(Context.class);
        AssetManager am = PowerMockito.mock(AssetManager.class);
        PowerMockito.doReturn(am).when(rv).getAssets();
        try {
            PowerMockito.when(am.open(anyString())).thenAnswer(new Answer<InputStream>() {
                @Override
                public InputStream answer(InvocationOnMock invocation) throws Throwable {
                    Object[] args = invocation.getArguments();
                    //Object mock = invocation.getMock();
                    String newFullPath = base + "/" + args[0];
                    return new FileInputStream(newFullPath);
                }
            });
        } catch (IOException ioe) {
            throw new RuntimeException("failed to mock open(), I think...", ioe);
        }
        return rv;
    }

    /**
     * Asserts that the contents of the two given files are identical.
     *
     * @param expectFileName will be read from CLASSPATH
     * @param gotFileName will be read from the filesystem
     * @param ignoreComments true if lines starting with "//" should be ignored
     * @param ignoreWhitespace true if \s+ should be evaluated as a single space
     */
    public static void compare(String expectFileName, String gotFileName,
                               boolean ignoreComments, boolean ignoreWhitespace)
            throws IOException {
        String es = snort(expectFileName, true, ignoreComments, ignoreWhitespace);
        String gs = snort(gotFileName, false, ignoreComments, ignoreWhitespace);
        assertEquals("expected contents of " + expectFileName + ", got " + gotFileName,
                es, gs);
    }

    private static String snort(String fileName, boolean classpath,
                                boolean ignoreComments, boolean ignoreWhitespace)
            throws IOException {
        BufferedReader in = new BufferedReader(classpath ?
                new InputStreamReader(TestUtil.class.getClassLoader().getResourceAsStream(fileName)) :
                new FileReader(fileName));
        StringBuilder buf = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            if (ignoreComments && (line.matches("^\\s*//.+$"))) {
                continue;
            }
            if (ignoreWhitespace) line = line.replaceAll("\\s+", " ");
            buf.append(line).append("\n");
        }
        return buf.toString();
    }

    public static <T> T linearSearch(List<T> list, Requirement<T> selector) {
        for (T tt : list) {
            if (selector.passes(tt)) return tt;
        }
        return null;
    }
}