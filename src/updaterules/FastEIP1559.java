package updaterules;

public class FastEIP1559 implements UpdateRule {
	
	private double d = 0.25;
	
	public double getD() {
		return d;
	}
	
	public double updateBasefee(double b, double g) {
		return b*(1+d*(2*g-1));
	}
}
