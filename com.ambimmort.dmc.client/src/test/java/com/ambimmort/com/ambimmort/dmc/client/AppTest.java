package com.ambimmort.com.ambimmort.dmc.client;

import com.ambimmort.dmc.client.DMCBuilderFactory;
import com.ambimmort.dmc.client.DomainUnAvailableException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest
        extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
//        try {
//            System.out.println(new DMCBuilderFactory<ITest>("http://localhost:8999", "test1", ITest.class).makeBuilder("com.ambimmort.dmc.server.core.MemoryEater").build().sayHelloFine("abc"));
//        } catch (DomainUnAvailableException ex) {
//            System.out.println("e");
//        } catch (Throwable e) {
//            e.printStackTrace();
//            System.out.println(e.getClass());
////            e.printStackTrace();
//        }
        assert (true);
    }
}
