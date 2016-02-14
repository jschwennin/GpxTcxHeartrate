package de.schwennj;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;
import com.topografix.gpx._1._1.Gpx;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by Jochen on 07.02.2016.
 */
public class MainGui extends JPanel implements ActionListener {
    private JPanel panel1;

    private JTextField gpxFilename;
    private JTextField tcxFilename;
    private JTextField outputFilename;
    private JButton importGPXButton;
    private JButton importTCXButton;
    private JButton mergeHeartrateButton;
    private JTextArea textArea;

    private GpxHandler gpxHandler;
    private TcxHandler tcxHandler;
    private Gpx gpx;
    private TrainingCenterDatabaseT tcx;

    private Logger logger;
    final JFileChooser fcGPX = new JFileChooser();
    final JFileChooser fcTCX = new JFileChooser();
    final JFileChooser fcOutput = new JFileChooser();

    final String newline = System.lineSeparator();

    public MainGui() {
        this.logger = Logger.getLogger(this.getClass().getName());

        gpxHandler = new GpxHandler();
        tcxHandler = new TcxHandler();

        fcGPX.setFileFilter(new FileNameExtensionFilter("*.gpx (GPX Track)", "gpx"));
//        fcGPX.setCurrentDirectory();
        fcTCX.setFileFilter(new FileNameExtensionFilter("*.tcx (Garmin Training)", "tcx"));

        gpx = null;
        tcx = null;

        importGPXButton.addActionListener(this);
        importTCXButton.addActionListener(this);
        mergeHeartrateButton.addActionListener(this);

    }

    public void actionPerformed(ActionEvent event) {
        //Handle open button action.
        if (event.getSource() == importGPXButton) {
            int returnVal = fcGPX.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fcGPX.getSelectedFile();
                logger.log(Level.INFO, "Opening: " + file.getName() + ".");
                gpxFilename.setText(file.getName());
                try {
                    gpx = gpxHandler.parseGpx(file);
                    textArea.append(newline + "Parsed GPX file " + gpxFilename.getText());
                } catch (JAXBException ex) {
                    textArea.append(newline + "Could not parse GPX file " + gpxFilename.getText());
                    logger.log(Level.SEVERE, "Could not parse GPX file " + gpxFilename.getText());
                    logger.log(Level.WARNING, ex.getMessage());
                    gpx = null;
                    gpxFilename.setText("");
                }
            } else {
                logger.log(Level.INFO, "Open command cancelled by user.");
            }
        }
        if (event.getSource() == importTCXButton) {
            fcTCX.setCurrentDirectory(fcGPX.getCurrentDirectory());
            int returnVal = fcTCX.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fcTCX.getSelectedFile();
                logger.log(Level.INFO, "Opening: " + file.getName() + ".");
                tcxFilename.setText(file.getName());
                try {
                    tcx = tcxHandler.parseTcx(file);
                    textArea.append(newline + "Parsed TCX file " + tcxFilename.getText());
                } catch (JAXBException ex) {
                    textArea.append(newline + "Could not parse TCX file " + tcxFilename.getText());
                    logger.log(Level.SEVERE, "Could not parse TCX file " + tcxFilename.getText());
                    logger.log(Level.WARNING, ex.getMessage());
                    tcx = null;
                    tcxFilename.setText("");
                }
            } else {
                logger.log(Level.INFO, "Open command cancelled by user.");
            }
        }
        if (event.getSource() == mergeHeartrateButton) {
            if ((gpx != null) && (tcx != null)) {
                fcOutput.setCurrentDirectory(fcGPX.getCurrentDirectory());
                int returnVal = fcOutput.showOpenDialog(this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fcOutput.getSelectedFile();
                    logger.log(Level.INFO, "Selected output: " + file.getName() + ".");
                    outputFilename.setText(file.getName());

                    Map<XMLGregorianCalendar, Short> heartrates;
                    heartrates = tcxHandler.getHeartrates(tcx);
                    gpxHandler.insertHeartrates(heartrates, gpx);
                    try {
                        gpxHandler.writeGpx(gpx, file);
                        textArea.append(newline + "Wrote result to " + outputFilename.getText());
                        logger.log(Level.INFO, "Wrote result to " + outputFilename.getText());
                        gpx = null;
                        tcx = null;
                    } catch (JAXBException ex) {
                        logger.log(Level.SEVERE, "Could not write result to " + outputFilename.getText());
                        logger.log(Level.WARNING, ex.getMessage());
                    }

                } else {
                    logger.log(Level.INFO, "Open command cancelled by user.");
                }
            } else {
                textArea.append(newline + "GPX or TCX not correctly selected");
                if (gpx == null) {
                    textArea.append(newline + "Please choose GPX file");
                } else if (tcx == null) {
                    textArea.append(newline + "Please choose TCX file");
                }
                logger.log(Level.INFO, "GPX or TCX not correctly selected");
            }
        }
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("MainGui");
        frame.setContentPane(new MainGui().panel1);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
