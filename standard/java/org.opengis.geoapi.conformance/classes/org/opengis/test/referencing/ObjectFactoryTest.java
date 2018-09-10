/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    Copyright (C) 2009-2018 Open Geospatial Consortium, Inc.
 *    All Rights Reserved. http://www.opengeospatial.org/ogc/legal
 *
 *    Permission to use, copy, and modify this software and its documentation, with
 *    or without modification, for any purpose and without fee or royalty is hereby
 *    granted, provided that you include the following on ALL copies of the software
 *    and documentation or portions thereof, including modifications, that you make:
 *
 *    1. The full text of this NOTICE in a location viewable to users of the
 *       redistributed or derivative work.
 *    2. Notice of any changes or modifications to the OGC files, including the
 *       date changes were made.
 *
 *    THIS SOFTWARE AND DOCUMENTATION IS PROVIDED "AS IS," AND COPYRIGHT HOLDERS MAKE
 *    NO REPRESENTATIONS OR WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 *    TO, WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE OR THAT
 *    THE USE OF THE SOFTWARE OR DOCUMENTATION WILL NOT INFRINGE ANY THIRD PARTY
 *    PATENTS, COPYRIGHTS, TRADEMARKS OR OTHER RIGHTS.
 *
 *    COPYRIGHT HOLDERS WILL NOT BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL OR
 *    CONSEQUENTIAL DAMAGES ARISING OUT OF ANY USE OF THE SOFTWARE OR DOCUMENTATION.
 *
 *    The name and trademarks of copyright holders may NOT be used in advertising or
 *    publicity pertaining to the software without specific, written prior permission.
 *    Title to copyright in this software and any associated documentation will at all
 *    times remain with copyright holders.
 */
package org.opengis.test.referencing;

import java.util.Map;
import java.util.List;
import java.util.Collections;
import javax.measure.Unit;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;

import org.opengis.parameter.*;
import org.opengis.referencing.cs.*;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.datum.*;
import org.opengis.referencing.operation.*;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.ObjectFactory;
import org.opengis.util.Factory;
import org.opengis.util.FactoryException;
import org.opengis.test.Units;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assume.*;
import static org.opengis.test.Assert.*;
import static org.opengis.referencing.cs.AxisDirection.*;


/**
 * Tests the creation of referencing objects from the {@linkplain ObjectFactory object factories}
 * given at construction time.
 *
 * <div class="note"><b>Usage example:</b>
 * in order to specify their factories and run the tests in a JUnit framework, implementors can
 * define a subclass in their own test suite as in the example below:
 *
 * <blockquote><pre>import org.junit.runner.RunWith;
 *import org.junit.runners.JUnit4;
 *import org.opengis.test.referencing.ObjectFactoryTest;
 *
 *&#64;RunWith(JUnit4.class)
 *public class MyTest extends ObjectFactoryTest {
 *    public MyTest() {
 *        super(new MyDatumFactory(), new MyCSFactory(), new MyCRSFactory(), new MyOpFactory());
 *    }
 *}</pre></blockquote>
 * </div>
 *
 * @see AuthorityFactoryTest
 * @see org.opengis.test.TestSuite
 *
 * @author  Cédric Briançon (Geomatys)
 * @author  Martin Desruisseaux (Geomatys)
 * @version 3.1
 * @since   2.3
 */
@RunWith(Parameterized.class)
public strictfp class ObjectFactoryTest extends ReferencingTestCase {
    /**
     * Factory to use for building {@link Datum} instances, or {@code null} if none.
     */
    protected final DatumFactory datumFactory;

    /**
     * Factory to use for building {@link CoordinateSystem} instances, or {@code null} if none.
     */
    protected final CSFactory csFactory;

    /**
     * Factory to use for building {@link CoordinateReferenceSystem} instances, or {@code null} if none.
     */
    protected final CRSFactory crsFactory;

    /**
     * Factory to use for building {@link Conversion} instances, or {@code null} if none.
     */
    protected final CoordinateOperationFactory copFactory;

    /**
     * Returns a default set of factories to use for running the tests. Those factories are given
     * in arguments to the constructor when this test class is instantiated directly by JUnit (for
     * example as a {@linkplain org.junit.runners.Suite.SuiteClasses suite} element), instead than
     * subclassed by the implementor. The factories are fetched as documented in the
     * {@link #factories(Class[])} javadoc.
     *
     * @return the default set of arguments to be given to the {@code ObjectFactoryTest} constructor.
     *
     * @since 3.1
     */
    @Parameterized.Parameters
    @SuppressWarnings("unchecked")
    public static List<Factory[]> factories() {
        return factories(DatumFactory.class, CSFactory.class, CRSFactory.class, CoordinateOperationFactory.class);
    }

    /**
     * Creates a new test using the given factories. If a given factory is {@code null},
     * then the tests which depend on it will be skipped.
     *
     * @param datumFactory  factory for creating {@link Datum} instances.
     * @param csFactory     factory for creating {@link CoordinateSystem} instances.
     * @param crsFactory    factory for creating {@link CoordinateReferenceSystem} instances.
     * @param copFactory    factory for creating {@link Conversion} instances.
     */
    public ObjectFactoryTest(
            final DatumFactory             datumFactory,
            final CSFactory                   csFactory,
            final CRSFactory                 crsFactory,
            final CoordinateOperationFactory copFactory)
    {
        super(datumFactory, csFactory, crsFactory, copFactory);
        this.datumFactory = datumFactory;
        this.csFactory    = csFactory;
        this.crsFactory   = crsFactory;
        this.copFactory   = copFactory;
    }

    /**
     * Returns the authority factory tests backed by the object factories.
     */
    private AuthorityFactoryTest createAuthorityFactoryTest() {
        final PseudoEpsgFactory factory = new PseudoEpsgFactory(Units.getDefault(),
                datumFactory, csFactory, crsFactory, copFactory, null, validators);
        return new AuthorityFactoryTest(factory, factory, factory);
    }

    /**
     * Builds a map containing only one value, composed by the {@link IdentifiedObject#NAME_KEY}
     * identifier and the value specified.
     *
     * @param  value  the value for the name key.
     * @return a map containing only the value specified for the name key.
     */
    private static Map<String,String> name(final String value) {
        return Collections.singletonMap(IdentifiedObject.NAME_KEY, value);
    }

    /**
     * Tests the creation of the EPSG:4326 {@link GeographicCRS}. This method wraps the
     * object factories in an {@link PseudoEpsgFactory} instance, then delegates to the
     * {@link AuthorityFactoryTest#testWGS84()} method.
     *
     * @throws FactoryException if a factory fails to create a referencing object.
     */
    @Test
    public void testWGS84() throws FactoryException {
        createAuthorityFactoryTest().testWGS84();
    }

    /**
     * Tests the creation of the WGS84 {@linkplain CoordinateReferenceSystem CRS} with ellipsoidal height, and
     * verifies that the axes are in the given (<var>latitude</var>, <var>longitude</var>, <var>height</var>) order.
     *
     * @throws FactoryException if a factory fails to create a referencing object.
     */
    @Test
    public void testWGS84_3D() throws FactoryException {
        CoordinateSystemAxis λ, φ, h;
        EllipsoidalCS cs;
        GeographicCRS crs;              // The final product of this method.
        GeodeticDatum datum;

        final Unit<Length> metre = units.metre();
        final Unit<Angle> degree = units.degree();

        // Build a geodetic datum.
        assumeNotNull(datumFactory);
        validators.validate(datum = datumFactory.createGeodeticDatum(name("World Geodetic System 1984"),
                                    datumFactory.createEllipsoid    (name("WGS 84"), 6378137.0, 298.257223563, metre),
                                    datumFactory.createPrimeMeridian(name("Greenwich"), 0.0, degree)));

        // Build an ellipsoidal coordinate system.
        assumeNotNull(csFactory);
        validators.validate(λ  = csFactory.createCoordinateSystemAxis(name("Geodetic longitude"), "λ", EAST,  degree));
        validators.validate(φ  = csFactory.createCoordinateSystemAxis(name("Geodetic latitude"),  "φ", NORTH, degree));
        validators.validate(h  = csFactory.createCoordinateSystemAxis(name("Ellipsoidal height"), "h", UP,    metre));
        validators.validate(cs = csFactory.createEllipsoidalCS(name("WGS 84"), φ, λ, h));

        // Finally build the geographic coordinate reference system.
        assumeNotNull(crsFactory);
        validators.validate(crs = crsFactory.createGeographicCRS(name("WGS84(DD)"), datum, cs));

        datum = crs.getDatum();
        verifyIdentification(datum, "World Geodetic System 1984", null);
        verifyPrimeMeridian(datum.getPrimeMeridian(), "Greenwich", 0.0, degree);

        cs = crs.getCoordinateSystem();
        verifyCoordinateSystem(cs, EllipsoidalCS.class,
                new AxisDirection[] {
                    AxisDirection.NORTH,
                    AxisDirection.EAST,
                    AxisDirection.UP
                }, degree, degree, metre);
        verifyIdentification(cs.getAxis(0), "Geodetic latitude", null);
        verifyIdentification(cs.getAxis(1), "Geodetic longitude", null);
        verifyIdentification(cs.getAxis(2), "Ellipsoidal height", null);
    }

    /**
     * Tests the creation of a geocentric CRS.
     *
     * @throws FactoryException if a factory fails to create a referencing object.
     */
    @Test
    public void testGeocentric() throws FactoryException {
        final CoordinateSystemAxis X, Y, Z;
        final CartesianCS   cs;
        final GeocentricCRS crs;            // The final product of this method.
        final GeodeticDatum datum;
        final PrimeMeridian greenwich;
        final Ellipsoid     ellipsoid;

        final Unit<Length> metre = units.metre();
        final Unit<Angle> degree = units.degree();

        assumeNotNull(datumFactory);
        validators.validate(greenwich = datumFactory.createPrimeMeridian  (name("Greenwich Meridian"), 0, degree));
        validators.validate(ellipsoid = datumFactory.createFlattenedSphere(name("WGS84 Ellipsoid"), 6378137, 298.257223563, metre));
        validators.validate(datum     = datumFactory.createGeodeticDatum  (name("WGS84 Datum"), ellipsoid, greenwich));

        assumeNotNull(csFactory);
        validators.validate(X  = csFactory.createCoordinateSystemAxis(name("Geocentric X"), "X", GEOCENTRIC_X, metre));
        validators.validate(Y  = csFactory.createCoordinateSystemAxis(name("Geocentric Y"), "Y", GEOCENTRIC_Y, metre));
        validators.validate(Z  = csFactory.createCoordinateSystemAxis(name("Geocentric Z"), "Z", GEOCENTRIC_Z, metre));
        validators.validate(cs = csFactory.createCartesianCS(name("Geocentric CS"), X, Z, Y));

        assumeNotNull(crsFactory);
        validators.validate(crs = crsFactory.createGeocentricCRS(name("Geocentric CRS"), datum, cs));
        assertAxisDirectionsEqual("GeocentricCRS", crs.getCoordinateSystem(), GEOCENTRIC_X, GEOCENTRIC_Z, GEOCENTRIC_Y);
    }

    /**
     * Tests the creation of a projected CRS with vertical height.
     *
     * @throws FactoryException if a factory fails to create a referencing object.
     *
     * @deprecated Renamed {@link #testProjectedWithGeoidalHeight()} for making clearer that this is not
     *             a projected CRS associated to a 3D coordinate system.
     */
    @Test
    @Deprecated
    public void testProjected3D() throws FactoryException {
        testProjectedWithGeoidalHeight();
    }

    /**
     * Tests the creation of a compound CRS made of a projected CRS with a gravity-related height.
     *
     * @throws FactoryException if a factory fails to create a referencing object.
     */
    @Test
    public void testProjectedWithGeoidalHeight() throws FactoryException {
        final CoordinateSystemAxis axisN, axisE, axisH, axisφ, axisλ;

        final EllipsoidalCS   baseCS;
        final GeographicCRS   baseCRS;
        final GeodeticDatum   baseDatum;
        final PrimeMeridian   greenwich;
        final Ellipsoid       ellipsoid;

        final CartesianCS     projectedCS;
        final ProjectedCRS    projectedCRS;
        final OperationMethod projectionMethod;
        final Conversion      baseToUTM;
        final int             utmZone = 12;

        final VerticalCS      heightCS;
        final VerticalCRS     heightCRS;
        final VerticalDatum   heightDatum;
        final CompoundCRS     crs3D;            // The final product of this method.

        final Unit<Length> metre = units.metre();
        final Unit<Angle> degree = units.degree();

        assumeNotNull(datumFactory);
        validators.validate(greenwich   = datumFactory.createPrimeMeridian  (name("Greenwich Meridian"), 0, degree));
        validators.validate(ellipsoid   = datumFactory.createFlattenedSphere(name("WGS84 Ellipsoid"), 6378137, 298.257223563, metre));
        validators.validate(baseDatum   = datumFactory.createGeodeticDatum  (name("WGS84 Datum"), ellipsoid, greenwich));
        validators.validate(heightDatum = datumFactory.createVerticalDatum  (name("WGS84 geoidal height"), VerticalDatumType.GEOIDAL));

        assumeNotNull(csFactory);
        validators.validate(axisN       = csFactory.createCoordinateSystemAxis(name("Northing"),               "N", NORTH, metre));
        validators.validate(axisE       = csFactory.createCoordinateSystemAxis(name("Easting"),                "E", EAST,  metre));
        validators.validate(axisH       = csFactory.createCoordinateSystemAxis(name("Gravity-related Height"), "H", UP,    metre));
        validators.validate(axisφ       = csFactory.createCoordinateSystemAxis(name("Geodetic Latitude"),      "φ", NORTH, degree));
        validators.validate(axisλ       = csFactory.createCoordinateSystemAxis(name("Geodetic Longitude"),     "λ", EAST,  degree));
        validators.validate(baseCS      = csFactory.createEllipsoidalCS       (name("2D ellipsoidal"),  axisλ, axisφ));
        validators.validate(projectedCS = csFactory.createCartesianCS         (name("2D Cartesian CS"), axisN, axisE));
        validators.validate(heightCS    = csFactory.createVerticalCS          (name("Height CS"),       axisH));

        assumeNotNull(crsFactory);
        baseCRS   = crsFactory.createGeographicCRS(name("2D geographic CRS"), baseDatum, baseCS);
        heightCRS = crsFactory.createVerticalCRS  (name("Height CRS"),      heightDatum, heightCS);

        assumeNotNull(copFactory);
        validators.validate(projectionMethod = copFactory.getOperationMethod("Transverse_Mercator"));
        final ParameterValueGroup paramUTM = projectionMethod.getParameters().createValue();
        paramUTM.parameter("central_meridian")  .setValue(-180 + utmZone*6 - 3);
        paramUTM.parameter("latitude_of_origin").setValue(0.0);
        paramUTM.parameter("scale_factor")      .setValue(0.9996);
        paramUTM.parameter("false_easting")     .setValue(500000.0);
        paramUTM.parameter("false_northing")    .setValue(0.0);
        validators.validate(paramUTM);

        validators.validate(baseToUTM    = copFactory .createDefiningConversion(name("Transverse_Mercator"), projectionMethod, paramUTM));
        validators.validate(projectedCRS = crsFactory.createProjectedCRS(name("WGS 84 / UTM Zone 12 (2D)"), baseCRS, baseToUTM, projectedCS));
        validators.validate(crs3D        = crsFactory.createCompoundCRS(name("3D Compound WGS 84 / UTM Zone 12"), projectedCRS, heightCRS));
        assertAxisDirectionsEqual("CompoundCRS", crs3D.getCoordinateSystem(), NORTH, EAST, UP);
    }
}
