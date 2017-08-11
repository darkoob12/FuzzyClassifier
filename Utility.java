import java.util.ArrayList;


public class Utility {
	//Insertion Sort Implementation
	public static void sortAscend(ArrayList<Integer> l,double s[]) {	
		for (int i = 1;i < l.size();i++) {
			int j = i - 1;
			int key = l.get(i);
			double value  = s[i];
			while ((j > -1) && (s[j] >= value)) {
				l.set(j+1, l.get(j));
				s[j+1] = s[j];
				j--;
			}
			l.set(j+1,key);
			s[j+1] = value;
		}
	}
	
	//GENERATING NEXT COMBINATION IN LEXICOGRAPHIC ORDER
	public static int[] nextCombin(int cur[],int n,int r){
		boolean equ = true;
		for (int q = 0;q < r ;q++) {
			if (cur[(r-1) - q] != n - (q))
				equ = false;
		}
		if (equ) { 
			return null;  //this was the last combination
		} else {
			int i = r - 1;
			while (cur[i] == n - r + i + 1) {
				i--;	
			}
			
			cur[i]++;
			for (int j = i + 1;j < r;j++) {
				cur[j] = cur[i] + j - i;
			}
			return cur;
		}
	}
	
	//CALCULATING n!
	public static int fact(int n) {
		int f = 1;
		if (n > 0) {
			for (int k = n;k > 0;k--) {
				f *= k;
			}
		}
		return f;
	}
	
	public static int[][] getAllComb(int n, int r) {
		int ret[][] = new int[(int) Math.pow(n, r)][r];
		int t;
		switch (r){
		case 1:
			t = 0;
			for (int i = 0;i < n;i++){
				ret[t][0] = i;
				t++;
			}
			break;
		case 2:
			t = 0;
			for (int i = 0;i < n;i++){
				for (int j = 0;j < n;j++){
					ret[t][0] = i;
					ret[t][1] = j;
					t++;
				}
			}
			break;
		case 3:
			t = 0;
			for (int i = 0;i < n;i++){
				for (int j = 0;j < n;j++){
					for (int k = 0;k < n;k++){
						ret[t][0] = i;
						ret[t][1] = j;
						ret[t][2] = k;
						t++;
					}
				}
			}
			break;
		default:
			ret = null;
			break;
		}	
		return ret;
	}
	
	public static int getCombNum(int n, int r) {
		return fact(n)/(fact(n-r)*fact(r));
	}


	//SUMMING VALUES IN AN ARRAY
	public static double sumValues(double arr[]) {
		double ret = 0;
		for (double d:arr){
			ret += d;
		} 
		return ret;
	}

	//RETURNING INDEX OF MAXIMUM VALUE OF AN ARRAY 
	public static int maxInd(double arr[]) {
		int ret = 0;
		for (int i = 1;i < arr.length;i++){
			if (arr[i] > arr[ret])
				ret = i;
		}
		return ret;
}
	

}
