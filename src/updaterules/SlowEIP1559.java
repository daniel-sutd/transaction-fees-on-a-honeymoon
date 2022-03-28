package updaterules;

public class SlowEIP1559 implements UpdateRule {
	
	private double d = 0.0625;
	
	public double getD() {
		return d;
	}
	
	public double updateBasefee(double b, double g) {
		return b*(1+d*(2*g-1));
	}
}
