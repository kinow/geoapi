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
package org.opengis.metadata.identification;

import java.util.List;
import java.util.ArrayList;

import org.opengis.util.CodeList;
import org.opengis.annotation.UML;

import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;


/**
 * Status of the dataset or progress of a review.
 *
 * @author  Martin Desruisseaux (IRD)
 * @author  Rémi Maréchal (Geomatys)
 * @version 3.1
 * @since   2.0
 */
@UML(identifier="MD_ProgressCode", specification=ISO_19115)
public final class Progress extends CodeList<Progress> {
    /**
     * Serial number for compatibility with different versions.
     */
    private static final long serialVersionUID = 7521085150853319219L;

    /**
     * List of all enumerations of this type.
     * Must be declared before any enum declaration.
     */
    private static final List<Progress> VALUES = new ArrayList<>(18);

    /**
     * Production of the data has been completed.
     */
    @UML(identifier="completed", obligation=CONDITIONAL, specification=ISO_19115)
    public static final Progress COMPLETED = new Progress("COMPLETED");

    /**
     * Data has been stored in an offline storage facility
     */
    @UML(identifier="historicalArchive", obligation=CONDITIONAL, specification=ISO_19115)
    public static final Progress HISTORICAL_ARCHIVE = new Progress("HISTORICAL_ARCHIVE");

    /**
     * Data is no longer relevant.
     */
    @UML(identifier="obsolete", obligation=CONDITIONAL, specification=ISO_19115)
    public static final Progress OBSOLETE = new Progress("OBSOLETE");

    /**
     * Data is continually being updated.
     */
    @UML(identifier="onGoing", obligation=CONDITIONAL, specification=ISO_19115)
    public static final Progress ON_GOING = new Progress("ON_GOING");

    /**
     * Fixed date has been established upon or by which the data will be created or updated.
     */
    @UML(identifier="planned", obligation=CONDITIONAL, specification=ISO_19115)
    public static final Progress PLANNED = new Progress("PLANNED");

    /**
     * Data needs to be generated or updated.
     */
    @UML(identifier="required", obligation=CONDITIONAL, specification=ISO_19115)
    public static final Progress REQUIRED = new Progress("REQUIRED");

    /**
     * Data is currently in the process of being created.
     */
    @UML(identifier="underDevelopment", obligation=CONDITIONAL, specification=ISO_19115)
    public static final Progress UNDER_DEVELOPMENT = new Progress("UNDER_DEVELOPMENT");

    /**
     * Progress concluded and no changes will be accepted.
     *
     * @since 3.1
     */
    @UML(identifier="final", obligation=CONDITIONAL, specification=ISO_19115)
    public static final Progress FINAL = new Progress("FINAL");

    /**
     * Committed to, but not yet addressed.
     *
     * @since 3.1
     */
    @UML(identifier="pending", obligation=CONDITIONAL, specification=ISO_19115)
    public static final Progress PENDING = new Progress("PENDING");

    /**
     * Item is no longer recommended for use. It has not been superseded by another item.
     *
     * @since 3.1
     */
    @UML(identifier="retired", obligation=CONDITIONAL, specification=ISO_19115)
    public static final Progress RETIRED = new Progress("RETIRED");

    /**
     * Replaced by new.
     *
     * @since 3.1
     */
    @UML(identifier="superseded", obligation=CONDITIONAL, specification=ISO_19115)
    public static final Progress SUPERSEDED = new Progress("SUPERSEDED");

    /**
     * Provisional changes likely before resource becomes final of complete.
     *
     * @since 3.1
     */
    @UML(identifier="tentative", obligation=CONDITIONAL, specification=ISO_19115)
    public static final Progress TENTATIVE = new Progress("TENTATIVE");

    /**
     * Acceptable under specific condition.
     *
     * @since 3.1
     */
    @UML(identifier="valid", obligation=CONDITIONAL, specification=ISO_19115)
    public static final Progress VALID = new Progress("VALID");

    /**
     * Agreed to by sponsor.
     *
     * @since 3.1
     */
    @UML(identifier="accepted", obligation=CONDITIONAL, specification=ISO_19115)
    public static final Progress ACCEPTED = new Progress("ACCEPTED");

    /**
     * Rejected by sponsor.
     *
     * @since 3.1
     */
    @UML(identifier="notAccepted", obligation=CONDITIONAL, specification=ISO_19115)
    public static final Progress NOT_ACCEPTED = new Progress("NOT_ACCEPTED");

    /**
     * Removed from consideration.
     *
     * @since 3.1
     */
    @UML(identifier="withdrawn", obligation=CONDITIONAL, specification=ISO_19115)
    public static final Progress WITHDRAWN = new Progress("WITHDRAWN");

    /**
     * Suggested that development needs to be undertaken.
     *
     * @since 3.1
     */
    @UML(identifier="proposed", obligation=CONDITIONAL, specification=ISO_19115)
    public static final Progress PROPOSED = new Progress("PROPOSED");

    /**
     * Resource superseded and will become obsolete, use only for historical purposes.
     *
     * @since 3.1
     */
    @UML(identifier="deprecated", obligation=CONDITIONAL, specification=ISO_19115)
    public static final Progress DEPRECATED = new Progress("DEPRECATED");

    /**
     * Constructs an element of the given name. The new element is
     * automatically added to the list returned by {@link #values()}.
     *
     * @param name  the name of the new element. This name shall not be in use by another element of this type.
     */
    private Progress(final String name) {
        super(name, VALUES);
    }

    /**
     * Returns the list of {@code Progress}s.
     *
     * @return the list of codes declared in the current JVM.
     */
    public static Progress[] values() {
        synchronized (VALUES) {
            return VALUES.toArray(new Progress[VALUES.size()]);
        }
    }

    /**
     * Returns the list of codes of the same kind than this code list element.
     * Invoking this method is equivalent to invoking {@link #values()}, except that
     * this method can be invoked on an instance of the parent {@code CodeList} class.
     *
     * @return all code {@linkplain #values() values} for this code list.
     */
    @Override
    public Progress[] family() {
        return values();
    }

    /**
     * Returns the progress that matches the given string, or returns a
     * new one if none match it. More specifically, this methods returns the first instance for
     * which <code>{@linkplain #name() name()}.{@linkplain String#equals equals}(code)</code>
     * returns {@code true}. If no existing instance is found, then a new one is created for
     * the given name.
     *
     * @param  code  the name of the code to fetch or to create.
     * @return a code matching the given name.
     */
    public static Progress valueOf(String code) {
        return valueOf(Progress.class, code);
    }
}
