/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    Copyright (C) 2004-2018 Open Geospatial Consortium, Inc.
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
package org.opengis.parameter;

import org.opengis.annotation.UML;
import org.opengis.annotation.Classifier;
import org.opengis.annotation.Stereotype;
import org.opengis.annotation.Specification;
import org.opengis.util.MemberName;                     // For javadoc
import org.opengis.util.InternationalString;
import org.opengis.metadata.Identifier;
import org.opengis.referencing.IdentifiedObject;

import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;


/**
 * Abstract definition of a parameter or group of parameters used by an operation method.
 * This interface combines information provided by Referencing by Coordinates (ISO 19111),
 * Service Metadata (ISO 19115) and Web Processing Services (WPS) standards.
 * The main information are:
 *
 * <table class="ogc">
 *   <caption>Main parameter properties</caption>
 *   <tr>
 *     <th>Getter method</th>
 *     <th class="sep">ISO 19111</th>
 *     <th class="sep">WPS</th>
 *     <th class="sep">ISO 19115</th>
 *     <th class="sep">Remarks</th>
 *   </tr>
 *   <tr>
 *     <td>{@link #getName()}</td>
 *     <td class="sep">{@code name}</td>
 *     <td class="sep">{@code Identifier}</td>
 *     <td class="sep">{@code name}</td>
 *     <td class="sep">See {@linkplain #getName() method javadoc} for {@code MemberName} ↔ {@code Identifier} mapping.</td>
 *   </tr>
 *   <!-- "Title" (WPS) equivalent to "designation" (Feature), but not yet provided. -->
 *   <tr>
 *     <td>{@link #getDescription()}</td>
 *     <td class="sep"></td>
 *     <td class="sep">{@code Abstract}</td>
 *     <td class="sep">{@code description}</td>
 *     <td class="sep">Also known as “definition”.</td>
 *   </tr>
 *   <tr>
 *     <td>{@link #getDirection()}</td>
 *     <td class="sep"></td>
 *     <td class="sep"></td>
 *     <td class="sep">{@code direction}</td>
 *     <td class="sep">Tells if the parameter is a WPS {@code Input} or {@code Output} structure.</td>
 *   </tr>
 *   <tr>
 *     <td>{@link #getMinimumOccurs()}</td>
 *     <td class="sep">{@code minimumOccurs}</td>
 *     <td class="sep">{@code MinOccurs}</td>
 *     <td class="sep">{@code optionality}</td>
 *     <td class="sep">{@code optionality   = (minimumOccurs > 0)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link #getMaximumOccurs()}</td>
 *     <td class="sep">{@code maximumOccurs}</td>
 *     <td class="sep">{@code MaxOccurs}</td>
 *     <td class="sep">{@code repeatability}</td>
 *     <td class="sep">{@code repeatability = (maximumOccurs > 1)}</td>
 *   </tr>
 * </table>
 *
 * @departure rename
 *   GeoAPI uses a name which contains the "{@code Descriptor}" word for consistency with other
 *   libraries in Java (e.g. {@code ParameterListDescriptor} in Java Advanced Imaging).
 *
 * @author  Martin Desruisseaux (IRD)
 * @author  Jody Garnett (Refractions Research)
 * @version 3.1
 * @since   2.0
 *
 * @see GeneralParameterValue
 */
@Classifier(Stereotype.ABSTRACT)
@UML(identifier="CC_GeneralOperationParameter", specification=ISO_19111)
public interface GeneralParameterDescriptor extends IdentifiedObject {
    /**
     * The name, as used by the service or operation for this parameter.
     *
     * <div class="note"><b>Note on Service Metadata name</b><br>
     * The metadata standard ({@linkplain Specification#ISO_19111 ISO 19115}) defines the {@code name}
     * property as of type {@link MemberName} instead than {@code Identifier}. The details of mapping
     * the former to the later are left to implementors, but the following can be used as guidelines:
     *
     * <table class="ogc">
     *   <caption>Suggested mapping from {@code MemberName} to {@code Identifier}</caption>
     *   <tr>
     *     <th>Member name property</th>
     *     <th>Equivalence</th>
     *     <th>Remarks</th>
     *   </tr>
     *   <tr>
     *     <td><code>{@linkplain MemberName#scope()}.name().toString()</code></td>
     *     <td>{@link Identifier#getCodeSpace()}</td>
     *     <td></td>
     *   </tr>
     *   <tr>
     *     <td>{@link MemberName#toString()}</td>
     *     <td>{@link Identifier#getCode()}</td>
     *     <td></td>
     *   </tr>
     *   <tr>
     *     <td>{@link MemberName#getAttributeType()}</td>
     *     <td>{@link ParameterDescriptor#getValueClass()}</td>
     *     <td>See {@link org.opengis.util.TypeName} for a suggested mapping to {@link java.lang.Class}.</td>
     *   </tr>
     * </table>
     *
     * Some implementations may allow the {@code Identifier} to be casted to {@link MemberName}.
     * Alternatively, the member type can also be specified in the {@linkplain #getAlias() aliases} list.
     * </div>
     *
     * @return the name, as used by the service or operation for this parameter.
     */
    @Override
    @UML(identifier="name", obligation=MANDATORY, specification=ISO_19111)
    Identifier getName();

    /**
     * Indication if the parameter is an input to the service, an output or both.
     * This information applies mostly to <cite>service metadata</cite>.
     *
     * @return indication if the parameter is an input to the service, an output or both,
     *         or {@code null} if unspecified.
     *
     * @since 3.1
     */
    @UML(identifier="direction", obligation=OPTIONAL, specification=ISO_19115)
    ParameterDirection getDirection();

    /**
     * A narrative explanation of the role of the parameter.
     *
     * @return a narrative explanation of the role of the parameter, or {@code null} if none.
     *
     * @since 3.1
     *
     * @see #getName()
     * @see #getRemarks()
     */
    @UML(identifier="description", obligation=OPTIONAL, specification=ISO_19115)
    InternationalString getDescription();

    /**
     * The minimum number of times that values for this parameter group or parameter are required.
     * The default value is 1. A value of 0 means an optional parameter.
     *
     * @return the minimum occurrence.
     *
     * @see #getMaximumOccurs()
     */
    @UML(identifier="minimumOccurs", obligation=OPTIONAL, specification=ISO_19111)
    int getMinimumOccurs();

    /**
     * The maximum number of times that values for this parameter group or parameter can be included.
     * The default value is 1. A value greater than 1 means a repeatable parameter.
     *
     * <p>If this parameter is an instance of {@link ParameterDescriptor} used for the description of
     * {@link org.opengis.referencing.operation.OperationMethod} parameters, then the value is always 1.
     * In other contexts (e.g. parameter group or service metadata) it may vary.
     *
     * @departure generalization
     *   Moved up (in the interface hierarchy) the {@code maximumOccurs} method from
     *   {@code ParameterDescriptorGroup} into this super-interface, for parallelism
     *   with the {@code minimumOccurs} method.
     *
     * @return the maximum occurrence, or {@link Integer#MAX_VALUE} if there is no limit.
     *
     * @see #getMinimumOccurs()
     */
    @UML(identifier="CC_OperationParameterGroup.maximumOccurs", obligation=OPTIONAL, specification=ISO_19111)
    int getMaximumOccurs();

    /**
     * Creates a new instance of {@linkplain GeneralParameterValue parameter value or group}
     * initialized with the {@linkplain ParameterDescriptor#getDefaultValue default value(s)}.
     * The {@linkplain GeneralParameterValue#getDescriptor parameter value descriptor} for
     * the created parameter value(s) will be {@code this} object.
     *
     * @return a new parameter initialized to its default value.
     *
     * @departure extension
     *   This method is not part of the ISO specification. It is provided in GeoAPI as a kind of
     *   factory method.
     */
    GeneralParameterValue createValue();
}
