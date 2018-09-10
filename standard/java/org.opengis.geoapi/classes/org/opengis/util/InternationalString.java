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
package org.opengis.util;

import java.util.Map;  // For javadoc
import java.util.Locale;
import org.opengis.annotation.UML;
import static org.opengis.annotation.Specification.ISO_19115;


/**
 * A {@linkplain String string} that has been internationalized into several {@linkplain Locale locales}.
 * This interface is used as a replacement for the {@link String} type whenever an attribute needs to be
 * internationalization capable.
 *
 * <p>The {@linkplain Comparable natural ordering} is defined by the {@linkplain String#compareTo
 * lexicographical ordering of strings} in the default locale, as returned by {@link #toString()}.
 * This string also defines the {@linkplain CharSequence character sequence} for this object.</p>
 *
 * @departure rename
 *   This is called {@code PT_FreeText} in ISO 19115 standard, and can be applied to all metadata
 *   elements who's data type is {@code CharacterString} and domain is “free text”. GeoAPI uses the
 *   {@code InternationalString} name for historical reasons and for consistency with similar object in
 *   <a href="http://www.jcp.org/en/jsr/detail?id=150">JSR-150</a> <cite>(Internationalization Service for J2EE)</cite>.
 *
 * @author  Martin Desruisseaux (IRD)
 * @version 3.0
 * @since   2.0
 *
 * @see NameFactory#createInternationalString(Map)
 */
@UML(identifier="PT_FreeText", specification=ISO_19115)
public interface InternationalString extends CharSequence, Comparable<InternationalString> {
    /**
     * Returns this string in the given locale. If no string is available in the given locale,
     * then some fallback locale is used. The fallback locale is implementation-dependent, and
     * is not necessarily the same than the default locale used by the {@link #toString()} method.
     *
     * @param  locale  the desired locale for the string to be returned.
     * @return the string in the given locale if available, or in an
     *         implementation-dependent fallback locale otherwise.
     *
     * @see Locale#getDefault()
     * @see Locale#ROOT
     */
    String toString(Locale locale);

    /**
     * Returns this string in the default locale. The default locale is implementation-dependent.
     * It may be the {@linkplain Locale#getDefault() system default}, the {@linkplain Locale#ROOT
     * root locale} or any other locale at implementation choice.
     *
     * <p>All methods from {@link CharSequence} operate on this string.
     * This string is also used as the criterion for {@linkplain Comparable natural ordering}.</p>
     *
     * @return the string in the default locale.
     */
    @Override
    String toString();
}
