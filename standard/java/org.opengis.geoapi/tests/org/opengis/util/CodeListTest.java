/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    Copyright (C) 2007-2018 Open Geospatial Consortium, Inc.
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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import org.opengis.util.CodeList;
import org.opengis.util.ControlledVocabulary;
import org.opengis.metadata.constraint.Restriction;
import org.opengis.metadata.identification.CharacterSet;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Tests every {@link CodeList} types and (opportunistically) some enumerations.
 *
 * @author  Martin Desruisseaux (IRD, Geomatys)
 * @version 3.1
 * @since   2.0
 */
public final strictfp class CodeListTest {
    /**
     * The root directory of Java source code, or {@code null} if unspecified.
     */
    private String sourceDirectory;

    /**
     * Parse the Java source code of a {@code CodeList} implementation in order to find the initial capacity.
     * This method search for the numerical argument of the first {@code new ArrayList} instruction found in
     * the source code. Having an accurate initial capacity is not mandatory, but avoid a little bit of memory
     * reallocation at application startup time.
     *
     * @param  codeList  the code list for which to verify the initial capacity.
     * @return the declared initial capacity.
     * @throws IOException if an error occurred while reading the source file.
     * @throws NumberFormatException if the initial capacity can not be parsed.
     */
    private int getDeclaredCapacity(final Class<?> codeList) throws IOException {
        String line;
        int start;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(
                new File(sourceDirectory, codeList.getName().replace('.', '/').concat(".java"))), "UTF-8")))
        {
            do {
                line = in.readLine();
                if (line == null) {
                    throw new EOFException();
                }
                start = line.indexOf("new ArrayList");
            } while (start < 0);
        }
        start = line.indexOf('(', start);
        if (start >= 0) {
            final int end = line.indexOf(')', ++start);
            if (end >= 0) {
                return Integer.parseInt(line.substring(start, end));
            }
        }
        throw new NumberFormatException("Can not parse ArrayList initial capacity in following Java code line:\n" + line);
    }

    /**
     * Tests the common methods in every code lists. This method ensures that the a {@code values()} and {@code family()}
     * methods are defined for each code list, and verifies each declared code lists.
     *
     * @throws NoSuchFieldException      if a {@code CodeList} or an {@code Enum} constant can not be found.
     * @throws NoSuchMethodException     if a {@code values()} or {@code valueOf(String)} method is not found.
     * @throws IllegalAccessException    if a {@code values()} or {@code valueOf(String)} method is not public.
     * @throws InvocationTargetException if an error occurred while invoking {@code values()} or {@code valueOf(String)}.
     * @throws IOException               if an error occurred while reading source code.
     */
    @Test
    public void testAll() throws NoSuchFieldException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, IOException
    {
        sourceDirectory = System.getProperty("maven.source.directory");
        for (final Class<?> codeClass : Content.CONTROLLED_VOCABULARY.types()) {
            /*
             * Gets the values() method, which should public and static.
             * Then gets every CodeList instances returned by values().
             */
            final String className = codeClass.getCanonicalName();
            final Method valuesMethod = codeClass.getMethod("values", (Class<?>[]) null);
            assertTrue(className + ".values() is not public.", Modifier.isPublic(valuesMethod.getModifiers()));
            assertTrue(className + ".values() is not static.", Modifier.isStatic(valuesMethod.getModifiers()));
            final ControlledVocabulary[] values = (ControlledVocabulary[]) valuesMethod.invoke(null, (Object[]) null);
            assertNotNull(className + ".values() returned null.", values);
            /*
             * Tests every CodeList instances returned by values().
             * Every field should be public, static and final.
             */
            for (final ControlledVocabulary value : values) {
                final String valueName = value.name();
                final String fullName  = className + '.' + valueName;
                assertTrue(fullName + " is of unexpected type.", codeClass.isInstance(value));
                final Field field = codeClass.getField(valueName);
                final int modifiers = field.getModifiers();
                assertTrue  (fullName + " is not public.", Modifier.isPublic(modifiers));
                assertTrue  (fullName + " is not static.", Modifier.isStatic(modifiers));
                assertTrue  (fullName + " is not final.",  Modifier.isFinal (modifiers));
                assertEquals(fullName + " name mismatch.", valueName, field.getName());
                assertSame(fullName + " is not the expected instance.", value, field.get(null));
                assertArrayEquals(className + ".family() mismatch.", values, value.family());
            }
            /*
             * Gets the private VALUES field only if CodeList is the direct parent.
             */
            if (codeClass.getSuperclass().equals(CodeList.class)) {
                final String arrayName = className + ".VALUES";
                final Field field = codeClass.getDeclaredField("VALUES");
                final int modifiers = field.getModifiers();
                assertTrue (arrayName + " is not static.", Modifier.isStatic   (modifiers));
                assertTrue (arrayName + " is not final.",  Modifier.isFinal    (modifiers));
                assertFalse(arrayName + " is protected.",  Modifier.isProtected(modifiers));
                assertFalse(arrayName + " is public.",     Modifier.isPublic   (modifiers));
                field.setAccessible(true);
                final ArrayList<?> asList;
                try {
                    final Object candidate = field.get(null);
                    assertEquals(arrayName + " is not an ArrayList.", ArrayList.class, candidate.getClass());
                    asList = (ArrayList<?>) candidate;
                } catch (IllegalAccessException e) {
                    fail(arrayName + " is not accessible: " + e);
                    return;
                }
                assertArrayEquals(arrayName + " content does not match values().", values, asList.toArray());
                /*
                 * Verifies if the ArrayList initial capacity match the actual list size.
                 * It is not mandatory to have an accurate initial capacity, but it avoid
                 * a little bit of memory reallocation at application startup time.
                 */
                if (sourceDirectory != null) {
                    assertEquals(arrayName + " not properly sized.", asList.size(), getDeclaredCapacity(codeClass));
                }
            }
            /*
             * Tests valueOf(String).
             */
            final Method valueOfMethod = codeClass.getMethod("valueOf", String.class);
            for (final ControlledVocabulary value : values) {
                assertSame(value, valueOfMethod.invoke(null, value.name()));
            }
            /*
             * Tries to create a new code list element.
             */
            if (CodeList.class.isAssignableFrom(codeClass)) {
                final CodeList<?> value = (CodeList<?>) valueOfMethod.invoke(null, "MyNewCode");
                assertTrue(className + ".valueOf(String) did not created an instance of the expected class.", codeClass.isInstance(value));
                assertEquals("Newly created CodeList does not have the expected name.", "MyNewCode", value.name());
            }
        }
    }

    /**
     * Tests the {@link CharacterSet} code list. At the difference of other code lists,
     * its {@link CodeList#names()} method is overridden.
     */
    @Test
    @SuppressWarnings("deprecation")
    public void testCharacterSet() {
        final CodeList<CharacterSet> code = CharacterSet.UTF_8;
        assertEquals ("UTF_8", code.name());
        assertEquals ("utf8",  code.identifier());
        assertSame   (code, CharacterSet.valueOf("UTF_8"));
        assertNotSame(code, CharacterSet.valueOf("UTF_7"));
    }

    /**
     * Tests the {@link Restriction} code list. At the difference of other code lists,
     * its {@link CodeList#names()} method is overridden.
     */
    @Test
    public void testRestriction() {
        assertArrayEquals(new String[] {"COPYRIGHT", "copyright"}, Restriction.COPYRIGHT.names());
        assertArrayEquals(new String[] {"LICENCE", "LICENSE", "licence", "license"}, Restriction.LICENCE.names());
        assertSame(Restriction.COPYRIGHT, Restriction.valueOf("COPYRIGHT"));
        assertSame(Restriction.LICENCE,   Restriction.valueOf("LICENCE"));
        assertSame(Restriction.LICENCE,   Restriction.valueOf("LICENSE"));
    }
}
