package de.sofd.util;

import java.awt.*;
import java.awt.image.*;

public class AwtUtil {
    protected static Cursor emptyCursor;

    public static Cursor getEmptyCursor() {
        if (emptyCursor == null) {
            emptyCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                    new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB),
                    new Point(0, 0), "");
        }

        return emptyCursor;
    }
}