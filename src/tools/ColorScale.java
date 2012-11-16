package tools;

import java.awt.Color;
import org.jfree.chart.renderer.PaintScale;

/**
 * used in the ColorLegend to provide the paints for the scale
 * @author m.vitt
 */
public class ColorScale implements PaintScale {

    private double _lowerBound = 0d;
    private double _upperBound = 0d;
    float loHue = 0.25f;
    float hiHue = 1.0f;

    public ColorScale(double lo, double hi) {
        setLowerBound(lo);
        setUpperBound(hi);
    }

    public double getLowerBound() {
        return _lowerBound;
    }
    public double getUpperBound() {
        return _upperBound;
    }

    public Color getPaint(double value) {
        double lo = Math.max(value, getLowerBound());
        double hi = Math.min(value, getUpperBound());
        double x = value;
        if (lo == getLowerBound()) {
            x = lo;
        } else if (hi == getUpperBound()) {
            x = hi;
        }
        double hue = (x - getLowerBound()) / (getUpperBound() - getLowerBound());

        hue = hue < loHue ? loHue : hue;
        hue = hue > hiHue ? hiHue : hue;

        return Color.getHSBColor((1f - (float) hue), 1f,1f);
    }

    public void setLowerBound(double lowerBound) {
        this._lowerBound = lowerBound;
    }

    public void setUpperBound(double upperBound) {
        this._upperBound = upperBound;
    }
}
