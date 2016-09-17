package pillow.vts.aiengine.utill;

public class AIEngineUtill {
	
	public static double sigmoidActivation(double value){// this is sigmoid activation function
		
		value= 1/(1+Math.pow(Math.E,-value));
		return value;
	}
	
	public static double sigmoidPrimeActivation(double value){// this is sigmoidPrime activation function
		
		value= sigmoidActivation(value)*(1- sigmoidActivation(value));
		return value;
	}
	
	public static double getError(double targetResult, double calculatedResult){//calculate error
		
		double error= targetResult-calculatedResult;
		return error;
	}
	
	public static double calculateOutputSum(double sum, double error){
		
		double outPutSum= sigmoidPrimeActivation(sum)* error;
		return outPutSum;
	}
	
	public static double calculateHiddenSum(double outputSum, double sum, double oldWeight){
		
		double hiddenSum= outputSum/oldWeight*sigmoidPrimeActivation(sum);
		return hiddenSum;
	}
	
	public static double adujustWeight(double sum, double value){
		
		double newWeight= sigmoidPrimeActivation(sum/value);
		return newWeight;
	}
	
	public static double extractOutput(double output){
		
		double speed= 1/output;
		return speed;
	}

}
