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


/**
 * Implementation of some GeoAPI interfaces. In order to provide a simpler model, some classes merge
 * many distinct GeoAPI concepts. For example, many existing projection libraries make no distinction
 * between <cite>Coordinate System</cite> (CS) and <cite>Coordinate Reference System</cite> (CRS).
 * Those implementation examples follow this simplified model by providing a single class implementing
 * both the CS and CRS interfaces.
 *
 * <p>The following table lists the classes that implement more than one GeoAPI interface:</p>
 *
 * <blockquote><table><tr>
 *   <th>Simple class</th><th></th>
 *   <th colspan="2">Implements</th>
 * </tr><tr>
 *   <td>{@link org.opengis.example.referencing.SimpleCRS}</td><td>&nbsp;:&nbsp;</td>
 *   <td>{@link org.opengis.referencing.crs.CoordinateReferenceSystem},</td>
 *   <td>{@link org.opengis.referencing.cs.CoordinateSystem}</td>
 * </tr><tr>
 *   <td>{@link org.opengis.example.referencing.SimpleDatum}</td><td>&nbsp;:&nbsp;</td>
 *   <td>{@link org.opengis.referencing.datum.GeodeticDatum},</td>
 *   <td>{@link org.opengis.referencing.datum.Ellipsoid}</td>
 * </tr><tr>
 *   <td>{@link org.opengis.example.referencing.SimpleTransform}</td><td>&nbsp;:&nbsp;</td>
 *   <td>{@link org.opengis.referencing.operation.CoordinateOperation},</td>
 *   <td>{@link org.opengis.referencing.operation.MathTransform}</td>
 * </tr><tr>
 *   <td>{@link org.opengis.example.referencing.SimpleIdentifiedObject}</td><td>&nbsp;:&nbsp;</td>
 *   <td>{@link org.opengis.referencing.IdentifiedObject},</td>
 *   <td>{@link org.opengis.metadata.Identifier}</td>
 * </tr><tr>
 *   <td>{@link org.opengis.example.metadata.SimpleCitation}</td><td>&nbsp;:&nbsp;</td>
 *   <td>{@link org.opengis.metadata.citation.Citation},</td>
 *   <td>{@link org.opengis.util.InternationalString}</td>
 * </tr><tr>
 *   <td>{@link org.opengis.example.metadata.SimpleGeographicBoundingBox}</td><td>&nbsp;:&nbsp;</td>
 *   <td>{@link org.opengis.metadata.extent.GeographicBoundingBox},</td>
 *   <td>{@link org.opengis.metadata.extent.Extent}</td>
 * </tr><tr>
 *   <td>{@link org.opengis.example.parameter.SimpleParameter}</td><td>&nbsp;:&nbsp;</td>
 *   <td>{@link org.opengis.parameter.ParameterValue},</td>
 *   <td>{@link org.opengis.parameter.ParameterDescriptor}</td>
 * </tr><tr>
 *   <td>{@link org.opengis.example.parameter.SimpleParameterGroup}</td><td>&nbsp;:&nbsp;</td>
 *   <td>{@link org.opengis.parameter.ParameterValueGroup},</td>
 *   <td>{@link org.opengis.parameter.ParameterDescriptorGroup}</td>
 * </tr></table></blockquote>
 *
 * <p>Every classes in those example packages are hereby placed into the Public Domain.
 * This means anyone is free to do whatever they wish with those files.</p>
 *
 * @version 4.0
 * @since 2.2
 */
module org.opengis.example {
    requires transitive org.opengis.geoapi;
}
