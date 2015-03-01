package projectparissud.tangramar;

import android.support.v4.view.MotionEventCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import edu.dhbw.andar.ARToolkit;
import edu.dhbw.andar.AndARActivity;
import edu.dhbw.andar.exceptions.AndARException;


public class MainActivity extends AndARActivity {

    private static final String DEBUG_TAG = "Debug";
    public Tangram tangram;

    ARToolkit artoolkit;

    PieceMarker hiroMarker;
    PieceMarker refMarker;



    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Init the tangram object and the pieces
        tangram = new Tangram(loadObject("chair"));
        // TODO : init tangram figure model with loadObject()

        try {
            artoolkit = super.getArtoolkit();
//            super.setNonARRenderer(new LightingRenderer());//or might be omited

            // reference marker
            //refMarker = new PieceMarker("reference", "android.patt", 76.0, new double[]{0,0});
            //artoolkit.registerARObject(refMarker);
            // another marker
            //hiroMarker = new PieceMarker("test", "patt.hiro", 80.0, new double[]{0,0}, refMarker);
            //artoolkit.registerARObject(hiroMarker);

            //artoolkit.registerARObject(loadObject("chair"));
            tangram.registerPieces(artoolkit);


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

    private boolean firstTouch = true;
    @Override
    public boolean onTouchEvent(MotionEvent event){

        int action = MotionEventCompat.getActionMasked(event);

        switch(action) {
//            case (MotionEvent.ACTION_DOWN) :
//                break;
//            case (MotionEvent.ACTION_MOVE) :
//                break;
            case (MotionEvent.ACTION_UP) :
                // when the user touches the screen, we take a picture of the marker configuration
                // that is, we save the current pose of the markers in the reference Coordinate System
//                tangram.printPiecesPositions();
                if (this.firstTouch) {
                    tangram.setCorrectFigure();
                    this.firstTouch = false;

                } else {
                    tangram.updatePoses();
                    boolean isCorrect = tangram.isCorrectFigure();
                    if (isCorrect) {
                        System.out.println("Position ok !");
                        tangram.pieces.get(6).marker.finalizeModel();
                        tangram.pieces.get(6).marker.display = true;
                    }
                }
//                this.firstTouch = this.firstTouch ? false : true;
//                break;
//            case (MotionEvent.ACTION_CANCEL) :
//                break;
//            case (MotionEvent.ACTION_OUTSIDE) :
//                break;


            default :
                return super.onTouchEvent(event);
        }
    }

    public Model loadObject(String name){
        String modelFileName = name + ".obj";
        BaseFileUtil fileUtil= null;
        File modelFile=null;
        fileUtil = new AssetsFileUtil(getResources().getAssets());
        fileUtil.setBaseFolder("models/");

        //read the model file:
        ObjParser parser = new ObjParser(fileUtil);
        if(fileUtil != null) {
            BufferedReader fileReader = fileUtil.getReaderFromName(modelFileName);
            if(fileReader != null) {
                Model model;
                try {
                    model = parser.parse("Model", fileReader);
                    return model;
                } catch (IOException e) {
                    //e.printStackTrace();
                } catch (ParseException e){
                    //
                }
            }
        }
        return null;
    }
}
