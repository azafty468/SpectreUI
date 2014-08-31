package spectre.ui;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spectre.ui.exceptions.SpectreViewException;
import spectre.shared.GameObject;
import spectre.shared.Message;
import spectre.ui.view.ApplicationView;
import spectre.ui.view.commands.UICommand;
import spectre.ui.view.commands.UICommandDisplayPrintList;

import javax.swing.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by andrewzafft on 6/14/14.
 */
public class MainViewThread extends Thread {
    protected static final Logger log = LoggerFactory.getLogger(MainViewThread.class);

    private final ConcurrentLinkedQueue<UICommand> outgoingCommands;
    private final ConcurrentLinkedQueue<UICommand> incomingCommands;
    private final long minTimeBetweenFrames;
    public volatile boolean isRunning = false;

    public MainViewThread(ConcurrentLinkedQueue<UICommand> outgoingCommands, ConcurrentLinkedQueue<UICommand> incomingCommands, int maxFPS) {
        this.outgoingCommands = outgoingCommands;
        this.incomingCommands = incomingCommands;
        minTimeBetweenFrames = (1000l) / maxFPS;
    }

    public synchronized boolean getIsRunning() { return isRunning; }

    protected synchronized void setIsRunning(boolean isProcessingDocument) {
        this.isRunning = isProcessingDocument;
    }

    @Override
    public void run() {
        setIsRunning(true);

        ApplicationView myView = ApplicationView.getInstance();
        if(!myView.initialize(outgoingCommands)) {
            JOptionPane.showMessageDialog(null, "Error while initialization the base Application View.  Exiting.");
            setIsRunning(false);
        }

        myView.displayMessage(new Message("Starting application view", Message.MessageType.COMMANDOUT));
        DateTime lastDraw = new DateTime(0);
        while (isRunning) {
            if ((new DateTime().getMillis() - lastDraw.getMillis()) > minTimeBetweenFrames) {
                // trigger a redraw of the screen

                while (incomingCommands.size() > 0) {
                    UICommand command = incomingCommands.remove();

                    if (command instanceof UICommandDisplayPrintList) {
                        UICommandDisplayPrintList uidpl = (UICommandDisplayPrintList) command;
                        myView.setCurrentOpenGLPrintList(uidpl.printList, uidpl.extendedDetails);

                    } else {
                        myView.displayMessage(new Message("Unrecognized UI command: " + command.getClass(), Message.MessageType.COMMANDOUT));
                    }
                }

                try {
                    myView.drawOpenGLScene();
                } catch (SpectreViewException e) {
                    log.error("Caught exception while drawing: " + e.toString());
                    setIsRunning(false);
                }

                lastDraw = new DateTime();
            }

            try {
                if ((minTimeBetweenFrames - ((new DateTime().getMillis() - lastDraw.getMillis()))) > 0)
                    Thread.sleep(minTimeBetweenFrames - ((new DateTime().getMillis() - lastDraw.getMillis())));
            } catch (InterruptedException e) {
                log.info("Sleep interrupted: " + e.toString());
            }
        }
    }

}
