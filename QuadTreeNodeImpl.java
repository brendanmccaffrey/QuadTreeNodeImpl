// CIS 121, HW4 QuadTree

public class QuadTreeNodeImpl implements QuadTreeNode {
    /**
     * ! Do not delete this method ! Please implement your logic inside this method
     * without modifying the signature of this method, or else your code won't
     * compile.
     * <p/>
     * Be careful that if you were to create another method, make sure it is not
     * public.
     *
     * @param image image to put into the tree
     * @return the newly build QuadTreeNode instance which stores the compressed
     *         image
     * @throws IllegalArgumentException if image is null
     * @throws IllegalArgumentException if image is empty
     * @throws IllegalArgumentException if image.length is not a power of 2
     * @throws IllegalArgumentException if image, the 2d-array, is not a perfect
     *                                  square
     */

    int dimension, color;
    QuadTreeNodeImpl ul, ur, ll, lr;

    public static QuadTreeNode buildFromIntArray(int[][] image) {
        if (illegal(image)) {
            throw new IllegalArgumentException("Invalid Image");
        }
        int dim = image.length;
        QuadTreeNodeImpl root = helpBuild(dim, 0, 0, image);
        return root;
    }
    
////////////////////////////////////////////////////////////////////
////////////////////////// CONSTRUCTORS ////////////////////////////
////////////////////////////////////////////////////////////////////

    public QuadTreeNodeImpl(int dim, QuadTreeNodeImpl tl, QuadTreeNodeImpl tr, 
            QuadTreeNodeImpl bl, QuadTreeNodeImpl br,
            int color) {
        this.dimension = dim;
        this.ul = tl;
        this.ur = tr;
        this.ll = bl;
        this.lr = br;
        this.color = color;
    }

    public QuadTreeNodeImpl(int dim, int color) {
        this.dimension = dim;
        this.ul = null;
        this.ur = null;
        this.ll = null;
        this.lr = null;
        this.color = color;
    }

    private static QuadTreeNodeImpl helpBuild(int dim, int row, int col, int[][] image) {
        int half = dim / 2;
        QuadTreeNodeImpl node;
        if (half >= 1) {
            // Create children
            QuadTreeNodeImpl a = helpBuild(half, row, col, image);
            QuadTreeNodeImpl b = helpBuild(half, row, col + half, image);
            QuadTreeNodeImpl c = helpBuild(half, row + half, col, image);
            QuadTreeNodeImpl d = helpBuild(half, row + half, col + half, image);
            // Check if they are all leaves of the same color
            if (isLeaves(a, b, c, d)
                    && (image[row][col] == image[row][col + half] && 
                    image[row + half][col] == image[row][col + half]
                            && image[row + half][col] == image[row + half][col + half])) {
                node = new QuadTreeNodeImpl(dim, image[row][col]);
            } else {
                node = new QuadTreeNodeImpl(dim, a, b, c, d, image[row][col]);
            }
        } else {
            node = new QuadTreeNodeImpl(dim, image[row][col]);
        }
        return node;
    }

////////////////////////////////////////////////////////////////////
////////////////////// SUPPLEMENT METHODS //////////////////////////
////////////////////////////////////////////////////////////////////

    private static boolean isLeaves(QuadTreeNodeImpl tl, QuadTreeNodeImpl tr, QuadTreeNodeImpl bl,
            QuadTreeNodeImpl br) {
        return tl.isLeaf() && tr.isLeaf() && bl.isLeaf() && br.isLeaf();
    }

    @Override
    public boolean isLeaf() {
        return (ul == null && ur == null && ll == null && lr == null);
    }

    // Check for valid input
    private static boolean illegal(int[][] image) {
        if (image == null) {
            return true;
        }
        if (image.length == 0) {
            return true;
        }
        // Check if square
        if (image.length != image[0].length) {
            return true;
        }
        // Check if dimension is power of 2
        if (Math.ceil(Math.log(image.length) / Math.log(2)) != 
                Math.ceil(Math.log(image.length) / Math.log(2))) {
            return true;
        }
        return false;
    }

    @Override
    public QuadTreeNodeImpl getQuadrant(QuadName quadrant) {
        if (quadrant == QuadName.TOP_LEFT) {
            return ul;
        }
        if (quadrant == QuadName.TOP_RIGHT) {
            return ur;
        }
        if (quadrant == QuadName.BOTTOM_LEFT) {
            return ll;
        }
        if (quadrant == QuadName.BOTTOM_RIGHT) {
            return lr;
        }
        throw new IllegalArgumentException();
    }

    @Override
    public int getDimension() {
        return this.dimension;
    }

////////////////////////////////////////////////////////////////////
/////////////////////////// GET COLOR //////////////////////////////
////////////////////////////////////////////////////////////////////

    @Override
    public int getColor(int x, int y) {
        if (Math.min(x, y) < 0 || Math.max(x, y) > this.getDimension()) {
            throw new IllegalArgumentException();
        }
        return getColorHelp(this, x, y);
    }

    // @Override
    private int getColor() {
        return color;
    }

    private int getColorHelp(QuadTreeNodeImpl node, int x, int y) {
        if (node.isLeaf()) {
            return node.getColor();
        }
        int half = node.getDimension() / 2;
        // Find requested quadrant
        if (x >= half) {
            if (y >= half) {
                return getColorHelp(node.lr, x - half, y - half);
            }
            return getColorHelp(node.ur, x - half, y);
        }
        if (y >= half) {
            return getColorHelp(node.ll, x, y - half);
        }
        return getColorHelp(node.ul, x, y);

    }
    
////////////////////////////////////////////////////////////////////
/////////////////////////// SET COLOR //////////////////////////////
////////////////////////////////////////////////////////////////////

    @Override
    public void setColor(int x, int y, int c) {
        if (Math.min(x, y) < 0 || Math.max(x, y) > this.getDimension()) {
            throw new IllegalArgumentException();
        }
        setColorHelp(this, x, y, c);
    }

    private void setColor(int c) {

        this.color = c;
    }

    private void setColorHelp(QuadTreeNodeImpl node, int x, int y, int c) {
        if (node.getDimension() == 1) {
            node.setColor(c);
        } else {
            int half = node.getDimension() / 2;
            if (node.isLeaf()) {
                // NEEDS PARTIAL DECOMPRESSION
                int tempCol = node.getColor();
                // Create children
                node.ul = new QuadTreeNodeImpl(half, tempCol);
                node.ur = new QuadTreeNodeImpl(half, tempCol);
                node.ll = new QuadTreeNodeImpl(half, tempCol);
                node.lr = new QuadTreeNodeImpl(half, tempCol);
            }
            node.color = -1;
            // Find requested quadrant and recurse
            if (x >= half) {
                if (y >= half) {
                    setColorHelp(node.lr, x - half, y - half, c);
                } else {
                    setColorHelp(node.ur, x - half, y, c);
                }
            } else if (y >= half) {
                setColorHelp(node.ll, x, y - half, c);
            } else {
                setColorHelp(node.ul, x, y, c);
            }
            // Now we must check if the image needs compression
            if (isLeaves(node.ul, node.ur, node.ll, node.lr) && node.ul.color == node.ur.color
                    && node.ur.color == node.lr.color && node.lr.color == node.ll.color) {
                // Compress by making children null and reset color
                node.color = node.ul.color;
                node.ul = null;
                node.ur = null;
                node.ll = null;
                node.lr = null;
            }
        }
    }

////////////////////////////////////////////////////////////////////
/////////////////////////// GET SIZE ///////////////////////////////
////////////////////////////////////////////////////////////////////

    @Override
    public int getSize() {
        boolean nullLeaves = (this.ul == null) && (this.ur == null) 
                && (this.ll == null) && (this.lr == null);
        if (nullLeaves || this.isLeaf()) {
            return 1;
        }
        return (1 + ul.getSize() + ur.getSize() + ll.getSize() + lr.getSize());
    }

////////////////////////////////////////////////////////////////////
/////////////////////////// DECOMPRESS /////////////////////////////
////////////////////////////////////////////////////////////////////

    @Override
    public int[][] decompress() {
        int dim = this.getDimension();
        int[][] image = new int[dim][dim];
        decompresser(this, 0, 0, image);
        return image;
    }

    private static void decompresser(QuadTreeNodeImpl node, int row, int col, int[][] image) {
        int half = node.getDimension() / 2;
        if (node.isLeaf()) {
            for (int i = row; i < row + node.getDimension(); i++) {
                for (int j = col; j < col + node.getDimension(); j++) {
                    image[i][j] = node.getColor();
                }
            }
        } else {
            decompresser(node.getQuadrant(QuadName.TOP_LEFT), row, col, image);
            decompresser(node.getQuadrant(QuadName.TOP_RIGHT), row, col + half, image);
            decompresser(node.getQuadrant(QuadName.BOTTOM_LEFT), row + half, col, image);
            decompresser(node.getQuadrant(QuadName.BOTTOM_RIGHT), row + half, col + half, image);
        }
    }
////////////////////////////////////////////////////////////////////
/////////////////////// COMPRESSION RATIO //////////////////////////
////////////////////////////////////////////////////////////////////

    @Override
    public double getCompressionRatio() {
        double nodes = this.getSize();
        double pixels = Math.pow(this.getDimension(), 2);
        return (nodes / pixels);
    }
}
