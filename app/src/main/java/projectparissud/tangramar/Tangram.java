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
    // A figure would be a list of 7 coordinates (x,y), one for each piece

    // Constructor
    public Tangram(Figure _target){
        target = _target;

        // Create the pieces
        pieces = new ArrayList<TangramPiece>();
        pieces.add(new TangramPiece("Big Triangle 1", target.piecesCoordinates.get(0)));
        pieces.add(new TangramPiece("Big Triangle 2", target.piecesCoordinates.get(1)));
        pieces.add(new TangramPiece("Medium Triangle", target.piecesCoordinates.get(2)));
        pieces.add(new TangramPiece("Small Triangle 1", target.piecesCoordinates.get(3)));
        pieces.add(new TangramPiece("Small Triangle 2", target.piecesCoordinates.get(4)));
        pieces.add(new TangramPiece("Square", target.piecesCoordinates.get(5)));
        pieces.add(new TangramPiece("Parallelogram", target.piecesCoordinates.get(6)));
    }

}
