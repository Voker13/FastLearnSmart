import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


public class Main {

	private static Instance instance;
	
	public static void main(String[] args) throws JAXBException, FileNotFoundException {
		JAXBContext jc = JAXBContext.newInstance(Instance.class);

		Unmarshaller unmarshaller = jc.createUnmarshaller();
		File xml = null;
		if (args.length == 0) {
		    xml = new File("Instance-400.xml");
		} else {
		    xml = new File(args[0]);
		}

		// load instance from File
		instance = (Instance) unmarshaller.unmarshal(xml);

		// save depot as special Locaion
		depot = instance.getLocations().get(0);

		// clone Array to have a cop for drawing
		locations = (ArrayList<Location>) instance.getLocations();
		locCopy = (ArrayList<Location>) locations.clone();
		
		// remove depot from Locations
		locations.remove(0);

		generateAngleToLocation();
		generateDistance0ToLocation();

	}

}
