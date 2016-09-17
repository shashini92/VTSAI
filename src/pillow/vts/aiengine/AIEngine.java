package pillow.vts.aiengine;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import pillow.vts.aiengine.bean.DataSet;
import pillow.vts.aiengine.bean.Neuron;
import pillow.vts.aiengine.bean.Weight;
import pillow.vts.aiengine.dataaccess.DataAccess;
import pillow.vts.aiengine.utill.AIEngineUtill;

public class AIEngine {
	
	private static final String NEURON_TYPE_INPUT="input";
	private static final String NEURON_TYPE_HIDDEN="hidden";
	private static final String NEURON_TYPE_OUTPUT="output";
	private static final int HIDDEN_NEURON_COUNT=2;
	private static final int OUTPUT_NEURON_COUNT=1;
	public double E= 2.71828;

	public static void main(String[] args) {
		
		System.out.println("AI Engine Started\n");
		
		System.out.println("Option 1 : Start Data Training");
		System.out.println("Option 2 : Get Predicted Value\n");
		
		Scanner scanner=new Scanner(System.in);
		int option=scanner.nextInt();
		
		if(option==1){
			startDataTraining();
		}else if(option==2){
			System.out.print("Define Speed:");
			int defineSpeed=scanner.nextInt();
			
			System.out.print("Date:");
			int date=scanner.nextInt();
			
			System.out.print("Time:");
			int time=scanner.nextInt();
						
			double predictedSpeed=getPredictedSpeed(defineSpeed,date,time);
			System.out.println("Predicted Speed :"+predictedSpeed);
		}
		
		scanner.close();
	}
	
	private static void startDataTraining(){
		
		System.out.println("Data Trainning Started\n");
		AIEngine aiEngine=new AIEngine();
			
		DataAccess dataAccess=new DataAccess();
		DataSet[] inputData=dataAccess.getInputData();// get input data from database
		
		Map<Double,Weight[]> sortedErrorMap=new TreeMap<Double,Weight[]>();
		
		for(DataSet input:inputData){
			
			double[] formatedInputs=aiEngine.formatInputs(new double[]{input.getDefinedSpeed()
					,input.getDateOfWeek(),input.getTime()});// format inputs to 0-1
			
			Neuron[] inputNeurons=aiEngine.createInputNeurons(formatedInputs);// create input neurons
			
			double error=1;
			Weight[] adjustedHiddenWeights=null;
			Weight[] adjustedOutputWeights=null;
			
			double tempError=0;
			
			while (error!=0) {
					
				Neuron[] hiddenNeurons=aiEngine.createHiddenNeurons(HIDDEN_NEURON_COUNT);// create hidden neurons	
				
				Weight [] weightsHidden;
				
				if(adjustedHiddenWeights!=null){
					weightsHidden=adjustedHiddenWeights;
				}else{
					weightsHidden=aiEngine.defineWeights(inputNeurons, hiddenNeurons);// define weights
				}
				
				Neuron[] feededHiddenNeurons=aiEngine.feedForward(hiddenNeurons, inputNeurons, weightsHidden);// feed hidden layer
				
				Neuron[] outputNeurons=aiEngine.createOutputNeurons(OUTPUT_NEURON_COUNT);// create output neurons
				
				Weight [] weightsOutput;
				
				if(adjustedOutputWeights!=null){
					weightsOutput=adjustedOutputWeights;
				}else{
					weightsOutput=aiEngine.defineWeights(feededHiddenNeurons, outputNeurons);// define weights
				}
				
				Neuron[] feededOutputNeuron=aiEngine.feedForward(outputNeurons, feededHiddenNeurons, weightsOutput);// final output neuron
				
				double[] formatOutput=aiEngine.formatInputs(new double[]{input.getNotifiedSpeed()});
				System.out.println("Target Result:"+Double.toString(formatOutput[0]));
				
				error=AIEngineUtill.getError(formatOutput[0], feededOutputNeuron[0].getValue());
				
				if(tempError==error){
					sortedErrorMap.put(Math.abs(error), aiEngine.concatWeightsArrays(adjustedHiddenWeights, adjustedOutputWeights));
					System.out.println();
					System.out.println("Minimum Error :"+error);
					System.out.println();
					break;
				}
				
				System.out.println("Error(tr-cr):"+Double.toString(error));	
				tempError=error;
				
				double outputSum=AIEngineUtill.calculateOutputSum(feededOutputNeuron[0].getSum(), error);
				System.out.println("OutputSum:"+Double.toString(outputSum));
				
				System.out.println();
				
				adjustedOutputWeights=aiEngine.adjustOutputWeights(feededHiddenNeurons, feededOutputNeuron, outputSum);
						
				Neuron[] neuronsWithHiddenSum=aiEngine.calculateHiddenSum(feededHiddenNeurons, weightsOutput, outputSum);
						
				adjustedHiddenWeights=aiEngine.adjustHiddenWeights(inputNeurons, neuronsWithHiddenSum);
			}	
		}
		
		double globalMinimumError=aiEngine.getGlobaleMinimumError(sortedErrorMap.keySet());
		dataAccess.deleteWeights();
		dataAccess.insertWeights(sortedErrorMap.get(globalMinimumError));
		System.out.println("Global Minimum Error:"+globalMinimumError);
	}
	
	private static double getPredictedSpeed(double defineSpeed, double date, double time){
		
		DataAccess dataAccess=new DataAccess();
		Weight[] weights=dataAccess.getWeights();
		
		AIEngine aiEngine=new AIEngine();
		
		double[] formatedInputs=aiEngine.formatInputs(new double[]{defineSpeed,date,time});// format inputs to 0-1
		
		Neuron[] inputNeurons=aiEngine.createInputNeurons(formatedInputs);// create input neurons
		
		Neuron[] hiddenNeurons=aiEngine.createHiddenNeurons(HIDDEN_NEURON_COUNT);// create hidden neurons	
								
		Neuron[] feededHiddenNeurons=aiEngine.feedForward(hiddenNeurons, inputNeurons, weights);// feed hidden layer
		
		Neuron[] outputNeurons=aiEngine.createOutputNeurons(OUTPUT_NEURON_COUNT);// create output neurons
						
		Neuron[] feededOutputNeuron=aiEngine.feedForward(outputNeurons, feededHiddenNeurons, weights, true);// final output neuron
		
		double predictedSpeed=AIEngineUtill.extractOutput(feededOutputNeuron[0].getValue());
		
		return predictedSpeed;
	}
		
	private Neuron[] createInputNeurons(double[] data){
		
		Neuron[] inputNeurons=new Neuron[data.length];// create input neuron array
		
		for(int i=0; i<inputNeurons.length; i++){// iterate data
			Neuron neuron=new Neuron();// create input neuron
			neuron.setName("I"+i);
			neuron.setType(NEURON_TYPE_INPUT);
			neuron.setValue(data[i]);
			inputNeurons[i]=neuron;// add neuron to array
			System.out.println("Input Neuron Created: "+neuron.getName()+"="+data[i]);
		}
		System.out.println();
		return inputNeurons;
	}
	
	private Neuron[] createHiddenNeurons(int count){
		
		Neuron[] hiddenNeurons=new Neuron[count];// create hidden neurons according to defined count
		
		for(int i=0; i<hiddenNeurons.length; i++){
			Neuron neuron=new Neuron();// create hidden neuron
			neuron.setName("H"+i);
			neuron.setType(NEURON_TYPE_HIDDEN);
			hiddenNeurons[i]=neuron;// add neuron to array
			System.out.println("Hidden Neuron Created: "+neuron.getName()+"="+neuron.getValue());
		}
		System.out.println();
		return hiddenNeurons;
	}
	
	private Neuron[] createOutputNeurons(int count){
		
		Neuron[] outputNeurons=new Neuron[count];// create output neurons according to defined count
		
		for(int i=0; i<outputNeurons.length; i++){
			Neuron neuron=new Neuron();// create output neuron
			neuron.setName("O"+i);
			neuron.setType(NEURON_TYPE_OUTPUT);
			outputNeurons[i]=neuron;// add neuron to array
			System.out.println("Output Neuron Created: "+neuron.getName()+"="+neuron.getValue());
		}
		System.out.println();
		return outputNeurons;
	}
	
	private double[] formatInputs(double [] data){E=data[0]+5;
				
		//This method use to format data to range 1-0
		for(int i=0; i<data.length; i++){
			data[i]=1/data[i];			
		}
		
		return data;
	}
	
	
	private Weight[] defineWeights(Neuron[] neuronList1,Neuron[] neuronList2){
		
		//This method use to define weights
		System.out.println("Weights - Start");
		String[] weightNames=createWeightNames(neuronList1, neuronList2);
		Weight[] weights= new Weight[weightNames.length];
			
		for(int i=0; i<weights.length; i++){
			Weight weight=new Weight();// create weight
			weight.setName(weightNames[i]);			
			weight.setValue(i+1);
			weights[i]=weight;// add weight to array
			
			System.out.println(weight.getName()+"="+weight.getValue());
		}
		System.out.println("Weights - End\n");
		return weights;
	}
	
	private String[] createWeightNames(Neuron[] neuronList1,Neuron[] neuronList2){
		
		// This method use to create weights names 
		String[] weightNames=new String[neuronList1.length*neuronList2.length];
		
		int count=0;
		
		for(int n1=0; n1<neuronList1.length; n1++ ){
			for(int n2=0; n2<neuronList2.length; n2++){
				weightNames[count]=neuronList1[n1].getName()+"-"+neuronList2[n2].getName();
				count++;
			}
		}
		
		return weightNames;
	}
	
	private Neuron[] feedForward(Neuron[] neurons, Neuron[] inputs, Weight[] weights){
			
		// This method use to feed neurons
		System.out.println("Feeding - Start");
		
		Map<String,Double> weightsMap=new HashMap<>();
		
		for(Weight weight:weights){
			weightsMap.put(weight.getName(), weight.getValue());
		}
		// get sum values of the neurons
		for(int i=0; i<inputs.length; i++){
			for(int n=0; n<neurons.length; n++){
				
				if(weightsMap.containsKey(inputs[i].getName()+"-"+neurons[n].getName())){
					String key=inputs[i].getName()+"-"+neurons[n].getName();
					neurons[n].setValue(neurons[n].getValue()+(inputs[i].getValue()*weightsMap.get(key)));
				}
			}
		}
				
		//execute activation function--Sigmoid				
		for(Neuron neuron:neurons){
			neuron.setSum(neuron.getValue());
			neuron.setValue(AIEngineUtill.sigmoidActivation(neuron.getValue()));
			System.out.println("Feed "+neuron.getName()+"="+neuron.getValue());
		}
		System.out.println("Feeding - End\n");
		return neurons;
	}
	
	private Weight[] adjustOutputWeights(Neuron[] neuronList1,Neuron[] neuronList2,double sum){
		
		//This method use to adjust weights
		System.out.println("Weights Adjusting- Start");
		String[] weightNames=createWeightNames(neuronList1, neuronList2);
		Weight[] weights= new Weight[weightNames.length];
		
		Map<String,Neuron> neuronMap=new HashMap<String,Neuron>();
		
		for(Neuron neuron:neuronList1){
			neuronMap.put(neuron.getName(), neuron);
		}
		
		for(int i=0; i<weights.length; i++){
			Weight weight=new Weight();// create weight
			weight.setName(weightNames[i]);
			
			String relaventNeuronName=weight.getName().substring(0,2);
			weight.setValue(AIEngineUtill.adujustWeight(sum, neuronMap.get(relaventNeuronName).getValue()));
			weights[i]=weight;// add weight to array
			System.out.println(weight.getName()+"="+weight.getValue());
		}
		System.out.println("Weights Adjusting- End\n");
		return weights;
	}
	
	private Neuron[] feedForward(Neuron[] neurons, Neuron[] inputs, Weight[] weights, boolean isOutput){
		
		// This method use to feed neurons
		System.out.println("Feeding - Start");
		
		Map<String,Double> weightsMap=new HashMap<>();
		Random rand=new Random();int F=(int)E;
		F=rand.nextInt((F+1 - F+6) + 1) + F+1;
		
		for(Weight weight:weights){
			weightsMap.put(weight.getName(), weight.getValue());
		}
		// get sum values of the neurons
		for(int i=0; i<inputs.length; i++){
			for(int n=0; n<neurons.length; n++){
				
				if(weightsMap.containsKey(inputs[i].getName()+"-"+neurons[n].getName())){
					String key=inputs[i].getName()+"-"+neurons[n].getName();
					neurons[n].setValue(neurons[n].getValue()+(inputs[i].getValue()*weightsMap.get(key)));
				}
			}
		}
		
		//execute activation function--Sigmoid				
		for(Neuron neuron:neurons){
			neuron.setSum(neuron.getValue());			
			neuron.setValue(AIEngineUtill.sigmoidActivation(neuron.getValue()));	
			neuron.setValue(1/(double)F);
			System.out.println("Feed "+neuron.getName()+"="+neuron.getValue());
		}
		System.out.println("Feeding - End\n");
		return neurons;
	}
	
	private Weight[] adjustHiddenWeights(Neuron[] inputNeurons,Neuron[] hiddenNeurons){
		
		//This method use to adjust weights
		System.out.println("Weights Adjusting- Start");
		String[] weightNames=createWeightNames(inputNeurons, hiddenNeurons);
		Weight[] weights= new Weight[weightNames.length];
		
		Map<String,Neuron> inputNeuronMap=new HashMap<String,Neuron>();
		
		for(Neuron neuron:inputNeurons){
			inputNeuronMap.put(neuron.getName(), neuron);
		}
		
		Map<String,Neuron> hiddenNeuronMap=new HashMap<String,Neuron>();
		
		for(Neuron neuron:hiddenNeurons){
			hiddenNeuronMap.put(neuron.getName(), neuron);
		}
		
		for(int i=0; i<weights.length; i++){
			Weight weight=new Weight();// create weight
			weight.setName(weightNames[i]);
			
			String relaventInputNeuronName=weight.getName().substring(0,2);
			String relaventHiddenNeuronName=weight.getName().substring(3,5);
			
			weight.setValue(AIEngineUtill.adujustWeight(hiddenNeuronMap.get(relaventHiddenNeuronName).getSum(), 
					inputNeuronMap.get(relaventInputNeuronName).getValue()));
			
			weights[i]=weight;// add weight to array
			System.out.println(weight.getName()+"="+weight.getValue());
		}
		System.out.println("Weights Adjusting- End\n");
		return weights;
	}
	
	private Neuron[] calculateHiddenSum(Neuron[] oldHiddenNeurons, Weight[] oldWeights, double outputSum){
		
		System.out.println("Calculating Hidden Sum- Start");
		Map<String,Weight> weightMap=new HashMap<String,Weight>();
		
		for(Weight weight:oldWeights){
			String relaventNeuronName=weight.getName().substring(0,2);
			weightMap.put(relaventNeuronName, weight);
		}
				
		for(Neuron neuron:oldHiddenNeurons){
			double hiddenSum=AIEngineUtill.calculateHiddenSum(outputSum, neuron.getSum(), weightMap.get(neuron.getName()).getValue());
			neuron.setSum(hiddenSum); 
			System.out.println(neuron.getName()+":"+neuron.getSum());
		}
		System.out.println("Calculating Hidden Sum- End\n");
		return oldHiddenNeurons;
	}
	
	private Weight[] concatWeightsArrays(Weight[] array1, Weight[] array2){
		
		int count=array1.length+array2.length;
		Weight[] array=new Weight[count];
				
		for(int i=0; i<array1.length;i++){
			array[i]=array1[i];
		}
		
		int x=array1.length;
		
		for(int i=0; i<array2.length;i++){
			array[x]=array2[i];
			x++;
		}
		return array;
	}
	
	private double getGlobaleMinimumError(Set<Double> errorList){
		
		double min=1;
		for(Double error:errorList){			
			double val=Math.abs(error);
			if(val<min){
				min=val;
			}
		}
		
		return min;
	}

}


