package ch.ethz.rse.integration;

import ch.ethz.rse.VerificationProperty;
import ch.ethz.rse.VerificationResult;
import ch.ethz.rse.main.Runner;
import ch.ethz.rse.testing.VerificationTestCase;
import com.google.common.base.Strings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Allows to easily run an integration test for a single example
*/
public class SpecificExampleIT {

	/**
	 * Modify the configuration below to run a single example
	 */
	@Test
	void specificTest() {
		String packageName = "ch.ethz.rse.integration.tests.NonNeg_If_Safe_5";
		VerificationProperty verificationTask = VerificationProperty.NON_NEGATIVE;
		boolean expectedIsSafe = true;
		VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void constantTestTrue() {
		String packageName = "ch.ethz.rse.integration.tests.Basic_Test_Constant_Safe";
		VerificationProperty verificationTask = VerificationProperty.NON_NEGATIVE;
		boolean expectedIsSafe = true;
		VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void constantAdditionTestTrue() {
		String packageName = "ch.ethz.rse.integration.tests.Basic_Test_Constant_Addition_Safe";
		VerificationProperty verificationTask = VerificationProperty.NON_NEGATIVE;
		boolean expectedIsSafe = true;
		VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void constantAdditionTestFalse() {
		String packageName = "ch.ethz.rse.integration.tests.Basic_Test_Constant_Addition_Unsafe";
		VerificationProperty verificationTask = VerificationProperty.NON_NEGATIVE;
		boolean expectedIsSafe = false;
		VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void constantTestFalse() {
		String packageName = "ch.ethz.rse.integration.tests.Basic_Test_Constant_Unsafe";
		VerificationProperty verificationTask = VerificationProperty.NON_NEGATIVE;
		boolean expectedIsSafe = false;
		VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void variableTestTrue() {
		String packageName = "ch.ethz.rse.integration.tests.Basic_Test_Variable_Safe";
		VerificationProperty verificationTask = VerificationProperty.NON_NEGATIVE;
		boolean expectedIsSafe = true;
		VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void variableTestFalse() {
		String packageName = "ch.ethz.rse.integration.tests.Basic_Test_Variable_Unsafe";
		VerificationProperty verificationTask = VerificationProperty.NON_NEGATIVE;
		boolean expectedIsSafe = false;
		VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void specificTest2() {
	 	String packageName = "ch.ethz.rse.integration.tests.Basic_Test_Unsafe_3";
	 	VerificationProperty verificationTask = VerificationProperty.NON_NEGATIVE;
	 	boolean expectedIsSafe = false;
	 	VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void specificTest3() {
	 	String packageName = "ch.ethz.rse.integration.tests.NonNeg_While_Safe";
	 	VerificationProperty verificationTask = VerificationProperty.NON_NEGATIVE;
	 	boolean expectedIsSafe = true;
	 	VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void specificTest4() {
	 	String packageName = "ch.ethz.rse.integration.tests.NonNeg_While_Unsafe";
	 	VerificationProperty verificationTask = VerificationProperty.NON_NEGATIVE;
	 	boolean expectedIsSafe = false;
	 	VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void specificTest5() {
	 	String packageName = "ch.ethz.rse.integration.tests.NonNeg_If_Mult_Stmts_Unsafe";
	 	VerificationProperty verificationTask = VerificationProperty.NON_NEGATIVE;
	 	boolean expectedIsSafe = false;
	 	VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void specificTest6() {
	 	String packageName = "ch.ethz.rse.integration.tests.NonNeg_Mult_Stmts_Unsafe";
	 	VerificationProperty verificationTask = VerificationProperty.NON_NEGATIVE;
	 	boolean expectedIsSafe = false;
	 	VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void specificTest7() {
	 	String packageName = "ch.ethz.rse.integration.tests.NonNeg_If_Safe_6";
	 	VerificationProperty verificationTask = VerificationProperty.NON_NEGATIVE;
	 	boolean expectedIsSafe = true;
	 	VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void specificTest8() {
	 	String packageName = "ch.ethz.rse.integration.tests.NonNeg_If_Safe_7";
	 	VerificationProperty verificationTask = VerificationProperty.NON_NEGATIVE;
	 	boolean expectedIsSafe = true;
	 	VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void specificTest9() {
	 	String packageName = "ch.ethz.rse.integration.tests.NonNeg_If_Safe_8";
	 	VerificationProperty verificationTask = VerificationProperty.NON_NEGATIVE;
	 	boolean expectedIsSafe = true;
	 	VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void specificTest10() {
	 	String packageName = "ch.ethz.rse.integration.tests.NonNeg_If_Unsafe_7";
	 	VerificationProperty verificationTask = VerificationProperty.NON_NEGATIVE;
	 	boolean expectedIsSafe = false;
	 	VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void specificTest11() {
	 	String packageName = "ch.ethz.rse.integration.tests.NonNeg_If_Safe_9";
	 	VerificationProperty verificationTask = VerificationProperty.NON_NEGATIVE;
	 	boolean expectedIsSafe = false; // according to RSE server's master solution
	 	VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void specificTest12() {
	 	String packageName = "ch.ethz.rse.integration.tests.NonNeg_If_Safe_10";
	 	VerificationProperty verificationTask = VerificationProperty.NON_NEGATIVE;
	 	boolean expectedIsSafe = true; 
	 	VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void specificTest13() {
	 	String packageName = "ch.ethz.rse.integration.tests.NonNeg_While_Safe_1";
	 	VerificationProperty verificationTask = VerificationProperty.NON_NEGATIVE;
	 	boolean expectedIsSafe = true; 
	 	VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void trolleyTest1() {
		String packageName = "ch.ethz.rse.integration.tests.Basic_Test_Safe";
		VerificationProperty verificationTask = VerificationProperty.FITS_IN_TROLLEY;
		boolean expectedIsSafe = true;
		VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void trolleyTest2() {
		String packageName = "ch.ethz.rse.integration.tests.Basic_Test_Unsafe";
		VerificationProperty verificationTask = VerificationProperty.FITS_IN_TROLLEY;
		boolean expectedIsSafe = false;
		VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void trolleyTest3() {
		String packageName = "ch.ethz.rse.integration.tests.Trolley_Test_Unsafe";
		VerificationProperty verificationTask = VerificationProperty.FITS_IN_TROLLEY;
		boolean expectedIsSafe = false;
		VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void trolleyTest4() {
		String packageName = "ch.ethz.rse.integration.tests.Trolley_Test_Unsafe_1";
		VerificationProperty verificationTask = VerificationProperty.FITS_IN_TROLLEY;
		boolean expectedIsSafe = false;
		VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void trolleyTest5() {
		String packageName = "ch.ethz.rse.integration.tests.Trolley_Test_Safe";
		VerificationProperty verificationTask = VerificationProperty.FITS_IN_TROLLEY;
		boolean expectedIsSafe = true;
		VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void trolleyTest6() {
		String packageName = "ch.ethz.rse.integration.tests.Trolley_Test_Unsafe_2";
		VerificationProperty verificationTask = VerificationProperty.FITS_IN_TROLLEY;
		boolean expectedIsSafe = false;
		VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void trolleyTest7() {
		String packageName = "ch.ethz.rse.integration.tests.Trolley_Test_Safe_1";
		VerificationProperty verificationTask = VerificationProperty.FITS_IN_TROLLEY;
		boolean expectedIsSafe = false; // same as master solution on server
		VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void trolleyTest8() {
		String packageName = "ch.ethz.rse.integration.tests.Trolley_Test_Safe_2";
		VerificationProperty verificationTask = VerificationProperty.FITS_IN_TROLLEY;
		boolean expectedIsSafe = true; 
		VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void reserveTest() {
		String packageName = "ch.ethz.rse.integration.tests.Basic_Test_Constant_Unsafe_1";
		VerificationProperty verificationTask = VerificationProperty.FITS_IN_RESERVE;
		boolean expectedIsSafe = false; 
		VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void reserveTest1() {
		String packageName = "ch.ethz.rse.integration.tests.Basic_Test_Variable_Safe_1";
		VerificationProperty verificationTask = VerificationProperty.FITS_IN_RESERVE;
		boolean expectedIsSafe = true; 
		VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void reserveTest2() {
		String packageName = "ch.ethz.rse.integration.tests.Basic_Test_Variable_Unsafe_1";
		VerificationProperty verificationTask = VerificationProperty.FITS_IN_RESERVE;
		boolean expectedIsSafe = false; 
		VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void reserveTest3() {
		String packageName = "ch.ethz.rse.integration.tests.Basic_Test_If_Reserve_Safe";
		VerificationProperty verificationTask = VerificationProperty.FITS_IN_RESERVE;
		boolean expectedIsSafe = true; 
		VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void reserveTest4() {
		String packageName = "ch.ethz.rse.integration.tests.Basic_Test_If_Reserve_Safe_1";
		VerificationProperty verificationTask = VerificationProperty.FITS_IN_RESERVE;
		boolean expectedIsSafe = true; 
		VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void reserveTest5() {
		String packageName = "ch.ethz.rse.integration.tests.Basic_Test_If_Reserve_Unsafe";
		VerificationProperty verificationTask = VerificationProperty.FITS_IN_RESERVE;
		boolean expectedIsSafe = false; 
		VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void reserveTest6() {
		String packageName = "ch.ethz.rse.integration.tests.Basic_Test_If_Reserve_Unsafe_1";
		VerificationProperty verificationTask = VerificationProperty.FITS_IN_RESERVE;
		boolean expectedIsSafe = false; 
		VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void reserveTest7() {
		String packageName = "ch.ethz.rse.integration.tests.Basic_Test_If_Reserve_Safe_2";
		VerificationProperty verificationTask = VerificationProperty.FITS_IN_RESERVE;
		boolean expectedIsSafe = true; 
		VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void reserveTest8() {
		String packageName = "ch.ethz.rse.integration.tests.Basic_Test_Reserve_Unsafe";
		VerificationProperty verificationTask = VerificationProperty.FITS_IN_RESERVE;
		boolean expectedIsSafe = false; 
		VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void reserveTest9() {
		String packageName = "ch.ethz.rse.integration.tests.Basic_Test_If_Reserve_Safe_3";
		VerificationProperty verificationTask = VerificationProperty.FITS_IN_RESERVE;
		boolean expectedIsSafe = true; 
		VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void reserveTest10() {
		String packageName = "ch.ethz.rse.integration.tests.Basic_Test_If_Reserve_Unsafe_2";
		VerificationProperty verificationTask = VerificationProperty.FITS_IN_RESERVE;
		boolean expectedIsSafe = false; 
		VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void reserveTest11() {
		String packageName = "ch.ethz.rse.integration.tests.Wall_of_Fame_test";
		VerificationProperty verificationTask = VerificationProperty.FITS_IN_RESERVE;
		boolean expectedIsSafe = false; 
		VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void reserveTest12() {
		String packageName = "ch.ethz.rse.integration.tests.Basic_Test_If_Reserve_Safe_4";
		VerificationProperty verificationTask = VerificationProperty.FITS_IN_RESERVE;
		boolean expectedIsSafe = true; 
		VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	@Test
	void reserveTest13() {
		String packageName = "ch.ethz.rse.integration.tests.Basic_Test_If_Reserve_Unsafe_3";
		VerificationProperty verificationTask = VerificationProperty.FITS_IN_RESERVE;
		boolean expectedIsSafe = false; 
		VerificationTestCase t = new VerificationTestCase(packageName, verificationTask, expectedIsSafe);
		SpecificExampleIT.testOnExample(t);
	}

	private static final Logger logger = LoggerFactory.getLogger(SpecificExampleIT.class);

	public static void testOnExample(VerificationTestCase example) {

		Assumptions.assumeFalse(example.isDisabled());

		try {
			VerificationResult actual = Runner.verify(example.getVerificationTask());
			
			// check result
			SpecificExampleIT.compare(example.toString(), example.expected, actual);
			Assertions.assertEquals(example.expected, actual);
		} catch (Throwable e) {
			logger.error("Exception for example {}: {}", example, e);
			throw e;
		}
	}

	// LOGGING RESULTS

	private static void compare(String label, VerificationResult expected, VerificationResult actual) {
		String cmp = actual.compare(expected);

		String s = String.format("%s (expected:%s,got:%s)", label, expected.toString(), actual.toString());
		s = Strings.padEnd(s, 75, ' ');
		String summary = String.format("%s: %s", s, cmp);
		logger.info(summary);
	}

}
