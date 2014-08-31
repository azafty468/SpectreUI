package spectre.view;

import spectre.exceptions.SpectreViewException;
import spectre.shared.GameObject;
import spectre.shared.Message;
import spectre.shared.PrintList;
import spectre.view.commands.UICommand;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by andrewzafft on 6/3/14.
 */
public class ApplicationView extends JFrame implements WindowListener {
    private static ApplicationView thisApplication;
    private ConcurrentLinkedQueue<UICommand> requestConduit;
    private BasicOpenGLCanvas myGraphicCanvas;
    private BasicOpenGLCanvasController myGraphicsCanvasController;
    private CommandOutJPanel commandOutArea;
    //private VariableDisplayPanel myVariable;

    private ApplicationView() {
    }

    public static ApplicationView getInstance() {
        if (thisApplication == null)
            thisApplication = new ApplicationView();

        return thisApplication;
    }

    public void sendCommand(UICommand command) {
        requestConduit.add(command);
    }

    public boolean initialize(ConcurrentLinkedQueue<UICommand> requestConduit) {
        this.requestConduit = requestConduit;

        addWindowListener(this);
        setSize(1200, 900);
        setLayout(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // TODO do I really want to do this?  Or send a command?

        GLProfile.initSingleton();
        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = BasicOpenGLCanvas.createGLCapabilities(profile);
        myGraphicCanvas = new BasicOpenGLCanvas(0, 0, 1186, 700, capabilities);
        myGraphicsCanvasController = new BasicOpenGLCanvasController(myGraphicCanvas, Collections.singletonList("data/images/BaseGraphics.png"));       // TODO make this a start time parameter
        add(myGraphicCanvas);

        commandOutArea = new CommandOutJPanel(0, 705, 1178, 150);
        add(commandOutArea);

//        myVariable = new VariableDisplayPanel(805, 0, 370, 700);
//        add(myVariable);
        setVisible(true);

        return true;
    }

    public void displayMessage(Message newMessage){
        if (newMessage.displayInCommandOut()) {
            commandOutArea.displayMessage(newMessage.myMessage);
        }
    }

    public void setCurrentOpenGLPrintList(PrintList newPrintList, GameObject extendedDetails) {
        myGraphicsCanvasController.setPrintList(newPrintList);
        myGraphicsCanvasController.setExtendedDetails(extendedDetails);
    }

    public void drawOpenGLScene() throws SpectreViewException {
        myGraphicsCanvasController.renderScene();
        //throw new SpectreViewException("Done processing!");
    }

    @Override
    public void windowClosing(WindowEvent arg0) {
//        ApplicationController myController = ApplicationController.getInstance();
//        if (myController != null)
//            myController.stop();
		/*
		canvas.stop(); // first stop the drawing and updating
		frame.setVisible(false); // hide the window quickly
		frame.dispose(); // release all system resources
		*/
        System.exit(0); // finally exit.
    }

    // TODO send these interactions to the Controller
    public void windowActivated(WindowEvent arg0) { ; }
    public void windowClosed(WindowEvent arg0) { ; }
    public void windowDeactivated(WindowEvent arg0) {;}
    public void windowDeiconified(WindowEvent arg0) {;}
    public void windowIconified(WindowEvent arg0) {;}
    public void windowOpened(WindowEvent arg0) {;}
}
