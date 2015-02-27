package projectparissud.tangramar;

import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import edu.dhbw.andar.ARToolkit;
import edu.dhbw.andar.AndARActivity;
import edu.dhbw.andar.exceptions.AndARException;


public class MainActivity extends AndARActivity {

    private static final String DEBUG_TAG = "Debug";

    ARToolkit artoolkit;

    PieceMarker hiroMarker;
    PieceMarker refMarker;

    // Init the tangram object and the pieces
    Tangram tangram = new Tangram();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        try {
            artoolkit = super.getArtoolkit();

            // reference marker
            refMarker = new PieceMarker("reference", "android.patt", 76.0, new double[]{0,0});
            artoolkit.registerARObject(refMarker);
            // another marker
            hiroMarker = new PieceMarker("test", "patt.hiro", 80.0, new double[]{0,0}, refMarker);
            artoolkit.registerARObject(hiroMarker);


        } catch (AndARException ex){
            //handle the exception, that means: show the user what happened
            System.out.println("");
        }
        startPreview();
    }

    /**
     * Inform the user about exceptions that occurred in background threads.
     * This exception is rather severe and can not be recovered from.
     * TODO Inform the user and shut down the application.
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.e("AndAR EXCEPTION", ex.getMessage());
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        int action = MotionEventCompat.getActionMasked(event);

        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
            case (MotionEvent.ACTION_MOVE) :
            case (MotionEvent.ACTION_UP) :
            case (MotionEvent.ACTION_CANCEL) :
            case (MotionEvent.ACTION_OUTSIDE) :
                // when the user touches the screen, we take a picture of the marker configuration
                // that is, we save the current pose of the markers in the reference Coordinate System
                Log.d(DEBUG_TAG, "Taking a picture of the current marker configuration.");
                this.hiroMarker.setCorrectPoseInReferenceCS();
            default :
                return super.onTouchEvent(event);
        }
    }
}
