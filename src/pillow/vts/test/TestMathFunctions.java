package pillow.vts.test;

import static org.junit.Assert.*;
import org.junit.Test;
import pillow.vts.aiengine.utill.AIEngineUtill;

public class TestMathFunctions {
		
	double sum=500;
	
	@Test
	public void test() {
		
		double value=AIEngineUtill.sigmoidActivation(sum);
		assertTrue("Sigmoid Value Out Of Range" + value, 0 <= value && value <= 1);
		
		double primeValue=AIEngineUtill.sigmoidPrimeActivation(sum);
		assertTrue("Sigmoid Value Out Of Range" + primeValue, 0 <= primeValue && primeValue <= 1);
	}

}
