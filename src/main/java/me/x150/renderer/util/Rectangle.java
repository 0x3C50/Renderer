package me.x150.renderer.util;

import lombok.Getter;
import lombok.ToString;

/**
 * Describes a rectangle positioned in 2 dimensions with width and height
 */
@Getter
@ToString
public class Rectangle {
	private final double x, y, x1, y1;

	/**
	 * Constructs a new rectangle. The coordinates should follow the following constraints, and will be adjusted to fit them if necessary:
	 * <ul>
	 *     <li>minX < maxX</li>
	 *     <li>minY < maxY</li>
	 * </ul>
	 *
	 * @param minX Min X coordinate (left)
	 * @param minY Min Y coordinate (top)
	 * @param maxX Max X coordinate (right)
	 * @param maxY Max Y coordinate (bottom)
	 */
	public Rectangle(double minX, double minY, double maxX, double maxY) {
		double nx0 = Math.min(minX, maxX);
		double nx1 = Math.max(minX, maxX);
		double ny0 = Math.min(minY, maxY);
		double ny1 = Math.max(minY, maxY);
		this.x = nx0;
		this.y = ny0;
		this.x1 = nx1;
		this.y1 = ny1;
	}

	/**
	 * Returns true if the given coordinate is contained inside this rectangle. More specifically, this algorithm returns true when
	 * {@code px in x0..x1 and py in y0..y1}.
	 *
	 * @param x The X coordinate
	 * @param y The Y coordinate
	 * @return Whether the coordinates specified are inside the rectangle
	 */
	public boolean contains(double x, double y) {
		return x >= this.x && x <= this.x1 && y >= this.y && y <= this.y1;
	}

	/**
	 * Checks if {@code this} rectangle overlaps with {@code that} rectangle. An overlap is defined as any point of either rectangle being within the other.
	 *
	 * @param that Rectangle to check against
	 * @return {@code true} if both rectangles overlap, {@code false} otherwise
	 */
	public boolean overlaps(Rectangle that) {
		return this.x1 >= that.x && this.y1 >= that.y && this.x <= that.x1 && this.y <= that.y1;
	}
}
