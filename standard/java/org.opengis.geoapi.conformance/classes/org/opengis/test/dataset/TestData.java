/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    Copyright (C) 2018 Open Geospatial Consortium, Inc.
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
package org.opengis.test.dataset;

import java.io.File;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;

import static org.junit.Assert.*;


/**
 * Provides access to small built-in test files.
 * Data can be obtained as {@link URL}, {@link File}, {@link InputStream} or {@code byte[]} array.
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @version 3.1
 * @since   3.1
 */
public enum TestData {
    /**
     * A two-dimensional netCDF file using a geographic CRS for global data over the world.
     * The file contains temperature data from model analysis, without missing values.
     *
     * <ul>
     *   <li><b>Data type:</b> 16 bits signed integers (only the positive range is used)</li>
     *   <li><b>Coordinate Reference System:</b> two-dimensional geographic</li>
     *   <li><b>Geographic area:</b> world, with latitudes ranging from 90°S to 90°N and longitudes from 180°E to 180°W</li>
     *   <li><b>Size:</b> 73 × 73 cells in a file of 12.7 kilobytes</li>
     * </ul>
     * <table class="ogc">
     *   <caption>Global attributes</caption>
     *   <tr><th>Name</th><th>Value</th></tr>
     *   <tr><td>{@code Conventions}</td><td>CF-1.4</td></tr>
     *   <tr><td>{@code Metadata_Conventions}</td><td>Unidata Dataset Discovery v1.0</td></tr>
     *   <tr><td>{@code title}</td><td>Test data from Sea Surface Temperature Analysis Model</td></tr>
     *   <tr><td>{@code purpose}</td><td>GeoAPI conformance tests</td></tr>
     *   <tr><td>{@code summary}</td><td>Global, two-dimensional model data</td></tr>
     *   <tr><td>{@code keywords}</td><td>{@literal EARTH SCIENCE > Oceans > Ocean Temperature > Sea Surface Temperature}</td></tr>
     *   <tr><td>{@code keywords_vocabulary}</td><td>GCMD Science Keywords</td></tr>
     *   <tr><td>{@code geospatial_lat_min}</td><td>-90.0</td></tr>
     *   <tr><td>{@code geospatial_lat_max}</td><td>90.0</td></tr>
     *   <tr><td>{@code geospatial_lon_min}</td><td>-180.0</td></tr>
     *   <tr><td>{@code geospatial_lon_max}</td><td>180.0</td></tr>
     *   <tr><td>{@code geospatial_vertical_min}</td><td>0.0</td></tr>
     *   <tr><td>{@code geospatial_vertical_max}</td><td>0.0</td></tr>
     *   <tr><td>{@code time_coverage_start}</td><td>2005-09-22T00:00</td></tr>
     *   <tr><td>{@code time_coverage_duration}</td><td>0.0</td></tr>
     *   <tr><td>{@code cdm_data_type}</td><td>Grid</td></tr>
     *   <tr><td>{@code id}</td><td>NCEP/SST/Global_5x2p5deg/SST_Global_5x2p5deg_20050922_0000.nc</td></tr>
     *   <tr><td>{@code naming_authority}</td><td>edu.ucar.unidata</td></tr>
     *   <tr><td>{@code creator_name}</td><td>NOAA/NWS/NCEP</td></tr>
     *   <tr><td>{@code date_created}</td><td>2005-09-22T00:00</td></tr>
     *   <tr><td>{@code date_modified}</td><td>2018-05-15T13:00</td></tr>
     *   <tr><td>{@code date_metadata_modified}</td><td>2018-05-15T13:01</td></tr>
     *   <tr><td>{@code history}</td><td>Decimated and modified by GeoAPI for inclusion in conformance test suite.</td></tr>
     *   <tr><td>{@code comment}</td><td>For testing purpose only.</td></tr>
     *   <tr><td>{@code license}</td><td>Freely available</td></tr>
     * </table>
     *
     * In this file, all global attributes are character sequences, including the attributes that should
     * be floating points numbers ({@code geospatial_lat_min}, {@code geospatial_lat_max}, <i>etc.</i>).
     * Implementations are encouraged to be tolerant.
     */
    NETCDF_2D_GEOGRAPHIC("Cube2D_geographic_packed.nc", 12988),

    /**
     * A four-dimensional netCDF file using a projected CRS with elevation and time.
     * The file contains <cite>Current Icing Product</cite> data without missing values.
     * The coordinate reference system contains also an height axis and a time axis.
     *
     * <ul>
     *   <li><b>Data type:</b> 32 bits floating point numbers</li>
     *   <li><b>Coordinate Reference System:</b> four-dimensional projected + elevation + temporal</li>
     *   <li><b>Geographic area:</b> East part of North America</li>
     *   <li><b>Size:</b> 38 × 19 × 4 × 1 cells in a file of 14.2 kilobytes</li>
     * </ul>
     * <table class="ogc">
     *   <caption>Global attributes</caption>
     *   <tr><th>Name</th><th>Value</th></tr>
     *   <tr><td>{@code Conventions}</td><td>CF-1.4</td></tr>
     *   <tr><td>{@code title}</td><td>Test data from Current Icing Product (CIP)</td></tr>
     *   <tr><td>{@code purpose}</td><td>GeoAPI conformance tests</td></tr>
     *   <tr><td>{@code summary}</td><td>Hourly, three-dimensional diagnosis of the icing environment.</td></tr>
     *   <tr><td>{@code source}</td><td>U.S. National Weather Service - NCEP (WMC)</td></tr>
     *   <tr><td>{@code institution}</td><td>UCAR</td></tr>
     *   <tr><td>{@code topic_category}</td><td>climatology meteorology atmosphere</td></tr>
     *   <tr><td>{@code geospatial_lat_min}</td><td>15.94</td></tr>
     *   <tr><td>{@code geospatial_lat_max}</td><td>58.37</td></tr>
     *   <tr><td>{@code geospatial_lon_min}</td><td>-107.75</td></tr>
     *   <tr><td>{@code geospatial_lon_max}</td><td>-56.66</td></tr>
     *   <tr><td>{@code geospatial_vertical_min}</td><td>300.0</td></tr>
     *   <tr><td>{@code geospatial_vertical_max}</td><td>4875.0</td></tr>
     *   <tr><td>{@code geospatial_vertical_positive}</td><td>up</td></tr>
     *   <tr><td>{@code geospatial_lat_resolution}</td><td>0.93</td></tr>
     *   <tr><td>{@code geospatial_lon_resolution}</td><td>1.34</td></tr>
     *   <tr><td>{@code creator_name}</td><td>John Doe</td></tr>
     *   <tr><td>{@code creator_email}</td><td>john.doe@example.org</td></tr>
     *   <tr><td>{@code date_modified}</td><td>2012-02-21T21:14Z</td></tr>
     *   <tr><td>{@code date_metadata_modified}</td><td>2018-05-14T14:45Z</td></tr>
     *   <tr><td>{@code history}</td><td>Decimated and modified by GeoAPI for inclusion in conformance test suite.</td></tr>
     *   <tr><td>{@code comment}</td><td>For testing purpose only.</td></tr>
     * </table>
     *
     * If this file, all global attribute for numeric values use the {@code float} type.
     */
    NETCDF_4D_PROJECTED("Cube4D_projected_float.nc", 14544);

    /**
     * Name of the test file, located in the same directory (after JAR packaging) than the {@code TestData.class} file.
     */
    private final String filename;

    /**
     * Expected length in bytes.
     */
    private final int length;

    /**
     * Path to the (possibly temporary) file, or {@code null} if not yet created.
     */
    private transient File file;

    /**
     * Creates a new enumeration value.
     */
    private TestData(final String filename, final int length) {
        this.filename = filename;
        this.length = length;
    }

    /**
     * Returns a URL to the test file.
     * The URL is not necessary a file on the default file system; it may be an entry inside a JAR file.
     * If a path on the file system is desired, use {@link #file()} instead.
     *
     * @return a URL to the test file, possibly as an entry inside a JAR file.
     */
    public URL location() {
        final URL location = TestData.class.getResource(filename);
        assertNotNull(filename, location);
        return location;
    }

    /**
     * Returns a path on the file system to the test file. If the test file is inside a JAR file,
     * then it will be copied in a temporary directory and the path to the temporary file will be returned.
     *
     * @return a path on the default file system, possible as a temporary file.
     * @throws IOException if a copy operation was necessary but failed.
     */
    public synchronized File file() throws IOException {
        if (file == null) {
            final URI location;
            try {
                location = location().toURI();
            } catch (URISyntaxException e) {
                throw new IOException(e);
            } try {
                file = new File(location);
            } catch (IllegalArgumentException e) {
                try {
                    final byte[] data = content();
                    final File tmp = File.createTempFile("geoapi", filename.substring(filename.lastIndexOf('.')));
                    tmp.deleteOnExit();
                    try (FileOutputStream out = new FileOutputStream(tmp)) {
                        out.write(data);
                    }
                    file = tmp;                     // Set only on success.
                } catch (IOException fe) {
                    fe.addSuppressed(e);
                    throw fe;
                }
            }
        }
        return file;
    }

    /**
     * Opens an input stream on the test file.
     * It is caller responsibility to close the stream after usage.
     *
     * @return an input stream on the test file.
     */
    public InputStream open() {
        final InputStream stream = TestData.class.getResourceAsStream(filename);
        assertNotNull(filename, stream);
        return stream;
    }

    /**
     * Returns the full content of the test file as an array of bytes.
     *
     * @return the test file content.
     * @throws IOException if an error occurred while reading the test file.
     */
    public byte[] content() throws IOException {
        /*
         * We rely on the file having exactly the expected length, for avoiding array reallocations.
         * This assumption is verified by the TestDataTest.
         */
        final byte[] content = new byte[length];
        try (InputStream stream = open()) {
            int offset = 0, r, n;
            do {                        // TODO: replace this loop by stream.readNBytes(content, 0, length) in JDK9.
                r = length - offset;
                n = stream.read(content, offset, r);
                if (n < 0) throw new EOFException(filename);
            } while (r != n);
            if (stream.read() >= 0) {
                throw new IOException("Unexpected file length.");
            }
        }
        return content;
    }
}
