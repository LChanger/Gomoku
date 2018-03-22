package AI_package;

public class EvaluetePoint {
	private int x;
	private int y;
	private double value;
	public EvaluetePoint(int x,int y,double value) {
		this.x=x;
		this.y=y;
		this.value=value;
	}
	public EvaluetePoint(double value) {
		this.value=value;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public double getValue() {
		return value;
	}
	
}
