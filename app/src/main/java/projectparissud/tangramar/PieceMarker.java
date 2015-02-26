package projectparissud.tangramar;

import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.distance.EuclideanDistance;

import javax.microedition.khronos.opengles.GL10;

import edu.dhbw.andar.ARObject;
import edu.dhbw.andar.ARToolkit;

/**
 * Created by danilojun on 26/02/15.
 */
public class PieceMarker extends ARObject {
    // we need to find good values for this two constants...
    static final double MIN_DIST = 100.0;           // if a distance is smaller than this value, we consider that the marker has moved
    static final double POSE_DIST = 50.0;          // used to verify if the current pose is the same as the correct pose

    private boolean isCorrectPose;                  // variable to say if the current pose is the desired pose

    private PieceMarker reference;                  // reference marker. The pose of all the other markers will be mesured in the reference Coordinate System
    private double[] oldPoseInReferenceCS;          // previous value of the marker's pose in the reference Coordinate System
    private double[] correctPoseInReferenceCS;      // value of the desired marker's pose in the reference Coordinates System. This will be defined according to the figure we want to make in the Tangram

    // Constructors
    public PieceMarker(String name, String patternName, double markerWidth, double[] markerCenter) {
        super(name, patternName, markerWidth, markerCenter);
        this.oldPoseInReferenceCS = new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        this.correctPoseInReferenceCS = new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        this.reference = this;          // if we don't specify a reference marker, it adopts itself as reference
        this.isCorrectPose = false;     // the marker does not start in the correct pose...
    }
    public PieceMarker(String name, String patternName, double markerWidth, double[] markerCenter,
                       PieceMarker reference) {
        this(name, patternName, markerWidth, markerCenter);
        this.reference = reference;
    }

    public void setReference(PieceMarker reference) {
        this.reference = reference;
    }

    // get the pose of the marker in the Reference Coordinate System
    private double[] getPoseInReferenceCS() {
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

//        System.out.println("Hiro Marker !");

        if (this != this.reference) {
            double[] poseInReferenceCS = this.getPoseInReferenceCS();
            DistanceMeasure distance = new EuclideanDistance();
            // only perform tasks if the current pose has sufficiently changed, compared to the previous pose
            if (distance.compute(poseInReferenceCS, this.oldPoseInReferenceCS) > MIN_DIST) {
                System.out.println("Pose changed !");
                this.oldPoseInReferenceCS = poseInReferenceCS; // update old pose
                this.updateCorrectPose(); // verify if the current pose is the correct pose
                if (this.isCorrectPose) {
                    System.out.println("Correct pose !");
                }
            }
        }
    }
    @Override
    public void init(GL10 gl) {
    }
}
