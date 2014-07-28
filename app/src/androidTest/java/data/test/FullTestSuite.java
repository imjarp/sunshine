package data.test;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Created by JARP on 23/07/2014.
 */
public class FullTestSuite extends TestSuite {

    public static Test suite()
    {
        return  new TestSuiteBuilder(FullTestSuite.class).includeAllPackagesUnderHere().build();
    }

    public FullTestSuite()
    {
        super();
    }
}
