package spectre.ui.view;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import spectre.shared.GameObject;
import spectre.shared.Message;
import spectre.shared.Point;
import spectre.shared.PrintList;
import spectre.ui.view.commands.UICommandDisplayDetails;

import javax.media.opengl.GLProfile;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by andrewzafft on 6/17/14.
 */
public class BasicOpenGLCanvasController {
    private final BasicOpenGLCanvas myCanvas;
    private PrintList currentPrintList = null;
    private ArrayList<Point> queuedMouseClicks;
    public HashMap<String, Texture> mapTextures;
    public GameObject extendedDetails;

    public BasicOpenGLCanvasController(BasicOpenGLCanvas myCanvas, List<String> textureFiles) {
        this.myCanvas = myCanvas;
        myCanvas.setController(this);
        queuedMouseClicks = new ArrayList<>();
        mapTextures = new HashMap<String, Texture>();

        for (String texture : textureFiles)
            mapTextures.put(texture, null);

        extendedDetails = null;
    }

    public void setExtendedDetails(GameObject go) {
        this.extendedDetails = go;
    }


    public void setPrintList(PrintList newPrintList) {
        currentPrintList = newPrintList;
    }

    public PrintList getCurrentPrintList() {
        return currentPrintList;
    }

    public void renderScene() {
        myCanvas.display();
    }

    public void mouseClicked(MouseEvent arg0) {
        queuedMouseClicks.add(new Point(arg0.getX(), arg0.getY(), 0));
        myCanvas.myOperation = BasicOpenGLCanvas.OpenGLOperation.OGLO_SELECT;
    }

    public Point getNextMouseClick() {
        if (queuedMouseClicks.size() == 0)
            return null;
        return queuedMouseClicks.remove(0);
    }

    private static Texture loadTextureFromFile(String fileName, String fileType, GLProfile profile) {
        try {
            InputStream stream = new FileInputStream(fileName);
            TextureData data = TextureIO.newTextureData(profile, stream, false, fileType);
            return TextureIO.newTexture(data);
        }
        catch (Exception e) {
            ApplicationView.getInstance().displayMessage(new Message("Error loading graphic file: " + e.getMessage(), Message.MessageType.COMMANDOUT));
            e.printStackTrace();
            return null;
        }
    }

    public boolean convertTextureGraphics(GLProfile profile) {
        for (String textureName : mapTextures.keySet()) {
            if (mapTextures.get(textureName) == null) {
                ApplicationView.getInstance().displayMessage(new Message("Converting Graphic  File '" + textureName + "' to OpenGL standards", Message.MessageType.COMMANDOUT));
                Texture texture = loadTextureFromFile(textureName, "png", profile);
                if (texture == null)
                    return false;
                mapTextures.put(textureName, texture);
            }
        }

        return true;
    }

    public void processMouseClickHits(int hits, IntBuffer buffer) {
        int offset = 0;
        int names;
        float z1, z2;
        for (int i=0;i<hits;i++)
        {
            names = buffer.get(offset); offset++;
            z1 = (float) (buffer.get(offset)& 0xffffffffL) / 0x7fffffff; offset++;
            z2 = (float) (buffer.get(offset)& 0xffffffffL) / 0x7fffffff; offset++;

            for (int j=0;j<names;j++)
            {
                if (currentPrintList != null) {
                    long uniqueId = currentPrintList.findObjectIdByLocalName(buffer.get(offset));
                    if (uniqueId != -1)
                        ApplicationView.getInstance().sendCommand(new UICommandDisplayDetails(uniqueId));
                }
                offset++;
            }
        }
    }

}
