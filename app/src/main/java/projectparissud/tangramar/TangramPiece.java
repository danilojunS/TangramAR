package projectparissud.tangramar;

/**
 * Created by altay on 27/02/15.
 */
public class TangramPiece {
    public String name;
    public PieceMarker marker; // Marker referring to this piece
    static double MARKER_WIDTH = 80.;

    // Constructor
    // When we create a new piece, we specify its name and the position it should have in the reference
    public TangramPiece(String _name, double[] _coordinates, boolean isCenter, String _pattern)
    {
        name = _name;
        marker = new PieceMarker(name, _pattern, MARKER_WIDTH, new double[]{0, 0}, isCenter);
        marker.setCorrectPoseInReferenceCS(_coordinates);
    }

}
