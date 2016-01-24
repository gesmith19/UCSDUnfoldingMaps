package module3;

//Java utilities libraries
import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
import java.util.List;

//Processing library
import processing.core.PApplet;

//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;

//Parsing library
import parsing.ParseFeed;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Gillian Smith
 * Date: January 22, 2016
 * */
public class EarthquakeCityMap extends PApplet {

	// You can ignore this.  It's to keep eclipse from generating a warning.
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFLINE, change the value of this variable to true
	private static final boolean offline = false;
	
	// Less than this threshold is a light earthquake
	public static final float THRESHOLD_MODERATE = 5;
	// Less than this threshold is a minor earthquake
	public static final float THRESHOLD_LIGHT = 4;

	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	// The map
	private UnfoldingMap map;
	
	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";

	// define colours for markers
	private int yellow = color( 255, 255, 0 );
	private int red = color( 255, 0, 0 );
	private int blue = color( 0, 0, 255 );
	
	
	public void setup() {
		size(950, 600, OPENGL);

		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 700, 500, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom"; 	// Same feed, saved Aug 7, 2015, for working offline
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 700, 500, new Google.GoogleMapProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
			 earthquakesURL = "2.5_week.atom";
		}
		
	    map.zoomToLevel(2);
	    MapUtils.createDefaultEventDispatcher(this, map);	
			
	    // The List you will populate with new SimplePointMarkers
	    List<Marker> markers = new ArrayList<Marker>();

	    //Use provided parser to collect properties for each earthquake
	    //PointFeatures have a getLocation method
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    
	    // These print statements show you (1) all of the relevant properties 
	    // in the features, and (2) how to get one property and use it
	    if (earthquakes.size() > 0) {
	    	PointFeature f = earthquakes.get(0);
	    	System.out.println(f.getProperties());
	    	Object magObj = f.getProperty("magnitude");
	    	float mag = Float.parseFloat(magObj.toString());
	    	// PointFeatures also have a getLocation method
	    }
	    
	    // create markers for each earthquake
	    for ( PointFeature pf : earthquakes ) {
	    	markers.add( createMarker( pf ) );    	
	    }
	    
	    map.addMarkers( markers );
	} // end of setup()
		
	// A suggested helper method that takes in an earthquake feature and 
	// returns a SimplePointMarker for that earthquake
	private SimplePointMarker createMarker(PointFeature feature)
	{
		SimplePointMarker spm = new SimplePointMarker( feature.getLocation() );
		
		// find out the magnitude of the earthquake
		Object magObj = feature.getProperty( "magnitude" );
		float mag = Float.parseFloat( magObj.toString() );
		
		if ( mag >=  THRESHOLD_MODERATE ) {
			spm.setColor( red );
			spm.setRadius( (float) 16 );
		}
		else if ( mag < THRESHOLD_LIGHT ) {
			spm.setColor( blue );
			spm.setRadius( (float) 5 );
		}
		else {
			spm.setColor( yellow );
		}
		
		return spm;
	}
	
	public void draw() {
	    background(10);
	    map.draw();
	    addKey();
	}


	// helper method to draw key in GUI
	private void addKey() 
	{	
		// Remember you can use Processing's graphics methods here
	    
		//
		//rect(a, b, c, d, e)
		// a = x co-ord, b = y co-ord, c = width, d = height, e = radii for all 4 corners
		// 
		// ellipse(a, b, c, d)
		// a = x co-ord, b = y co-ord, c = width, d = height
		// when width = height, ellipse is a circle
		//
		// text(s, x, y, z)
		// s = string to be displayed, x = x co-ord, y = y co-ord, z = depth, default is 0
		// ie 1 line
		//
		// fill(rgb)
		// fill ( v1, v2, v3  )
		// v1= red value, v2 = green value, v3 blue value
		
		//set up the key area
		fill( 250, 243, 243 );

		rect( 30, 50, 150, 200, 24 );
		
		// title
		textSize( 14 );
		textAlign( CENTER );
		fill( 50 );
		text( "Earthquake Key", 100, 90 );
		
		// set the red marker
		fill( 255, 0, 0 );
		ellipse( 50, 120, 15, 15 );
		
		// set the red marker text
		textSize( 12 );
		textAlign( LEFT );
		fill( 50 );
		text( "5.0+ Magnitude", 70, 125 );
		
		// set the yellow marker
		fill( 255, 255, 0 );
		ellipse( 50, 150, 10, 10 );
				
		// set the yellow marker text
		textSize( 12 );
		textAlign( LEFT );
		fill( 50 );
		text( "4.0+ Magnitude", 70, 155 );
				
		// set the blue marker
		fill( 0, 0, 255 );
		ellipse( 50, 180, 5, 5 );
				
		// set the blue marker text
		textSize( 12 );
		textAlign( LEFT );
		fill( 50 );
		text( "Below 4.0", 70, 185 );
	}
}
