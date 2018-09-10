/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    Copyright (C) 2008-2018 Open Geospatial Consortium, Inc.
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
package org.opengis.style;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.measure.Unit;
import javax.swing.Icon;

import org.opengis.util.GenericName;
import org.opengis.filter.Filter;
import org.opengis.filter.Id;
import org.opengis.filter.expression.Expression;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.util.InternationalString;

/**
 * Factory used in the production of style objects.
 * <p>
 * This factory is responsible for the production of style objects; where noted
 * these create methods are in agreement with the Symbology Encoding 1.1
 * specification.
 *
 * @author Open Geospatial Consortium
 * @author Johann Sorel (Geomatys)
 * @since GeoAPI 2.2
 */
public interface StyleFactory {
    AnchorPoint anchorPoint(Expression x, Expression y);

    ChannelSelection channelSelection(SelectedChannelType gray);

    ChannelSelection channelSelection(SelectedChannelType red,
            SelectedChannelType green, SelectedChannelType blue);

    /**
     * Wrap up a "Categorize" function using the provided expressions.
     * <p>
     * The function will be created based on:
     * <ol>
     * <li>PropertyName; use "Rasterdata" to indicate this is a color map
     * <li>Literal: lookup value
     * <li>Literal: threshold 1
     * <li>Literal: value 1
     * <li>Literal: threshold 2
     * <li>Literal: value 2
     * <li>Literal: (Optional) succeeding or preceding
     * </ol>
     * @param propertyName
     *            Property name to categorize, or use "Raster"
     * @param mapping
     *            Defined as a series of Expressions
     * @return ColorMap wrapped around the "Cateogize" function
     */
    ColorMap colorMap(Expression propertyName, Expression... mapping);

    /**
     * Wrap up a replacement function using the provided expressions.
     *
     * @param propertyName
     *            Property name to categorize, or use "Raster"
     * @param mapping
     *            Defined as a series of Expressions
     * @return ColorReplacement wrapped around a Function
     */
    ColorReplacement colorReplacement(Expression propertyName,
            Expression... mapping);

    ContrastEnhancement contrastEnhancement(Expression gamma,
            ContrastMethod method);

    Description description(InternationalString title,
            InternationalString description);

    Displacement displacement(Expression dx, Expression dy);

    ExternalGraphic externalGraphic(OnlineResource resource,
            String format, Collection<ColorReplacement> replacements);

    ExternalGraphic externalGraphic(Icon inline,
            Collection<ColorReplacement> replacements);

    ExternalMark externalMark(OnlineResource resource, String format,
            int markIndex);

    ExternalMark externalMark(Icon inline);

    /**
     * @param rules
     *            May not be null or empty
     */
    FeatureTypeStyle featureTypeStyle(String name,
            Description description, Id definedFor, Set<GenericName> featureTypeNames,
            Set<SemanticType> types, List<Rule> rules);

    Fill fill(GraphicFill fill, Expression color, Expression opacity);

    Font font(List<Expression> family, Expression style,
            Expression weight, Expression size);

    Graphic graphic(List<GraphicalSymbol> symbols,
            Expression opacity, Expression size, Expression rotation,
            AnchorPoint anchor, Displacement disp);

    GraphicFill graphicFill(List<GraphicalSymbol> symbols,
            Expression opacity, Expression size, Expression rotation,
            AnchorPoint anchorPoint, Displacement displacement);

    GraphicLegend graphicLegend(List<GraphicalSymbol> symbols,
            Expression opacity, Expression size, Expression rotation,
            AnchorPoint anchorPoint, Displacement displacement);

    GraphicStroke graphicStroke(List<GraphicalSymbol> symbols,
            Expression opacity, Expression size, Expression rotation,
            AnchorPoint anchorPoint, Displacement displacement,
            Expression initialGap, Expression gap);

    Halo halo(Fill fill, Expression radius);

    LinePlacement linePlacement(Expression offset, Expression initialGap,
            Expression gap, boolean repeated, boolean aligned,
            boolean generalizedLine);

    /**
     * @param  name      handle used to refer to this symbolizer (machine readible)
     * @param  geometry  expression used to produce the Geometry to renderer; often a PropertyName
     * @param  unit      unit of measure used to define this symbolizer
     * @param  stroke    definition of how to stroke linework
     * @param  offset    offset used to position line relative to origional
     * @return newly created Line Symbolizer
     */
    LineSymbolizer lineSymbolizer(String name, Expression geometry,
            Description description, Unit<?> unit, Stroke stroke, Expression offset);

    Mark mark(Expression wellKnownName, Fill fill, Stroke stroke);

    Mark mark(ExternalMark externalMark, Fill fill, Stroke stroke);

    PointPlacement pointPlacement(AnchorPoint anchor,
            Displacement displacement, Expression rotation);

    /**
     * Creation of a PointSymbolizer to describe how geometry can be rendered as a point.
     *
     * @param  name         handle used to refer to this symbolizer (machine readable)
     * @param  geometry     expression used to extract the Geometry rendered; usually a PropertyName
     * @param  description  human readable description of symboizer
     * @param  unit         unit of Measure used to interpret symbolizer distances
     * @param  graphic      graphic used to represent the geometry when rendering
     * @return newly created PointSymbolizer
     */
    PointSymbolizer pointSymbolizer(String name, Expression geometry,
            Description description, Unit<?> unit, Graphic graphic);

    /**
     *
     * @param  name         handle used to refer to this symbolizer (machine readable)
     * @param  geometry     expression used to extract the Geometry rendered; usually a PropertyName
     * @param  description  human readable description of symboizer
     * @param  unit         unit of Measure used to interpret symbolizer distances
     */
    PolygonSymbolizer polygonSymbolizer(String name, Expression geometry,
            Description description, Unit<?> unit, Stroke stroke, Fill fill,
            Displacement displacement, Expression offset);

    /**
     *
     * @param  name         handle used to refer to this symbolizer (machine readable)
     * @param  geometry     expression used to extract the Geometry rendered; usually a PropertyName
     * @param  description  human readable description of symboizer
     * @param  unit         unit of Measure used to interpret symbolizer distances
     */
    RasterSymbolizer rasterSymbolizer(String name, Expression geometry,
            Description description, Unit<?> unit, Expression opacity,
            ChannelSelection channelSelection,
            OverlapBehavior overlapsBehaviour, ColorMap colorMap,
            ContrastEnhancement contrast, ShadedRelief shaded,
            Symbolizer outline);

    /**
     * Used to represent a symbolizer intended for a vendor specific rendering process. This
     * facility should be used to control subject matter that is beyond the scope of the traditional
     * symbology encoding data structure (subject matter like wind barbs or extra deegrees of
     * freedom like temporal symbolizers are good examples of the use of this facility).
     *
     * @param name
     *            handle used to refer to this symbolizer (machine readible)
     * @param geometry
     *            geometry expression to renderer; formally a PropertyName
     * @param description
     *            description of this symbolizer; human readable
     * @param unit
     *            unit of measure to use when interpretting this symbolizer
     * @param extensionName
     *            extension name used to identify the vendor specific extension being controlled
     * @param parameters
     *            named expressions used to configure the vendor specific rendering process
     * @return newly created ExtensionSymbolizer
     */
    ExtensionSymbolizer extensionSymbolizer(String name, String geometry, Description description,
            Unit<?> unit, String extensionName, Map<String, Expression> parameters);

    /**
     * Used to represent a symbolizer intended for a vendor specific rendering process. This
     * facility should be used to control subject matter that is beyond the scope of the traditional
     * symbology encoding data structure (subject matter like wind barbs or extra deegrees of
     * freedom like temporal symbolizers are good examples of the use of this facility).
     *
     * @param name
     *            handle used to refer to this symbolizer (machine readible)
     * @param geometry
     *            geometry expression to renderer;
     * @param description
     *            description of this symbolizer; human readable
     * @param unit
     *            unit of measure to use when interpretting this symbolizer
     * @param extensionName
     *            extension name used to identify the vendor specific extension being controlled
     * @param parameters
     *            named expressions used to configure the vendor specific rendering process
     * @return newly created ExtensionSymbolizer
     */
    ExtensionSymbolizer extensionSymbolizer(String name, Expression geometry, Description description,
            Unit<?> unit, String extensionName, Map<String, Expression> parameters);

    /**
     * Create a rule from the provided definition.
     *
     * @param  name         handle used to refer to this rule (machine readable)
     * @param  description  human readable description of this rule
     * @param  legend       graphic used to indicate this rule in a legend or user interface
     * @param  min          minimum scale denominator used to control when this rule is applied
     * @param  max          maximum scale denominator used to control when this rule is applied
     * @return newly created Rule
     */
    Rule rule(String name, Description description, GraphicLegend legend,
            double min, double max, List<Symbolizer> symbolizers, Filter filter);

    SelectedChannelType selectedChannelType(String channelName, ContrastEnhancement contrastEnhancement);

    ShadedRelief shadedRelief(Expression reliefFactor,
            boolean brightnessOnly);

    Stroke stroke(
            Expression color,
            Expression opacity,
            Expression width,
            Expression join,
            Expression cap,
            float[] dashes,
            Expression offset);

    Stroke stroke(
            GraphicFill fill,
            Expression color,
            Expression opacity,
            Expression width,
            Expression join,
            Expression cap,
            float[] dashes,
            Expression offset);

    Stroke stroke(
            GraphicStroke stroke,
            Expression color,
            Expression opacity,
            Expression width,
            Expression join,
            Expression cap,
            float[] dashes,
            Expression offset);

    Style style(String name, Description description, boolean isDefault,
            List<FeatureTypeStyle> featureTypeStyles,
            Symbolizer defaultSymbolizer);

    /**
     * Creation of a TextSymbolizer defining how labels are portrayed.
     *
     * @param  name         handle used to refer to this symbolizer (machine readable)
     * @param  geometry     geometry to be rendered
     * @param  description  human readable description
     * @param  unit         unit of measure used to interpret symbolizer sizes
     * @param  label        text displayed for this symbolizer
     * @param  font         font selected to renderer this symbolizer
     * @param  placement    placement information relative to orgiginal geometry
     * @param  halo         definition of a halo or outline surrounding the symbolizer
     * @param  fill         definition of fill used
     * @return newly created TextSymbolizer
     */
    TextSymbolizer textSymbolizer(String name, Expression geometry,
            Description description, Unit<?> unit, Expression label, Font font,
            LabelPlacement placement, Halo halo, Fill fill);
}
