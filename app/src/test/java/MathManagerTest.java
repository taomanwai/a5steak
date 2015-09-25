import android.content.Context;
import android.test.AndroidTestCase;

/**
 * Created by tommytao on 24/9/15.
 */
public class MathManagerTest extends AndroidTestCase {

    // InstrumentationTestCase
//    @Override
//    protected void setUp() throws Exception {
////        super.setUp();
//    }
//
//    @Override
//    protected void tearDown() throws Exception {R
////        super.tearDown();
//    }
//
//    @Override
//    protected void runTest() throws Throwable {
//        super.runTest();
//    }

    // AndroidTestCase


    @Override
    protected void setUp() throws Exception {
    }

    @Override
    protected void tearDown() throws Exception {
    }



    public void testAngleDerivationToLargerFromShouldReturnPositiveValue(){

        Context context = getContext();
//        String s = context.getString(R.string.app_name);

        assertEquals("test",
                8.0, 3+5.0);

    }



}
