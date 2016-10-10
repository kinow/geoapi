/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    This file is hereby placed into the Public Domain.
 *    This means anyone is free to do whatever they wish with this file.
 *
 *    The Proj.4 wrappers are provided as code examples, in the hope to facilitate
 *    GeoAPI implementations backed by other libraries. Implementors can take this
 *    source code and use it for any purpose, commercial or non-commercial, copyrighted
 *    or open-source, with no legal obligation to acknowledge the borrowing/copying
 *    in any way.
 */
package org.opengis.wrapper.proj4;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.io.Console;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


/**
 * Generates the list of supported EPSG codes, together with their axis directions.
 * This class requires the root to the Proj.4 data directory and a connection to an
 * EPSG database.
 *
 * <p>This class is not actually a JUnit test. It is rather a tool which need to be executed
 * when the Proj.4 "{@code epsg}" definition file has been changed.
 * The file generated by this tools shall be moved manually to the project
 * {@code geoapi-proj4/src/main/resources/org/opengis/wrapper/proj4/} directory.</p>
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @version 3.1
 * @since   3.1
 */
public class SupportedCodes {
    /**
     * The output file, to be written in the current directory.
     */
    private static final String OUTPUT_FILE = ResourcesLoader.AXIS_FILE;

    /**
     * Defaults axis orientations (in Proj.4 syntax) when we have not been able to get this
     * information from the database.
     */
    private static final String DEFAULT_ORIENTATION = "enu";

    /**
     * The text which appears before axis orientations in a Coordinate System name.
     * Used for extracting axis orientations only if the formal way failed.
     */
    private static final String AXIS_IN_CS_NAME = "Axes:";

    /**
     * The root directory of Proj.4 data files.
     */
    private final File projDataDirectory;

    /**
     * The connection to the EPSG database.
     */
    private final Connection epsgConnection;

    /**
     * The prepared statement for fetching the coordinate system code from a CRS code.
     */
    private final PreparedStatement coordSysStmt;

    /**
     * The prepared statement for fetching the name of a coordinate system.
     * Used in order to get the axis order when we failed to use the axis table.
     */
    private final PreparedStatement coordSysNameStmt;

    /**
     * The prepared statement for fetching axis orientation from a coordinate system code.
     */
    private final PreparedStatement axisOrientationStmt;

    /**
     * Axis orientations of for given coordinate system. It is worth to cache this
     * information because many CRS will typically use the same CS, and consequently
     * have the same axis orientations.
     */
    private final Map<Integer,String> cachedOrientations;

    /**
     * A map of axis orientations (in Proj.4 syntax) for each CRS codes found.
     */
    private final Map<String,String> orientationsForCode;

    /**
     * Where to print warnings.
     */
    private final PrintWriter out;

    /**
     * Creates a new object generating the list of supported EPSG codes.
     *
     * @param  projDataDirectory The root directory of Proj.4 data files.
     * @param  epsgConnection    The connection to the EPSG database.
     * @throws SQLException      If an error occurred while preparing the statements.
     */
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    private SupportedCodes(final File projDataDirectory, final Connection epsgConnection)
            throws SQLException
    {
        this.projDataDirectory = projDataDirectory;
        this.epsgConnection    = epsgConnection;
        coordSysStmt = epsgConnection.prepareStatement(
                "SELECT coord_sys_code, coord_ref_sys_kind, source_geogcrs_code FROM epsg_coordinatereferencesystem WHERE coord_ref_sys_code=?");
        coordSysNameStmt = epsgConnection.prepareStatement(
                "SELECT coord_sys_name FROM epsg_coordinatesystem WHERE coord_sys_code=?");
        axisOrientationStmt = epsgConnection.prepareStatement(
                "SELECT coord_axis_orientation FROM epsg_coordinateaxis WHERE coord_sys_code=? ORDER BY coord_axis_order");
        cachedOrientations = new HashMap<>(100);
        orientationsForCode = new LinkedHashMap<>(5000);
        final Console console = System.console();
        out = (console != null) ? console.writer() : new PrintWriter(System.out, true);
    }

    /**
     * Loads the given file and stores the codes in the {@link #orientationsForCode} map,
     * together with the axis orientations.
     *
     * @param  defFile      The definition file to read.
     * @throws IOException  If an error occurred while reading the file.
     * @throws SQLException If an error occurred while querying the database.
     */
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    private void load(final String defFile) throws IOException, SQLException {
        try (BufferedReader in = new BufferedReader(new FileReader(new File(projDataDirectory, defFile)))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (!(line = line.trim()).isEmpty() && !line.startsWith("#")) {
                    int start = line.indexOf('<');
                    if (start >= 0) {
                        final int end = line.indexOf('>', ++start);
                        if (end >= 0) {
                            final String code = line.substring(start, end).trim();
                            String orientation;
                            try {
                                orientation = getAxisOrientationsForCRS(Integer.parseInt(code));
                            } catch (NumberFormatException e) {
                                out.println("WARNING: can not parse \"" + code + "\" as an integer");
                                orientation = DEFAULT_ORIENTATION;
                            }
                            if (orientationsForCode.put(code, orientation) != null) {
                                System.out.println("WARNING: duplicated code: " + code);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the axis direction for the given CRS code. This method returns a comma-separated
     * list of axis direction if the given CRS has base CRS. The first element is for the given
     * CRS, and the next element (if any) is for the base CRS.
     *
     * @param  code The CRS code for which to get axis orientations.
     * @return The axis directions in Proj.4 syntax as a comma-separated list.
     * @throws SQLException if an error occurred while querying the EPSG database.
     */
    private String getAxisOrientationsForCRS(final int code) throws SQLException {
        int csCode=0, baseCode=0, numFound=0;
        coordSysStmt.setInt(1, code);
        try (ResultSet rs = coordSysStmt.executeQuery()) {
            while (rs.next()) {
                csCode = rs.getInt(1);
                if ("projected".equals(rs.getString(2))) {
                    baseCode = rs.getInt(3);
                }
                numFound++;
            }
        }
        if (numFound != 1) {
            out.println("WARNING: expected one record for CRS code " + code + " but found " + numFound);
            return DEFAULT_ORIENTATION;
        }
        final Integer key = csCode;
        String orientation = cachedOrientations.get(key);
        if (orientation == null) {
            orientation = getAxisOrientationsForCS(csCode);
            cachedOrientations.put(key, orientation);
        }
        if (baseCode != 0) {
            orientation = orientation + PJFactory.AXIS_ORDER_SEPARATOR + getAxisOrientationsForCRS(baseCode);
        }
        return orientation;
    }

    /**
     * Returns the axis direction for the given CS code.
     * This method check the cache before to perform the actual SQL query.
     *
     * @param  crsCode The CRS code for which to get axis orientations.
     * @return The axis directions in Proj.4 syntax.
     * @throws SQLException if an error occurred while querying the EPSG database.
     */
    private String getAxisOrientationsForCS(final int code) throws SQLException {
        axisOrientationStmt.setInt(1, code);
        ResultSet rs = axisOrientationStmt.executeQuery();
        final StringBuilder buffer = new StringBuilder();
        final StringBuilder warning = new StringBuilder();
        while (rs.next()) {
            final char c;
            final String orientation = rs.getString(1);
            switch (orientation) {
                case "east":  c='e'; break;
                case "west":  c='w'; break;
                case "north": c='n'; break;
                case "south": c='s'; break;
                case "up":    c='u'; break;
                case "down":  c='d'; break;
                default: {
                    warning.append(warning.length() == 0 ?
                                   "WARNING: unsupported axis orientation: (" : ", ").append(orientation);
                    c = ' ';       // Used after the loop for checking if an error occurred.
                    break;
                }
            }
            buffer.append(c);
        }
        rs.close();
        if (buffer.indexOf(" ") >= 0) {
            //
            // If we had unknown axis direction, try to infer the axis directions from the CS name. Example:
            // "Cartesian 2D CS for UPS north. Axes: N,E. Orientations: N along 180°E meridian, E along 90°E meridian. UoM: m."
            // This block of code will lock for the "Axes: N,E" part.
            //
            warning.append(')');
            boolean found = false;
            String def = DEFAULT_ORIENTATION;
            coordSysNameStmt.setInt(1, code);
            rs = coordSysNameStmt.executeQuery();
            while (rs.next()) { // Should be executed only once.
                final String name = rs.getString(1);
                final int s = name.indexOf(AXIS_IN_CS_NAME);
                if (s >= 0) {
                    final String sn = name.substring(s + AXIS_IN_CS_NAME.length()).trim();
                    if (sn.startsWith("E,N") || sn.startsWith("X,Y")) {def = "enu"; found=true; continue;}
                    if (sn.startsWith("N,E") || sn.startsWith("Y,X")) {def = "neu"; found=true; continue;}
                }
                warning.append(" for ").append(name);
            }
            rs.close();
            if (!found) {
                out.println(warning);
            }
            //
            // Trim the string to the number of axes found in the loop before this block of code.
            // In most cases, this result in dropping the "u" in the string.
            //
            final int length = buffer.length();
            buffer.setLength(0);
            buffer.append(def).setLength(length);
        }
        return buffer.toString();
    }

    /**
     * Writes the map of axis orientations in a {@value #OUTPUT_FILE} file the in current directory.
     *
     * @throws IOException If an error occurred while writing the file.
     */
    private void write() throws IOException {
        final File file = new File(OUTPUT_FILE).getAbsoluteFile();
        out.println("Creating " + file);
        //
        // Prepares one line for each orientation.
        //
        final Map<String,StringBuilder> lists = new LinkedHashMap<>();
        for (final Map.Entry<String,String> entry : orientationsForCode.entrySet()) {
            final String orientation = entry.getValue();
            StringBuilder list = lists.get(orientation);
            if (list == null) {
                list = new StringBuilder(1000);
                lists.put(orientation, list);
            }
            list.append(entry.getKey()).append(' ');
        }
        //
        // Copies the above line to the file.
        //
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("[EPSG]");
            writer.newLine();
            for (final Map.Entry<String,StringBuilder> entry : lists.entrySet()) {
                writer.write(entry.getKey());
                writer.write(':');
                writer.write(entry.getValue().toString().trim());
                writer.newLine();
            }
        }
    }

    /**
     * Closes the connection to the database. This method must be invoked when we are done.
     *
     * @throws SQLException If an error occurred while closing the connection.
     */
    private void close() throws SQLException {
        out.flush();
        axisOrientationStmt.close();
        coordSysNameStmt   .close();
        coordSysStmt       .close();
        epsgConnection     .close();
    }

    /**
     * Generates the list of EPSG codes and their axis orientations. This method expects the
     * following arguments:
     *
     * <ul>
     *   <li>{@code $PROJ4/proj/nad} where {@code $PROJ4} in the trunk directory of a Proj.4
     *       Subversion (SVN) check out. If the code has been checkout from the SVN repository,
     *       then this is the {@code "proj/nad"} subdirectory.</li>
     *   <li>{@code jdbc:postgresql://$HOST/$DATABASE?user=$USER&password=$PW} where the
     *       various {@code $FOO} variables are connection parameters to an EPSG database.
     *       Note that MySQL and Oracle databases should work too.</li>
     * </ul>
     *
     * The output file will be written in the current directory.
     *
     * @param  args The command line arguments.
     * @throws IOException  If an error occurred while reading the file.
     * @throws SQLException If an error occurred while querying the database.
     */
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    public static void main(final String[] args) throws IOException, SQLException {
        if (args.length != 2) {
            System.out.println("Expected arguments:");
            System.out.println("  Root of Proj.4 data directory");
            System.out.println("  URL to an EPSG database (JDBC syntax)");
            return;
        }
        final SupportedCodes codes = new SupportedCodes(new File(args[0]), DriverManager.getConnection(args[1]));
        try {
            codes.load("epsg");
        } finally {
            codes.close();
        }
        codes.write();
    }
}
