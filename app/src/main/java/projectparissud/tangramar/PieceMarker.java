package projectparissud.tangramar;

import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.distance.EuclideanDistance;

import java.nio.FloatBuffer;

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
    static final double POSE_TOLERANCE = 50.0;

    private boolean isCorrectPose;                  // variable to say if the current pose is the desired pose

    private boolean display;                        // do we display the drawing on this marker

    private PieceMarker reference;                  // reference marker. The pose of all the other markers will be mesured in the reference Coordinate System
    private double[] oldPoseInReferenceCS;          // previous value of the marker's pose in the reference Coordinate System
    private double[] correctPoseInReferenceCS;      // value of the desired marker's pose in the reference Coordinates System. This will be defined according to the figure we want to make in the Tangram

    // Constructors
    public PieceMarker(String name, String patternName, double markerWidth, double[] markerCenter, boolean display) {
        super(name, patternName, markerWidth, markerCenter);
        this.oldPoseInReferenceCS = new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        this.correctPoseInReferenceCS = new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        this.reference = this;          // if we don't specify a reference marker, it adopts itself as reference
        this.isCorrectPose = false;     // the marker does not start in the correct pose...
        this.display = false;
    }
    public PieceMarker(String name, String patternName, double markerWidth, double[] markerCenter, boolean display,
                       PieceMarker reference) {
        this(name, patternName, markerWidth, markerCenter, display);
        this.reference = reference;
    }

    public void setReference(PieceMarker reference) {
        this.reference = reference;
        this.isCorrectPose = true;
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
//        for (int i = 0; i < this.oldPoseInReferenceCS.length; i++) {
//            if (Math.abs(this.oldPoseInReferenceCS[i] - this.correctPoseInReferenceCS[i]) > POSE_DIST) {
//                // if two corresponding coordinates are too different, the current pose is not the correct pose
//                this.isCorrectPose = false;
//                return;
//            }
//        }
//        this.isCorrectPose = true;

        DistanceMeasure distance = new EuclideanDistance();
        if (distance.compute(this.oldPoseInReferenceCS, this.correctPoseInReferenceCS) < POSE_TOLERANCE) {
            this.isCorrectPose = true;
        } else {
            this.isCorrectPose = false;
        }

    }

    public boolean isCorrectPose() {
        return this.isCorrectPose;
    }

    /**
     * Everything drawn here will be drawn directly onto the marker,
     * as the corresponding translation matrix will already be applied.
     */
    @Override
    public final void draw(GL10 gl) {

//        System.out.println("Hiro Marker !");
        if(this.display) {
            if (this != this.reference) {
//                double[] poseInReferenceCS = this.getPoseInReferenceCS();
//                DistanceMeasure distance = new EuclideanDistance();
//                // only perform tasks if the current pose has sufficiently changed, compared to the previous pose
//                if (distance.compute(poseInReferenceCS, this.oldPoseInReferenceCS) > MIN_DIST) {
////                    System.out.println("Pose changed !");
//                    this.oldPoseInReferenceCS = poseInReferenceCS; // update old pose
//                    this.updateCorrectPose(); // verify if the current pose is the correct pose
////                    if (this.isCorrectPose) {
////                        System.out.println("Correct pose !");
////                    }
//                }
                this.oldPoseInReferenceCS = this.getPoseInReferenceCS();
                this.updateCorrectPose();
            }

            /**
             * Everything drawn here will be drawn directly onto the marker,
             * as the corresponding translation matrix will already be applied.
             super.draw(gl);

             SimpleBox box = new SimpleBox();
             FloatBuffer mat_flash;
             FloatBuffer mat_ambient;
             FloatBuffer mat_flash_shiny;
             FloatBuffer mat_diffuse;
             float   mat_ambientf[]     = {0f, 1.0f, 0f, 1.0f};
             float   mat_flashf[]       = {0f, 1.0f, 0f, 1.0f};
             float   mat_diffusef[]       = {0f, 1.0f, 0f, 1.0f};
             float   mat_flash_shinyf[] = {50.0f};

             mat_ambient = GraphicsUtil.makeFloatBuffer(mat_ambientf);
             mat_flash = GraphicsUtil.makeFloatBuffer(mat_flashf);
             mat_flash_shiny = GraphicsUtil.makeFloatBuffer(mat_flash_shinyf);
             mat_diffuse = GraphicsUtil.makeFloatBuffer(mat_diffusef);

             gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR,mat_flash);
             gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, mat_flash_shiny);
             gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, mat_diffuse);
             gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, mat_ambient);

             //draw cube
             gl.glColor4f(0, 1.0f, 0, 1.0f);
             gl.glTranslatef( 0.0f, 0.0f, 12.5f );

             //draw the box
             box.draw(gl);
             */
        }
    }
    @Override
    public void init(GL10 gl) {
    }
}
