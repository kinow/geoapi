/**************************************************************************************************
 **
 ** $Id$
 **
 ** $Source$
 **
 ** Copyright (C) 2003-2005 Open GIS Consortium, Inc.
 ** All Rights Reserved. http://www.opengis.org/legal/
 **
 *************************************************************************************************/
package org.opengis.go.display.style;

// J2SE direct dependencies
import java.awt.Color;
import java.awt.Font;

/**
 * Encapsulates the style data applicable to
 * {@link org.opengis.go.display.primitive.Graphic}s
 * that are of type Text in the sense of SLD (OGC 02-070).
 * <p>
 * Note that the "fill color" of a <code>TextSymbolizer</code> could also be
 * called the "text color" as it is the primary color used to draw the text.
 *
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version $Revision$, $Date$
 */
public interface TextSymbolizer extends GraphicStyle {

    //*************************************************************************
    //  Static Fields
    //*************************************************************************

    //**  Default values  **

    /**  Default fill color value.  */
    public static final Color DEFAULT_FILL_COLOR = Color.BLACK;

    /**  Default fill background color value.  */
    public static final Color DEFAULT_FILL_BACKGROUND_COLOR = Color.WHITE;

    /**  Default fill gradient points value.  */
    //public static final float[] DEFAULT_FILL_GRADIENT_POINTS = new float[2];

    /**  Default fill opacity value.  */
    public static final float DEFAULT_FILL_OPACITY = 1.f;

    /**  Default fill pattern value.  */
    public static final FillPattern DEFAULT_FILL_PATTERN = FillPattern.NONE;

    /**  Default fill style value.  */
    public static final FillStyle DEFAULT_FILL_STYLE = FillStyle.SOLID;

    // PENDING(jdc):  need a default font!!
    public static final Font DEFAULT_FONT = null;

    /**  Default halo radius value.  */
    public static final float DEFAULT_HALO_RADIUS = 0f;

    /**  Default rotation.  */
    public static final float DEFAULT_ROTATION = 0f;

    /**  Default xAnchor.  */
    public static final XAnchor DEFAULT_X_ANCHOR = XAnchor.CENTER;

    /**  Default xDisplacement.  */
    public static final float DEFAULT_X_DISPLACEMENT = 0f;

    /**  Default yAnchor.  */
    public static final YAnchor DEFAULT_Y_ANCHOR = YAnchor.MIDDLE;

    /**  Default yDisplacement.  */
    public static final float DEFAULT_Y_DISPLACEMENT = 0f;

    //*************************************************************************
    //  Methods
    //*************************************************************************

    /**
     * Returns the color used to draw the text.  This is the color used to fill
     * the interior of the font glyphs.
     * @return the text foreground color.
     */
    public Color getFillColor();

    /**
     * Sets the color used to draw the text.  This is the color used to fill
     * the interior of the font glyphs.
     * @param fillColor the text foreground color.
     */
    public void setFillColor(Color fillColor);

    /**
     * Returns the color that is used as the pattern background color when a
     * stipple pattern is used for the fill color.
     * @return the font glyph background color for stippled text rendering.
     */
    public Color getFillBackgroundColor();

    /**
     * Sets the color that is used as the pattern background color when a
     * stipple pattern is used for the fill color.
     * @param fillBackgroundColor the font glyph background color for 
     * stippled text rendering.
     */
    public void setFillBackgroundColor(Color fillBackgroundColor);

    /**
     * Returns the fill gradient points value, or null if there is no fill gradient.
     * @return the fill gradient points value.
     */
    public float[] getFillGradientPoints();

    /**
     * Sets the fill gradient points value.
     * @param fillGradientPoints the fill gradient points value, or null to
     * specify no fill gradient.
     */
    public void setFillGradientPoints(float[] fillGradientPoints);

    /**
     * Returns the fill opacity value.
     * @return the fill opacity value.
     */
    public float getFillOpacity();

    /**
     * Sets the fill opacity value.
     * @param fillOpacity the fill opacity value.
     */
    public void setFillOpacity(float fillOpacity);

    /**
     * Returns the fill pattern value.
     * @return the fill pattern value.
     */
    public FillPattern getFillPattern();

    /**
     * Sets the fill pattern value.
     * @param fillPattern the fill pattern value.
     */
    public void setFillPattern(FillPattern fillPattern);

    /**
     * Returns the fill style value.
     * @return the fill style value.
     */
    public FillStyle getFillStyle();

    /**
     * Sets the fill style value.
     * @param fillStyle the fill style value.
     */
    public void setFillStyle(FillStyle fillStyle);

    /**
     * Returns the Font object.
     * @return the Font object.
     */
    public Font getFont();

    /**
     * Sets the Font object.
     * @param object the Font object.
     */
    public void setFont(Font object);

    /**
     * Returns the halo radius value, or zero if no halo is to be drawn.
     * @return the value of the halo radius.
     */
    public float getHaloRadius();

    /**
     * Sets the halo radius value.  If zero, no halo will be drawn.
     * @param haloRadius the value of the halo radius.
     */
    public void setHaloRadius(float haloRadius);

    /**
     * Returns the color that is used to fill in the halo,
     * or null if no halo is to be drawn.
     * @return the halo color.
     */
    public Color getHaloColor();

    /**
     * Sets the halo color.
     * @param haloColor the halo color.
     */
    public void setHaloColor(Color haloColor);

    /**
     * Returns the color that is used to fill in a bounding box behind the text,
     * or null if no background is to be drawn using this symbolizer.
     * @return the color of the background behind the label, or null if no
     * background will be drawn.
     */
    public Color getBackgroundColor();

    /**
     * Sets the color that is used for rendering a background behind the text.
     * @param backgroundColor the font glyph background color for 
     * stippled text rendering.
     */
    public void setBackgroundColor(Color backgroundColor);

    /**
     * Returns the label rotation.
     * @return the label rotation.
     */
    public float getRotation();

    /**
     * Sets the label rotation.
     * @param labelRotation the label rotation.
     */
    public void setRotation(float labelRotation);

    /**
     * Returns the label XAnchor.
     * @return the label XAnchor.
     */
    public XAnchor getXAnchor();

    /**
     * Sets the label XAnchor.
     * @param xAnchor the label XAnchor.
     */
    public void setXAnchor(XAnchor xAnchor);

    /**
     * Returns the label X displacement.
     * @return the label X displacement.
     */
    public float getXDisplacement();

    /**
     * Sets the label X displacement.
     * @param xDisplacement the label X displacement.
     */
    public void setXDisplacement(float xDisplacement);

    /**
     * Returns the label YAnchor.
     * @return the label YAnchor.
     */
    public YAnchor getYAnchor();

    /**
     * Sets the label YAnchor.
     * @param yAnchor the label YAnchor.
     */
    public void setYAnchor(YAnchor yAnchor);

    /**
     * Returns the label Y displacement.
     * @return the label Y displacement.
     */
    public float getYDisplacement();

    /**
     * Sets the label Y displacement.
     * @param yDisplacement the label Y displacement.
     */
    public void setYDisplacement(float yDisplacement);
}
