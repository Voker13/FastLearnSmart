
import java.util.ArrayList;

public class Tour {

	private int duration = 0;
	private static final int maxDuration = 480;
	private int interlaceAt;
	private ArrayList<Location> tourStops = new ArrayList<>();

	public Tour() {
		tourStops.add(Main.getDepot());
	}

	public int getDuration() {
		return duration;
	}

	// public void setDuration(int duration) {
	// this.duration = duration;
	// }

	public int getMaxDuration() {
		return maxDuration;
	}

	// public void setMaxDuration(int maxDuration) {
	// this.maxDuration = maxDuration;
	// }

	public ArrayList<Location> getTourStops() {
		return tourStops;
	}

	// public void setTourStops(ArrayList<Location> tourStops) {
	// this.tourStops = tourStops;
	// }
	
	
	public void setTourStops(ArrayList<Location> tourStops) {
		this.tourStops = tourStops;
	}

	public int getInterlaceAt() {
		return interlaceAt;
	}

	public void setInterlaceAt(int interlaceAt) {
		this.interlaceAt = interlaceAt;
	}
	
	public String toString() {
		String returnString = "[";
		for (Location location : tourStops) {
			returnString += location.getName() + " ";
		}
		returnString += "] " + duration + " Minuten";

		return returnString;
	}
	
	/**
     * A method to prove if there is a interlace in a Tour, causing unnecessary extra time.
     * Sets the interlaceAt of this.tour.
     * Prints the place of the Tour, where the interlace is.
     * 
     * @return returns true if there is a interlace, else false
     */
    public boolean interlace() {
    	if (this.getTourStops().size() <= 2) {
    		System.err.println("interlace: tour.getTourStops().size() <= 2 !");
    		return false;
    	}
    	double angleOfFirstStop = this.getTourStops().get(1).getAngle();
    	if (angleOfFirstStop < this.getTourStops().get(2).getAngle()) {
    		for (int i=2; i<this.getTourStops().size(); i++) {
    			if (!this.getTourStops().get(i).equals(Main.getDepot()) && this.getTourStops().get(i).getAngle() < angleOfFirstStop) {
//    				System.out.println("interlace from position "+(i-1)+" to "+i);
//    				System.out.println("The new Startlocation of this Tour should be @"+(i-1));
    				this.setInterlaceAt(i-1);
    				return true;
    			}
    		}
    	}
    	else if (angleOfFirstStop > this.getTourStops().get(2).getAngle()) {
    		for (int i=2; i<this.getTourStops().size(); i++) {
    			if (!this.getTourStops().get(i).equals(Main.getDepot()) && this.getTourStops().get(i).getAngle() > angleOfFirstStop) {
//    				System.out.println("interlace from position "+(i-1)+" to "+i);
//    				System.out.println("The new Startlocation of this Tour should be @"+(i-1));
    				this.setInterlaceAt(i-1);
    				return true;
    			}
    		}
    	}
    	return false; // if here are no interlaces
    }

	public void solveInterlace() {
    	ArrayList<Location> zwischenspeicherLocations = new ArrayList<>();
    	zwischenspeicherLocations.add(Main.getDepot());
    	for (int i=this.getInterlaceAt(); i>=1; i--) {
    		zwischenspeicherLocations.add(this.getTourStops().get(i));
    	}
    	for (int i=this.getInterlaceAt()+1; i<this.getTourStops().size(); i++) {
    		zwischenspeicherLocations.add(this.getTourStops().get(i));
    	}
    	this.setTourStops(zwischenspeicherLocations);
    }
	
	public void addDurationEntireTour() {
//    	System.out.println(">>>DURATION_OLD: "+this.duration);
    	this.duration = 0;
    	for (int i=1; i<this.getTourStops().size(); i++) {
    		this.duration += (Main.getDistance(this.getTourStops().get(i), tourStops.get(i-1)) * 1000F * Main.getGroundAirQuotient() / Main.getMeterPerSecond()) / 60 + this.getTourStops().get(i).getDuration();
    	}
//    	System.out.println(">>>DURATION_NEW: "+this.duration);
    }

	public boolean addStopDepot(Location location, ArrayList<Location> locations) {
		if (duration
				+ (Main.getDistance(location, tourStops.get(tourStops.size() - 1)) * 1000F * Main.getGroundAirQuotient()
						/ Main.getMeterPerSecond()) / 60
				+ (Main.getDistance(Main.getDepot(), tourStops.get(tourStops.size() - 1)) * 1000F
						* Main.getGroundAirQuotient() / Main.getMeterPerSecond()) / 60
				+ location.getDuration() < maxDuration) {
			duration += (Main.getDistance(location, tourStops.get(tourStops.size() - 1)) * 1000F
					* Main.getGroundAirQuotient() / Main.getMeterPerSecond()) / 60 + location.getDuration();
			tourStops.add(location);
			locations.remove(Main.getIndex(locations, location));
			return true;
		}
		tourStops.add(Main.getDepot());
		duration += (Main.getDistance(Main.getDepot(), tourStops.get(tourStops.size() - 1)) * 1000F
				* Main.getGroundAirQuotient() / Main.getMeterPerSecond()) / 60;
		return false;
	}

	public void addDepot() {
		duration += (Main.getDistance(Main.getDepot(), tourStops.get(tourStops.size() - 1)) * 1000F
				* Main.getGroundAirQuotient() / Main.getMeterPerSecond()) / 60;
		tourStops.add(Main.getDepot());
	}
	
	/**
	 * 
	 * Tactics here: ....
	 * 
	 * @param locations
	 * 
	 * @return
	 */
	
	
	public boolean addNextStopSlicePlusFar(ArrayList<Location> locations) {
		// System.out.println(slice1.size()+slice2.size()+slice3.size()+slice4.size()+slice5.size()+slice6.size()+slice7.size()+slice8.size()+slice9.size()+slice10.size());
		if (!locations.isEmpty()) {
			return locations.isEmpty() ? false
					: tourStops.size() == 1
							? addStopDepot(Main.findFarthestLocation(tourStops.get(0), locations), locations)
							: addStopDepot(Main.findClosestLocation(tourStops.get(tourStops.size() - 1), locations), locations);
		}
		return false;
		
	}

}
