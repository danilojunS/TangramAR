package projectparissud.tangramar;

import java.util.ArrayList;

/**
 * Created by altay on 27/02/15.
 */
public class Tangram {
    // Pieces of the Tangram
    public ArrayList<TangramPiece> pieces;
    // We should have a list of figures
    // A figure would be a list of 7 coordinates (x,y), one for each piece

    // Constructor
    public Tangram(){
        pieces = new ArrayList<TangramPiece>();
        pieces.add(new TangramPiece("Big Triangle 1"));
        pieces.add(new TangramPiece("Big Triangle 2"));
        pieces.add(new TangramPiece("Medium Triangle"));
        pieces.add(new TangramPiece("Small Triangle 1"));
        pieces.add(new TangramPiece("Small Triangle 2"));
        pieces.add(new TangramPiece("Square"));
        pieces.add(new TangramPiece("Parallelogram"));
    }

    // Methods needed
    /*
    // For each piece, check if its position is near the correct position for this figure
    public boolean isFigure(list of coordinates);


     */
}
