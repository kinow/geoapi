/*$************************************************************************************************
 **
 ** $Id$
 **
 ** $Source$
 **
 ** Copyright (C) 2005 Open GIS Consortium, Inc.
 ** All Rights Reserved. http://www.opengis.org/legal/
 **
 *************************************************************************************************/
package org.opengis.coverage;

// J2SE direct dependencies
import java.util.Collection;
import java.util.Set;

// OpenGIS direct dependencies
import org.opengis.spatialschema.geometry.DirectPosition;

// Annotations
import org.opengis.annotation.UML;
import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;


/**
 * Basis for interpolating feature attribute values within a {@linkplain ContinuousCoverage
 * continuous coverage}. {@code ValueObject}s may be generated in the execution of an
 * {@link Coverage#evaluate(DirectPosition,Collection) evaluate} operation, and need not
 * be persistent.
 *
 * @author Martin Desruisseaux
 *
 * @todo Consider renaming as Value.
 */
@UML(identifier="CV_ValueObject", specification=ISO_19123)
public interface ValueObject {
    /**
     * Returns the set of <var>geometry</var>-<var>value</var> pairs that provide the basis for
     * constructing this {@code ValueObject} and for evaluating the {@linkplain ContinuousCoverage
     * continuous coverage} at {@linkplain DirectPosition direct positions} within this value object.
     */
    @UML(identifier="controlValue", obligation=MANDATORY, specification=ISO_19123)
    Set<? extends GeometryValuePair> getControlValues();

    /**
     * The domain object constructed from the {@linkplain GeometryValuePair#getGeometry
     * domain objects} of the <var>geometry</var>-<var>value</var> pairs that are linked
     * to this value object by the {@linkplain #getControlValues control values}.
     */
    @UML(identifier="geometry", obligation=MANDATORY, specification=ISO_19123)
    DomainObject getGeometry();  

    /**
     * Holds the values of the parameters required to execute the interpolate operation, as
     * specified by the {@linkplain ContinuousCoverage#getInterpolationParameterTypes
     * interpolation parameter types} attribute of the continuous coverage.
     *
     * @todo Consider leveraging the parameter package.
     */
    @UML(identifier="interpolationParameters", obligation=OPTIONAL, specification=ISO_19123)
    Object getInterpolationParameters(); // TODO

    /**
     * Returns the record of feature attribute values computed for the specified direct position.
     *
     * @todo ISO uses {@code Record} return type, which is not yet defined in GeoAPI.
     */
    @UML(identifier="interpolate", obligation=MANDATORY, specification=ISO_19123)
    Object interpolate(DirectPosition p);
}