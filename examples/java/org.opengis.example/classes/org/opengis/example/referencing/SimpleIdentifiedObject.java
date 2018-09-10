/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    This file is hereby placed into the Public Domain.
 *    This means anyone is free to do whatever they wish with this file.
 */
package org.opengis.example.referencing;

import java.util.Set;
import java.util.Objects;
import java.util.Collection;
import java.util.Collections;
import java.io.Serializable;
import org.opengis.util.GenericName;
import org.opengis.util.InternationalString;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.IdentifiedObject;


/**
 * An {@link IdentifiedObject} abstract base class, which contain only the {@linkplain #getName() name}
 * attribute. All other {@code IdentifiedObject} attributes are {@code null} or empty collections.
 *
 * <p>Since the {@linkplain #getName() name} is the only identifier contained by this class,
 * {@code SimpleIdentifiedObject} implements directly the {@link Identifier} interface.
 * Consequently this class can also be used as an {@code Identifier} implementation.</p>
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @version 3.1
 * @since   3.1
 */
public class SimpleIdentifiedObject implements IdentifiedObject, Identifier, Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -6322393597086660764L;

    /**
     * Returns the organization or party responsible for definition and maintenance of the
     * {@linkplain #code}. The {@linkplain Citation#getTitle() citation title} will be used
     * as {@linkplain #getCodeSpace() code space}.
     *
     * @see #getAuthority()
     * @see #getCodeSpace()
     */
    protected final Citation authority;

    /**
     * Alphanumeric value identifying an instance in the authority name space.
     *
     * @see #getCode()
     */
    protected final String code;

    /**
     * Creates a new object of the given authority and name.
     *
     * @param authority  the value to be returned by {@link #getAuthority()}, or {@code null} if none.
     * @param name       the name of the new object.
     */
    public SimpleIdentifiedObject(final Citation authority, final String name) {
        Objects.requireNonNull(name);
        this.authority = authority;
        this.code = name;
    }

    /**
     * Returns the name of this identified object, which is represented directly by {@code this}
     * implementation class. This is the only {@link IdentifiedObject} method in this class
     * returning a non-null and non-empty value.
     */
    @Override
    public Identifier getName() {
        return this;
    }

    /**
     * Returns the person or party responsible for maintenance of the namespace.
     * This method returns the citation given to the constructor.
     *
     * @return party responsible for definition and maintenance of the code, or {@code null} if none.
     */
    @Override
    public Citation getAuthority() {
        return authority;
    }

    /**
     * Returns the identifier or namespace in which the code is valid.
     * The default implementation returns the {@linkplain Citation#getTitle() title} of the
     * {@linkplain #getAuthority() authority}.
     *
     * @return the identifier or namespace in which the code is valid, or {@code null} if none.
     */
    @Override
    public String getCodeSpace() {
        return (authority != null) ? authority.getTitle().toString() : null;
    }

    /**
     * Returns the name given at construction time.
     *
     * @return alphanumeric value identifying an instance in the namespace.
     */
    @Override
    public String getCode() {
        return code;
    }

    /**
     * Version identifier for the namespace, as specified by the code authority.
     * The default implementation returns {@code null}.
     */
    @Override
    public String getVersion() {
        return null;
    }

    /**
     * Alternative names by which this object is identified.
     * The default implementation returns an empty set.
     *
     * @return the aliases, or an empty collection if there is none.
     */
    @Override
    public Collection<GenericName> getAlias() {
        return Collections.emptySet();
    }

    /**
     * An identifier which references elsewhere the object's defining information.
     * Alternatively an identifier by which this object can be referenced.
     * The default implementation returns an empty set.
     *
     * @return this object identifiers, or an empty set if there is none.
     */
    @Override
    public Set<Identifier> getIdentifiers() {
        return Collections.emptySet();
    }

    /**
     * Returns a natural language description of this object.
     * The default implementation returns {@code null}.
     *
     * @return the natural language description, or {@code null} if none.
     */
    @Override
    public InternationalString getDescription() {
        return null;
    }

    /**
     * Description of domain of usage, or limitations of usage, for which this object is valid.
     * Note that this method is not inherited from {@link IdentifiedObject}, but is
     * defined in sub-interfaces like {@link org.opengis.referencing.crs.SingleCRS}.
     *
     * <p>The default implementation returns {@code null}.</p>
     *
     * @return the domain of usage, or {@code null} if none.
     */
    public InternationalString getScope() {
        return null;
    }

    /**
     * Area or region or timeframe in which this object is valid.
     * Note that this method is not inherited from {@link IdentifiedObject}, but is
     * defined in sub-interfaces like {@link org.opengis.referencing.crs.SingleCRS}.
     *
     * <p>The default implementation returns {@code null}.</p>
     *
     * @return the valid domain, or {@code null} if not available.
     */
    public Extent getDomainOfValidity() {
        return null;
    }

    /**
     * Comments on or information about this object, including data source information.
     * The default implementation returns {@code null}.
     *
     * @return the remarks, or {@code null} if none.
     */
    @Override
    public InternationalString getRemarks() {
        return null;
    }

    /**
     * Returns a <cite>Well-Known Text</cite> (WKT) for this object. The default implementation
     * throws unconditionally the exception since we do not support WKT formatting.
     *
     * @return the Well Know Text for this object.
     * @throws UnsupportedOperationException if this object can't be formatted as WKT.
     */
    @Override
    public String toWKT() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a hash code value calculated from the {@linkplain #getName() name} identifier.
     * This hash code calculation is sufficient if each object name is unique.
     */
    @Override
    public int hashCode() {
        int hash = code.hashCode() ^ (int) serialVersionUID;
        if (authority != null) {
            hash += authority.hashCode() * 31;
        }
        return hash;
    }

    /**
     * Compares this identifier with the given object for equality.
     *
     * @param  object  the object to compare with this {@code SimpleIdentifiedObject}.
     * @return {@code true} if the given object is equals to this object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object != null && object.getClass() == getClass()) {
            final SimpleIdentifiedObject other = (SimpleIdentifiedObject) object;
            return code.equals(other.code) && Objects.equals(authority, other.authority);
        }
        return false;
    }

    /**
     * Returns a string representation of the {@linkplain #getName() name} identifier.
     * The default implementation build the string representation as below:
     *
     * <ul>
     *   <li>If this identifier has a {@linkplain #getCodeSpace() code space}, then returns
     *       the concatenation of the code space, the {@code ':'} character, then the
     *       {@linkplain #code}.</li>
     *   <li>Otherwise returns the {@linkplain #code} directly.</li>
     * </ul>
     */
    @Override
    public String toString() {
        final String codespace = getCodeSpace();
        return (codespace != null) ? codespace + ':' + code : code;
    }
}
