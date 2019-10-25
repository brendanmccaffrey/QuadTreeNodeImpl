import static org.junit.Assert.*;

import org.junit.Test;

public class QuadTreeNodeImplTest {

    @Test
    public void simpleDimensionTest() {
        int[][] image = { { 0, 1, 2, 3 }, { 4, 5, 6, 7 }, { 8, 9, 0, 1 }, { 2, 3, 4, 5 } };
        QuadTreeNode a = QuadTreeNodeImpl.buildFromIntArray(image);
        assertEquals(4, a.getDimension());
    }

    @Test
    public void compressionRatioTestSimple() {
        int[][] image = { { 0, 0, 1, 1 }, { 0, 0, 1, 1 }, { 0, 0, 1, 1 }, { 0, 0, 1, 1 } };
        QuadTreeNode a = QuadTreeNodeImpl.buildFromIntArray(image);
        double b = 5.0 / 16.0;
        assertEquals(b, a.getCompressionRatio(), .0001);
    }

    @Test
    public void compressionRatioTestOneColor() {
        int[][] image = { { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } };
        QuadTreeNode a = QuadTreeNodeImpl.buildFromIntArray(image);
        double b = 1.0 / 16.0;
        assertEquals(b, a.getCompressionRatio(), .0001);
    }

    @Test
    public void compressionRatioTestUncompressed() {
        int[][] image = { { 0, 2, 2, 3 }, { 0, 0, 6, 7 }, { 8, 9, 0, 1 }, { 2, 3, 4, 5 } };
        QuadTreeNode a = QuadTreeNodeImpl.buildFromIntArray(image);
        double b = 21.0 / 16.0;
        assertEquals(b, a.getCompressionRatio(), .0001);
    }

    @Test
    public void compressAndDecompressOneColor() {
        int[][] image = { { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } };
        QuadTreeNode a = QuadTreeNodeImpl.buildFromIntArray(image);
        double b = 1.0 / 16.0;
        assertEquals(b, a.getCompressionRatio(), .0001);
        int[][] img = a.decompress();
        assertArrayEquals(null, image, img);
    }

    @Test
    public void compressAndDecompressAsymmetric() {
        int[][] image = { { 0, 0, 2, 3 }, { 0, 0, 6, 7 }, { 8, 9, 0, 1 }, { 2, 3, 4, 5 } };
        QuadTreeNode a = QuadTreeNodeImpl.buildFromIntArray(image);
        double b = 17.0 / 16.0;
        assertEquals(b, a.getCompressionRatio(), .0001);
        int[][] img = a.decompress();
        assertArrayEquals(null, image, img);
    }

    @Test
    public void getColorTest() {
        int[][] image = { { 0, 2, 2, 3 }, { 0, 0, 6, 7 }, { 8, 9, 0, 1 }, { 2, 3, 4, 5 } };
        QuadTreeNode a = QuadTreeNodeImpl.buildFromIntArray(image);
        double b = 1.0 / 16.0;
        assertEquals(4, a.getColor(2, 3));
    }

    @Test
    public void setColorTest() {
        int[][] image = { { 0, 2, 2, 3 }, { 0, 0, 6, 7 }, { 8, 9, 0, 1 }, { 2, 3, 4, 5 } };
        int[][] imageAnswer = { { 0, 2, 2, 3 }, { 0, 0, 6, 7 }, { 8, 9, 0, 1 }, { 2, 3, 8, 5 } };
        QuadTreeNode a = QuadTreeNodeImpl.buildFromIntArray(image);
        double b = 1.0 / 16.0;
        a.setColor(2, 3, 8);
        int[][] img = a.decompress();
        assertArrayEquals(null, imageAnswer, img);
    }
    

    @Test
    public void setColorNeedsPartialDecompressionTest() {
        int[][] image = { { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } };
        int[][] imageAnswer = { { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 8 } };
        QuadTreeNode a = QuadTreeNodeImpl.buildFromIntArray(image);
        double b = 1.0 / 16.0;
        assertEquals(b, a.getCompressionRatio(), .0001);
        a.setColor(3, 3, 8);
        double c = 9.0 / 16.0;
        assertEquals(c, a.getCompressionRatio(), .0001);
        int[][] img = a.decompress();
        assertArrayEquals(null, imageAnswer, img);
    }

    @Test
    public void setColorNeedsPartialCompressionTest() {
        int[][] image = { { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 1 } };
        int[][] imageAnswer = { { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } };
        QuadTreeNode a = QuadTreeNodeImpl.buildFromIntArray(image);
        double b = 9.0 / 16.0;
        assertEquals(b, a.getCompressionRatio(), .0001);
        a.setColor(3, 3, 0);
        double c = 1.0 / 16.0;
        assertEquals(c, a.getCompressionRatio(), .0001);
        int[][] img = a.decompress();
        assertArrayEquals(null, imageAnswer, img);
    }

}
