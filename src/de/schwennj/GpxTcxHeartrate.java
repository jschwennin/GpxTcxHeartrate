package de.schwennj;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.*;
import com.topografix.gpx._1._1.Gpx;

import javax.xml.bind.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.util.Map;

public class GpxTcxHeartrate {

    public static void main(String[] args) {
        Map<XMLGregorianCalendar, Short> heartrates;
        try {

            GpxHandler gpxHandler = new GpxHandler();
            TcxHandler tcxHandler = new TcxHandler();

            File input_gpx = new File("C:\\Users\\Jochen\\Downloads\\garmin_strava\\Morning Ride.gpx");
            File input_hr = new File("C:\\Users\\Jochen\\Downloads\\garmin_strava\\activity_1030361650.tcx");
            File output_gpx = new File("C:\\Users\\Jochen\\Downloads\\garmin_strava\\Javatest.gpx");

            Gpx gpx = gpxHandler.parseGpx(input_gpx);

            TrainingCenterDatabaseT tcx = tcxHandler.parseTcx(input_hr);
            heartrates = tcxHandler.getHeartrates(tcx);

            gpxHandler.insertHeartrates(heartrates, gpx);

            gpxHandler.writeGpx(gpx, output_gpx);

        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

}
