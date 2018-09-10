/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    Copyright (C) 2014-2018 Open Geospatial Consortium, Inc.
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
package org.opengis.test.wkt;

import java.util.Date;
import java.util.List;
import javax.measure.Unit;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;
import javax.measure.quantity.Dimensionless;

import org.opengis.util.Factory;
import org.opengis.util.FactoryException;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.cs.*;
import org.opengis.referencing.datum.*;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.metadata.extent.Extent;
import org.opengis.test.referencing.ReferencingTestCase;
import org.opengis.test.Configuration;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.Test;

import static java.lang.Double.NaN;
import static org.junit.Assume.assumeTrue;
import static org.opengis.test.Assert.*;
import static org.opengis.referencing.cs.AxisDirection.*;


/**
 * Tests the Well-Known Text (WKT) parser of Coordinate Reference System (CRS) objects.
 * For running this test, vendors need to implement the {@link CRSFactory#createFromWKT(String)} method.
 * That method will be given various WKT strings from the
 * <a href="http://docs.opengeospatial.org/is/12-063r5/12-063r5.html">OGC 12-063r5 —
 * Well-known text representation of coordinate reference systems</a> specification.
 * The object returned by {@code createFromWKT(String)} will be checked for the following properties:
 *
 * <ul>
 *   <li>{@link IdentifiedObject#getName()} and {@link IdentifiedObject#getIdentifiers() getIdentifiers()} on the CRS and the datum</li>
 *   <li>{@link Ellipsoid#getSemiMajorAxis()} and {@link Ellipsoid#getInverseFlattening() getInverseFlattening()}</li>
 *   <li>{@link PrimeMeridian#getGreenwichLongitude()}</li>
 *   <li>{@link CoordinateSystem#getDimension()}</li>
 *   <li>{@link CoordinateSystemAxis#getAbbreviation()} when they were explicitly given in the WKT and do not need transliteration.</li>
 *   <li>{@link CoordinateSystemAxis#getDirection()} and {@link CoordinateSystemAxis#getUnit() getUnit()}</li>
 *   <li>{@link CoordinateReferenceSystem#getScope()} (optional – null allowed)</li>
 *   <li>{@link CoordinateReferenceSystem#getDomainOfValidity()} (optional – null allowed)</li>
 *   <li>{@link CoordinateReferenceSystem#getRemarks()} (optional – null allowed)</li>
 * </ul>
 *
 * <div class="note"><b>Usage example:</b>
 * in order to specify their factories and run the tests in a JUnit framework, implementors can
 * define a subclass in their own test suite as in the example below:
 *
 * <blockquote><pre>import org.junit.runner.RunWith;
 *import org.junit.runners.JUnit4;
 *import org.opengis.test.wkt.CRSParserTest;
 *
 *&#64;RunWith(JUnit4.class)
 *public class MyTest extends CRSParserTest {
 *    public MyTest() {
 *        super(new MyCRSFactory());
 *    }
 *}</pre></blockquote>
 * </div>
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @author  Johann Sorel (Geomatys)
 * @version 3.1
 * @since   3.1
 *
 * @see <a href="http://docs.opengeospatial.org/is/12-063r5/12-063r5.html">WKT 2 specification</a>
 */
@RunWith(Parameterized.class)
public strictfp class CRSParserTest extends ReferencingTestCase {
    /**
     * The factory to use for parsing WKT strings. The {@link CRSFactory#createFromWKT(String)} method
     * of this factory will be invoked for each test defined in this {@code CRSParserTest} class.
     */
    protected final CRSFactory crsFactory;

    /**
     * The instance returned by {@link CRSFactory#createFromWKT(String)} after parsing the WKT.
     * Subclasses can use this field if they wish to verify additional properties after the
     * verifications done by this {@code CRSParserTest} class.
     */
    protected CoordinateReferenceSystem object;

    /**
     * {@code true} if the test methods can invoke a <code>{@linkplain #validators validators}.validate(…)}</code>
     * method after parsing. Implementors can set this flag to {@code false} if their WKT parser is known to create
     * CRS objects that differ from the ISO 19111 model. One of the main reasons for disabling validation is because
     * the axis names specified by ISO 19162 differ from the axis names specified by ISO 19111.
     */
    protected boolean isValidationEnabled;

    /**
     * Returns a default set of factories to use for running the tests. Those factories are given
     * in arguments to the constructor when this test class is instantiated directly by JUnit (for
     * example as a {@linkplain org.junit.runners.Suite.SuiteClasses suite} element), instead than
     * sub-classed by the implementor. The factories are fetched as documented in the
     * {@link #factories(Class[])} javadoc.
     *
     * @return the default set of arguments to be given to the {@code ObjectFactoryTest} constructor.
     */
    @Parameterized.Parameters
    @SuppressWarnings("unchecked")
    public static List<Factory[]> factories() {
        return factories(CRSFactory.class);
    }

    /**
     * Creates a new test using the given factory.
     *
     * @param crsFactory  factory for parsing {@link CoordinateReferenceSystem} instances.
     */
    public CRSParserTest(final CRSFactory crsFactory) {
        super(crsFactory);
        this.crsFactory = crsFactory;
        @SuppressWarnings("unchecked")
        final boolean[] isEnabled = getEnabledFlags(
                Configuration.Key.isValidationEnabled);
        isValidationEnabled = isEnabled[0];
    }

    /**
     * Returns information about the configuration of the test which has been run.
     * This method returns a map containing:
     *
     * <ul>
     *   <li>All the following values associated to the {@link org.opengis.test.Configuration.Key} of the same name:
     *     <ul>
     *       <li>{@link #isValidationEnabled}</li>
     *       <li>{@link #crsFactory}</li>
     *     </ul>
     *   </li>
     * </ul>
     *
     * @return {@inheritDoc}
     */
    @Override
    public Configuration configuration() {
        final Configuration op = super.configuration();
        assertNull(op.put(Configuration.Key.isValidationEnabled, isValidationEnabled));
        assertNull(op.put(Configuration.Key.crsFactory,          crsFactory));
        return op;
    }

    /**
     * Asserts that the given datum has the expected name.
     *
     * @param datum  the datum to verify.
     * @param name   the string representation of the expected name (ignoring code space).
     */
    private static void verifyDatum(final Datum datum, final String name) {
        assertNotNull("SingleCRS.getDatum()", datum);
        assertEquals("datum.getName().getCode()", name, datum.getName().getCode());
    }

    /**
     * Compares the abbreviations of coordinate system axes against the expected values.
     * The comparison is case-sensitive, e.g. <var>h</var> (ellipsoidal height) is not the same than
     * <var>H</var> (gravity-related height).
     *
     * <p>The GeoAPI conformance tests invoke this method only for abbreviations that should not need transliteration.
     * For example the GeoAPI tests do not invoke this method for geodetic latitude and longitude axes, because some
     * implementations may keep the Greek letters φ and λ as specified in ISO 19111 while other implementations may
     * transliterate those Greek letters to the <var>P</var> and <var>L</var> Latin letters.</p>
     *
     * @param cs             the coordinate system to verify.
     * @param abbreviations  the expected abbreviations. Null elements are considered unrestricted.
     */
    private static void verifyAxisAbbreviations(final CoordinateSystem cs, final String... abbreviations) {
        final int dimension = Math.min(abbreviations.length, cs.getDimension());
        for (int i=0; i<dimension; i++) {
            final String expected = abbreviations[i];
            if (expected != null) {
                assertEquals("CoordinateSystemAxis.getAbbreviation()", expected, cs.getAxis(i).getAbbreviation());
            }
        }
    }

    /**
     * Asserts the the given character sequence is either null or equals to the given value.
     * This is used for optional elements like remarks.
     *
     * @param property  the property being tested, for producing a message in case of assertion failure.
     * @param expected  the expected value.
     * @param actual    the actual value.
     */
    private static void assertNullOrEquals(final String property, final String expected, final CharSequence actual) {
        if (actual != null) {
            assertEquals(property, expected, actual.toString());
        }
    }

    /**
     * Pre-process the WKT string before parsing.
     * The default implementation performs the following changes for strict ISO 19162 compliance:
     *
     * <ul>
     *   <li>Double the straight quotation marks {@code "} (U+0022).</li>
     *   <li>Replace the left quotation marks {@code “} (U+201C) and right quotation marks {@code ”} (U+201D)
     *       by straight quotation marks {@code "} (U+0022).</li>
     * </ul>
     *
     * Subclasses can override this method if they wish to perform additional pre-processing.
     * The use of left and right quotation marks is intended to make easier for subclasses to
     * identify the beginning and end of quoted texts.
     *
     * @param  wkt  the Well-Known Text to pre-process.
     * @return the Well-Known Text to parse.
     */
    protected String preprocessWKT(final String wkt) {
        final StringBuilder b = new StringBuilder(wkt);
        for (int i = wkt.lastIndexOf('"'); i >= 0; i = wkt.lastIndexOf('"', i-1)) {
            b.insert(i, '"');
        }
        for (int i=0; i<b.length(); i++) {
            final char c = b.charAt(i);
            if (c == '“' || c == '”') {
                b.setCharAt(i, '"');
            }
        }
        return b.toString();
    }

    /**
     * Parses the given WKT.
     *
     * @param  type  the expected object type.
     * @param  text  the WKT string to parse.
     * @return the parsed object.
     * @throws FactoryException if an error occurred during the WKT parsing.
     */
    private <T extends CoordinateReferenceSystem> T parse(final Class<T> type, final String text) throws FactoryException {
        assumeTrue("No CRSFactory.", crsFactory != null);
        object = crsFactory.createFromWKT(preprocessWKT(text));
        assertInstanceOf("CRSFactory.createFromWKT(String)", type, object);
        return type.cast(object);
    }

    /**
     * Parses a three-dimensional geodetic CRS.
     * The WKT parsed by this test is (except for quote characters):
     *
     * <blockquote><pre>GEODCRS[“WGS 84”,
     *  DATUM[“World Geodetic System 1984”,
     *    ELLIPSOID[“WGS 84”, 6378137, 298.257223563,
     *      LENGTHUNIT[“metre”,1.0]]],
     *  CS[ellipsoidal,3],
     *    AXIS[“(lat)”,north,ANGLEUNIT[“degree”,0.0174532925199433]],
     *    AXIS[“(lon)”,east,ANGLEUNIT[“degree”,0.0174532925199433]],
     *    AXIS[“ellipsoidal height (h)”,up,LENGTHUNIT[“metre”,1.0]]]</pre></blockquote>
     *
     * @throws FactoryException if an error occurred during the WKT parsing.
     *
     * @see <a href="http://docs.opengeospatial.org/is/12-063r5/12-063r5.html#56">OGC 12-063r5 §8.4 example 2</a>
     */
    @Test
    public void testGeographic3D() throws FactoryException {
        final GeodeticCRS crs = parse(GeodeticCRS.class,
                "GEODCRS[“WGS 84”,\n" +
                "  DATUM[“World Geodetic System 1984”,\n" +
                "    ELLIPSOID[“WGS 84”, 6378137, 298.257223563,\n" +
                "      LENGTHUNIT[“metre”,1.0]]],\n" +
                "  CS[ellipsoidal,3],\n" +
                "    AXIS[“(lat)”,north,ANGLEUNIT[“degree”,0.0174532925199433]],\n" +
                "    AXIS[“(lon)”,east,ANGLEUNIT[“degree”,0.0174532925199433]],\n" +
                "    AXIS[“ellipsoidal height (h)”,up,LENGTHUNIT[“metre”,1.0]]]");

        if (isValidationEnabled) {
            configurationTip = Configuration.Key.isValidationEnabled;
            validators.validate(crs);
            configurationTip = null;
        }
        verifyWGS84(crs, true, units.degree(), units.metre());
        verifyAxisAbbreviations(crs.getCoordinateSystem(), null, null, "h");
    }

    /**
     * Verifies the CRS name, datum and axes for {@code GEODCRS[“WGS 84”]}.
     * This method does not verify axis abbreviations.
     *
     * @param  crs     the Coordinate Reference System which is expected to be WGS 84.
     * @param  is3D    whether the CRS contains an ellipsoidal height axis.
     * @param  degree  value of {@link org.opengis.test.Units#degree()} (for fetching it only once per test).
     * @param  metre   value of {@link org.opengis.test.Units#metre()}  (for fetching it only once per test).
     */
    private void verifyWGS84(final GeodeticCRS crs, final boolean is3D,
            final Unit<Angle> degree, final Unit<Length> metre)
    {
        final GeodeticDatum   datum;
        final AxisDirection[] directions;

        verifyIdentification (crs, "WGS 84", null);
        verifyDatum          (datum = crs.getDatum(), "World Geodetic System 1984");
        verifyFlattenedSphere(datum.getEllipsoid(), "WGS 84", 6378137, 298.257223563, metre);
        verifyPrimeMeridian  (datum.getPrimeMeridian(), null, 0, degree);
        directions = new AxisDirection[is3D ? 3 : 2];
        directions[0] = NORTH;
        directions[1] = EAST;
        if (is3D) {
            directions[2] = UP;
        }
        verifyCoordinateSystem(crs.getCoordinateSystem(), EllipsoidalCS.class, directions, degree, degree, metre);
    }

    /**
     * Parses a geodetic CRS which contain a remark written using non-ASCII characters.
     * The WKT parsed by this test is (except for quote characters):
     *
     * <blockquote><pre>GEODCRS[“S-95”,
     *  DATUM[“Pulkovo 1995”,
     *    ELLIPSOID[“Krassowsky 1940”, 6378245, 298.3,
     *      LENGTHUNIT[“metre”,1.0]]],
     *  CS[ellipsoidal,2],
     *    AXIS[“latitude”,north,ORDER[1]],
     *    AXIS[“longitude”,east,ORDER[2]],
     *    ANGLEUNIT[“degree”,0.0174532925199433],
     *  REMARK[“Система Геодеэических Координвт года 1995(СК-95)”]]</pre></blockquote>
     *
     * @throws FactoryException if an error occurred during the WKT parsing.
     *
     * @see <a href="http://docs.opengeospatial.org/is/12-063r5/12-063r5.html#34">OGC 12-063r5 §7.3.5 example 3</a>
     */
    @Test
    public void testGeographicWithUnicode() throws FactoryException {
        final GeodeticCRS crs = parse(GeodeticCRS.class,
                "GEODCRS[“S-95”,\n" +
                "  DATUM[“Pulkovo 1995”,\n" +
                "    ELLIPSOID[“Krassowsky 1940”, 6378245, 298.3,\n" +
                "      LENGTHUNIT[“metre”,1.0]]],\n" +
                "  CS[ellipsoidal,2],\n" +
                "    AXIS[“latitude”,north,ORDER[1]],\n" +
                "    AXIS[“longitude”,east,ORDER[2]],\n" +
                "    ANGLEUNIT[“degree”,0.0174532925199433],\n" +
                "  REMARK[“Система Геодеэических Координвт года 1995(СК-95)”]]");

        if (isValidationEnabled) {
            configurationTip = Configuration.Key.isValidationEnabled;
            validators.validate(crs);
            configurationTip = null;
        }
        final Unit<Angle> degree = units.degree();
        final Unit<Length> metre = units.metre();
        final GeodeticDatum datum;

        verifyIdentification  (crs, "S-95", null);
        verifyDatum           (datum = crs.getDatum(), "Pulkovo 1995");
        verifyFlattenedSphere (datum.getEllipsoid(), "Krassowsky 1940", 6378245, 298.3, metre);
        verifyPrimeMeridian   (datum.getPrimeMeridian(), null, 0, degree);
        verifyCoordinateSystem(crs.getCoordinateSystem(), EllipsoidalCS.class, new AxisDirection[] {NORTH,EAST}, degree);
        assertNullOrEquals("remark", "Система Геодеэических Координвт года 1995(СК-95)", crs.getRemarks());
    }

    /**
     * Parses a geodetic CRS which contains a remark and an identifier.
     * The WKT parsed by this test is (except for quote characters):
     *
     * <blockquote><pre>GEODCRS[“NAD83”,
     *  DATUM[“North American Datum 1983”,
     *    ELLIPSOID[“GRS 1980”, 6378137, 298.257222101, LENGTHUNIT[“metre”,1.0]]],
     *  CS[ellipsoidal,2],
     *    AXIS[“latitude”,north],
     *    AXIS[“longitude”,east],
     *    ANGLEUNIT[“degree”,0.0174532925199433],
     *  ID[“EPSG”,4269],
     *  REMARK[“1986 realisation”]]</pre></blockquote>
     *
     * @throws FactoryException if an error occurred during the WKT parsing.
     *
     * @see <a href="http://docs.opengeospatial.org/is/12-063r5/12-063r5.html#56">OGC 12-063r5 §8.4 example 3</a>
     */
    @Test
    public void testGeographicWithIdentifier() throws FactoryException {
        final GeodeticCRS crs = parse(GeodeticCRS.class,
                "GEODCRS[“NAD83”,\n" +
                "  DATUM[“North American Datum 1983”,\n" +
                "    ELLIPSOID[“GRS 1980”, 6378137, 298.257222101, LENGTHUNIT[“metre”,1.0]]],\n" +
                "  CS[ellipsoidal,2],\n" +
                "    AXIS[“latitude”,north],\n" +
                "    AXIS[“longitude”,east],\n" +
                "    ANGLEUNIT[“degree”,0.0174532925199433],\n" +
                "  ID[“EPSG”,4269],\n" +
                "  REMARK[“1986 realisation”]]");

        if (isValidationEnabled) {
            configurationTip = Configuration.Key.isValidationEnabled;
            validators.validate(crs);
            configurationTip = null;
        }
        verifyNAD23(crs, true, units.degree(), units.metre());
        assertNullOrEquals("remark", "1986 realisation", crs.getRemarks());
    }

    /**
     * Verifies the CRS name, datum and axes for {@code GEODCRS[“NAD83”]}.
     * This method does not verify the remark, since it is not included in the components of {@code COMPOUNDCRS[…]}.
     *
     * @param  degree  value of {@link org.opengis.test.Units#degree()} (for fetching it only once per test).
     * @param  metre   value of {@link org.opengis.test.Units#metre()}  (for fetching it only once per test).
     */
    private void verifyNAD23(final GeodeticCRS crs, final boolean hasIdentifier,
            final Unit<Angle> degree, final Unit<Length> metre)
    {
        final GeodeticDatum datum;

        verifyIdentification  (crs, "NAD83", hasIdentifier ? "4269" : null);
        verifyDatum           (datum = crs.getDatum(), "North American Datum 1983");
        verifyFlattenedSphere (datum.getEllipsoid(), "GRS 1980", 6378137, 298.257222101, metre);
        verifyPrimeMeridian   (datum.getPrimeMeridian(), null, 0, degree);
        verifyCoordinateSystem(crs.getCoordinateSystem(), EllipsoidalCS.class, new AxisDirection[] {NORTH,EAST}, degree);
    }

    /**
     * Parses a geodetic CRS with a prime meridian other than Greenwich and all angular units in grads.
     * The WKT parsed by this test is (except for quote characters):
     *
     * <blockquote><pre>GEODCRS[“NTF (Paris)”,
     *  DATUM[“Nouvelle Triangulation Francaise”,
     *    ELLIPSOID[“Clarke 1880 (IGN)”, 6378249.2, 293.4660213]],
     *  PRIMEM[“Paris”,2.5969213],
     *  CS[ellipsoidal,2],
     *    AXIS[“latitude”,north,ORDER[1]],
     *    AXIS[“longitude”,east,ORDER[2]],
     *    ANGLEUNIT[“grad”,0.015707963267949],
     *  REMARK[“Nouvelle Triangulation Française”]]</pre></blockquote>
     *
     * @throws FactoryException if an error occurred during the WKT parsing.
     *
     * @see <a href="http://docs.opengeospatial.org/is/12-063r5/12-063r5.html#56">OGC 12-063r5 §8.4 example 4</a>
     */
    @Test
    public void testGeographicWithGradUnits() throws FactoryException {
        final GeodeticCRS crs = parse(GeodeticCRS.class,
                "GEODCRS[“NTF (Paris)”,\n" +
                "  DATUM[“Nouvelle Triangulation Francaise”,\n" +
                "    ELLIPSOID[“Clarke 1880 (IGN)”, 6378249.2, 293.4660213]],\n" +
                "  PRIMEM[“Paris”,2.5969213],\n" +
                "  CS[ellipsoidal,2],\n" +
                "    AXIS[“latitude”,north,ORDER[1]],\n" +
                "    AXIS[“longitude”,east,ORDER[2]],\n" +
                "    ANGLEUNIT[“grad”,0.015707963267949],\n" +
                "  REMARK[“Nouvelle Triangulation Française”]]");

        if (isValidationEnabled) {
            configurationTip = Configuration.Key.isValidationEnabled;
            validators.validate(crs);
            configurationTip = null;
        }
        final Unit<Angle>  grad  = units.grad();
        final Unit<Length> metre = units.metre();
        final GeodeticDatum datum;

        verifyIdentification  (crs, "NTF (Paris)", null);
        verifyDatum           (datum = crs.getDatum(), "Nouvelle Triangulation Francaise");
        verifyFlattenedSphere (datum.getEllipsoid(), "Clarke 1880 (IGN)", 6378249.2, 293.4660213, metre);
        verifyPrimeMeridian   (datum.getPrimeMeridian(), "Paris", 2.5969213, grad);
        verifyCoordinateSystem(crs.getCoordinateSystem(), EllipsoidalCS.class, new AxisDirection[] {NORTH,EAST}, grad);
        assertNullOrEquals("remark", "Nouvelle Triangulation Française", crs.getRemarks());
    }

    /**
     * Parses a geodetic CRS with Cartesian coordinate system.
     * The WKT parsed by this test is (except for quote characters):
     *
     * <blockquote><pre>GEODETICCRS[“JGD2000”,
     *  DATUM[“Japanese Geodetic Datum 2000”,
     *    ELLIPSOID[“GRS 1980”, 6378137, 298.257222101]],
     *  CS[Cartesian,3],
     *    AXIS[“(X)”,geocentricX],
     *    AXIS[“(Y)”,geocentricY],
     *    AXIS[“(Z)”,geocentricZ],
     *    LENGTHUNIT[“metre”,1.0],
     *  SCOPE[“Geodesy, topographic mapping and cadastre”],
     *  AREA[“Japan”],
     *  BBOX[17.09,122.38,46.05,157.64],
     *  TIMEEXTENT[2002-04-01,2011-10-21],
     *  ID[“EPSG”,4946,URI[“urn:ogc:def:crs:EPSG::4946”]],
     *  REMARK[“注：JGD2000ジオセントリックは現在JGD2011に代わりました。”]]</pre></blockquote>
     *
     * @throws FactoryException if an error occurred during the WKT parsing.
     *
     * @see <a href="http://docs.opengeospatial.org/is/12-063r5/12-063r5.html#56">OGC 12-063r5 §8.4 example 1</a>
     */
    @Test
    public void testGeocentric() throws FactoryException {
        final GeodeticCRS crs = parse(GeodeticCRS.class,
                "GEODETICCRS[“JGD2000”,\n" +
                "  DATUM[“Japanese Geodetic Datum 2000”,\n" +
                "    ELLIPSOID[“GRS 1980”, 6378137, 298.257222101]],\n" +
                "  CS[Cartesian,3],\n" +
                "    AXIS[“(X)”,geocentricX],\n" +
                "    AXIS[“(Y)”,geocentricY],\n" +
                "    AXIS[“(Z)”,geocentricZ],\n" +
                "    LENGTHUNIT[“metre”,1.0],\n" +
                "  SCOPE[“Geodesy, topographic mapping and cadastre”],\n" +
                "  AREA[“Japan”],\n" +
                "  BBOX[17.09,122.38,46.05,157.64],\n" +
                "  TIMEEXTENT[2002-04-01,2011-10-21],\n" +
                "  ID[“EPSG”,4946,URI[“urn:ogc:def:crs:EPSG::4946”]],\n" +
                "  REMARK[“注：JGD2000ジオセントリックは現在JGD2011に代わりました。”]]");

        if (isValidationEnabled) {
            configurationTip = Configuration.Key.isValidationEnabled;
            validators.validate(crs);
            configurationTip = null;
        }
        final Unit<Angle> degree = units.degree();
        final Unit<Length> metre = units.metre();
        final GeodeticDatum datum;
        final CoordinateSystem cs;
        final Extent extent;

        verifyIdentification   (crs, "JGD2000", "4946");
        verifyDatum            (datum = crs.getDatum(), "Japanese Geodetic Datum 2000");
        verifyFlattenedSphere  (datum.getEllipsoid(), "GRS 1980", 6378137, 298.257222101, metre);
        verifyPrimeMeridian    (datum.getPrimeMeridian(), null, 0, degree);
        verifyAxisAbbreviations(cs = crs.getCoordinateSystem(), "X", "Y", "Z");
        verifyCoordinateSystem (cs, CartesianCS.class, new AxisDirection[] {GEOCENTRIC_X, GEOCENTRIC_Y, GEOCENTRIC_Z}, metre);
        verifyGeographicExtent (extent = crs.getDomainOfValidity(), "Japan", 17.09, 122.38, 46.05, 157.64);
        verifyTimeExtent       (extent, new Date(1017619200000L), new Date(1319155200000L), 1);
        assertNullOrEquals("scope", "Geodesy, topographic mapping and cadastre", crs.getScope());
        assertNullOrEquals("remark", "注：JGD2000ジオセントリックは現在JGD2011に代わりました。", crs.getRemarks());
    }

    /**
     * Parses a projected CRS with linear units in metres and axes in (<var>Y</var>,<var>X</var>) order.
     * The WKT parsed by this test is (except for quote characters):
     *
     * <blockquote><pre>PROJCRS[“ETRS89 Lambert Azimuthal Equal Area CRS”,
     *  BASEGEODCRS[“ETRS89”,
     *    DATUM[“ETRS89”,
     *      ELLIPSOID[“GRS 80”, 6378137, 298.257222101, LENGTHUNIT[“metre”,1.0]]]],
     *  CONVERSION[“LAEA”,
     *    METHOD[“Lambert Azimuthal Equal Area”,ID[“EPSG”,9820]],
     *    PARAMETER[“Latitude of natural origin”,  52.0, ANGLEUNIT[“degree”,0.0174532925199433]],
     *    PARAMETER[“Longitude of natural origin”, 10.0, ANGLEUNIT[“degree”,0.0174532925199433]],
     *    PARAMETER[“False easting”,  4321000.0, LENGTHUNIT[“metre”,1.0]],
     *    PARAMETER[“False northing”, 3210000.0, LENGTHUNIT[“metre”,1.0]]],
     *  CS[Cartesian,2],
     *    AXIS[“(Y)”,north,ORDER[1]],
     *    AXIS[“(X)”,east,ORDER[2]],
     *    LENGTHUNIT[“metre”,1.0],
     *  SCOPE[“Description of a purpose”],
     *  AREA[“An area description”],
     *  ID[“EuroGeographics”,“ETRS-LAEA”]]</pre></blockquote>
     *
     * @throws FactoryException if an error occurred during the WKT parsing.
     *
     * @see <a href="http://docs.opengeospatial.org/is/12-063r5/12-063r5.html#68">OGC 12-063r5 §9.5 example 1</a>
     */
    @Test
    public void testProjectedYX() throws FactoryException {
        final ProjectedCRS crs = parse(ProjectedCRS.class,
                "PROJCRS[“ETRS89 Lambert Azimuthal Equal Area CRS”,\n" +
                "  BASEGEODCRS[“ETRS89”,\n" +
                "    DATUM[“ETRS89”,\n" +
                "      ELLIPSOID[“GRS 80”, 6378137, 298.257222101, LENGTHUNIT[“metre”,1.0]]]],\n" +
                "  CONVERSION[“LAEA”,\n" +
                "    METHOD[“Lambert Azimuthal Equal Area”,ID[“EPSG”,9820]],\n" +
                "    PARAMETER[“Latitude of natural origin”,  52.0, ANGLEUNIT[“degree”,0.0174532925199433]],\n" +
                "    PARAMETER[“Longitude of natural origin”, 10.0, ANGLEUNIT[“degree”,0.0174532925199433]],\n" +
                "    PARAMETER[“False easting”,  4321000.0, LENGTHUNIT[“metre”,1.0]],\n" +
                "    PARAMETER[“False northing”, 3210000.0, LENGTHUNIT[“metre”,1.0]]],\n" +
                "  CS[Cartesian,2],\n" +
                "    AXIS[“(Y)”,north,ORDER[1]],\n" +
                "    AXIS[“(X)”,east,ORDER[2]],\n" +
                "    LENGTHUNIT[“metre”,1.0],\n" +
                "  SCOPE[“Description of a purpose”],\n" +
                "  AREA[“An area description”],\n" +
                "  ID[“EuroGeographics”,“ETRS-LAEA”]]");

        if (isValidationEnabled) {
            configurationTip = Configuration.Key.isValidationEnabled;
            validators.validate(crs);
            configurationTip = null;
        }
        final Unit<Angle> degree = units.degree();
        final Unit<Length> metre = units.metre();
        final GeodeticDatum datum;
        final CoordinateSystem cs;

        verifyIdentification   (crs, "ETRS89 Lambert Azimuthal Equal Area CRS", "ETRS-LAEA");
        verifyIdentification   (crs.getBaseCRS(), "ETRS89", null);
        verifyIdentification   (crs.getConversionFromBase(), "LAEA", null);
        verifyDatum            (datum = crs.getDatum(), "ETRS89");
        verifyFlattenedSphere  (datum.getEllipsoid(), "GRS 80", 6378137, 298.257222101, metre);
        verifyPrimeMeridian    (datum.getPrimeMeridian(), null, 0, degree);
        verifyAxisAbbreviations(cs = crs.getCoordinateSystem(), "Y", "X");
        verifyCoordinateSystem (cs, CartesianCS.class, new AxisDirection[] {NORTH,EAST}, metre);

        final ParameterValueGroup group = crs.getConversionFromBase().getParameterValues();
        verifyParameter(group, "Latitude of natural origin",  52.0, degree);
        verifyParameter(group, "Longitude of natural origin", 10.0, degree);
        verifyParameter(group, "False easting",          4321000.0, metre);
        verifyParameter(group, "False northing",         3210000.0, metre);

        verifyGeographicExtent(crs.getDomainOfValidity(), "An area description", NaN, NaN, NaN, NaN);
        assertNullOrEquals("scope", "Description of a purpose", crs.getScope());
    }

    /**
     * Parses a projected CRS with linear units in feet.
     * The WKT parsed by this test is (except for quote characters):
     *
     * <blockquote><pre>PROJCRS[“NAD27 / Texas South Central”,
     *  BASEGEODCRS[“NAD27”,
     *    DATUM[“North American Datum 1927”,
     *      ELLIPSOID[“Clarke 1866”, 20925832.164, 294.97869821,
     *        LENGTHUNIT[“US survey foot”,0.304800609601219]]]],
     *  CONVERSION[“Texas South Central SPCS27”,
     *    METHOD[“Lambert Conic Conformal (2SP)”,ID[“EPSG”,9802]],
     *    PARAMETER[“Latitude of false origin”,27.83333333333333,
     *      ANGLEUNIT[“degree”,0.0174532925199433],ID[“EPSG”,8821]],
     *    PARAMETER[“Longitude of false origin”,-99.0,
     *      ANGLEUNIT[“degree”,0.0174532925199433],ID[“EPSG”,8822]],
     *    PARAMETER[“Latitude of 1st standard parallel”,28.383333333333,
     *      ANGLEUNIT[“degree”,0.0174532925199433],ID[“EPSG”,8823]],
     *    PARAMETER[“Latitude of 2nd standard parallel”,30.283333333333,
     *      ANGLEUNIT[“degree”,0.0174532925199433],ID[“EPSG”,8824]],
     *    PARAMETER[“Easting at false origin”,2000000.0,
     *      LENGTHUNIT[“US survey foot”,0.304800609601219],ID[“EPSG”,8826]],
     *    PARAMETER[“Northing at false origin”,0.0,
     *      LENGTHUNIT[“US survey foot”,0.304800609601219],ID[“EPSG”,8827]]],
     *  CS[Cartesian,2],
     *    AXIS[“(x)”,east],
     *    AXIS[“(y)”,north],
     *    LENGTHUNIT[“US survey foot”,0.304800609601219],
     *  REMARK[“Fundamental point: Meade’s Ranch KS, latitude 39°13'26.686"N, longitude 98°32'30.506"W.”]]</pre></blockquote>
     *
     * @throws FactoryException if an error occurred during the WKT parsing.
     *
     * @see <a href="http://docs.opengeospatial.org/is/12-063r5/12-063r5.html#68">OGC 12-063r5 §9.5 example 2</a>
     */
    @Test
    public void testProjectedWithFootUnits() throws FactoryException {
        final ProjectedCRS crs = parse(ProjectedCRS.class,
                "PROJCRS[“NAD27 / Texas South Central”,\n" +
                "  BASEGEODCRS[“NAD27”,\n" +
                "    DATUM[“North American Datum 1927”,\n" +
                "      ELLIPSOID[“Clarke 1866”, 20925832.164, 294.97869821,\n" +
                "        LENGTHUNIT[“US survey foot”,0.304800609601219]]]],\n" +
                "  CONVERSION[“Texas South Central SPCS27”,\n" +
                "    METHOD[“Lambert Conic Conformal (2SP)”,ID[“EPSG”,9802]],\n" +
                "    PARAMETER[“Latitude of false origin”,27.83333333333333,\n" +
                "      ANGLEUNIT[“degree”,0.0174532925199433],ID[“EPSG”,8821]],\n" +
                "    PARAMETER[“Longitude of false origin”,-99.0,\n" +
                "      ANGLEUNIT[“degree”,0.0174532925199433],ID[“EPSG”,8822]],\n" +
                "    PARAMETER[“Latitude of 1st standard parallel”,28.383333333333,\n" +
                "      ANGLEUNIT[“degree”,0.0174532925199433],ID[“EPSG”,8823]],\n" +
                "    PARAMETER[“Latitude of 2nd standard parallel”,30.283333333333,\n" +
                "      ANGLEUNIT[“degree”,0.0174532925199433],ID[“EPSG”,8824]],\n" +
                "    PARAMETER[“Easting at false origin”,2000000.0,\n" +
                "      LENGTHUNIT[“US survey foot”,0.304800609601219],ID[“EPSG”,8826]],\n" +
                "    PARAMETER[“Northing at false origin”,0.0,\n" +
                "      LENGTHUNIT[“US survey foot”,0.304800609601219],ID[“EPSG”,8827]]],\n" +
                "  CS[Cartesian,2],\n" +
                "    AXIS[“(X)”,east],\n" +
                "    AXIS[“(Y)”,north],\n" +
                "    LENGTHUNIT[“US survey foot”,0.304800609601219],\n" +
                "  REMARK[“Fundamental point: Meade’s Ranch KS, latitude 39°13'26.686\"N, longitude 98°32'30.506\"W.”]]");

        if (isValidationEnabled) {
            configurationTip = Configuration.Key.isValidationEnabled;
            validators.validate(crs);
            configurationTip = null;
        }
        final Unit<Angle>  degree       = units.degree();
        final Unit<Length> footSurveyUS = units.footSurveyUS();
        final CoordinateSystem cs;

        verifyAxisAbbreviations(cs = crs.getCoordinateSystem(), "X", "Y");
        verifyCoordinateSystem (cs, CartesianCS.class, new AxisDirection[] {EAST,NORTH}, footSurveyUS);
        verifyTexasSouthCentral(crs, degree, footSurveyUS);
        assertNullOrEquals("remark", "Fundamental point: Meade’s Ranch KS, latitude 39°13'26.686\"N, longitude 98°32'30.506\"W.", crs.getRemarks());
    }

    /**
     * Verifies the CRS name, datum and conversion parameters for {@code PROJCRS[“NAD27 / Texas South Central”]}.
     * This method does not verify the axes and remark, since they are not specified in {@code BASEPROJCRS[…]}.
     *
     * @param  degree        value of {@link org.opengis.test.Units#degree()} (for fetching it only once per test).
     * @param  footSurveyUS  value of {@link org.opengis.test.Units#footSurveyUS()}.
     */
    private void verifyTexasSouthCentral(final ProjectedCRS crs,
            final Unit<Angle> degree, final Unit<Length> footSurveyUS)
    {
        final GeodeticDatum datum;

        verifyIdentification   (crs, "NAD27 / Texas South Central", null);
        verifyIdentification   (crs.getBaseCRS(), "NAD27", null);
        verifyIdentification   (crs.getConversionFromBase(), "Texas South Central SPCS27", null);
        verifyDatum            (datum = crs.getDatum(), "North American Datum 1927");
        verifyFlattenedSphere  (datum.getEllipsoid(), "Clarke 1866", 20925832.164, 294.97869821, footSurveyUS);
        verifyPrimeMeridian    (datum.getPrimeMeridian(), null, 0, degree);

        final ParameterValueGroup group = crs.getConversionFromBase().getParameterValues();
        verifyParameter(group, "Latitude of false origin",          27.83333333333333, degree);
        verifyParameter(group, "Longitude of false origin",        -99.0,              degree);
        verifyParameter(group, "Latitude of 1st standard parallel", 28.383333333333,   degree);
        verifyParameter(group, "Latitude of 2nd standard parallel", 30.283333333333,   degree);
        verifyParameter(group, "Easting at false origin",           2000000.0,         footSurveyUS);
        verifyParameter(group, "Northing at false origin",          0.0,               footSurveyUS);
    }

    /**
     * Parses a projected CRS with implicit parameter units.
     * The WKT parsed by this test is (except for quote characters and the line feed in {@code REMARK}):
     *
     * <blockquote><pre>PROJCRS[“NAD83 UTM 10”,
     *  BASEGEODCRS[“NAD83(86)”,
     *    DATUM[“North American Datum 1983”,
     *      ELLIPSOID[“GRS 1980”,6378137,298.257222101]],
     *    ANGLEUNIT[“degree”,0.0174532925199433],
     *    PRIMEM[“Greenwich”,0]],
     *  CONVERSION[“UTM zone 10N”,ID[“EPSG”,16010],
     *    METHOD[“Transverse Mercator”],
     *    PARAMETER[“Latitude of natural origin”,0.0],
     *    PARAMETER[“Longitude of natural origin”,-123.0],
     *    PARAMETER[“Scale factor”,0.9996],
     *    PARAMETER[“False easting”,500000.0],
     *    PARAMETER[“False northing”,0.0]],
     *  CS[Cartesian,2],
     *    AXIS[“(E)”,east,ORDER[1]],
     *    AXIS[“(N)”,north,ORDER[2]],
     *    LENGTHUNIT[“metre”,1.0],
     *  REMARK[“In this example units are implied. This is allowed for backward compatibility.
     *          It is recommended that units are explicitly given in the string,
     *          as in the previous two examples.”]]</pre></blockquote>
     *
     * @throws FactoryException if an error occurred during the WKT parsing.
     *
     * @see <a href="http://docs.opengeospatial.org/is/12-063r5/12-063r5.html#68">OGC 12-063r5 §9.5 example 3</a>
     */
    @Test
    public void testProjectedWithImplicitParameterUnits() throws FactoryException {
        final ProjectedCRS crs = parse(ProjectedCRS.class,
                "PROJCRS[“NAD83 UTM 10”,\n" +
                "  BASEGEODCRS[“NAD83(86)”,\n" +
                "    DATUM[“North American Datum 1983”,\n" +
                "      ELLIPSOID[“GRS 1980”, 6378137, 298.257222101]],\n" +
                "    ANGLEUNIT[“degree”,0.0174532925199433],\n" +
                "    PRIMEM[“Greenwich”,0]],\n" +
                "  CONVERSION[“UTM zone 10N”,ID[“EPSG”,16010],\n" +
                "    METHOD[“Transverse Mercator”],\n" +
                "    PARAMETER[“Latitude of natural origin”,0.0],\n" +
                "    PARAMETER[“Longitude of natural origin”,-123.0],\n" +
                "    PARAMETER[“Scale factor”,0.9996],\n" +
                "    PARAMETER[“False easting”,500000.0],\n" +
                "    PARAMETER[“False northing”,0.0]],\n" +
                "  CS[Cartesian,2],\n" +
                "    AXIS[“(E)”,east,ORDER[1]],\n" +
                "    AXIS[“(N)”,north,ORDER[2]],\n" +
                "    LENGTHUNIT[“metre”,1.0],\n" +
                "  REMARK[“In this example units are implied. This is allowed for backward compatibility." +
                         " It is recommended that units are explicitly given in the string," +
                         " as in the previous two examples.”]]");

        if (isValidationEnabled) {
            configurationTip = Configuration.Key.isValidationEnabled;
            validators.validate(crs);
            configurationTip = null;
        }
        final Unit<Angle> degree = units.degree();
        final Unit<Length> metre = units.metre();
        final GeodeticDatum datum;
        final CoordinateSystem cs;

        verifyIdentification   (crs, "NAD83 UTM 10", null);
        verifyIdentification   (crs.getBaseCRS(), "NAD83(86)", null);
        verifyIdentification   (crs.getConversionFromBase(), "UTM zone 10N", "16010");
        verifyDatum            (datum = crs.getDatum(), "North American Datum 1983");
        verifyFlattenedSphere  (datum.getEllipsoid(), "GRS 1980", 6378137, 298.257222101, metre);
        verifyPrimeMeridian    (datum.getPrimeMeridian(), null, 0, degree);
        verifyAxisAbbreviations(cs = crs.getCoordinateSystem(), "E", "N");
        verifyCoordinateSystem (cs, CartesianCS.class, new AxisDirection[] {EAST,NORTH}, metre);

        final ParameterValueGroup group = crs.getConversionFromBase().getParameterValues();
        verifyParameter(group, "Latitude of natural origin",     0.0, degree);
        verifyParameter(group, "Longitude of natural origin", -123.0, degree);
        verifyParameter(group, "Scale factor",                0.9996, units.one());
        verifyParameter(group, "False easting",             500000.0, metre);
        verifyParameter(group, "False northing",                 0.0, metre);
    }

    /**
     * Parses a vertical CRS.
     * The WKT parsed by this test is (except for quote characters):
     *
     * <blockquote><pre>VERTCRS[“NAVD88”,
     *  VDATUM[“North American Vertical Datum 1988”],
     *  CS[vertical,1],
     *    AXIS[“gravity-related height (H)”,up],LENGTHUNIT[“metre”,1.0]]</pre></blockquote>
     *
     * @throws FactoryException if an error occurred during the WKT parsing.
     *
     * @see <a href="http://docs.opengeospatial.org/is/12-063r5/12-063r5.html#73">OGC 12-063r5 §10.4</a>
     */
    @Test
    public void testVertical() throws FactoryException {
        final VerticalCRS crs = parse(VerticalCRS.class,
                "VERTCRS[“NAVD88”,\n" +
                "  VDATUM[“North American Vertical Datum 1988”],\n" +
                "  CS[vertical,1],\n" +
                "    AXIS[“gravity-related height (H)”,up],LENGTHUNIT[“metre”,1.0]]");

        if (isValidationEnabled) {
            configurationTip = Configuration.Key.isValidationEnabled;
            validators.validate(crs);
            configurationTip = null;
        }
        verifyNAD28(crs, units.metre());
    }

    /**
     * Verifies the CRS name, datum and axis for {@code VERTCRS[“NAD88”]}.
     *
     * @param  metre  value of {@link org.opengis.test.Units#metre()}  (for fetching it only once per test).
     */
    private void verifyNAD28(final VerticalCRS crs, final Unit<Length> metre) {
        verifyIdentification(crs, "NAVD88", null);
        verifyDatum(crs.getDatum(), "North American Vertical Datum 1988");
        verifyCoordinateSystem (crs.getCoordinateSystem(), VerticalCS.class, new AxisDirection[] {UP}, metre);
        verifyAxisAbbreviations(crs.getCoordinateSystem(), "H");
    }

    /**
     * Parses a temporal CRS.
     * The WKT parsed by this test is (except for quote characters):
     *
     * <blockquote><pre>TIMECRS[“GPS Time”,
     *   TDATUM[“Time origin”,TIMEORIGIN[1980-01-01T00:00:00.0Z]],
     *   CS[temporal,1],AXIS[“time”,future],TIMEUNIT[“day”,86400.0]]</pre></blockquote>
     *
     * @throws FactoryException if an error occurred during the WKT parsing.
     *
     * @see <a href="http://docs.opengeospatial.org/is/12-063r5/12-063r5.html#92">OGC 12-063r5 §14.4</a>
     */
    @Test
    public void testTemporal() throws FactoryException {
        final TemporalCRS crs = parse(TemporalCRS.class,
                "TIMECRS[“GPS Time”,\n" +
                "  TDATUM[“Time origin”,TIMEORIGIN[1980-01-01T00:00:00.0Z]],\n" +
                "  CS[temporal,1],AXIS[“time”,future],TIMEUNIT[“day”,86400.0]]");

        if (isValidationEnabled) {
            configurationTip = Configuration.Key.isValidationEnabled;
            validators.validate(crs);
            configurationTip = null;
        }
        verifyGPSTime(crs);
    }

    /**
     * Verifies the CRS name, datum and axis for {@code TIMECRS[“GPS Time”]}.
     */
    private void verifyGPSTime(final TemporalCRS crs) {
        verifyIdentification   (crs, "GPS Time", null);
        verifyDatum            (crs.getDatum(), "Time origin");
        verifyCoordinateSystem (crs.getCoordinateSystem(), TimeCS.class, new AxisDirection[] {FUTURE}, units.day());
        assertEquals("TimeOrigin", new Date(315532800000L), crs.getDatum().getOrigin());
    }

    /**
     * Parses a parametric CRS.
     * The WKT parsed by this test is (except for quote characters):
     *
     * <blockquote><pre>PARAMETRICCRS[“WMO standard atmosphere layer 0”,
     *   PDATUM[“Mean Sea Level”,ANCHOR[“1013.25 hPa at 15°C”]],
     *   CS[parametric,1],
     *   AXIS[“pressure (hPa)”,up],
     *   PARAMETRICUNIT[“hPa”,100.0]]</pre></blockquote>
     *
     * @throws FactoryException if an error occurred during the WKT parsing.
     *
     * @see <a href="http://docs.opengeospatial.org/is/12-063r5/12-063r5.html#87">OGC 12-063r5 §13.4 example 1</a>
     */
    @Test
    public void testParametric() throws FactoryException {
        final ParametricCRS crs = parse(ParametricCRS.class,
                "PARAMETRICCRS[“WMO standard atmosphere layer 0”,\n" +
                "PDATUM[“Mean Sea Level”,ANCHOR[“1013.25 hPa at 15°C”]],\n" +
                "CS[parametric,1],\n" +
                "AXIS[“pressure (hPa)”,up],PARAMETRICUNIT[“hPa”,100.0]]");

        if (isValidationEnabled) {
            configurationTip = Configuration.Key.isValidationEnabled;
            validators.validate(crs);
            configurationTip = null;
        }
        verifyIdentification   (crs, "WMO standard atmosphere layer 0", null);
        verifyDatum            (crs.getDatum(), "Mean Sea Level");
        verifyCoordinateSystem (crs.getCoordinateSystem(), ParametricCS.class, new AxisDirection[] {UP}, units.hectopascal());
    }

    /**
     * Parses an engineering CRS with North and West axis directions.
     * The WKT parsed by this test is (except for quote characters):
     *
     * <blockquote><pre>ENGINEERINGCRS[“Astra Minas Grid”,
     *  ENGINEERINGDATUM[“Astra Minas”],
     *  CS[Cartesian,2],
     *    AXIS[“northing (X)”,north,ORDER[1]],
     *    AXIS[“westing (Y)”,west,ORDER[2]],
     *    LENGTHUNIT[“metre”,1.0],
     *  ID[“EPSG”,5800]]</pre></blockquote>
     *
     * @throws FactoryException if an error occurred during the WKT parsing.
     *
     * @see <a href="http://docs.opengeospatial.org/is/12-063r5/12-063r5.html#78">OGC 12-063r5 §11.4 example 2</a>
     */
    @Test
    public void testEngineering() throws FactoryException {
        final EngineeringCRS crs = parse(EngineeringCRS.class,
                "ENGINEERINGCRS[“Astra Minas Grid”,\n" +
                "  ENGINEERINGDATUM[“Astra Minas”],\n" +
                "  CS[Cartesian,2],\n" +
                "    AXIS[“northing (X)”,north,ORDER[1]],\n" +
                "    AXIS[“westing (Y)”,west,ORDER[2]],\n" +
                "    LENGTHUNIT[“metre”,1.0],\n" +
                "  ID[“EPSG”,5800]]");

        if (isValidationEnabled) {
            configurationTip = Configuration.Key.isValidationEnabled;
            validators.validate(crs);
            configurationTip = null;
        }
        final Unit<Length> metre = units.metre();
        final CoordinateSystem cs;

        verifyIdentification   (crs, "Astra Minas Grid", "5800");
        verifyDatum            (crs.getDatum(), "Astra Minas");
        verifyAxisAbbreviations(cs = crs.getCoordinateSystem(), "X", "Y");
        verifyCoordinateSystem (cs, CartesianCS.class, new AxisDirection[] {NORTH,WEST}, metre);
    }

    /**
     * Parses an engineering CRS with South-West and South-East axis directions.
     * The WKT parsed by this test is (except for quote characters):
     *
     * <blockquote><pre>ENGCRS[“A construction site CRS”,
     *  EDATUM[“P1”,ANCHOR[“Peg in south corner”]],
     *  CS[Cartesian,2],
     *    AXIS[“site east”,southWest,ORDER[1]],
     *    AXIS[“site north”,southEast,ORDER[2]],
     *    LENGTHUNIT[“metre”,1.0],
     *  TIMEEXTENT[“date/time t1”,“date/time t2”]]</pre></blockquote>
     *
     * @throws FactoryException if an error occurred during the WKT parsing.
     *
     * @see <a href="http://docs.opengeospatial.org/is/12-063r5/12-063r5.html#78">OGC 12-063r5 §11.4 example 1</a>
     */
    @Test
    public void testEngineeringRotated() throws FactoryException {
        final EngineeringCRS crs = parse(EngineeringCRS.class,
                "ENGCRS[“A construction site CRS”,\n" +
                "  EDATUM[“P1”,ANCHOR[“Peg in south corner”]],\n" +
                "  CS[Cartesian,2],\n" +
                "    AXIS[“site east”,southWest,ORDER[1]],\n" +
                "    AXIS[“site north”,southEast,ORDER[2]],\n" +
                "    LENGTHUNIT[“metre”,1.0],\n" +
                "  TIMEEXTENT[“date/time t1”,“date/time t2”]]");

        if (isValidationEnabled) {
            configurationTip = Configuration.Key.isValidationEnabled;
            validators.validate(crs);
            configurationTip = null;
        }
        final Unit<Length> metre = units.metre();

        verifyIdentification   (crs, "A construction site CRS", null);
        verifyDatum            (crs.getDatum(), "P1");
        assertNullOrEquals     ("datum.anchor", "Peg in south corner", crs.getDatum().getAnchorPoint());
        verifyCoordinateSystem (crs.getCoordinateSystem(), CartesianCS.class, new AxisDirection[] {SOUTH_WEST, SOUTH_EAST}, metre);
    }

    /**
     * Parses an engineering CRS anchored to a ship.
     * The WKT parsed by this test is (except for quote characters):
     *
     * <blockquote><pre>ENGCRS[“A ship-centred CRS”,
     *  EDATUM[“Ship reference point”,ANCHOR[“Centre of buoyancy”]],
     *  CS[Cartesian,3],
     *    AXIS[“(x)”,forward],
     *    AXIS[“(y)”,starboard],
     *    AXIS[“(z)”,down],
     *    LENGTHUNIT[“metre”,1.0]]</pre></blockquote>
     *
     * @throws FactoryException if an error occurred during the WKT parsing.
     *
     * @see <a href="http://docs.opengeospatial.org/is/12-063r5/12-063r5.html#78">OGC 12-063r5 §11.4 example 2</a>
     */
    @Test
    public void testEngineeringForShip() throws FactoryException {
        final EngineeringCRS crs = parse(EngineeringCRS.class,
                "ENGCRS[“A ship-centred CRS”,\n" +
                "  EDATUM[“Ship reference point”,ANCHOR[“Centre of buoyancy”]],\n" +
                "  CS[Cartesian,3],\n" +
                "    AXIS[“(x)”,forward],\n" +
                "    AXIS[“(y)”,starboard],\n" +
                "    AXIS[“(z)”,down],\n" +
                "    LENGTHUNIT[“metre”,1.0]]");

        if (isValidationEnabled) {
            configurationTip = Configuration.Key.isValidationEnabled;
            validators.validate(crs);
            configurationTip = null;
        }
        final Unit<Length> metre = units.metre();
        final CoordinateSystem cs;

        verifyIdentification   (crs, "A ship-centred CRS", null);
        verifyDatum            (crs.getDatum(), "Ship reference point");
        assertNullOrEquals     ("datum.anchor", "Centre of buoyancy", crs.getDatum().getAnchorPoint());
        verifyAxisAbbreviations(cs = crs.getCoordinateSystem(), "x", "y", "z");
        verifyCoordinateSystem (cs, CartesianCS.class, new AxisDirection[] {valueOf("forward"), valueOf("starboard"), DOWN}, metre);
    }

    /**
     * Parses a derived geodetic CRS.
     * The WKT parsed by this test is (except for quote characters):
     *
     * <blockquote><pre>GEODCRS[“ETRS89 Lambert Azimuthal Equal Area CRS”,
     *  BASEGEODCRS[“WGS 84”,
     *    DATUM[“WGS 84”,
     *      ELLIPSOID[“WGS 84”,6378137,298.2572236,LENGTHUNIT[“metre”,1.0]]]],
     *  DERIVINGCONVERSION[“Atlantic pole”,
     *    METHOD[“Pole rotation”,ID[“Authority”,1234]],
     *    PARAMETER[“Latitude of rotated pole”,52.0,
     *      ANGLEUNIT[“degree”,0.0174532925199433]],
     *    PARAMETER[“Longitude of rotated pole”,-30.0,
     *      ANGLEUNIT[“degree”,0.0174532925199433]],
     *    PARAMETER[“Axis rotation”,-25.0,
     *      ANGLEUNIT[“degree”,0.0174532925199433]]],
     *  CS[ellipsoidal,2],
     *    AXIS[“latitude”,north,ORDER[1]],
     *    AXIS[“longitude”,east,ORDER[2]],
     *    ANGLEUNIT[“degree”,0.0174532925199433]]</pre></blockquote>
     *
     * @throws FactoryException if an error occurred during the WKT parsing.
     *
     * @see <a href="http://docs.opengeospatial.org/is/12-063r5/12-063r5.html#103">OGC 12-063r5 §15.3.5 example 3</a>
     */
    @Test
    public void testDerivedGeodetic() throws FactoryException {
        final DerivedCRS crs = parse(DerivedCRS.class,
                "GEODCRS[“ETRS89 Lambert Azimuthal Equal Area CRS”,\n" +
                "  BASEGEODCRS[“WGS 84”,\n" +
                "    DATUM[“WGS 84”,\n" +
                "      ELLIPSOID[“WGS 84”, 6378137, 298.2572236, LENGTHUNIT[“metre”,1.0]]]],\n" +
                "  DERIVINGCONVERSION[“Atlantic pole”,\n" +
                "    METHOD[“Pole rotation”,ID[“Authority”,1234]],\n" +
                "    PARAMETER[“Latitude of rotated pole”,52.0,\n" +
                "      ANGLEUNIT[“degree”,0.0174532925199433]],\n" +
                "    PARAMETER[“Longitude of rotated pole”,-30.0,\n" +
                "      ANGLEUNIT[“degree”,0.0174532925199433]],\n" +
                "    PARAMETER[“Axis rotation”,-25.0,\n" +
                "      ANGLEUNIT[“degree”,0.0174532925199433]]],\n" +
                "  CS[ellipsoidal,2],\n" +
                "    AXIS[“latitude”,north,ORDER[1]],\n" +
                "    AXIS[“longitude”,east,ORDER[2]],\n" +
                "    ANGLEUNIT[“degree”,0.0174532925199433]]");

        if (isValidationEnabled) {
            configurationTip = Configuration.Key.isValidationEnabled;
            validators.validate(crs);
            configurationTip = null;
        }
        final Unit<Angle> degree = units.degree();
        final Unit<Length> metre = units.metre();
        final GeodeticDatum datum;

        verifyIdentification  (crs, "ETRS89 Lambert Azimuthal Equal Area CRS", null);
        verifyCoordinateSystem(crs.getCoordinateSystem(), EllipsoidalCS.class, new AxisDirection[] {NORTH,EAST}, degree);

        assertInstanceOf("baseCRS", GeodeticCRS.class, crs.getBaseCRS());
        verifyDatum           (datum = ((GeodeticCRS) crs.getBaseCRS()).getDatum(), "WGS 84");
        verifyFlattenedSphere (datum.getEllipsoid(), "WGS 84", 6378137, 298.2572236, metre);
        verifyPrimeMeridian   (datum.getPrimeMeridian(), null, 0, degree);

        final ParameterValueGroup group = crs.getConversionFromBase().getParameterValues();
        verifyParameter(group, "Latitude of rotated pole",   52, degree);
        verifyParameter(group, "Longitude of rotated pole", -30, degree);
        verifyParameter(group, "Axis rotation",             -25, degree);
    }

    /**
     * Parses a derived engineering CRS having a base geodetic CRS.
     * The WKT parsed by this test is (except for quote characters):
     *
     * <blockquote><pre>ENGCRS[“Topocentric example A”,
     *  BASEGEODCRS[“WGS 84”,
     *    DATUM[“WGS 84”,
     *      ELLIPSOID[“WGS 84”, 6378137, 298.2572236, LENGTHUNIT[“metre”,1.0]]]],
     *  DERIVINGCONVERSION[“Topocentric example A”,
     *    METHOD[“Geographic/topocentric conversions”,ID[“EPSG”,9837]],
     *    PARAMETER[“Latitude of topocentric origin”,55.0,
     *      ANGLEUNIT[“degree”,0.0174532925199433]],
     *    PARAMETER[“Longitude of topocentric origin”,5.0,
     *      ANGLEUNIT[“degree”,0.0174532925199433]],
     *    PARAMETER[“Ellipsoidal height of topocentric origin”,0.0,
     *      LENGTHUNIT[“metre”,1.0]]],
     *  CS[Cartesian,3],
     *    AXIS[“Topocentric East (U)”,east,ORDER[1]],
     *    AXIS[“Topocentric North (V)”,north,ORDER[2]],
     *    AXIS[“Topocentric height (W)”,up,ORDER[3]],
     *    LENGTHUNIT[“metre”,1.0]]</pre></blockquote>
     *
     * @throws FactoryException if an error occurred during the WKT parsing.
     *
     * @see <a href="http://docs.opengeospatial.org/is/12-063r5/12-063r5.html#107">OGC 12-063r5 §15.5.2 example 2</a>
     */
    @Test
    public void testDerivedEngineeringFromGeodetic() throws FactoryException {
        final DerivedCRS crs = parse(DerivedCRS.class,
                "ENGCRS[“Topocentric example A”,\n" +
                "  BASEGEODCRS[“WGS 84”,\n" +
                "    DATUM[“WGS 84”,\n" +
                "      ELLIPSOID[“WGS 84”, 6378137, 298.2572236, LENGTHUNIT[“metre”,1.0]]]],\n" +
                "  DERIVINGCONVERSION[“Topocentric example A”,\n" +
                "    METHOD[“Geographic/topocentric conversions”,ID[“EPSG”,9837]],\n" +
                "    PARAMETER[“Latitude of topocentric origin”,55.0,\n" +
                "      ANGLEUNIT[“degree”,0.0174532925199433]],\n" +
                "    PARAMETER[“Longitude of topocentric origin”,5.0,\n" +
                "      ANGLEUNIT[“degree”,0.0174532925199433]],\n" +
                "    PARAMETER[“Ellipsoidal height of topocentric origin”,0.0,\n" +
                "     LENGTHUNIT[“metre”,1.0]]],\n" +
                "  CS[Cartesian,3],\n" +
                "    AXIS[“Topocentric East (U)”,east,ORDER[1]],\n" +
                "    AXIS[“Topocentric North (V)”,north,ORDER[2]],\n" +
                "    AXIS[“Topocentric height (W)”,up,ORDER[3]],\n" +
                "    LENGTHUNIT[“metre”,1.0]]");

        if (isValidationEnabled) {
            configurationTip = Configuration.Key.isValidationEnabled;
            validators.validate(crs);
            configurationTip = null;
        }
        final Unit<Angle> degree = units.degree();
        final Unit<Length> metre = units.metre();
        final GeodeticDatum datum;

        verifyIdentification   (crs, "Topocentric example A", null);
        verifyCoordinateSystem (crs.getCoordinateSystem(), EllipsoidalCS.class, new AxisDirection[] {EAST,NORTH,UP}, metre);
        verifyAxisAbbreviations(crs.getCoordinateSystem(), "U", "V", "W");

        assertInstanceOf("baseCRS", GeodeticCRS.class, crs.getBaseCRS());
        verifyDatum           (datum = ((GeodeticCRS) crs.getBaseCRS()).getDatum(), "WGS 84");
        verifyFlattenedSphere (datum.getEllipsoid(), "WGS 84", 6378137, 298.2572236, metre);
        verifyPrimeMeridian   (datum.getPrimeMeridian(), null, 0, degree);

        final ParameterValueGroup group = crs.getConversionFromBase().getParameterValues();
        verifyParameter(group, "Latitude of topocentric origin",          55, degree);
        verifyParameter(group, "Longitude of topocentric origin",          5, degree);
        verifyParameter(group, "Ellipsoidal height of topocentric origin", 0, metre);
    }

    /**
     * Parses a derived engineering CRS having a base projected CRS.
     * The WKT parsed by this test is (except for quote characters):
     *
     * <blockquote><pre>ENGCRS[“Gulf of Mexico speculative seismic survey bin grid”,
     *  BASEPROJCRS[“NAD27 / Texas South Central”,
     *    BASEGEODCRS[“NAD27”,
     *      DATUM[“North American Datum 1927”,
     *        ELLIPSOID[“Clarke 1866”,20925832.164,294.97869821,
     *          LENGTHUNIT[“US survey foot”,0.304800609601219]]]],
     *    CONVERSION[“Texas South CentralSPCS27”,
     *      METHOD[“Lambert Conic Conformal (2SP)”,ID[“EPSG”,9802]],
     *      PARAMETER[“Latitude of false origin”,27.83333333333333,
     *        ANGLEUNIT[“degree”,0.0174532925199433],ID[“EPSG”,8821]],
     *      PARAMETER[“Longitude of false origin”,-99.0,
     *        ANGLEUNIT[“degree”,0.0174532925199433],ID[“EPSG”,8822]],
     *      PARAMETER[“Latitude of 1st standard parallel”,28.383333333333,
     *        ANGLEUNIT[“degree”,0.0174532925199433],ID[“EPSG”,8823]],
     *      PARAMETER[“Latitude of 2nd standard parallel”,30.283333333333,
     *        ANGLEUNIT[“degree”,0.0174532925199433],ID[“EPSG”,8824]],
     *      PARAMETER[“Easting at false origin”,2000000.0,
     *        LENGTHUNIT[“US survey foot”,0.304800609601219],ID[“EPSG”,8826]],
     *      PARAMETER[“Northing at false origin”,0.0,
     *        LENGTHUNIT[“US survey foot”,0.304800609601219],ID[“EPSG”,8827]]]],
     *  DERIVINGCONVERSION[“Gulf of Mexico speculative survey bin grid”,
     *    METHOD[“P6 (I = J-90°) seismic bin grid transformation”,ID[“EPSG”,1049]],
     *    PARAMETER[“Bin grid origin I”,5000,SCALEUNIT[“Bin”,1.0],ID[“EPSG”,8733]],
     *    PARAMETER[“Bin grid origin J”,0,SCALEUNIT[“Bin”,1.0],ID[“EPSG”,8734]],
     *    PARAMETER[“Bin grid origin Easting”,871200,
     *      LENGTHUNIT[“US survey foot”,0.304800609601219],ID[“EPSG”,8735]],
     *    PARAMETER[“Bin grid origin Northing”, 10280160,
     *      LENGTHUNIT[“US survey foot”,0.304800609601219],ID[“EPSG”,8736]],
     *    PARAMETER[“Scale factor of bin grid”,1.0,
     *      SCALEUNIT[“Unity”,1.0],ID[“EPSG”,8737]],
     *    PARAMETER[“Bin width on I-axis”,82.5,
     *      LENGTHUNIT[“US survey foot”,0.304800609601219],ID[“EPSG”,8738]],
     *    PARAMETER[“Bin width on J-axis”,41.25,
     *      LENGTHUNIT[“US survey foot”,0.304800609601219],ID[“EPSG”,8739]],
     *    PARAMETER[“Map grid bearing of bin grid J-axis”,340,
     *      ANGLEUNIT[“degree”,0.0174532925199433],ID[“EPSG”,8740]],
     *    PARAMETER[“Bin node increment on I-axis”,1.0,
     *      SCALEUNIT[“Bin”,1.0],ID[“EPSG”,8741]],
     *    PARAMETER[“Bin node increment on J-axis”,1.0,
     *      SCALEUNIT[“Bin”,1.0],ID[“EPSG”,8742]]],
     *  CS[Cartesian,2],
     *    AXIS[“(I)”,northNorthWest],
     *    AXIS[“(J)”,westSouthWest],
     *    SCALEUNIT[“Bin”,1.0]]</pre></blockquote>
     *
     * @throws FactoryException if an error occurred during the WKT parsing.
     *
     * @see <a href="http://docs.opengeospatial.org/is/12-063r5/12-063r5.html#107">OGC 12-063r5 §15.5.2 example 1</a>
     */
    @Test
    public void testDerivedEngineeringFromProjected() throws FactoryException {
        final DerivedCRS crs = parse(DerivedCRS.class,
                "ENGCRS[“Gulf of Mexico speculative seismic survey bin grid”,\n" +
                "  BASEPROJCRS[“NAD27 / Texas South Central”,\n" +
                "    BASEGEODCRS[“NAD27”,\n" +
                "      DATUM[“North American Datum 1927”,\n" +
                "        ELLIPSOID[“Clarke 1866”,20925832.164,294.97869821,\n" +
                "          LENGTHUNIT[“US survey foot”,0.304800609601219]]]],\n" +
                "    CONVERSION[“Texas South CentralSPCS27”,\n" +
                "      METHOD[“Lambert Conic Conformal (2SP)”,ID[“EPSG”,9802]],\n" +
                "      PARAMETER[“Latitude of false origin”,27.83333333333333,\n" +
                "        ANGLEUNIT[“degree”,0.0174532925199433],ID[“EPSG”,8821]],\n" +
                "      PARAMETER[“Longitude of false origin”,-99.0,\n" +
                "        ANGLEUNIT[“degree”,0.0174532925199433],ID[“EPSG”,8822]],\n" +
                "      PARAMETER[“Latitude of 1st standard parallel”,28.383333333333,\n" +
                "        ANGLEUNIT[“degree”,0.0174532925199433],ID[“EPSG”,8823]],\n" +
                "      PARAMETER[“Latitude of 2nd standard parallel”,30.283333333333,\n" +
                "        ANGLEUNIT[“degree”,0.0174532925199433],ID[“EPSG”,8824]],\n" +
                "      PARAMETER[“Easting at false origin”,2000000.0,\n" +
                "        LENGTHUNIT[“US survey foot”,0.304800609601219],ID[“EPSG”,8826]],\n" +
                "      PARAMETER[“Northing at false origin”,0.0,\n" +
                "        LENGTHUNIT[“US survey foot”,0.304800609601219],ID[“EPSG”,8827]]]],\n" +
                "  DERIVINGCONVERSION[“Gulf of Mexico speculative survey bin grid”,\n" +
                "    METHOD[“P6 (I = J-90°) seismic bin grid transformation”,ID[“EPSG”,1049]],\n" +
                "    PARAMETER[“Bin grid origin I”,5000,SCALEUNIT[“Bin”,1.0],ID[“EPSG”,8733]],\n" +
                "    PARAMETER[“Bin grid origin J”,0,SCALEUNIT[“Bin”,1.0],ID[“EPSG”,8734]],\n" +
                "    PARAMETER[“Bin grid origin Easting”,871200,\n" +
                "      LENGTHUNIT[“US survey foot”,0.304800609601219],ID[“EPSG”,8735]],\n" +
                "    PARAMETER[“Bin grid origin Northing”, 10280160,\n" +
                "      LENGTHUNIT[“US survey foot”,0.304800609601219],ID[“EPSG”,8736]],\n" +
                "    PARAMETER[“Scale factor of bin grid”,1.0,\n" +
                "      SCALEUNIT[“Unity”,1.0],ID[“EPSG”,8737]],\n" +
                "    PARAMETER[“Bin width on I-axis”,82.5,\n" +
                "      LENGTHUNIT[“US survey foot”,0.304800609601219],ID[“EPSG”,8738]],\n" +
                "    PARAMETER[“Bin width on J-axis”,41.25,\n" +
                "      LENGTHUNIT[“US survey foot”,0.304800609601219],ID[“EPSG”,8739]],\n" +
                "    PARAMETER[“Map grid bearing of bin grid J-axis”,340,\n" +
                "      ANGLEUNIT[“degree”,0.0174532925199433],ID[“EPSG”,8740]],\n" +
                "    PARAMETER[“Bin node increment on I-axis”,1.0,\n" +
                "      SCALEUNIT[“Bin”,1.0],ID[“EPSG”,8741]],\n" +
                "    PARAMETER[“Bin node increment on J-axis”,1.0,\n" +
                "      SCALEUNIT[“Bin”,1.0],ID[“EPSG”,8742]]],\n" +
                "  CS[Cartesian,2],\n" +
                "    AXIS[“(I)”,northNorthWest],\n" +
                "    AXIS[“(J)”,westSouthWest],\n" +
                "    SCALEUNIT[“Bin”,1.0]]");

        if (isValidationEnabled) {
            configurationTip = Configuration.Key.isValidationEnabled;
            validators.validate(crs);
            configurationTip = null;
        }
        final Unit<Angle>  degree       = units.degree();
        final Unit<Length> footSurveyUS = units.footSurveyUS();
        final Unit<Dimensionless> one   = units.one();
        final CoordinateSystem cs;

        verifyIdentification   (crs, "Gulf of Mexico speculative seismic survey bin grid", null);
        verifyAxisAbbreviations(cs = crs.getCoordinateSystem(), "I", "J");
        verifyCoordinateSystem (cs, CartesianCS.class, new AxisDirection[] {NORTH_NORTH_WEST, WEST_SOUTH_WEST}, one);
        assertInstanceOf("baseCRS", ProjectedCRS.class, crs.getBaseCRS());
        verifyTexasSouthCentral((ProjectedCRS) crs.getBaseCRS(), degree, footSurveyUS);

        final ParameterValueGroup group = crs.getConversionFromBase().getParameterValues();
        verifyParameter(group, "Bin grid origin I",                  5000, one);
        verifyParameter(group, "Bin grid origin J",                     0, one);
        verifyParameter(group, "Bin grid origin Easting",          871200, footSurveyUS);
        verifyParameter(group, "Bin grid origin Northing",       10280160, footSurveyUS);
        verifyParameter(group, "Scale factor of bin grid",              1, one);
        verifyParameter(group, "Bin width on I-axis",               82.50, footSurveyUS);
        verifyParameter(group, "Bin width on J-axis",               41.25, footSurveyUS);
        verifyParameter(group, "Map grid bearing of bin grid J-axis", 340, degree);
        verifyParameter(group, "Bin node increment on I-axis",          1, one);
        verifyParameter(group, "Bin node increment on J-axis",          1, one);
    }

    /**
     * Parses a compound CRS with a vertical component.
     * The WKT parsed by this test is (except for quote characters):
     *
     * <blockquote><pre>COMPOUNDCRS[“NAD83 + NAVD88”,
     *  GEODCRS[“NAD83”,
     *    DATUM[“North American Datum 1983”,
     *      ELLIPSOID[“GRS 1980”,6378137,298.257222101,
     *        LENGTHUNIT[“metre”,1.0]]],
     *      PRIMEMERIDIAN[“Greenwich”,0],
     *    CS[ellipsoidal,2],
     *      AXIS[“latitude”,north,ORDER[1]],
     *      AXIS[“longitude”,east,ORDER[2]],
     *      ANGLEUNIT[“degree”,0.0174532925199433]],
     *    VERTCRS[“NAVD88”,
     *      VDATUM[“North American Vertical Datum 1988”],
     *      CS[vertical,1],
     *        AXIS[“gravity-related height (H)”,up],
     *        LENGTHUNIT[“metre”,1]]]</pre></blockquote>
     *
     * @throws FactoryException if an error occurred during the WKT parsing.
     *
     * @see <a href="http://docs.opengeospatial.org/is/12-063r5/12-063r5.html#112">OGC 12-063r5 §16.2 example 1</a>
     */
    @Test
    public void testCompoundWithVertical() throws FactoryException {
        final CompoundCRS crs = parse(CompoundCRS.class,
                "COMPOUNDCRS[“NAD83 + NAVD88”,\n" +
                "  GEODCRS[“NAD83”,\n" +
                "    DATUM[“North American Datum 1983”,\n" +
                "      ELLIPSOID[“GRS 1980”,6378137,298.257222101,\n" +
                "        LENGTHUNIT[“metre”,1.0]]],\n" +
                "      PRIMEMERIDIAN[“Greenwich”,0],\n" +
                "    CS[ellipsoidal,2],\n" +
                "      AXIS[“latitude”,north,ORDER[1]],\n" +
                "      AXIS[“longitude”,east,ORDER[2]],\n" +
                "      ANGLEUNIT[“degree”,0.0174532925199433]],\n" +
                "  VERTCRS[“NAVD88”,\n" +
                "    VDATUM[“North American Vertical Datum 1988”],\n" +
                "    CS[vertical,1],\n" +
                "      AXIS[“gravity-related height (H)”,up],\n" +
                "      LENGTHUNIT[“metre”,1]]]");

        if (isValidationEnabled) {
            configurationTip = Configuration.Key.isValidationEnabled;
            validators.validate(crs);
            configurationTip = null;
        }
        final Unit<Angle> degree = units.degree();
        final Unit<Length> metre = units.metre();

        verifyIdentification(crs, "NAD83 + NAVD88", null);
        final List<CoordinateReferenceSystem> components = crs.getComponents();
        assertEquals("components.size()", 2, components.size());
        assertInstanceOf("components[0]", GeodeticCRS.class, components.get(0));
        assertInstanceOf("components[1]", VerticalCRS.class, components.get(1));
        verifyNAD23((GeodeticCRS) components.get(0), false, degree, metre);
        verifyNAD28((VerticalCRS) components.get(1), metre);
    }

    /**
     * Parses a compound CRS with a temporal component.
     * The WKT parsed by this test is (except for quote characters):
     *
     * <blockquote><pre>COMPOUNDCRS[“GPS position and time”,
     *   GEODCRS[“WGS 84”,
     *     DATUM[“World Geodetic System 1984”,
     *       ELLIPSOID[“WGS 84”,6378137,298.257223563]],
     *     CS[ellipsoidal,2],
     *       AXIS[“(lat)”,north,ORDER[1]],
     *       AXIS[“(lon)”,east,ORDER[2]],
     *       ANGLEUNIT[“degree”,0.0174532925199433]],
     *   TIMECRS[“GPS Time”,
     *     TIMEDATUM[“Time origin”,TIMEORIGIN[1980-01-01]],
     *     CS[temporal,1],
     *       AXIS[“time (T)”,future],
     *       TIMEUNIT[“day”,86400]]]</pre></blockquote>
     *
     * @throws FactoryException if an error occurred during the WKT parsing.
     *
     * @see <a href="http://docs.opengeospatial.org/is/12-063r5/12-063r5.html#112">OGC 12-063r5 §16.2 example 3</a>
     */
    @Test
    public void testCompoundWithTime() throws FactoryException {
        final CompoundCRS crs = parse(CompoundCRS.class,
                "COMPOUNDCRS[“GPS position and time”,\n" +
                "  GEODCRS[“WGS 84”,\n" +
                "    DATUM[“World Geodetic System 1984”,\n" +
                "      ELLIPSOID[“WGS 84”,6378137,298.257223563]],\n" +
                "    CS[ellipsoidal,2],\n" +
                "      AXIS[“(lat)”,north,ORDER[1]],\n" +
                "      AXIS[“(lon)”,east,ORDER[2]],\n" +
                "      ANGLEUNIT[“degree”,0.0174532925199433]],\n" +
                "  TIMECRS[“GPS Time”,\n" +
                "    TIMEDATUM[“Time origin”,TIMEORIGIN[1980-01-01]],\n" +
                "    CS[temporal,1],\n" +
                "      AXIS[“time (T)”,future],\n" +
                "      TIMEUNIT[“day”,86400]]]");

        if (isValidationEnabled) {
            configurationTip = Configuration.Key.isValidationEnabled;
            validators.validate(crs);
            configurationTip = null;
        }
        final Unit<Angle> degree = units.degree();
        final Unit<Length> metre = units.metre();

        verifyIdentification(crs, "GPS position and time", null);
        final List<CoordinateReferenceSystem> components = crs.getComponents();
        assertEquals("components.size()", 2, components.size());
        assertInstanceOf("components[0]", GeodeticCRS.class, components.get(0));
        assertInstanceOf("components[1]", TemporalCRS.class, components.get(1));
        verifyWGS84  ((GeodeticCRS) components.get(0), false, degree, metre);
        verifyGPSTime((TemporalCRS) components.get(1));
    }

    /**
     * Parses a compound CRS with a parametric component.
     * The WKT parsed by this test is (except for quote characters):
     *
     * <blockquote><pre>COMPOUNDCRS[“ICAO layer 0”,
     *   GEODETICCRS[“WGS 84”,
     *     DATUM[“World Geodetic System 1984”,
     *       ELLIPSOID[“WGS 84”,6378137,298.257223563,
     *         LENGTHUNIT[“metre”,1.0]]],
     *     CS[ellipsoidal,2],
     *       AXIS[“latitude”,north,ORDER[1]],
     *       AXIS[“longitude”,east,ORDER[2]],
     *       ANGLEUNIT[“degree”,0.0174532925199433]],
     *   PARAMETRICCRS[“WMO standard atmosphere”,
     *     PARAMETRICDATUM[“Mean Sea Level”,
     *       ANCHOR[“Mean Sea Level = 1013.25 hPa”]],
     *         CS[parametric,1],
     *           AXIS[“pressure (P)”,unspecified],
     *           PARAMETRICUNIT[“hPa”,100]]]</pre></blockquote>
     *
     * @throws FactoryException if an error occurred during the WKT parsing.
     *
     * @see <a href="http://docs.opengeospatial.org/is/12-063r5/12-063r5.html#112">OGC 12-063r5 §16.2 example 2</a>
     */
    @Test
    public void testCompoundWithParametric() throws FactoryException {
        final CompoundCRS crs = parse(CompoundCRS.class,
                "COMPOUNDCRS[“ICAO layer 0”,\n" +
                "  GEODETICCRS[“WGS 84”,\n" +
                "    DATUM[“World Geodetic System 1984”,\n" +
                "      ELLIPSOID[“WGS 84”,6378137,298.257223563,\n" +
                "        LENGTHUNIT[“metre”,1.0]]],\n" +
                "    CS[ellipsoidal,2],\n" +
                "      AXIS[“latitude”,north,ORDER[1]],\n" +
                "      AXIS[“longitude”,east,ORDER[2]],\n" +
                "      ANGLEUNIT[“degree”,0.0174532925199433]],\n" +
                "  PARAMETRICCRS[“WMO standard atmosphere”,\n" +
                "    PARAMETRICDATUM[“Mean Sea Level”,\n" +
                "      ANCHOR[“Mean Sea Level = 1013.25 hPa”]],\n" +
                "        CS[parametric,1],\n" +
                "          AXIS[“pressure (P)”,unspecified],\n" +
                "          PARAMETRICUNIT[“hPa”,100]]]");

        if (isValidationEnabled) {
            configurationTip = Configuration.Key.isValidationEnabled;
            validators.validate(crs);
            configurationTip = null;
        }
        final Unit<Angle> degree = units.degree();
        final Unit<Length> metre = units.metre();

        verifyIdentification(crs, "ICAO layer 0", null);
        final List<CoordinateReferenceSystem> components = crs.getComponents();
        assertEquals("components.size()", 2, components.size());
        assertInstanceOf("components[0]", GeodeticCRS.class, components.get(0));
        assertInstanceOf("components[1]", ParametricCRS.class, components.get(1));
        verifyWGS84((GeodeticCRS) components.get(0), false, degree, metre);

        final ParametricCRS ps = (ParametricCRS) components.get(1);
        verifyIdentification(ps, "WMO standard atmosphere", null);
        verifyDatum(ps.getDatum(), "Mean Sea Level");
        assertInstanceOf("coordinateSystem", ParametricCS.class, ps.getCoordinateSystem());
    }
}
