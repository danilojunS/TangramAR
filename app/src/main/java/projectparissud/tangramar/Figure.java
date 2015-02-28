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
    // The id of the piece at the center of the figure (where we will render the 3D model)
    public int centerPiece;

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

    public boolean match(ArrayList<double[]> _piecesCoordinates){
        double distance = 0;
        int i = 0;
        for(double[] coordinates : piecesCoordinates){
            double[] _coordinates = _piecesCoordinates.get(i);
            // compare coordinates and _coordinates (should be nearly the same for (x,y) and (rotation matrix))
            // WARNING : we can exchange the two big triangles and the two small triangles !
            // i.e. we should try to exchange _piecesCoordinates.get(0)/_piecesCoordinates.get(1) and _piecesCoordinates.get(3)/_piecesCoordinates.get(4)
            double x_diff = Math.abs(_coordinates[0] - coordinates[0]);
            double y_diff = Math.abs(_coordinates[1] - coordinates[1]);
            distance += Math.sqrt(x_diff * x_diff + y_diff * y_diff);
            i++;
        }
        distance = distance / _piecesCoordinates.size(); // Mean of the distances
        // TODO : adjust this value
        return distance < 50.;
    }
}
