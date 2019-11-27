package com.example.ti22_csd_hueapplication;

import android.graphics.Color;
import android.util.Log;

public class HSBC {
    /**
     * 813        * Converts the components of a color, as specified by the HSB
     * 814        * model, to an equivalent set of values for the default RGB model.
     * 815        * <p>
     * 816        * The <code>saturation</code> and <code>brightness</code> components
     * 817        * should be floating-point values between zero and one
     * 818        * (numbers in the range 0.0-1.0).  The <code>hue</code> component
     * 819        * can be any floating-point number.  The floor of this number is
     * 820        * subtracted from it to create a fraction between 0 and 1.  This
     * 821        * fractional number is then multiplied by 360 to produce the hue
     * 822        * angle in the HSB color model.
     * 823        * <p>
     * 824        * The integer that is returned by <code>HSBtoRGB</code> encodes the
     * 825        * value of a color in bits 0-23 of an integer value that is the same
     * 826        * format used by the method {@link #getRGB() <code>getRGB</code>}.
     * 827        * This integer can be supplied as an argument to the
     * 828        * <code>Color</code> constructor that takes a single integer argument.
     * 829        * @param     hue   the hue component of the color
     * 830        * @param     saturation   the saturation of the color
     * 831        * @param     brightness   the brightness of the color
     * 832        * @return    the RGB value of the color with the indicated hue,
     * 833        *                            saturation, and brightness.
     * 834        * @see       java.awt.Color#getRGB()
     * 835        * @see       java.awt.Color#Color(int)
     * 836        * @see       java.awt.image.ColorModel#getRGBdefault()
     * 837        * @since     JDK1.0
     * 838
     */
    private static int HSBtoRGB(float hue, float saturation, float brightness) {
        int r = 0, g = 0, b = 0;
        if (saturation == 0) {
            r = g = b = (int) (brightness * 255.0f + 0.5f);
        } else {
            float h = (hue - (float) Math.floor(hue)) * 6.0f;
            float f = h - (float) java.lang.Math.floor(h);
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - (saturation * (1.0f - f)));
            switch ((int) h) {
                case 0:
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (t * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                    break;
                case 1:
                    r = (int) (q * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                    break;
                case 2:
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (t * 255.0f + 0.5f);
                    break;
                case 3:
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (q * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                    break;
                case 4:
                    r = (int) (t * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                    break;
                case 5:
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (q * 255.0f + 0.5f);
                    break;
            }
        }
        return 0xff000000 | (r << 16) | (g << 8) | (b << 0);
    }

    public static int getRGBFromHSB(int hue, int sat, int bri) {

        float hueF = (float) hue / 65636;
        float satF = (float) sat / 256;
        float briF = (float) bri / 256;
//        Log.d("___________COLOR____", "getRGBFromHSB: " + hueF + " - " + satF + " - " + briF);

        return HSBtoRGB(hueF, satF, briF);
    }

}

