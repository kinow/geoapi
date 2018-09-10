/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    Copyright (C) 2009-2018 Open Geospatial Consortium, Inc.
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
package org.opengis.metadata;

import java.util.Set;
import java.util.HashSet;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.util.Locale;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.opengis.geoapi.Content;
import org.opengis.util.CodeList;
import org.opengis.annotation.UML;
import org.opengis.annotation.Specification;

import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Tests the content of {@code Descriptions.properties} file.
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @version 3.1
 * @since   2.3
 */
public final strictfp class DescriptionsTest {
    /**
     * Whether to test the given type.
     */
    private static boolean filter(final Class<?> type) {
        if (type.getName().startsWith("org.opengis.metadata.")) {
            // Exclude some types for which descriptions are still missing.
            // TODO - https://github.com/opengeospatial/geoapi/issues/24
            return (type != org.opengis.metadata.constraint.Releasability.class) &&
                   (type != org.opengis.metadata.content.AttributeGroup.class) &&
                   (type != org.opengis.metadata.content.FeatureTypeInfo.class) &&
                   (type != org.opengis.metadata.MetadataScope.class);
        }
        // Some additional types for which descriptions are provided.
        return (type == org.opengis.annotation.Obligation.class) ||
               (type == org.opengis.parameter.ParameterDirection.class) ||
               (type == org.opengis.referencing.ReferenceSystemType.class);
    }

    /**
     * Asserts that the given key exists in the given resource bundle.
     */
    private static void assertResourceExists(final ResourceBundle resources, final String identifier) {
        final String value;
        try {
            value = resources.getString(identifier);
        } catch (MissingResourceException e) {
            fail(e.toString());
            return;
        }
        assertNotNull(identifier, value);
    }

    /**
     * Ensures that every metadata interfaces have a description, and that there is no extra definitions.
     * This test is theoretically locale-sensitive since we search for the resources in the current locale.
     * However it should works for every locales since the English locale is used as a fallback.
     */
    @Test
    public void testAll() {
        final ResourceBundle resources = ResourceBundle.getBundle("org.opengis.metadata.Descriptions");
        /*
         * Get the set of keys. We will remove entries from this set as we found them.
         * When this test is finished, the set of keys should be empty.
         */
        final Set<String> keys = new HashSet<>();
        for (final Enumeration<String> e=resources.getKeys(); e.hasMoreElements();) {
            final String key = e.nextElement();
            assertTrue("Duplicated key" , keys.add(key));
        }
        for (final Class<?> type : Content.ALL.types()) {
            if (!filter(type)) continue;
            UML uml = type.getAnnotation(UML.class);
            assertNotNull("Missing UML annotation", uml);
            final String classIdentifier = uml.identifier();
            if (Enum.class.isAssignableFrom(type)) {
                assertResourceExists(resources, classIdentifier);
                assertTrue(classIdentifier, keys.remove(classIdentifier));
                for (final Field code : type.getDeclaredFields()) {
                    uml = code.getAnnotation(UML.class);
                    if (uml != null) {
                        final String identifier = classIdentifier + '.' + uml.identifier();
                        assertResourceExists(resources, identifier);
                        assertTrue(identifier, keys.remove(identifier));
                    }
                }
            } else if (CodeList.class.isAssignableFrom(type)) {
                /*
                 * Check a code list and its fields. Note that the fields without UML
                 * annotation (for example serialVersionUID) must be ignored. We also
                 * need to ignore ISO 19115-2 code list items for now.
                 */
                if (uml.specification() == Specification.ISO_19115_2) {
                    // Resources for ISO 19115-2 code lists are not yet provided.
                    continue;
                }
                assertResourceExists(resources, classIdentifier);
                assertTrue(classIdentifier, keys.remove(classIdentifier));
                for (final Field code : type.getDeclaredFields()) {
                    if (code.isAnnotationPresent(Deprecated.class)) {
                        continue;                                       // Skip deprecated fields or methods.
                    }
                    uml = code.getAnnotation(UML.class);
                    if (uml != null) {
                        if (uml.specification() == Specification.ISO_19115_2) {
                            // Resources for ISO 19115-2 code lists are not yet provided.
                            continue;
                        }
                        if (uml.identifier().equals("8859part12")) {
                            // Special case: "A future ISO/IEC 8-bit single-byte coded graphic
                            // character set" declared in ISO 19115 but not yet in gmxCodelists.xml.
                            continue;
                        }
                        final String identifier = classIdentifier + '.' + uml.identifier();
                        assertResourceExists(resources, identifier);
                        assertTrue(identifier, keys.remove(identifier));
                    }
                }
            } else {
                /*
                 * Check a metadata interface and its methods. Note that a few methods are
                 * GeoAPI extensions without UML (e.g. explicit definition of hashCode()),
                 * which must be excluded.
                 */
                assertResourceExists(resources, classIdentifier);
                assertTrue(classIdentifier, keys.remove(classIdentifier));
                for (final Method method : type.getDeclaredMethods()) {
                    uml = method.getAnnotation(UML.class);
                    if (uml != null) {
                        final String identifier = classIdentifier + '.' + uml.identifier();
                        assertResourceExists(resources, identifier);
                        assertTrue(identifier, keys.remove(identifier));
                    }
                }
            }
        }
        assertTrue("Some keys do not map any class or method: " + keys, keys.isEmpty());
    }

    /**
     * Tests the content of a few specific items in the English locale.
     */
    @Test
    public void testEnglish() {
        final ResourceBundle resources = ResourceBundle.getBundle("org.opengis.metadata.Descriptions", Locale.ENGLISH);
        assertEquals("Unique identifier for the resource. Example: Universal Product Code (UPC), National Stock Number (NSN).",
                resources.getString("CI_Citation.identifier"));
    }
}
