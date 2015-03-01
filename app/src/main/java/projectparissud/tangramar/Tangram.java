package projectparissud.tangramar;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import edu.dhbw.andar.ARToolkit;
import edu.dhbw.andar.exceptions.AndARException;

/**
 * Created by altay on 27/02/15.
 */
public class Tangram {
    // Pieces of the Tangram
    public ArrayList<TangramPiece> pieces;
    // We should have a list of figures
    public Figure target;

    public static final int NUMBER_OF_PIECES = 7;
    // A figure would be a list of 7 coordinates (x,y), one for each piece

    private PieceMarker reference;

    // Constructor
    public Tangram(){
        target = null;

        // Create the pieces
        pieces = new ArrayList<TangramPiece>();
        for(int i = 0; i < NUMBER_OF_PIECES; i++){

            pieces.add(new TangramPiece(intToPieceName(i), null, false, intToPatternName(i)));
        }

        reference = pieces.get(6).marker;

        for(TangramPiece piece : pieces){
            piece.marker.setReference(reference);
        }
    }

    public Tangram(Figure _target){
        target = _target;

        // Create the pieces
        pieces = new ArrayList<TangramPiece>();
        for(int i = 0; i < NUMBER_OF_PIECES; i++){

            pieces.add(new TangramPiece(intToPieceName(i), target.piecesCoordinates.get(i), i == target.centerPiece, intToPatternName(i)));
        }
    }

    public boolean match(){
        ArrayList<double[]> coordinates = new ArrayList<double[]>();
        for(TangramPiece piece : pieces){
            coordinates.add(piece.marker.getPoseInReferenceCS());
        }
        return target.match(coordinates);
    }

    public static String intToPieceName(int id){
        switch(id){
            case 0:
                return "BigTriangle1";
            case 1:
                return "BigTriangle2";
            case 2:
                return "MediumTriangle";
            case 3:
                return "SmallTriangle1";
            case 4:
                return "SmallTriangle2";
            case 5:
                return "Parallelogram";
            case 6:
                return "Square";
            default:
                return "Unknown";
        }
    }

    public static String intToPatternName(int id){
        switch(id){
            case 0:
                return "android.patt";
            case 1:
                return "patt.hiro";
            case 2:
                return "bat.patt";
            case 3:
                return "barcode.patt";
            case 4:
                return "star.patt";
            case 5:
                return "lightning.patt";
            case 6:
                return "center.patt";
            default:
                return "Unknown";
        }
    }

    public void printPiecesPositions() {
        for(TangramPiece piece : pieces){
            if(piece.marker.isVisible()) {
                double[] coordinates = piece.marker.getPoseInReferenceCS();
                String string = piece.name + " : ";
                string += Arrays.toString(coordinates);
                Log.e("PIECE POSITION", string);
            }
        }
    }

    public void registerPieces(ARToolkit artoolkit) throws AndARException {
        for(TangramPiece piece : pieces){
            artoolkit.registerARObject(piece.marker);
        }
    }

    public boolean isCorrectFigure() {
        boolean isCorrectFigure = true;

        for(TangramPiece piece : pieces) {
            isCorrectFigure = isCorrectFigure && piece.marker.isCorrectPose();
        }
        return isCorrectFigure;
    }
}
