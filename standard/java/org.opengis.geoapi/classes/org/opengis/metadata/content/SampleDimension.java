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
package org.opengis.metadata.content;

import javax.measure.Unit;
import org.opengis.util.Record;
import org.opengis.util.RecordType;
import org.opengis.annotation.UML;

import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.ISO_19115;
import static org.opengis.annotation.Specification.ISO_19115_2;


/**
 * The characteristics of each dimension (layer) included in the resource.
 *
 * @author  Rémi Maréchal (geomatys)
 * @version 3.1
 * @since   3.1
 */
@UML(identifier="MD_SampleDimension", specification=ISO_19115)
public interface SampleDimension extends RangeDimension {
    /**
     * Minimum value of data values in each dimension included in the resource.
     * May be {@code null} if unspecified.
     *
     * @return minimum value of data values in each dimension included in the resource, or {@code null} if none.
     */
    @UML(identifier="minValue", obligation=OPTIONAL, specification=ISO_19115)
    Double getMinValue();

    /**
     * Maximum value of data values in each dimension included in the resource.
     * May be {@code null} if unspecified.
     *
     * @return maximum value of data values in each dimension included in the resource, or {@code null} if none.
     */
    @UML(identifier="maxValue", obligation=OPTIONAL, specification=ISO_19115)
    Double getMaxValue();

    /**
     * Mean value of data values in each dimension included in the resource.
     * May be {@code null} if unspecified.
     *
     * @return the mean value of data values in each dimension included in the resource, or {@code null} if none.
     */
    @UML(identifier="meanValue", obligation=OPTIONAL, specification=ISO_19115)
    Double getMeanValue();

    /**
     * Number of values used in a thematic classification resource.
     * May be {@code null} if unspecified.
     *
     * <div class="note"><b>Example:</b> the number of classes in a Land Cover Type coverage
     * or the number of cells with data in other types of coverages.</div>
     *
     * @return the number of values used in a thematic classification resource, or {@code null} if none.
     */
    @UML(identifier="numberOfValues", obligation=OPTIONAL, specification=ISO_19115)
    Integer getNumberOfValues();

    /**
     * Standard deviation of data values in each dimension included in the resource.
     * May be {@code null} if unspecified.
     *
     * @return standard deviation of data values in each dimension included in the resource, or {@code null} if none.
     */
    @UML(identifier="standardDeviation", obligation=OPTIONAL, specification=ISO_19115)
    Double getStandardDeviation();

    /**
     * Units of data in each dimension included in the resource.
     * May be {@code null} if unspecified.
     *
     * @return units of data in each dimension included in the resource, or {@code null} if none.
     *
     * @condition Mandatory if {@linkplain #getMinValue()}, {@linkplain #getMaxValue()}
     *            or {@linkplain #getMeanValue()} are provided.
     */
    @UML(identifier="units", obligation=CONDITIONAL, specification=ISO_19115)
    Unit<?> getUnits();

    /**
     * Scale factor which has been applied to the cell value.
     * May be {@code null} if unspecified.
     *
     * @return scale factor which has been applied to the cell value, or {@code null} if none.
     */
    @UML(identifier="scaleFactor", obligation=OPTIONAL, specification=ISO_19115)
    Double getScaleFactor();

    /**
     * Physical value corresponding to a cell value of zero.
     * May be {@code null} if unspecified.
     *
     * @return Physical value corresponding to a cell value of zero, or {@code null} if none.
     */
    @UML(identifier="offset", obligation=OPTIONAL, specification=ISO_19115)
    Double getOffset();

    /**
     * Type of transfer function to be used when scaling a physical value for a given element.
     *
     * @departure harmonization
     *   ISO 19115-2 defines this property in the {@code MI_Band} type (a {@code MD_Band} subtype)
     *   for historical reasons. GeoAPI moves this property up in the hierarchy to a more natural place
     *   when not constrained by historical reasons, which is together with the offset and scale factor.
     *
     * @return type of transfer function.
     */
    @UML(identifier="transferFunctionType", obligation=OPTIONAL, specification=ISO_19115_2)
    TransferFunctionType getTransferFunctionType();

    /**
     * Maximum number of significant bits in the uncompressed representation for the value in each band of each pixel.
     * May be {@code null} if unspecified.
     *
     * @return maximum number of significant bits in the uncompressed representation
     *         for the value in each band of each pixel, or {@code null} if none.
     */
    @UML(identifier="bitsPerValue", obligation=OPTIONAL, specification=ISO_19115)
    Integer getBitsPerValue();

    /**
     * Smallest distance between which separate points can be distinguished, as specified in instrument design.
     *
     * <div class="warning"><b>Upcoming API change — units of measurement</b><br>
     * The return type of this method may change in GeoAPI 4.0. It may be replaced by the
     * {@link javax.measure.quantity.Length} type in order to provide unit of measurement
     * together with the value.
     * </div>
     *
     * @departure harmonization
     *   ISO 19115-2 defines this property in the {@code MI_Band} type (a {@code MD_Band} subtype)
     *   for historical reasons. GeoAPI moves this property up in the hierarchy since this property
     *   can apply to any sample dimension, not only the measurements in the electromagnetic spectrum.
     *
     * @return Smallest distance between which separate points can be distinguished.
     * @unitof Distance
     */
    @UML(identifier="nominalSpatialResolution", obligation=OPTIONAL, specification=ISO_19115_2)
    Double getNominalSpatialResolution();

    /**
     * Type of other attribute description.
     * May be {@code null} if unspecified.
     *
     * @return type of other attribute description, or {@code null} if none.
     */
    @UML(identifier="otherPropertyType", obligation=OPTIONAL, specification=ISO_19115)
    RecordType getOtherPropertyType();

    /**
     * Instance of other attribute type that defines attributes not explicitly included in {@link CoverageContentType}.
     * May be {@code null} if unspecified.
     *
     * @return instance of other/attributeType that defines attributes, or {@code null} if none.
     */
    @UML(identifier="otherProperty", obligation=OPTIONAL, specification=ISO_19115)
    Record getOtherProperty();
}
