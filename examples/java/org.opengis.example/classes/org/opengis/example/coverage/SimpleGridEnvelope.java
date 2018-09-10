/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    This file is hereby placed into the Public Domain.
 *    This means anyone is free to do whatever they wish with this file.
 */
package org.opengis.example.coverage;

import java.util.Arrays;
import java.io.Serializable;
import java.awt.Rectangle;

import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.coverage.grid.GridCoordinates;


/**
 * A {@link GridEnvelope} implementation which store values in a {@code int[]} array.
 * This class defines a range of grid coverage coordinates.
 *
 * <p><b>CAUTION:</b>
 * ISO 19123 defines {@linkplain #getHigh() high} coordinates as <strong>inclusive</strong>.
 * We follow this specification for all getters methods, but keep in mind that this is the
 * opposite of Java2D usage where {@link Rectangle} maximal values are exclusive.</p>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.1
 *
 * @since 3.1
 */
public class SimpleGridEnvelope implements GridEnvelope, Serializable {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 1487728226944612950L;

    /**
     * Minimum and maximum grid ordinates. The first half contains minimum ordinates (inclusive),
     * while the last half contains maximum ordinates (<strong>inclusive</strong>).
     */
    private final int[] index;

    /**
     * Checks if ordinate values in the minimum index are less than or
     * equal to the corresponding ordinate value in the maximum index.
     *
     * @throws IllegalArgumentException if an ordinate value in the minimum index is not
     *         less than or equal to the corresponding ordinate value in the maximum index.
     */
    private static void checkCoherence(final int[] index) throws IllegalArgumentException {
        final int dimension = index.length >>> 1;
        for (int i=0; i<dimension; i++) {
            final int lower = index[i];
            final int upper = index[dimension+i];
            if (!(lower < upper)) {
                throw new IllegalArgumentException("Invalid range at dimension " + i);
            }
        }
    }

    /**
     * Creates a new grid envelope as a copy of the given one.
     *
     * @param envelope  the grid envelope to copy.
     */
    public SimpleGridEnvelope(final GridEnvelope envelope) {
        final int dimension = envelope.getDimension();
        index = new int[dimension << 1];
        for (int i=0; i<dimension; i++) {
            index[i] = envelope.getLow(i);
            index[i + dimension] = envelope.getHigh(i);
        }
        checkCoherence(index);
    }

    /**
     * Constructs a new grid envelope.
     *
     * @param low   the valid minimum inclusive grid coordinate. The array contains a minimum
     *              value (inclusive) for each dimension of the grid coverage. The lowest valid
     *              grid coordinate is often zero, but this is not mandatory.
     * @param high  the valid maximum grid coordinate, <strong>inclusive</strong>.
     *              The array contains a maximum value for each dimension of the grid coverage.
     *
     * @see #getLow()
     * @see #getHigh()
     */
    public SimpleGridEnvelope(final int[] low, final int[] high) {
        if (low.length != high.length) {
            throw new IllegalArgumentException("Mismatched array length.");
        }
        index = Arrays.copyOf(low, low.length + high.length);
        System.arraycopy(high, 0, index, low.length, high.length);
        checkCoherence(index);
    }

    /**
     * Returns the number of dimensions.
     */
    @Override
    public int getDimension() {
        return index.length >>> 1;
    }

    /**
     * Returns the valid minimum inclusive grid coordinates.
     * The sequence contains a minimum value for each dimension of the grid coverage.
     */
    @Override
    public GridCoordinates getLow() {
        return new SimpleGridCoordinates(index, 0, index.length >>> 1);
    }

    /**
     * Returns the valid maximum <strong>inclusive</strong> grid coordinates.
     * The sequence contains a maximum value for each dimension of the grid coverage.
     */
    @Override
    public GridCoordinates getHigh() {
        return new SimpleGridCoordinates(index, index.length >>> 1, index.length);
    }

    /**
     * Returns the valid minimum inclusive grid coordinate along the specified dimension.
     *
     * @see #getLow()
     */
    @Override
    public int getLow(final int dimension) {
        if (dimension < (index.length >>> 1)) {
            return index[dimension];
        }
        throw new ArrayIndexOutOfBoundsException(dimension);
    }

    /**
     * Returns the valid maximum <strong>inclusive</strong>
     * grid coordinate along the specified dimension.
     *
     * @see #getHigh()
     */
    @Override
    public int getHigh(final int dimension) {
        if (dimension >= 0) {
            return index[dimension + (index.length >>> 1)];
        }
        throw new ArrayIndexOutOfBoundsException(dimension);
    }

    /**
     * Returns the number of integer grid coordinates along the specified dimension.
     * This is equal to {@code getHigh(dimension) - getLow(dimension) + 1}.
     */
    @Override
    public int getSpan(final int dimension) {
        return index[dimension + (index.length >>> 1)] - index[dimension] + 1;
    }

    /**
     * Returns a hash value for this grid envelope. This value need not remain
     * consistent between different implementations of the same class.
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(index) ^ (int) serialVersionUID;
    }

    /**
     * Compares the specified object with this grid envelope for equality.
     *
     * @param  object  the object to compare with this grid envelope for equality.
     * @return {@code true} if the given object is equal to this grid envelope.
     */
    @Override
    public boolean equals(final Object object) {
        if (object instanceof SimpleGridEnvelope) {
            final SimpleGridEnvelope that = (SimpleGridEnvelope) object;
            return Arrays.equals(this.index, that.index);
        }
        return false;
    }

    /**
     * Returns a string representation of this grid envelope. The returned string is
     * implementation dependent. It is usually provided for debugging purposes.
     */
    @Override
    public String toString() {
        final int dimension = index.length >>> 1;
        final StringBuilder buffer = new StringBuilder(getClass().getSimpleName()).append('[');
        for (int i=0; i<dimension; i++) {
            if (i != 0) {
                buffer.append(", ");
            }
            buffer.append(index[i]).append('…').append(index[i + dimension]);
        }
        return buffer.append(']').toString();
    }
}
