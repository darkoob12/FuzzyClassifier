import java.util.ArrayList;


public class Partition {
	///////////////
	//Fields
	private static float[][] par; 	//determining fuzzy sets

	//////////////////
	// constructor - making a partition in the feature space
	public Partition(){
		par = new float[14][3];
		int i = 0;
		float t;
		for(int k = 1;k < 5;k++) {
			for (int j = 0;j < k+1;j++) {
				for (int r = -1;r < 2;r++){
					t = (j + r)/(float)k;
					if (t<0)
						t = 0;
					else if (t>1)
						t = 1;
					par[i][r+1] = t;
				}
				i++;
			}
		}
	}
	
	
	/////////////////
	//Methods
	/////////////////
	//returning membership of a feature in all of the fuzzy sets
	public ArrayList<Double> getMem(double x) {
		ArrayList<Double> mem = new ArrayList<Double>();
		for (int i = 0;i < 14;i++){
			mem.add(tri(x,par[i][0],par[i][1],par[i][2]));
		}
		mem.add((double)1);	//representing 15th set 'don't care'
		return mem;
	}
	
	//returning the compatibility degree of a pattern with fuzzy sets
	public ArrayList<Double> getComp(ArrayList<Double> instance){
		ArrayList<Double> mem = new ArrayList<Double>();
		ArrayList<Double> temp = null;
		for (int i = 0;i < 14;i++){
			mem.add((double) 1);
		}
		for(double d:instance) {
			temp = getMem(d);
			for (int i = 0;i < 14;i++){
				mem.set(i, mem.get(i) * temp.get(i));
			}
		}
		
		mem.add((double)1); //set 'don't care'
		return mem;
	}
	
	// triangular fuzzy membership function
	public double tri(double x,double a,double b,double c) {
		double t1 = (x - a) / (b - a);
		double t2 = (c - x) / (c - b);
		return Math.max(Math.min(t1, t2), 0);
	}
	
	// a triangular fuzzy membership function
	public double tri(double x,int fuzzy) {
		double ret = 0;
		double a = par[fuzzy][0];
		double b = par[fuzzy][1];
		double c = par[fuzzy][2];
		double t1 = (x - a) / (b - a);
		double t2 = (c - x) / (c - b);
		ret = Math.max(Math.min(t1, t2), 0);
		if (Double.isNaN(ret)){
			ret = 0;
		}
		return ret;
	}

	public double tri2(double x,int fuzzy) {
		double ret = 0;
		double a = par[fuzzy][0];
		double b = par[fuzzy][1];
		double c = par[fuzzy][2];
		
		if (x <= a){
			ret = 0;
		}else if (x>a && x<b) {
			ret = (x-a)/(b-a);
		}else if (x == b) {
			ret = 1;
		}else if (x>b && x<c) {
			ret = (c-x)/(c-b);
		}else {
			ret = 0;
		}
		
		return ret;
	}

	public double getRuleMem(rule argRule,ArrayList<Double> instance){
		double mem = 1;
		for (int x = 0;x < argRule.size;x++) {
			mem *= tri2(instance.get(argRule.features[x] - 1),argRule.fuzzysets[x]);
		}
		return mem;
	}

	public double getRuleMem(int fet[],int fuzz[],ArrayList<Double> instance){
		double mem = 1;
		for (int x = 0;x < fet.length;x++) {
			mem *= tri(instance.get(fet[x]),fuzz[x]);
		}
		return mem; 
	}
}
