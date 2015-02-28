package projectparissud.tangramar;

import java.util.ArrayList;

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

    // Constructor
    public Tangram(Figure _target){
        target = _target;

        // Create the pieces
        pieces = new ArrayList<TangramPiece>();
        for(int i = 0; i < NUMBER_OF_PIECES; i++){

            pieces.add(new TangramPiece(intToPieceName(i), target.piecesCoordinates.get(i), i == target.centerPiece));
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
                return "Square";
            case 6:
                return "Parallelogram";
            default:
                return "Unknown";
        }
    }
}
