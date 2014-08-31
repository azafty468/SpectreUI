package spectre.view;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.awt.TextRenderer;
import spectre.shared.*;
import spectre.shared.Point;
import spectre.view.screen.BaseScreen;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.nio.IntBuffer;
import java.util.ArrayList;

/**
 * Created by andrewzafft on 6/17/14.
 */
public class BasicOpenGLCanvas extends GLCanvas implements GLEventListener, MouseListener {
    private int height;
    private int width;
    private GLU glu;
    public static enum OpenGLOperation { OGLO_UNKNOWN, OGLO_UPDATE, OGLO_SELECT };
    public OpenGLOperation myOperation;
    private BasicOpenGLCanvasController myController;
    private String lastDrawnTexture;
    TriangleMesh cornerScreenGraphic;
    TriangleMesh midScreenGraphic;
    BaseScreen myBaseScreen;

    public BasicOpenGLCanvas(int newLeft, int newTop, int width, int height, GLCapabilities capabilities) {
        super(capabilities);

        this.width = width;
        this.height = height;

        setSize(width, height);
        addGLEventListener(this);
        addMouseListener(this);
        //addMouseMotionListener(this);  //was previously commented out
        myOperation = OpenGLOperation.OGLO_UPDATE;
        lastDrawnTexture = null;

        myBaseScreen = null;
        setScreenBorderObjects();
    }

    private void setScreenBorderObjects() {
        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(-.5f, -.5f, 0f));
        points.add(new Point(+.5f, -.5f, 0f));
        points.add(new Point(+.5f, +.5f, 0f));
        points.add(new Point(-.5f, +.5f, 0f));

        ArrayList<TexturePoint> textures = new ArrayList<>();
        textures.add(new TexturePoint(0.49609375d, 0.96875d));
        textures.add(new TexturePoint(0.5546875d, 0.96875d));
        textures.add(new TexturePoint(0.5546875d, 0.998046875d));
        textures.add(new TexturePoint(0.49609375d, 0.998046875d));
        cornerScreenGraphic = new TriangleMesh(points, null, textures);

        textures = new ArrayList<>();
        textures.add(new TexturePoint(0.56640625d,  0.96875d));
        textures.add(new TexturePoint(0.625d,  0.96875d));
        textures.add(new TexturePoint(0.625d, 0.998046875d));
        textures.add(new TexturePoint(0.56640625d, 0.998046875d));
        midScreenGraphic = new TriangleMesh(points, null, textures);
    }

    public void setController(BasicOpenGLCanvasController myController) {
        this.myController = myController;
    }

    public static GLCapabilities createGLCapabilities(GLProfile profile) {
        GLCapabilities capabilities = new GLCapabilities(profile);
        capabilities.setRedBits(8);
        capabilities.setBlueBits(8);
        capabilities.setGreenBits(8);
        capabilities.setAlphaBits(8);
        return capabilities;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        drawable.setGL(new DebugGL2(gl));

        // Global settings.
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glDepthFunc(GL2.GL_LEQUAL);
        gl.glShadeModel(GL2.GL_SMOOTH);
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
        gl.glEnable(GL2.GL_CULL_FACE);
        gl.glClearColor(0f, 0f, 0f, 1f);

        gl.glTexParameterf(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_WRAP_S, gl.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_WRAP_T, gl.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MAG_FILTER, gl.GL_NEAREST);
        gl.glTexParameterf(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MIN_FILTER, gl.GL_NEAREST);

        gl.glEnable(GL2.GL_TEXTURE_2D);

        glu = new GLU();

        if (!myController.convertTextureGraphics(getGLProfile())) {
            ApplicationView.getInstance().displayMessage(new Message("OpenGL error, must abort", Message.MessageType.COMMANDOUT));
            System.exit(0);
        }

        reshape(drawable, 0, 0, width, height);
        myBaseScreen = new BaseScreen(myController, drawable);
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {
        //TODO implement
    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();
//        GL4 gl = glAutoDrawable.getGL().getGL4();

        switch (myOperation) {
            case OGLO_UPDATE:
                drawScreen(gl);
                drawScreenBorder(gl, glAutoDrawable);
                break;

            case OGLO_SELECT: {
                for (Point currentMouseClick = myController.getNextMouseClick(); currentMouseClick != null; currentMouseClick = myController.getNextMouseClick()) {
                    int buffsize = 512;
                    int hits = 0;
                    float x = (float) currentMouseClick.x;
                    float y = (float) currentMouseClick.y;
                    int[] viewport = new int[4];

                    IntBuffer selectBuffer = Buffers.newDirectIntBuffer(buffsize);
                    gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
                    gl.glSelectBuffer(buffsize, selectBuffer);
                    gl.glRenderMode(GL2.GL_SELECT);
                    gl.glInitNames();

                    gl.glMatrixMode(GL2.GL_PROJECTION);
                    gl.glPushMatrix();
                    gl.glLoadIdentity();

                    glu.gluPickMatrix(x, (float) (viewport[3] - y), 1, 1, viewport, 0);
                    glu.gluPerspective(45, (float) getWidth() / (float) getHeight(), 1, 1000);
                    glu.gluLookAt(0, 0, 20, 0, 0, 0, 0, 1, 0);

                    gl.glMatrixMode (GL2.GL_MODELVIEW);
                    drawScreen(gl);

                    gl.glMatrixMode(GL2.GL_PROJECTION);
                    gl.glPopMatrix();
                    gl.glFlush();
                    gl.glMatrixMode (GL2.GL_MODELVIEW);

                    hits = gl.glRenderMode(GL2.GL_RENDER);

                    if(hits > 0) {                                               // If There Were More Than 0 Hits
                        myController.processMouseClickHits(hits, selectBuffer);
                    }
                    else {
                        ApplicationView.getInstance().displayMessage(new Message("No hit detected", Message.MessageType.COMMANDOUT));
                    }
                }

                myOperation = OpenGLOperation.OGLO_UPDATE;
                break;
            }
        }
    }

    /**
     * Draw the scene to the GL2 object.
     *
     * @param gl
     */
    public void drawScreen(GL2 gl) {
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        // This is a fixed movement to translate to the center of the main drawing area.  If camera height is adjusted, it needs to be after this and reset before ending this function.
        gl.glTranslatef(-5.0f, 0.0f, 0.0f);

        // camera rotation

        // how does this work when the camera moves in height?
        PrintList printList = null;
        if (myController != null)
            printList = myController.getCurrentPrintList();

        if (printList != null) {
            for (PrintListNode pnl : printList.getPrintList()) {
                if (lastDrawnTexture == null || lastDrawnTexture != pnl.baseTextureName) {
                    myController.mapTextures.get(pnl.baseTextureName).enable(gl);
                    myController.mapTextures.get(pnl.baseTextureName).bind(gl);
                    lastDrawnTexture = pnl.baseTextureName;
                }

                float x = pnl.relativeX;
                float y = pnl.relativeY;
                float scale = 1f;

                gl.glPushName(pnl.localName);
                gl.glTranslatef(-scale * x, -scale * y, 0);
                gl.glColor3f(pnl.colorRed, pnl.colorGreen, pnl.colorBlue);
                pnl.triangleMesh.drawGraphics(gl);
                gl.glTranslatef(scale * x, scale * y, 0);
                gl.glPopName();
            }

        } else {
            gl.glDisable(GL2.GL_TEXTURE_2D);
            gl.glPushName(1);
            gl.glColor3f(0.9f, 0.5f, 0.2f);
            gl.glBegin(GL.GL_TRIANGLE_FAN);
            gl.glVertex3f(-1.0f, -1, 0);
            gl.glVertex3f(+1.0f, -1, 0);
            gl.glVertex3f(0.0f, 1, 0);
            gl.glEnd();
            gl.glEnable(GL2.GL_TEXTURE_2D);
            gl.glPopName();
        }
        gl.glFlush();

        // remove camera rotation

        // move back to the center of the OpenGL screen
        gl.glTranslatef(5.0f, 0.0f, 0.0f);
    }


    public void drawScreenBorder(GL2 gl, GLAutoDrawable glAutoDrawable) {
        if (lastDrawnTexture == null || lastDrawnTexture != "data/images/BaseGraphics.png") {
            //TODO This name shouldn't be hardcoded
            myController.mapTextures.get("data/images/BaseGraphics.png").enable(gl);
            myController.mapTextures.get("data/images/BaseGraphics.png").bind(gl);
            lastDrawnTexture = "data/images/BaseGraphics.png";
        }

        gl.glTranslatef(-13.5f, 7.75f, 0.0f);

        gl.glColor3f(1.0f, 1.0f, 1.0f);
        for (int iter = 0; iter < 2; iter++) {
            cornerScreenGraphic.drawGraphics(gl);

            for (int x = 0; x < 26; x++) {
                gl.glTranslatef(1.0f, 0.0f, 0.0f);
                midScreenGraphic.drawGraphics(gl);
            }

            gl.glTranslatef(1.0f, 0.0f, 0.0f);
            gl.glRotatef(-90.0f, 0.0f, 0.0f, 1.0f);
            cornerScreenGraphic.drawGraphics(gl);

            for (int x = 0; x < 15; x++) {
                gl.glTranslatef(1.0f, 0.0f, 0.0f);
                midScreenGraphic.drawGraphics(gl);
            }

            gl.glTranslatef(1.0f, 0.0f, 0.0f);
            gl.glRotatef(-90.0f, 0.0f, 0.0f, 1.0f);
        }
        gl.glTranslatef(13.5f, -7.75f, 0.0f);

        gl.glColor3f(1.0f, 0.78515625f, 0.0546875f);
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glPointSize(4.0f);
        gl.glBegin(GL.GL_LINES);
        gl.glVertex3f(5.0f, -7.75f, 0.0f);
        gl.glVertex3f(5.0f, +7.25f, 0.0f);
        gl.glEnd();
        gl.glEnable(GL2.GL_TEXTURE_2D);

        if (myBaseScreen != null)
            myBaseScreen.drawScreen(gl, glAutoDrawable);

    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glViewport(0, 0, width, height);
        this.width = width;
        this.height = height;
        setBasePerspective(gl, 20);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    private void setBasePerspective(GL2 gl, float distance) {
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        float widthHeightRatio = (float) getWidth() / (float) getHeight();
        glu.gluPerspective(45, widthHeightRatio, 1, 1000);
        glu.gluLookAt(0, 0, distance, 0, 0, 0, 0, 1, 0);
    }

    /**
     * This receives the mouse click event.
     * @param arg0
     */
    public void mouseClicked(MouseEvent arg0) {
        myController.mouseClicked(arg0);
    }

    public void mouseEntered(MouseEvent arg0) {;}
    public void mouseExited(MouseEvent arg0) {;}
    public void mousePressed(MouseEvent arg0) { ; }
    public void mouseReleased(MouseEvent arg0) {;}
}
