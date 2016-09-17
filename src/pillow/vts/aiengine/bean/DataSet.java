package pillow.vts.aiengine.bean;

public class DataSet {// This class use to create object at runtime for store input data 
	// that get from the database

	private double definedSpeed;
	private double notifiedSpeed;
	private double dateOfWeek;
	private double time;
	
	public double getDefinedSpeed() {
		return definedSpeed;
	}
	public void setDefinedSpeed(double definedSpeed) {
		this.definedSpeed = definedSpeed;
	}
	public double getNotifiedSpeed() {
		return notifiedSpeed;
	}
	public void setNotifiedSpeed(double notifiedSpeed) {
		this.notifiedSpeed = notifiedSpeed;
	}
	public double getDateOfWeek() {
		return dateOfWeek;
	}
	public void setDateOfWeek(double dateOfWeek) {
		this.dateOfWeek = dateOfWeek;
	}
	public double getTime() {
		return time;
	}
	public void setTime(double time) {
		this.time = time;
	}
}
