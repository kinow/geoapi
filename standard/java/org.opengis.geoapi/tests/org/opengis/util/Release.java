/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    Copyright (C) 2005-2018 Open Geospatial Consortium, Inc.
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

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.net.MalformedURLException;
import java.io.IOException;
import java.io.Closeable;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.lang.reflect.Method;

import static org.junit.Assert.*;
import static org.junit.Assume.*;


/**
 * Information about a released GeoAPI JAR file. This is used by {@link CompatibilityTest}
 * for verifying that a new release does not introduce incompatible changes.
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @version 3.1
 * @since   3.1
 */
final class Release implements Closeable {
    /**
     * Filename extension of class files.
     */
    private static final String CLASS_EXT = ".class";

    /**
     * The package of legacy JSR-275 (Unit of Measurement).
     * This API has been replaced by JSR-363 (Unit of Measurement API).
     */
    private static final String LEGACY_UNIT_PACKAGE = "javax.measure.unit.";

    /**
     * Path to the JAR file of the GeoAPI version represented by the release.
     */
    private final File file;

    /**
     * Whether the {@linkplain #file JAR file} depends on the legacy JSR-275 API for units of measurement.
     * If {@code false}, then the JAR depends on JSR-363 instead. This field should always be {@code false}
     * (meaning that the JAR depends on JSR-363) unless we are comparing archived versions of GeoAPI.
     */
    private final boolean isUsingLegacyUnits;

    /**
     * For loading classes in {@link #file}.
     */
    private final ClassLoader loader;

    /**
     * Creates information about the given GeoAPI version.
     *
     * @param  mavenRepository  location of the Maven repository on the local machine.
     * @param  version          the GeoAPI version.
     * @param  parent           parent class loader to assign to the custom class loader.
     * @throws MalformedURLException if an error occurred while building the URL to the JAR files.
     */
    Release(final File mavenRepository, final String version, final ClassLoader parent) throws MalformedURLException {
        isUsingLegacyUnits = version.startsWith("2.") || version.startsWith("3.0.0");
        file = new File(mavenRepository, "org/opengis/geoapi/" + version + "/geoapi-" + version + ".jar");
        assumeTrue("GeoAPI " + version + " not in Maven repository.", file.isFile());
        final File depFile = new File(mavenRepository, isUsingLegacyUnits
                ? "javax/measure/jsr-275/0.9.3/jsr-275-0.9.3.jar"
                : "javax/measure/unit-api/1.0/unit-api-1.0.jar");

        assertTrue("Required dependency not found: " + depFile, depFile.isFile());
        final URL dependency = depFile.toURI().toURL();
        loader = new URLClassLoader(new URL[] {file.toURI().toURL(), dependency}, parent);
    }

    /**
     * Returns the fully qualified names of all classes found in the JAR file.
     */
    final Collection<String> listClasses() throws IOException {
        final List<String> entries = new ArrayList<>();
        try (JarFile jar = new JarFile(file)) {
            final Enumeration<JarEntry> it = jar.entries();
            while (it.hasMoreElements()) {
                String entry = it.nextElement().getName();
                if (entry.endsWith(CLASS_EXT)) {
                    entry = entry.substring(0, entry.length() - CLASS_EXT.length()).replace('/', '.');
                    assertTrue(entries.add(entry));
                }
            }
        }
        return entries;
    }

    /**
     * Loads class of the given name.
     */
    final Class<?> loadClass(final String name) throws ClassNotFoundException {
        return Class.forName(name, false, loader);
    }

    /**
     * Returns the parameters of the given methods.
     *
     * @param  source  the API from which the given method has been loaded.
     * @param  method  the method from which to get parameter types.
     */
    final Class<?>[] getParameterTypes(final Release source, final Method method) throws ClassNotFoundException {
        final Class<?>[] paramTypes = method.getParameterTypes();
        for (int i=0; i<paramTypes.length; i++) {
            if (!paramTypes[i].isPrimitive()) {
                /*
                 * GeoAPI types are not represented by the same Class instances, so we need to perform
                 * a call to Class.forName(…) for them even if the class name is exactly the same.
                 * In the case of a migration from JSR-275 to JSR-363, we also have a class name change.
                 */
                String className = paramTypes[i].getName();
                if (source.isUsingLegacyUnits != isUsingLegacyUnits) {
                    if (isUsingLegacyUnits) {
                        if (className.equals("javax.measure.Unit")) {
                            className = LEGACY_UNIT_PACKAGE + "Unit";
                        }
                    } else {
                        className = normalize(className);
                    }
                }
                paramTypes[i] = Class.forName(className, false, loader);
            }
        }
        return paramTypes;
    }

    /**
     * Replaces JSR-275 class names by JSR-363 names.
     */
    static String normalize(String className) {
        if (className.startsWith(LEGACY_UNIT_PACKAGE)) {
            className = "javax.measure." + className.substring(LEGACY_UNIT_PACKAGE.length());
        }
        return className;
    }

    /**
     * Closes the class loader.
     */
    @Override
    public void close() throws IOException {
        if (loader instanceof Closeable) {
            ((Closeable) loader).close();
        }
    }

    /**
     * Returns the JAR filename. This is used when reporting JUnit error for debugging purpose.
     */
    @Override
    public String toString() {
        String name = file.getName();
        if (isUsingLegacyUnits) {
            name += " (uses legacy units)";
        }
        return name;
    }
}
