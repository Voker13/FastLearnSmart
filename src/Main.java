import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class Main {

	private static ArrayList<Location> locations;
	private static Instance instance;
	private static ArrayList<Edge> edges;
	private static double groundAirQuotient;
	private static double kilometerPerHour;
	private static double meterPerSecond;
	private static Location depot;
	private static int distanceAir = 0;
	private static int distanceGround = 0;

	public static void main(String[] args) throws JAXBException, FileNotFoundException {

		long time = System.currentTimeMillis();
		
		JAXBContext jc = JAXBContext.newInstance(Instance.class);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		File xml = null;
		if (args.length == 0) {
			xml = new File("Instance-400.xml");
		} else {
			xml = new File(args[0]);
		}

		long time3 = System.currentTimeMillis();
		long timez = time3 - time;
		System.out.println(timez);
		
		// load instance from File
		instance = (Instance) unmarshaller.unmarshal(xml);

		// save depot as special Location
		depot = instance.getLocations().get(0);
		locations = (ArrayList<Location>) instance.getLocations();
		locations.remove(0);

		edges = (ArrayList<Edge>) instance.getEdges();

		calculateGroundToAirQuotient();
		calculateAvarageSpeed();
		generateAngleToLocation();
		generateDistance0ToLocation();
		
		/**
		 * tactic
		 */
		slicesPlusFarStrategy();

		long time2 = System.currentTimeMillis();
		System.out.println(time2-timez);
		
	}

	private static Tour findWorkDaySlicePlusFar(ArrayList<Location> locations) {
		Tour tour = new Tour();
		while (tour.addNextStopSlicePlusFar(locations)) {

		}
		return tour;
	}

	private static void slicesPlusFarStrategy() {
		ArrayList<Tour> allToursSlicePlusFar = new ArrayList<Tour>();

		while (!locations.isEmpty()) {
			allToursSlicePlusFar.add(findWorkDaySlicePlusFar(locations));
		}
		int durationOverallSlicePlusFar = 0;
		for (Tour tour : allToursSlicePlusFar) {
			durationOverallSlicePlusFar += tour.getDuration();
		}

		System.err.println("SlicePlusFar Strategy: " + (allToursSlicePlusFar.size())
				+ " Touren mit einer Gesamtfahrzeit von " + durationOverallSlicePlusFar + " Minuten");
	}

	public static Location findClosestLocation(Location location, ArrayList<Location> locations) {
		Location returnLocation = locations.get(0);
		for (int i = 1; i < locations.size(); i++) {
			if ((getDistance(location, locations.get(i)) < getDistance(returnLocation, location))) {
				returnLocation = locations.get(i);
			}
		}
		return returnLocation;
	}

	public static Location findFarthestLocation(Location location, ArrayList<Location> locations) {
		Location returnLocation = locations.get(0);
		for (int i = 1; i < locations.size(); i++) {
			if ((getDistance(location, locations.get(i)) > getDistance(returnLocation, location))) {
				returnLocation = locations.get(i);
			}
		}
		return returnLocation;
	}

	private static void calculateAvarageSpeed() {
		int time = 0;
		for (int i = 0; i < locations.size(); i++) {
			if (!(i == 0)) {
				time += getEdge(0, i).getDuration();
			}
		}

		kilometerPerHour = (distanceGround * 60) / (time);
		meterPerSecond = kilometerPerHour / 3.6F;
	}

	private static void calculateGroundToAirQuotient() {
		for (int i = 0; i < locations.size(); i++) {
			if (!(i == 0)) {
				distanceGround += getEdge(0, i).distance / 1000;
				distanceAir += getDistance(depot, locations.get(i));
			}
		}
		groundAirQuotient = (float) distanceGround / distanceAir;
	}

	public static float getDistance(Location location1, Location location2) {
		float lat1 = location1.getLat();
		float long1 = location1.getLong() / 10;
		while (long1 > 20) {
			long1 /= 10;
		}
		float lat2 = location2.getLat();
		float long2 = location2.getLong() / 10;
		while (long2 > 20) {
			long2 /= 10;
		}
		double lat = (lat1 + lat2) / 2 * 0.01745;
		float latDifference = (float) (111.3 * (lat1 - lat2));
		float longDifference = (float) (111.3 * Math.cos(lat) * (long1 - long2));
		
		return (float) Math.sqrt(latDifference * latDifference + longDifference * longDifference);
	}

	public static void generateAngleToLocation() {
		double x0 = depot.getLong();
		double y0 = depot.getLat();
		for (Location location : locations) {
			double dx = x0 - location.getLong();
			double dy = y0 - location.getLat();
			if (dx >= 0 && dy >= 0) {
				location.setAngle(Math.toDegrees(Math.atan(dy / dx)));
			} else if (dx < 0 && dy >= 0) {
				location.setAngle(Math.toDegrees(Math.atan(dy / dx)) + 180);
			} else if (dx < 0 && dy < 0) {
				location.setAngle(Math.toDegrees(Math.atan(dy / dx)) + 180);
			} else if (dx >= 0 && dy < 0) {
				location.setAngle(Math.toDegrees(Math.atan(dy / dx)) + 360);
			}
			// //System.err.println(location.getAngle());
		}
	}

	public static void generateDistance0ToLocation() {
		double x0 = depot.getLong();
		double y0 = depot.getLat();
		for (Location location : locations) {
			double dx = x0 - location.getLong();
			double dy = y0 - location.getLat();
			location.setDistance0(Math.sqrt((dx * dx) + (dy * dy)));
			// //System.err.println("distance0: "+location.getDistance0());
		}
	}

	public static int getIndex(ArrayList<Location> locations, Location location) {
		for (int i = 0; i < locations.size(); i++) {
			if (locations.get(i).equals(location)) {

				return i;
			}
		}

		return -1;
	}

	private static Edge getEdge(int one, int two) {
		for (Edge edge : edges) {
			if ((edge.from == one && edge.to == two) || (edge.from == two && edge.to == one)) {
				return edge;
			}
		}
		return null;
	}

	public static Location getDepot() {
		return depot;
	}

	public static double getGroundAirQuotient() {
		return groundAirQuotient;
	}

	public static double getMeterPerSecond() {
		return meterPerSecond;
	}

}
