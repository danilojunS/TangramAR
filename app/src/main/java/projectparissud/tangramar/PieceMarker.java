package projectparissud.tangramar;

import android.opengl.GLUtils;

import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.distance.EuclideanDistance;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import edu.dhbw.andar.ARObject;
import edu.dhbw.andar.ARToolkit;
import edu.dhbw.andar.pub.SimpleBox;
import edu.dhbw.andar.util.GraphicsUtil;

/**
 * Created by danilojun on 26/02/15.
 */
public class PieceMarker extends ARObject {
    // we need to find good values for this two constants...
    static final double MIN_DIST = 100.0;           // if a distance is smaller than this value, we consider that the marker has moved
    static final double POSE_DIST = 50.0;          // used to verify if the current pose is the same as the correct pose

    private boolean isCorrectPose;                  // variable to say if the current pose is the desired pose

    private boolean display;                        // do we display the drawing on this marker

    private PieceMarker reference;                  // reference marker. The pose of all the other markers will be mesured in the reference Coordinate System
    private double[] oldPoseInReferenceCS;          // previous value of the marker's pose in the reference Coordinate System
    private double[] correctPoseInReferenceCS;      // value of the desired marker's pose in the reference Coordinates System. This will be defined according to the figure we want to make in the Tangram

    // Constructors
    public PieceMarker(String name, String patternName, double markerWidth, double[] markerCenter, boolean _display, Model _model) {
        super(name, patternName, markerWidth, markerCenter);
        this.oldPoseInReferenceCS = new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        this.correctPoseInReferenceCS = new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        this.reference = this;          // if we don't specify a reference marker, it adopts itself as reference
        this.isCorrectPose = false;     // the marker does not start in the correct pose...
        this.display = _display;
        this.model = _model;
        model.finalize();
        //separate texture from non textured groups for performance reasons
        Vector<Group> groups = model.getGroups();
        Vector<Group> texturedGroups = new Vector<Group>();
        Vector<Group> nonTexturedGroups = new Vector<Group>();
        for (Iterator<Group> iterator = groups.iterator(); iterator.hasNext();) {
            Group currGroup = iterator.next();
            if(currGroup.isTextured()) {
                texturedGroups.add(currGroup);
            } else {
                nonTexturedGroups.add(currGroup);
            }
        }
        this.texturedGroups = texturedGroups.toArray(new Group[texturedGroups.size()]);
        this.nonTexturedGroups = nonTexturedGroups.toArray(new Group[nonTexturedGroups.size()]);
    }
    public PieceMarker(String name, String patternName, double markerWidth, double[] markerCenter, boolean display, Model model,
                       PieceMarker reference) {
        this(name, patternName, markerWidth, markerCenter, display, model);
        this.reference = reference;
    }

    public void setReference(PieceMarker reference) {
        this.reference = reference;
    }

    // get the pose of the marker in the Reference Coordinate System
    public double[] getPoseInReferenceCS() {
        // calculate the pose only if the reference is visible (it is in the scene)
        if (this.reference.isVisible()) {
            // I think this is the right way to do it, I saw it in the code of the Pong game
            double[] transmat = this.getTransMatrix();
            double[] refMat = new double[12];
            ARToolkit.arUtilMatInv(reference.getTransMatrix(), refMat);

            double[] poseInReferenceCS = new double[12];

            ARToolkit.arUtilMatMul(refMat, transmat, poseInReferenceCS);

            return poseInReferenceCS;
        }
        // else: the current pose does not change
        return this.oldPoseInReferenceCS;
    }

    public void setCorrectPoseInReferenceCS() {
        this.correctPoseInReferenceCS = this.getPoseInReferenceCS();
    }

    public void setCorrectPoseInReferenceCS(double[] coordinates){
        this.correctPoseInReferenceCS = coordinates;
    }

    private void updateCorrectPose() {
        // Verify if the current pose (in oldPoseInReferenceCS) is the correct pose
        // Trying to compare each coordinate of the transformation matrix of the correct pose and the current pose
        // In other words, it verifies if the array oldPoseInReferenceCS is sufficiently close to correctPoseInReferenceCS
        // PS: I don't know which value to put in the POSE_DIST in a way to optmize this :(
        for (int i = 0; i < this.oldPoseInReferenceCS.length; i++) {
            if (Math.abs(this.oldPoseInReferenceCS[i] - this.correctPoseInReferenceCS[i]) > POSE_DIST) {
                // if two corresponding coordinates are too different, the current pose is not the correct pose
                this.isCorrectPose = false;
                return;
            }
        }
        this.isCorrectPose = true;
    }

    /**
     * Everything drawn here will be drawn directly onto the marker,
     * as the corresponding translation matrix will already be applied.
     */
    @Override
    public final void draw(GL10 gl) {
        if(this.display) {
            super.draw(gl);
            //gl = (GL10) GLDebugHelper.wrap(gl, GLDebugHelper.CONFIG_CHECK_GL_ERROR, log);
            //do positioning:
            gl.glScalef(model.scale, model.scale, model.scale);
            gl.glTranslatef(model.xpos, model.ypos, model.zpos);
            gl.glRotatef(model.xrot, 1, 0, 0);
            gl.glRotatef(model.yrot, 0, 1, 0);
            gl.glRotatef(model.zrot, 0, 0, 1);

            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);

            //first draw non textured groups
            gl.glDisable(GL10.GL_TEXTURE_2D);
            int cnt = nonTexturedGroups.length;
            for (int i = 0; i < cnt; i++) {
                Group group = nonTexturedGroups[i];
                Material mat = group.getMaterial();
                if(mat != null) {
                    gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, mat.specularlight);
                    gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, mat.ambientlight);
                    gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, mat.diffuselight);
                    gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, mat.shininess);
                }
                gl.glVertexPointer(3,GL10.GL_FLOAT, 0, group.vertices);
                gl.glNormalPointer(GL10.GL_FLOAT,0, group.normals);
                gl.glDrawArrays(GL10.GL_TRIANGLES, 0, group.vertexCount);
            }

            //now we can continue with textured ones
            gl.glEnable(GL10.GL_TEXTURE_2D);
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

            cnt = texturedGroups.length;
            for (int i = 0; i < cnt; i++) {
                Group group = texturedGroups[i];
                Material mat = group.getMaterial();
                if(mat != null) {
                    gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, mat.specularlight);
                    gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, mat.ambientlight);
                    gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, mat.diffuselight);
                    gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, mat.shininess);
                    if(mat.hasTexture()) {
                        gl.glTexCoordPointer(2,GL10.GL_FLOAT, 0, group.texcoords);
                        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs.get(mat).intValue());
                    }
                }
                gl.glVertexPointer(3,GL10.GL_FLOAT, 0, group.vertices);
                gl.glNormalPointer(GL10.GL_FLOAT,0, group.normals);
                gl.glDrawArrays(GL10.GL_TRIANGLES, 0, group.vertexCount);
            }

            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
            gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        }
    }

    private Model model;
    private Group[] texturedGroups;
    private Group[] nonTexturedGroups;
    private HashMap<Material, Integer> textureIDs = new HashMap<Material, Integer>();

    @Override
    public void init(GL10 gl){
        int[]  tmpTextureID = new int[1];
        //load textures of every material(that has a texture):
        Iterator<Material> materialI = model.getMaterials().values().iterator();
        while (materialI.hasNext()) {
            Material material = (Material) materialI.next();
            if(material.hasTexture()) {
                //load texture
                gl.glGenTextures(1, tmpTextureID, 0);
                gl.glBindTexture(GL10.GL_TEXTURE_2D, tmpTextureID[0]);
                textureIDs.put(material, tmpTextureID[0]);
                GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, material.getTexture(), 0);
                material.getTexture().recycle();
                gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
                gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
            }
        }

        //transfer vertices to video memory
    }
}
