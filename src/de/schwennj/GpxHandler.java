package de.schwennj;

import com.garmin.xmlschemas.trackpointextension.v1.TrackPointExtension;
import com.topografix.gpx._1._1.ExtensionsType;
import com.topografix.gpx._1._1.Gpx;
import com.topografix.gpx._1._1.WptType;

import javax.xml.bind.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.io.PrintStream;
import java.util.Map;

/**
 * Created by Jochen on 07.02.2016.
 */
public class GpxHandler {
    private JAXBContext jaxbContext;

    public GpxHandler() {
        try {
            this.jaxbContext = JAXBContext.newInstance(Gpx.class);
        }
        catch (JAXBException e) {
            System.err.println("Could not create JAXB context: " + e.getMessage());
        }

    }

    public void writeGpx(Gpx gpx, PrintStream output_gpx) throws JAXBException {
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(gpx, output_gpx);
    }

    public void writeGpx(Gpx gpx, File output_gpx) throws JAXBException {
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(gpx, output_gpx);
    }

    public Gpx parseGpx(File file) throws JAXBException {
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        JAXBElement<Gpx> root = (JAXBElement<Gpx>)jaxbUnmarshaller.unmarshal(file);
        return root.getValue();
    }

    public void insertHeartrates(Map<XMLGregorianCalendar, Short> heartrates, Gpx gpx) {
        short lastHr = 80;
        for (WptType wpt : gpx.getTrk().get(0).getTrkseg().get(0).getTrkpt()) {
            TrackPointExtension tpe = new TrackPointExtension();
            XMLGregorianCalendar time = wpt.getTime();
            ExtensionsType extension = new ExtensionsType();
            if (heartrates.containsKey(time)) {
                lastHr = heartrates.get(time);
            }
            tpe.setHr(lastHr);
            extension.getAny().add(tpe);
            wpt.setExtensions(extension);
        }
    }


}
