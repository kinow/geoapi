/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    Copyright (C) 2006-2018 Open Geospatial Consortium, Inc.
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
package org.opengis.temporal;

import java.util.Collection;
import java.util.Date;
import org.opengis.annotation.UML;

import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;
import org.opengis.referencing.IdentifiedObject;

/**
 * Provides a reference to the ordinal era in which the instant occurs.
 *
 * @author Alexander Petkov
 * @author Martin Desruisseaux (Geomatys)
 * @author Remi Marechal (Geomatys).
 * @since   2.3
 * @version 4.0
 */
@UML(identifier="TM_OrdinalEra", specification=ISO_19108)
public interface OrdinalEra  extends IdentifiedObject {

    /**
     * Returns the beginning {@link Date} at which this {@link OrdinalEra} begun, or {@code null} if none.
     * 
     * @return the beginning {@link Date} at which this {@link OrdinalEra} begun, or {@code null} if none.
     */
    @UML(identifier="begin", obligation=OPTIONAL, specification=ISO_19108)
    Date getBegin();
    
    /**
     * Returns the ending {@link Date} at which this {@link OrdinalEra} stop, or {@code null} if none.
     * 
     * @return the ending {@link Date} at which this {@link OrdinalEra} stop, or {@code null} if none.
     */
    @UML(identifier="end", obligation=OPTIONAL, specification=ISO_19108)
    Date getEnd();
    
    /**
     * Returns {@link OrdinalEra} sequence that subdivide or compose this {@link OrdinalEra}.
     * 
     * @return {@link OrdinalEra} sequence that subdivide or compose this {@link OrdinalEra}.
     * @since 4.0
     */
    @UML(identifier="member", obligation=MANDATORY, specification=ISO_19108)
    Collection<OrdinalEra> getMember();
}
