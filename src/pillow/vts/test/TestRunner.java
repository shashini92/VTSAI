package pillow.vts.test;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner {

	public static void main(String[] args) {
		
	  System.out.println("Test Config File Data- Start");
      Result result1 = JUnitCore.runClasses(TestReadConfigFile.class);
		
      for (Failure failure : result1.getFailures()) {
         System.out.println(failure.toString());
      }
		
      System.out.println(result1.wasSuccessful());
      System.out.println("Test Config File Data- End\n");
      
      System.out.println("Test Math Functions- Start");
      Result result2 = JUnitCore.runClasses(TestMathFunctions.class);
		
      for (Failure failure : result2.getFailures()) {
         System.out.println(failure.toString());
      }
		
      System.out.println(result2.wasSuccessful());
      System.out.println("Test Math Functions- End\n");
	}
}
