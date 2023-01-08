package me.x150.renderer.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Describes a rectangle
 */
@AllArgsConstructor
public class Rectangle {
    @Getter
    @Setter
    private double x, y, x1, y1;

    /**
     * Returns true if a coordinate is inside the rectangle
     *
     * @param x The X coordinate
     * @param y The Y coordinate
     *
     * @return Whether the coordinates specified are inside the rectangle
     */
    public boolean contains(double x, double y) {
        return x >= this.x && x <= this.x1 && y >= this.y && y <= this.y1;
    }
}
