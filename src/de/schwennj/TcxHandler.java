package de.schwennj;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jochen on 07.02.2016.
 */
public class TcxHandler {

    private JAXBContext jaxbContext;

    public TcxHandler() {
        try {
            this.jaxbContext = JAXBContext.newInstance(TrainingCenterDatabaseT.class);
        }
        catch (JAXBException e) {
            System.err.println("Could not create JAXB context: " + e.getMessage());
        }
    }


    public TrainingCenterDatabaseT parseTcx(File file) throws JAXBException {
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        JAXBElement<TrainingCenterDatabaseT> root = (JAXBElement<TrainingCenterDatabaseT>)jaxbUnmarshaller.unmarshal(file);
        return root.getValue();
    }

    public Map<XMLGregorianCalendar, Short> getHeartrates(TrainingCenterDatabaseT tcx) {
        Map<XMLGregorianCalendar, Short> heartrates = new HashMap<>();
        for (ActivityT activity : tcx.getActivities().getActivity()) {
            for(ActivityLapT lap : activity.getLap()) {
                for (TrackT track : lap.getTrack()) {
                    for (TrackpointT trackpoint : track.getTrackpoint()) {
                        XMLGregorianCalendar time = trackpoint.getTime();
                        Short bpm = 0;
                        if (trackpoint.getHeartRateBpm() != null) {
                            bpm = trackpoint.getHeartRateBpm().getValue();
                        }
                        if (bpm != 0) {
                            if (heartrates.containsKey(time)) {
                                System.err.println("Duplicate time " + time.toString());
                            } else {
                                heartrates.put(time, bpm);
                            }
                        }
                    }
                }
            }
        }
        return heartrates;
    }


}
