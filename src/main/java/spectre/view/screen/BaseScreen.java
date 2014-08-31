package spectre.view.screen;

import com.jogamp.opengl.util.awt.TextRenderer;
import spectre.shared.GameObject;
import spectre.shared.Message;
import spectre.view.BasicOpenGLCanvasController;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew
 * Date: 8/31/14
 * Time: 1:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class BaseScreen {
    private BasicOpenGLCanvasController myController;
    TextRenderer textRendererLarge;
    TextRenderer textRendererSmall;

    public BaseScreen(BasicOpenGLCanvasController myController, GLAutoDrawable drawable) {
        this.myController = myController;

        textRendererLarge = new TextRenderer(new Font("SansSerif", Font.BOLD, 36));
        textRendererSmall = new TextRenderer(new Font("SansSerif", Font.BOLD, 12));
    }

    public void drawScreen(GL2 gl, GLAutoDrawable glAutoDrawable) {
        if (myController == null)
            return;

        textRendererLarge.beginRendering(glAutoDrawable.getWidth(), glAutoDrawable.getHeight());
        textRendererLarge.draw("Game Information", 820, 578);
        textRendererLarge.endRendering();

        if (myController.extendedDetails == null)
            return;

        GameObject go = myController.extendedDetails;

        // TODO build structures on top of this to make elements easier to work with, including action buttons that generate a UICommand object
        // TODO build a controller for every screen
        textRendererSmall.beginRendering(glAutoDrawable.getWidth(), glAutoDrawable.getHeight());
        textRendererSmall.draw("Name: " + go.name, 820, 558);
        textRendererSmall.draw("Id: " + go.uniqueId, 820, 548);
        textRendererSmall.draw("Location: " + go.myLocation.toString(), 820, 538);
        textRendererSmall.endRendering();
    }
}
