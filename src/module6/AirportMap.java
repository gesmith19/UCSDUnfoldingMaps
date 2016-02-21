package module6;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.utils.MapUtils;


import de.fhpotsdam.unfolding.geo.Location;
import parsing.ParseFeed;
import processing.core.PApplet;

/** An applet that shows airports (and routes)
 * on a world map.  
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
@SuppressWarnings("unused")
public class AirportMap extends PApplet {
	
	UnfoldingMap map;
	private List<Marker> airportList;
	List<Marker> routeList;
	
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	
	private HashMap<Integer, AirportMarker> airportRoutes;
	private static final long serialVersionUID = 1L;
	
	public void setup() {
		// setting up PAppler
		size(800,600, OPENGL);
		
		// setting up map and default events
		map = new UnfoldingMap(this, 50, 50, 750, 550);
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// get features from airport data
		List<PointFeature> features = ParseFeed.parseAirports(this, "airports.dat");
		
		// list for markers, hashmap for quicker access when matching with routes
		airportList = new ArrayList<Marker>();
		HashMap<Integer, Location> airports = new HashMap<Integer, Location>();
		airportRoutes = new HashMap<Integer, AirportMarker>();
		
		// create markers from features
		for ( PointFeature feature : features ) {
			AirportMarker m = new AirportMarker( feature );
	
			m.setRadius(5);
			airportList.add(m);
			
			// put airport in hashmap with OpenFlights unique id for key
			airports.put(Integer.parseInt(feature.getId()), feature.getLocation());
			airportRoutes.put( Integer.parseInt( feature.getId() ), m);
		
		}
		
		
		// parse route data
		List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
		routeList = new ArrayList<Marker>();
		for(ShapeFeature route : routes) {
			
			// get source and destination airportIds
			int source = Integer.parseInt((String)route.getProperty("source"));
			int dest = Integer.parseInt((String)route.getProperty("destination"));
			
			// get locations for airports on route
			if(airports.containsKey(source) && airports.containsKey(dest)) {
				route.addLocation(airports.get(source));
				route.addLocation(airports.get(dest));
			}
			
			SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());
			sl.setHidden( true );
		    
//			System.out.println(sl.getProperties());
			
			//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
			
			 routeList.add(sl);
			
			 // this adds the routes to the routes in the airportmarker instance
			if ( airportRoutes.containsKey( source ) && airportRoutes.containsKey( dest ) ) {
				airportRoutes.get( source ).addRoute( sl );
				airportRoutes.get( dest ).addRoute( sl );
			}
		}
		
		sortAndPrint(20);
		
		
		//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
		map.addMarkers(routeList);
		
		map.addMarkers(airportList);
		
	}
	
	public void draw() {
		background(0);
		map.draw();
		
	}
	
	private void sortAndPrint(int numToPrint) {
		List<AirportMarker> alm = new ArrayList<AirportMarker>();
		for ( Marker m : airportList ) {
			AirportMarker am = ( AirportMarker )m;
			alm.add( am );
		}
		Collections.sort( alm, Collections.reverseOrder() );
		int max = ( numToPrint >= alm.size() ? alm.size() : numToPrint ); 
		for ( int i = 0; i < max; i++ ) {
			System.out.println(alm.get( i ) );
		}
	}

	
	/** Event handler that gets called automatically when the 
	 * mouse moves.
	 */
	@Override
	public void mouseMoved()
	{
		// clear the last selection
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
		
		}
		selectMarkerIfHover( airportList );

		//loop();
	}

	// If there is a marker selected 
	private void selectMarkerIfHover(List<Marker> markers)
	{
		// Abort if there's already a marker selected
		if (lastSelected != null) {
			return;
		}
		
		for (Marker m : markers) {
			CommonMarker marker = (CommonMarker)m;
			if (marker.isInside(map,  mouseX, mouseY)) {
				lastSelected = marker;
				marker.setSelected(true);
				return;
			}
		}
	}
	
	/** The event handler for mouse clicks
	 * It will display an airport and lines for the routes out of and into the airport
	 */
	@Override
	public void mouseClicked()
	{
		if (lastClicked != null) {
			displayDefaultMarkers();
			lastClicked = null;
		}
		else if (lastClicked == null) {
			checkAirportsForClick();
		}
	}
	
/*
 * default setting for markers.  Airports are displayed and routes are hidden	
 */
	private void displayDefaultMarkers() {
		
		for ( Marker m : airportList ) {
			m.setHidden( false );
		}
		
		for ( Marker m : routeList ) {
			m.setHidden( true );
		}
	}
	
		
	private void checkAirportsForClick() {
		if (lastClicked != null) return;
		
		for (Marker marker : airportList) {
		if (!marker.isHidden() && marker.isInside(map, mouseX, mouseY)) {
			lastClicked = (CommonMarker)marker;
	//		System.out.println(marker.getProperties());
			
				
				// hide the other airports
				for (Marker mk : airportList) {
				if (mk != lastClicked) {
					mk.setHidden(true);
				}
			}
				
				// display the routes for selected airport
				int count = 0;
				for (Marker sl : ((AirportMarker)lastClicked).routes) {
				sl.setHidden(false);
		//		System.out.println(sl.getProperties());
				count += 1;
				
				}
				System.out.println( "number of routes displayed: " + count);
				
			return;
			}
		}
	} // end of checkAirportsForClick()
}
