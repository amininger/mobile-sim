package probcog.perception;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import probcog.sensor.Sensor;
import probcog.sensor.SimKinectSensor;
import probcog.sensor.SimKinectSensor.SimPixel;
import probcog.sim.SimLocation;
import april.config.Config;
import april.sim.SimObject;
import april.sim.SimWorld;

public class SimKinectSegment implements Segmenter
{
    private ArrayList<Sensor> sensors = new ArrayList<Sensor>();
    private SimKinectSensor kinect;
    
    private final static int PIXEL_STRIDE = 2; 
    // Number of pixels to move for each sample
    // Higher values produce a less dense image
    
    // Set up some "Random" colors to draw the segments
    static int[] colors = new int[]{0xff3300CC, 0xff9900CC, 0xffCC0099, 0xffCC0033,
        0xff0033CC, 0xff470AFF, 0xff7547FF, 0xffCC3300,
        0xff0099CC, 0xffD1FF47, 0xffC2FF0A, 0xffCC9900,
        0xff00CC99, 0xff00CC33, 0xff33CC00, 0xff99CC00};

    public SimKinectSegment(Config config_, SimWorld world) throws IOException
    {
    	
        kinect = new SimKinectSensor(world);

        sensors.add(kinect);    // XXX
    }
    
    public ArrayList<Obj> getSegmentedObjects(){
    	HashMap<SimObject, PointCloud> pointClouds = new HashMap<SimObject, PointCloud>();  
    	
    	PointCloud lastPC = null;
    	SimObject lastObj = null;    	
    	
        int height = kinect.getHeight();
        int width = kinect.getWidth();

        // Go through the camera and get points for each pixel 
        // Keep the points separated based on the object that they hit
        for (int y = 0; y < height; y += PIXEL_STRIDE) {
            for (int x = 0; x < width; x += PIXEL_STRIDE) {
            	SimPixel pixel = kinect.getPixel(x, y);
            	if(pixel.target == null){
            		// Ray didn't hit anything
            		continue;
            	}
            	if(pixel.target instanceof SimLocation){
            		// We aren't segmenting sim locations
            		continue;
            	}
            	if(pixel.target == lastObj){
            		// Small optimization to avoid some map searching for consecutive rays hitting the same object
            		lastPC.addPoint(pixel.point);
            		continue;
            	} 
            	lastObj = pixel.target;
            	lastPC = pointClouds.get(lastObj);
            	if(lastPC == null){
            		lastPC = new PointCloud();
            		pointClouds.put(lastObj, lastPC);
            	}
        		lastPC.addPoint(pixel.point);
            }
        }
        
        // Turn the segmented point clouds into Obj's
    	ArrayList<Obj> segmentedObjs = new ArrayList<Obj>();
    	for(Map.Entry<SimObject, PointCloud> entry : pointClouds.entrySet()){
    		Obj obj = new Obj(false, entry.getValue());
    		obj.setSourceSimObject(entry.getKey());
    		segmentedObjs.add(obj);
    	}
    	
    	return segmentedObjs;    	
    }

    // === Provide access to the raw sensor === //
    // XXX This seems a bit hacky to provide...
    public ArrayList<Sensor> getSensors()
    {
        return sensors;
    }
}
