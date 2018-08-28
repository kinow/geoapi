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
 * This module is a "proof of concept" implementation of GeoAPI referencing interfaces using
 * JNI binding to the PROJ library. The <code>org.proj4</code> package contains the native methods,
 * mostly independent from GeoAPI interfaces. The <code>org.opengis.wrapper.proj4</code> package
 * contains the GeoAPI implementations on top of the <code>org.proj4</code> package.
 *
 * @version 4.0
 * @since 2.2
 */
module org.opengis.wrapper.proj4 {
    requires transitive org.opengis.geoapi;
}
