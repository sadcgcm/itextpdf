package com.itextpdf.text.pdf.parser;

import java.util.List;

/**
 * Contains information relating to construction the current path.
 *
 * @since 5.5.6
 */
public class PathConstructionRenderInfo {

    /**
     * See {@link com.itextpdf.text.pdf.parser.Path#moveTo(float, float)}
     */
    public static final byte MOVETO = 1;

    /**
     * See {@link com.itextpdf.text.pdf.parser.Path#lineTo(float, float)}
     */
    public static final byte LINETO = 2;

    /**
     * See {@link com.itextpdf.text.pdf.parser.Path#curveTo(float, float, float, float, float, float)}
     */
    public static final byte CURVE_123 = 3;

    /**
     * See {@link com.itextpdf.text.pdf.parser.Path#curveTo(float, float, float, float)}
     */
    public static final byte CURVE_23 = 4;

    /**
     * See {@link com.itextpdf.text.pdf.parser.Path#curveFromTo(float, float, float, float)}
     */
    public static final byte CURVE_13 = 5;

    /**
     * See {@link com.itextpdf.text.pdf.parser.Path#closeSubpath()}
     */
    public static final byte CLOSE = 6;

    /**
     * See {@link com.itextpdf.text.pdf.parser.Path#rectangle(float, float, float, float)}
     */
    public static final byte RECT = 7;

    private byte operation;
    private List<Float> segmentData;
    private Matrix ctm;

    /**
     * @param operation   Indicates which path-construction operation should be performed.
     * @param segmentData Contains data of a new segment being added to the current path.
     *                    E.g. x, y, w, h for rectangle; x, y for line etc.
     * @param ctm         Current transformation matrix.
     */
    public PathConstructionRenderInfo(byte operation, List<Float> segmentData, Matrix ctm) {
        this.operation = operation;
        this.segmentData = segmentData;
        this.ctm = ctm;
    }

    /**
     * See {@link #PathConstructionRenderInfo(byte, java.util.List, Matrix)}
     */
    public PathConstructionRenderInfo(byte operation, Matrix ctm) {
        this(operation, null, ctm);
    }

    /**
     * @return construction operation should be performed on the current path.
     */
    public byte getOperation() {
        return operation;
    }

    /**
     * @return {@link java.util.List} containing data of a new segment (E.g. x, y, w, h for rectangle;
     *         x, y for line etc.) if the specified operation relates to adding the segment to the
     *         current path, <CODE>null</CODE> otherwise.
     */
    public List<Float> getSegmentData() {
        return segmentData;
    }

    /**
     * @return Current transformation matrix.
     */
    public Matrix getCtm() {
        return ctm;
    }
}
