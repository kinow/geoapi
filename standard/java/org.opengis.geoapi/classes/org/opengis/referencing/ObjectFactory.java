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
package org.opengis.referencing;

import java.util.Map;
import java.util.Locale;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.util.Factory;
import org.opengis.util.InternationalString;
import org.opengis.util.GenericName;


/**
 * Base interface for all factories of {@linkplain IdentifiedObject identified objects}.
 * Factories build up complex objects from simpler objects or values.
 * This factory allows applications to make
 * {@linkplain org.opengis.referencing.cs.CoordinateSystem coordinate systems},
 * {@linkplain org.opengis.referencing.datum.Datum datum} or
 * {@linkplain org.opengis.referencing.crs.CoordinateReferenceSystem coordinate reference systems}
 * that cannot be created by an {@linkplain AuthorityFactory authority factory}.
 * This factory is very flexible, whereas the authority factory is easier to use.
 *
 * <h3>Object properties</h3>
 * Most factory methods expect a {@link Map Map&lt;String,?&gt;} argument.
 * The table below lists the keys that {@code ObjectFactory} implementations shall accept,
 * together with the type of values associated to those keys (the <cite>alternative types</cite> column
 * gives examples of types that factory implementations may accept as well for convenience).
 * A value for the {@code "name"} key is mandatory, while all other properties are optional.
 * Factory methods shall ignore unknown properties.
 *
 * <table class="ogc">
 *   <caption>Keys for standard properties</caption>
 *   <tr>
 *     <th>Key</th>
 *     <th>Value type</th>
 *     <th>Alternative types</th>
 *     <th>Value returned by</th>
 *   </tr>
 *   <tr>
 *     <td>{@value org.opengis.referencing.IdentifiedObject#NAME_KEY}</td>
 *     <td>{@link Identifier}</td>
 *     <td>{@link String} (see <cite>alternative</cite> below)</td>
 *     <td>{@link IdentifiedObject#getName()}</td>
 *   </tr>
 *   <tr>
 *     <td>{@value org.opengis.referencing.IdentifiedObject#ALIAS_KEY}</td>
 *     <td><code>{@linkplain GenericName}[]</code></td>
 *     <td>{@link GenericName}, {@link String} or <code>{@linkplain String}[]</code></td>
 *     <td>{@link IdentifiedObject#getAlias()}</td>
 *   </tr>
 *   <tr>
 *     <td>{@value org.opengis.referencing.IdentifiedObject#IDENTIFIERS_KEY}</td>
 *     <td><code>{@linkplain Identifier}[]</code></td>
 *     <td>{@link Identifier}</td>
 *     <td>{@link IdentifiedObject#getIdentifiers()}</td>
 *   </tr>
 *   <tr>
 *     <td>{@value org.opengis.referencing.IdentifiedObject#REMARKS_KEY}</td>
 *     <td>{@link InternationalString}</td>
 *     <td>{@link String} (see <cite>localization</cite> below)</td>
 *     <td>{@link IdentifiedObject#getRemarks()}</td>
 *   </tr>
 * </table>
 *
 * <div class="note"><b>Note:</b>
 * Multi-values are arrays instead than collections in order to allow implementations to check the element
 * type by Java reflection. Such reflection can not be performed on collections because of type erasure.</div>
 *
 * <p>Implementations may allow an alternative way to define the {@code "name"} property for user convenience:</p>
 * <table class="ogc">
 *   <caption>Convenience keys (non-standard)</caption>
 *   <tr>
 *     <th>Key</th>
 *     <th>Value type</th>
 *     <th>Alternative types</th>
 *     <th>Value returned by</th>
 *   </tr>
 *   <tr>
 *     <td>{@value org.opengis.referencing.IdentifiedObject#NAME_KEY}</td>
 *     <td>{@link String}</td>
 *     <td></td>
 *     <td>{@link Identifier#getCode()}</td>
 *   </tr>
 *   <tr>
 *     <td>{@value org.opengis.metadata.Identifier#AUTHORITY_KEY}</td>
 *     <td>{@link Citation}</td>
 *     <td>{@link String}</td>
 *     <td>{@link Identifier#getAuthority()}</td>
 *   </tr>
 *   <tr>
 *     <td>{@value org.opengis.metadata.Identifier#CODESPACE_KEY}</td>
 *     <td>{@link String}</td>
 *     <td></td>
 *     <td>{@link Identifier#getCodeSpace()}</td>
 *   </tr>
 *   <tr>
 *     <td>{@value org.opengis.metadata.Identifier#VERSION_KEY}</td>
 *     <td>{@link String}</td>
 *     <td></td>
 *     <td>{@link Identifier#getVersion()}</td>
 *   </tr>
 * </table>
 *
 * <h3>Localization</h3>
 * Localizable attributes like {@code "remarks"} can be specified either as a single {@code InternationalString},
 * or as one or many {@code String}s associated to keys suffixed by a language and country code.
 * For example the {@code "remarks_fr"} key stands for remarks in {@linkplain Locale#FRENCH French} and the
 * {@code "remarks_fr_CA"} key stands for remarks in {@linkplain Locale#CANADA_FRENCH French Canadian}.
 *
 * @departure harmonization
 *   This interface is not part of any OGC specification. It is added for uniformity,
 *   in order to provide a common base class for all referencing factories producing
 *   {@code IdentifiedObject} instances.
 *
 * @author  Martin Desruisseaux (IRD)
 * @version 3.0
 * @since   2.0
 */
public interface ObjectFactory extends Factory {
}
