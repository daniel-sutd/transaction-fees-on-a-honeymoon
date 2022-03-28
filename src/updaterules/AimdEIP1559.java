package updaterules;

import java.util.ArrayList;
import java.util.List;

public class AimdEIP1559 implements UpdateRule {
	
	private double d = 0.125;
	
	int n = 8;
	
	private List<Double> gs;
	
	public AimdEIP1559() {
		gs = new ArrayList<Double>(n);
	}
	
	public double getD() {
		return d;
	}
	
	public double updateBasefee(double b, double g) {
		double avgG = 0;
		for(Double gg : gs) {
			avgG += gg;
		}
		avgG /= gs.size();
		if(avgG >= 0.75 || avgG < 0.25) {
			d += 0.025;
		} else {
			d *= 0.95;
		}

		if(gs.size() >= n) gs.remove(0);
		gs.add(g);
		d = Math.max(Math.min(d,1),0.025);
		
		return b*(1+d*(2*g-1));
	}
}
