/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    This file is hereby placed into the Public Domain.
 *    This means anyone is free to do whatever they wish with this file.
 */
package org.opengis.example.referencing;

import javax.vecmath.GMatrix;
import org.opengis.referencing.operation.Matrix;


/**
 * A {@link Matrix} built on top of Java3D {@code vecmath} library.
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @version 3.1
 * @since   3.1
 */
public class SimpleMatrix extends GMatrix implements Matrix {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -1756594679120634738L;

    /**
     * Creates a matrix of size {@code numRow}&nbsp;×&nbsp;{@code numCol}.
     * Elements on the diagonal (<var>j</var> == <var>i</var>) are set to 1.
     *
     * @param numRow  number of rows.
     * @param numCol  number of columns.
     */
    public SimpleMatrix(final int numRow, final int numCol) {
        super(numRow, numCol);
    }

    /**
     * Creates a new matrix initialized to the same content than the given matrix.
     *
     * @param matrix  the matrix to copy.
     */
    public SimpleMatrix(final Matrix matrix) {
        super(matrix.getNumRow(), matrix.getNumCol());
        for (int j=super.getNumRow(); --j>=0;) {
            for (int i=super.getNumCol(); --i>=0;) {
                setElement(j, i, matrix.getElement(j, i));
            }
        }
    }

    /**
     * Returns {@code true} if this matrix is an identity matrix.
     */
    @Override
    public boolean isIdentity() {
        final int numRow = getNumRow();
        final int numCol = getNumCol();
        if (numRow != numCol) {
            return false;
        }
        for (int j=0; j<numRow; j++) {
            for (int i=0; i<numCol; i++) {
                if (getElement(j,i) != (i==j ? 1 : 0)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns a clone of this matrix.
     */
    @Override
    public SimpleMatrix clone() {
        return (SimpleMatrix) super.clone();
    }
}
