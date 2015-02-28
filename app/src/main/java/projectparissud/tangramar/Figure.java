package projectparissud.tangramar;

import java.util.ArrayList;

/**
 * Created by altay on 28/02/15.
 */
public class Figure {
    /* Pieces coordinates on a 2D plan :
    (x, y) and a 2x2 rotation matrix
     */
    public String name;
    public ArrayList<double[]> piecesCoordinates;

    // We shall have a list of all figures we can create to display in the first menu
    static ArrayList<Figure> allFigures = new ArrayList<Figure>();

    public Figure(String _name, ArrayList<double[]> _piecesCoordinates) throws Exception {
        name = _name;
        if(_piecesCoordinates.size() == 7) {
            for (double[] _coordinates : _piecesCoordinates) {
                if (_coordinates.length == 6) {
                    piecesCoordinates.add(_coordinates);
                } else {
                    throw new Exception("Trying to instantiate a figure with bad coordinates");
                }
            }
        } else {
            throw new Exception("A figure should have 7 pieces");
        }
    }

    public boolean match(ArrayList<double[]> _coordinates){
        for(double[] coordinates : piecesCoordinates){
            // compare coordinates and _coordinates (should be nearly the same for (x,y) and (rotation matrix))
        }
        return false;
    }
}
