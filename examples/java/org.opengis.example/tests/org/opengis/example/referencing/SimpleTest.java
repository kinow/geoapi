/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    This file is hereby placed into the Public Domain.
 *    This means anyone is free to do whatever they wish with this file.
 */
package org.opengis.example.referencing;

import org.junit.Test;
import org.opengis.referencing.cs.RangeMeaning;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.EllipsoidalCS;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.datum.GeodeticDatum;

import static org.junit.Assert.*;


/**
 * Tests the creation of miscellaneous simple objects.
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @version 3.1
 * @since   3.1
 */
public strictfp class SimpleTest {
    /**
     * Tests the {@link SimpleDatum#WGS84} constant.
     */
    @Test
    public void testDatum() {
        final GeodeticDatum datum = SimpleDatum.WGS84;
        assertEquals(0, datum.getPrimeMeridian().getGreenwichLongitude(), 0);
        assertEquals("EPSG:World Geodetic System 1984", datum.toString());
        assertEquals("Object shall be equal to itself.", datum, datum);
    }

    /**
     * Tests the {@link org.opengis.example.referencing.SimpleCRS.Geographic#WGS84} constant.
     */
    @Test
    public void testGeographicCRS() {
        final GeographicCRS crs = SimpleCRS.Geographic.WGS84;
        assertSame(SimpleDatum.WGS84, crs.getDatum());
        final EllipsoidalCS cs = crs.getCoordinateSystem();
        assertEquals(2, cs.getDimension());
        assertSame  (AxisDirection.NORTH,     cs.getAxis(0).getDirection());
        assertSame  (AxisDirection.EAST,      cs.getAxis(1).getDirection());
        assertSame  (RangeMeaning.EXACT,      cs.getAxis(0).getRangeMeaning());
        assertSame  (RangeMeaning.WRAPAROUND, cs.getAxis(1).getRangeMeaning());
        assertEquals("EPSG:WGS 84", crs.toString());
        assertEquals("Object shall be equal to itself.", crs, crs);
    }
}
