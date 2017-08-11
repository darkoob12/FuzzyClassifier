import java.util.ArrayList;


public class rule {
	//fields
	public int size;
	public int features[];
	public int label;
	public int fuzzysets[];
	public double Mu;	//sum of comp. degree of patterns with label similar label of this rule  
	public double SumMu; //sum of comp. degree of all patterns;
	public double weight;
	public ArrayList<Integer> I;
	
	//constructors
	public rule(int argSize) {
		size = argSize;
		features = new int[size];
		fuzzysets = new int[size];
		I = new ArrayList<Integer>();
		weight = 1;
	}
	
	public String toString() {
		String s = "";
		for (int d = 0;d < size;d++) {
			s += features[d] + ":" + fuzzysets[d] + " ";  
		}
		s += " => " + label + " , " + weight;
		return s;
	}
	//HEURISTIC RULE EVALUTAION MEASURE
	public double heuristic() {
		return ((2 * Mu) - SumMu); //Positive - Negative	
	}

}
