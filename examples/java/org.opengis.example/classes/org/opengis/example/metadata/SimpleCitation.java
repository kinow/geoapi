/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    This file is hereby placed into the Public Domain.
 *    This means anyone is free to do whatever they wish with this file.
 */
package org.opengis.example.metadata;

import java.util.Set;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.io.Serializable;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.CitationDate;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.metadata.citation.PresentationForm;
import org.opengis.metadata.citation.Responsibility;
import org.opengis.metadata.citation.Series;
import org.opengis.metadata.identification.BrowseGraphic;
import org.opengis.util.InternationalString;

import static java.util.Collections.emptySet;


/**
 * A {@link Citation} containing only a {@linkplain #getTitle() title} attribute as an
 * {@link InternationalString}. All other citation attributes are {@code null} or empty
 * collections.
 *
 * <p>This class can also be used as an {@link InternationalString} implementation. Because
 * there is only one attribute - the {@linkplain #getTitle() title} - there is no ambiguity
 * about the value represented by the citation or the international string.</p>
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @version 3.1
 * @since   3.1
 */
public class SimpleCitation implements Citation, InternationalString, Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 5270363538303937381L;

    /**
     * The <cite>Open Geospatial Consortium</cite> authority.
     *
     * @see org.opengis.util.Factory#getVendor()
     */
    public static final SimpleCitation OGC = new SimpleCitation("OGC");

    /**
     * The <cite>European Petroleum Survey Group</cite> authority.
     * This authority provides many CRS definitions.
     *
     * @see Identifier#getAuthority()
     * @see org.opengis.referencing.AuthorityFactory#getAuthority()
     */
    public static final SimpleCitation EPSG = new SimpleCitation("EPSG");

    /**
     * The citation title to be returned by {@link #getTitle()} as an {@link InternationalString}.
     * This is also the value returned by the {@code InternationalString} methods like
     * {@link #toString(Locale)} and {@link #toString()}.
     *
     * @see #getTitle()
     * @see #toString()
     * @see #toString(Locale)
     */
    protected final String title;

    /**
     * Creates a new citation having the given title. The given string will be returned,
     * directly or indirectly, by the {@link #getTitle()}, {@link #toString()} and
     * {@link #toString(Locale)} methods.
     *
     * @param title  the citation title to be returned indirectly by {@link #getTitle()}.
     */
    public SimpleCitation(final String title) {
        Objects.requireNonNull(title);
        this.title = title;
    }

    /**
     * Returns the number of characters in the {@linkplain #getTitle() title}.
     *
     * @return the number of {@code char}s in the {@linkplain #getTitle() title}.
     */
    @Override
    public int length() {
        return title.length();
    }

    /**
     * Returns the {@linkplain #getTitle() title} character at the given index
     *
     * @param  index  the index of the {@code char} value to be returned.
     * @return the specified {@code char} value.
     */
    @Override
    public char charAt(final int index) {
        return title.charAt(index);
    }

    /**
     * Returns a {@linkplain String#substring(int, int) substring} of the title for the
     * given range of index.
     *
     * @param  start  the start index, inclusive.
     * @param  end    the end index, exclusive.
     * @return the specified substring of the {@linkplain #getTitle() title}.
     */
    @Override
    public String subSequence(final int start, final int end) {
        return title.substring(start, end);
    }

    /**
     * Returns the {@linkplain #getTitle() title} as an unlocalized string.
     * This method returns directly the string given to the constructor.
     *
     * @return the string given to the constructor.
     *
     * @see #getTitle()
     */
    @Override
    public String toString() {
        return title;
    }

    /**
     * Returns the {@linkplain #getTitle() title}, ignoring the given locale. For localization
     * support, an other class (or a subclass of this {@code SimpleCitation} class) is required.
     *
     * @param  locale  ignored by the {@code SimpleCitation} implementation.
     * @return the string given to the constructor, localized if supported by the implementation.
     *
     * @see #getTitle()
     */
    @Override
    public String toString(final Locale locale) {
        return title;
    }

    /*
     * Returns the citation title, which is represented directly by {@code this} implementation
     * class. This is the only {@link Citation} method in this class returning a non-null and
     * non-empty value.
     *
     * @see #toString()
     */
    @Override
    public InternationalString getTitle() {
        return this;
    }

    /**
     * Short name or other language name by which the cited information is known.
     * The default implementation returns an empty set.
     */
    @Override
    public Set<InternationalString> getAlternateTitles() {
        return emptySet();
    }

    /**
     * Reference date for the cited resource.
     * The default implementation returns an empty set.
     */
    @Override
    public Set<CitationDate> getDates() {
        return emptySet();
    }

    /**
     * Version of the cited resource.
     * The default implementation returns {@code null}.
     */
    @Override
    public InternationalString getEdition() {
        return null;
    }

    /**
     * Date of the edition, or {@code null} if none.
     * The default implementation returns {@code null}.
     */
    @Override
    public Date getEditionDate() {
        return null;
    }

    /**
     * Unique identifier for the resource.
     * The default implementation returns an empty set.
     */
    @Override
    public Set<Identifier> getIdentifiers() {
        return emptySet();
    }

    /**
     * Name and position information for an individual or organization that is responsible
     * for the resource. The default implementation returns an empty set.
     */
    @Override
    public Set<Responsibility> getCitedResponsibleParties() {
        return emptySet();
    }

    /**
     * Mode in which the resource is represented, or an empty set if none.
     * The default implementation returns an empty set.
     */
    @Override
    public Set<PresentationForm> getPresentationForms() {
        return emptySet();
    }

    /**
     * Information about the series, or aggregate dataset, of which the dataset is a part.
     * The default implementation returns {@code null}.
     */
    @Override
    public Series getSeries() {
        return null;
    }

    /**
     * Other information required to complete the citation that is not recorded elsewhere.
     * The default implementation returns an empty set.
     */
    @Override
    public Set<InternationalString> getOtherCitationDetails() {
        return emptySet();
    }

    /**
     * @deprecated Removed in ISO 19115:2014.
     */
    @Override
    @Deprecated
    public InternationalString getCollectiveTitle() {
        return null;
    }

    /**
     * International Standard Book Number, or {@code null} if none.
     * The default implementation returns {@code null}.
     */
    @Override
    public String getISBN() {
        return null;
    }

    /**
     * International Standard Serial Number, or {@code null} if none.
     * The default implementation returns {@code null}.
     */
    @Override
    public String getISSN() {
        return null;
    }

    /**
     * Returns online references to the cited resource.
     * The default implementation returns an empty set.
     *
     * @return online reference to the cited resource, or an empty collection if there is none.
     */
    @Override
    public Set<OnlineResource> getOnlineResources() {
        return emptySet();
    }

    /**
     * Returns citation graphic or logo for cited party.
     * The default implementation returns an empty set.
     *
     * @return graphics or logo for cited party, or an empty collection if there is none.
     */
    @Override
    public Set<BrowseGraphic> getGraphics() {
        return emptySet();
    }

    /**
     * Compares the {@linkplain #getTitle() title} with the string representation of the given object
     * for order.
     *
     * @param   object  the object to be compared.
     * @return  a negative integer, zero, or a positive integer as this object
     *          is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(final InternationalString object) {
        return title.compareTo(object.toString());
    }

    /**
     * Returns {@code true} if the given object is a {@code SimpleCitation} having a title
     * equals to this title.
     *
     * @param  object  the object to compare with this {@code SimpleCitation}, or {@code null}.
     * @return {@code true} if the given object is equals to this {@code SimpleCitation}.
     */
    @Override
    public boolean equals(final Object object) {
        return (object instanceof SimpleCitation) && title.equals(((SimpleCitation) object).title);
    }

    /**
     * Returns a hash code value for this citation. The hash code is computed from
     * the {@linkplain #getTitle() title} string given to the constructor.
     *
     * @return a hash code value for this citation.
     */
    @Override
    public int hashCode() {
        return title.hashCode() ^ (int) serialVersionUID;
    }
}
