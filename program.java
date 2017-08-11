import java.io.*;
import java.util.*;



public class program {
	

	public static ArrayList<ArrayList<Double>> dataset = new ArrayList<ArrayList<Double>>();	//storing data
	public static ArrayList<Integer> labels = new ArrayList<Integer>(); //storing labels of members
	public static ArrayList<rule> c_rules = new ArrayList<rule>();	//candidate rules
	public static ArrayList<rule> rules = new ArrayList<rule>();  //final rule set
	
	
	public static void main(String[] args) {
		
		//////READING DATA FROM FILE
		load("wine.data");

		//////NORMALIZING EACH FEATURE
		normalize();

		/////GENERATING CANDIDATE FUZZY RULES
		ruleGen();
		
		/////SELECTING Q RULE FROM CANDIDATES
		select(100);
		c_rules.clear();
		c_rules = null;
		
		for (int i = 0;i < rules.size();i++)
			setI(i);
		
		//this part is used for outputing data to a file
		File f = new File("foo.csv");
		try {
			f.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter("foo.csv")); 
			for (int p = 0;p<3;p++){
				for (int i = 0;i < rules.size();i++) {
					ruleWeight(i);
					bw.write(errorRate() + ",");
				}
				ArrayList<rule> tep = new ArrayList<rule>();
				for (rule r:rules) {
					if (r.weight != 0)
						tep.add(r);
				}
				rules.clear();
				for (rule r:tep) {
					rules.add(r);
				}
				tep.clear();
				tep = null;
				System.out.print(performance() + " , " + rules.size());
				System.out.println();
			}
			bw.flush();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		

		System.out.println("Hello World!");
		
	}//  END OF MAIN FUNCTION
	
	public static void load(String fname) {
		try {
			String strLine;  //storing a line from file
			BufferedReader br = new BufferedReader(new FileReader(fname));
			String strTemp; //storing a value for conversion
			double value;   //storing a value
			ArrayList<Double> temp;	//storing a member 
			

			StringTokenizer st = null;
			boolean isClass;
			while ((strLine = br.readLine()) != null) {
				st = new StringTokenizer(strLine,",");
				isClass = true;
				temp = new ArrayList<Double>();
				while (st.hasMoreTokens()) {
					strTemp = st.nextToken();
					if (isClass){
						//saving label for current sample
						labels.add(Integer.valueOf(strTemp));
						isClass = false;
					} else {
					value = Double.valueOf(strTemp);
					temp.add(value);
					}
				}
				dataset.add(temp);
				
			}
			br.close();
		} catch (Exception e) {
			//Auto-generated catch block
			e.printStackTrace();
		}finally{
			//Nothing to do;
		}
		
	}
	
	public static double errorRate() {
		int ret;
		int eval[] = new int[dataset.size()];
		for(int i = 0;i < dataset.size();i++) {
			eval[i] = singleWinner(dataset.get(i));;
		}
		
		int t =0;
		for (int i = 0;i < eval.length;i++){
			if (eval[i] != labels.get(i))
					t++;
		}
		ret = t;
/*		System.out.println(ret);*/
		return ret;
	}
	

	public static double performance() {
		double ret;
		int eval[] = new int[dataset.size()];
		for(int i = 0;i < dataset.size();i++) {
			eval[i] = singleWinner(dataset.get(i));;
		}
		
		int t =0;
		for (int i = 0;i < eval.length;i++){
			if (eval[i] == labels.get(i))
					t++;
		}
		ret = t/((double)eval.length);
/*		System.out.println(ret);*/
		return ret;
	}
	
	//USING SINGLE WINNER REASONING METHOD TO EVALUATE A NEW PATTERN'S CLASS LABEL
	public static int singleWinner(ArrayList<Double> instance) {
		Partition per = new Partition();
		int cl = 0;
		double winner = -1;
		for (rule r: rules) {
			double t = per.getRuleMem(r, instance)*r.weight;
			if (t > winner) {
				winner = t;
				cl = r.label;
			}
		}
		return cl;
	}
	
	
	//PROJECTING EACH FEATURE IN [0,1]
	static void normalize() {
		int dim = dataset.get(0).size();
		double min ,max;
		for (int i = 0;i<dim;i++) {
			min = max = dataset.get(0).get(i);
			for (ArrayList<Double> arr: dataset) {
				if (arr.get(i) < min)
					min = arr.get(i);
				if (arr.get(i) > max)
					max = arr.get(i);
			}
			double t1 = max - min;
			double t2;
			for (ArrayList<Double> arr: dataset) {
				t2 = arr.get(i) - min;
				t2 /= t1;
				arr.set(i, t2);
			}
		}
	}
	
	//DETERMINING CONSEQUENT CLASS LABEL FOR EACH RULE
	public static int whichLabel(rule Rule){
		Partition per = new Partition();
		
		//calculating confidence for each label
		double conf[] = new double[3];
		double Mu[] = new double[3];
		//for each label calculate confidence
		double sd = 0;
		for (int i = 0;i < 3;i++){
			double sn = 0;
			for (int j = 0;j < dataset.size();j++){
				if (i == 0)	sd += per.getRuleMem(Rule, dataset.get(j));
				if (labels.get(j) == i + 1) {
					sn += per.getRuleMem(Rule, dataset.get(j));
				}
			}
			if (sd == 0) {
				Rule.label = -1;
				Rule.Mu = 0;
				Rule.SumMu = 0;
				return -1;
			} else {
				conf[i] = sn/sd;
				Mu[i] = sn;
			}
		}
		//selecting label with maximum confidence
		int t = -1;
		if (conf[0]>conf[1] && conf[0]>conf[2])
			t = 0;
		if (conf[1]>conf[0] && conf[1]>conf[2])
			t = 1;
		if (conf[2]>conf[0] && conf[2]>conf[1])
			t = 2;
		if (t != -1) {
			Rule.label = t + 1;
			Rule.Mu = Mu[t];
			Rule.SumMu = Utility.sumValues(Mu);
		}
		return t;
	}
	

	// GENERATING ALL RULES OF LENGTH 1 AND 2
	public static void ruleGen() {
		//rules with length 1 , 2 
		for (int s = 1;s < 3;s++) {
			int ft[][] = Utility.getAllComb(14,s);
			int init[] = new int[s];
			for (int i = 0;i < s;i++){
				init[i] = i+1;
			}
			int t[] = init;
			rule r = null;
			while (t != null) {
				for (int[] fz: ft){
					r = new rule(s);
					for (int b = 0;b < s;b++){
						r.features[b] = t[b];
						r.fuzzysets[b] = fz[b];
					}
					if (whichLabel(r) != -1) {
						c_rules.add(r);
					}
				}
				t = Utility.nextCombin(init,13,s);
				//System.out.println(Runtime.getRuntime().freeMemory());
			}
		} 
	}
	
	// SELECTING TOP Q RULE FROM EACH CLASS
	public static void select(int Q) {
		for (int l = 0;l < 3;l++) {
			ArrayList<rule> temp_rules = new ArrayList<rule>();
			for(rule r : c_rules) {
				if (r.label == l+1) {
					temp_rules.add(r);
				}
			}
			for (int k = 0; k < Q;k++){
				int index = 0;
				double max = temp_rules.get(0).heuristic();
				for (int i = 0;i < temp_rules.size(); i++){
					double tr = temp_rules.get(i).heuristic();
					if (tr > max) {
						index = i;
						max  = tr;
					}
				}
				rules.add(temp_rules.get(index));
				temp_rules.remove(index);
			}
		}
	}
	
	public static int setI(int ri){
		int with,without;
		for (int i = 0;i < dataset.size();i++) {
			rules.get(ri).weight = Double.MAX_VALUE;
			with = singleWinner(dataset.get(i));
			rules.get(ri).weight = 0;
			without = singleWinner(dataset.get(i));
			if (with == labels.get(i) || without == labels.get(i)) {
				if (with != without){
					rules.get(ri).I.add(i);
				}
			}
		}
		rules.get(ri).weight = 1;
		return rules.get(ri).I.size();		
	}

	// SETTING RULE WEIGHT FOR EACH SELECTED RULE
	public static void ruleWeight(int ri) {
		//calculating score for each rule in I if I is not empty
		if (rules.get(ri).I.size() > 0) {
			double listscore[] = new double[rules.get(ri).I.size()];
			for (int i = 0;i < listscore.length;i++) {
				listscore[i] = getScore(rules.get(ri), i);
			}
			Utility.sortAscend(rules.get(ri).I,listscore);

			double th = bestThreshold(ri,listscore);
			rules.get(ri).weight = th;
			if (th != 0)  {
				System.out.println(rules.get(ri));
			}
		}else {
			rules.get(ri).weight = 0;
		}
	}
	
	public static double bestThreshold(int ruleInd,double scores[]) {
		double best_th = 0;
		rules.get(ruleInd).weight = 0;
		double cur = performance();
		double opt = cur;
		double th;
		for (int i = 0;i < rules.get(ruleInd).I.size() - 1;i++) {
			th = (scores[i] + scores[i+1]) / 2;
			rules.get(ruleInd).weight = th;
			cur = performance();
			//System.out.print(cur + ", ");
			if (cur > opt) {
				opt = cur;
				best_th = th;
			}
		}
		th = scores[scores.length - 1] + .2;
		rules.get(ruleInd).weight = th;
		cur = performance();
		if (cur > opt) {
			opt = cur;
			best_th = th;
		}
/*		if (best_th != 0)
			System.out.println(opt + " , " + cur + " , " + best_th);*/
		return best_th;
	} 
	
	public static double getScore(rule r,int index) {
		Partition per = new Partition();
		double d = per.getRuleMem(r, dataset.get(index));
		double cmax = -1;
		for (rule rd:rules) {
			if (rd.label != labels.get(index)) {
				double w = rd.weight * per.getRuleMem(rd, dataset.get(index));
				if (cmax < w){
					cmax = w;
				}
			}
		}
		
		return (cmax / d);
	} 
}

