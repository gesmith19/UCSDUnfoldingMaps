package module6;

import java.util.ArrayList;
import java.util.List;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import processing.core.PConstants;
import processing.core.PGraphics;

/** 
 * A class to represent AirportMarkers on a world map.
 *   
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class AirportMarker extends CommonMarker implements Comparable<AirportMarker> {
	public  List<SimpleLinesMarker> routes;
	
	public AirportMarker(Feature city) {
		super(((PointFeature)city).getLocation(), city.getProperties());
		routes = new ArrayList<SimpleLinesMarker>();
	}
	
	@Override
	public void drawMarker(PGraphics pg, float x, float y) {
		colourDetermine( pg );
		pg.ellipse( x, y, 5, 5 );
	}

	@Override
	public void showTitle(PGraphics pg, float x, float y) {
		// show rectangle with title
		
		String title1 = getCode() + " " + getName();
		String title2 = getCity() + " " + getCountry();
		String title3 = "Number of routes: " + getRouteNumber();
		float w1 = pg.textWidth( title1 );
		float w2 = pg.textWidth( title2 );
		float w3 = pg.textWidth( title3 );
		
		float w = 0;
		
		// work out max width
		if ( w1 > w2 && w1 > w3 ){
			w = w1;	
		}
		else if ( w2 > w1 && w2 > w3 ){
			w = w2;
		}
		else if ( w3 > w1 && w3 > w2 ) {
			w = w3;
		}
		else
			w = w1;
		
		pg.pushStyle();
		
		// create the rectangle
        pg.stroke( 110 );
        pg.fill( 234, 235,213 );
        pg.rect( x, y + 10, w + 8, 54, 7 );
       
        // write the text in the rectangle
        pg.textAlign(PConstants.LEFT, PConstants.TOP);
        pg.fill( 0 );
        pg.textSize( 12 );
        pg.text( title1, x + 3 , y + 12 );
        pg.text( title2, x + 3 , y + 28 );
        pg.text( title3, x + 3, y + 44 );
        
     // Restore previous drawing style
     		pg.popStyle();
	}
	
	// determine color of marker from depth
	// We use: Deep = red, intermediate = blue, shallow = yellow
	private void colourDetermine( PGraphics pg ) {
		
		// get the number of routes
		int num = getRouteNumber();
					
		if ( num < 20 ) {
				pg.fill( 255, 255, 0 );
		}
		else if ( num < 50 ) {
				pg.fill( 0, 0, 255 );
		}
		else {
				pg.fill( 255, 0, 0 );
		}
	}
	
	private int getRouteNumber() {
		return routes.size();
	}
	
	public void addRoute(SimpleLinesMarker sl) {
		routes.add( sl );
	}
	
	public int compareTo(AirportMarker marker) {
		
		int thisRoutes = this.getRouteNumber();
		int otherRoutes = marker.getRouteNumber();
		int cmp = thisRoutes > otherRoutes ? +1 : thisRoutes < otherRoutes ? -1 : 0;
		return cmp;
	}
	
	// getter methods
	
	public String getName() {
		return getProperty( "name" ).toString();
	}
	
	public String getCode() {
		return getProperty( "code" ).toString();
	}
	
	public String getCity() {
	    return getProperty( "city" ).toString();
	}
	
	public String getCountry() {
		return getProperty( "country" ).toString();
	}
	
	public String toString() {
		String inf = getCity() + " " + getCode() + " " + getName() + " " + getCountry();
		inf = inf + " " + "routes: " + getRouteNumber();
		return inf;
    }
}
