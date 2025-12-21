package com.puttysoftware.xio;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class XDataWriter implements AutoCloseable {
    // Fields
    private final BufferedWriter bw;
    private final String docTag;
    private static final String END_OF_LINE = "\r\n"; //$NON-NLS-1$

    // Constructors
    public XDataWriter(final String filename, final String newDocTag)
            throws IOException {
        this.bw = new BufferedWriter(new FileWriter(filename));
        this.docTag = newDocTag;
        this.writeXHeader();
        this.writeOpeningDocTag();
    }

    // Methods
    @Override
    public void close() throws IOException {
        this.writeClosingDocTag();
        this.bw.close();
    }

    public void writeDouble(final double d) throws IOException {
        this.bw.write("<" + XDataConstants.DOUBLE_TAG + ">" + Double.toString(d) //$NON-NLS-1$ //$NON-NLS-2$
                + "</" + XDataConstants.DOUBLE_TAG + ">" //$NON-NLS-1$ //$NON-NLS-2$
                + XDataWriter.END_OF_LINE);
    }

    public void writeInt(final int i) throws IOException {
        this.bw.write("<" + XDataConstants.INT_TAG + ">" + Integer.toString(i) //$NON-NLS-1$ //$NON-NLS-2$
                + "</" + XDataConstants.INT_TAG + ">" //$NON-NLS-1$ //$NON-NLS-2$
                + XDataWriter.END_OF_LINE);
    }

    public void writeLong(final long l) throws IOException {
        this.bw.write("<" + XDataConstants.LONG_TAG + ">" + Long.toString(l) //$NON-NLS-1$ //$NON-NLS-2$
                + "</" + XDataConstants.LONG_TAG + ">" //$NON-NLS-1$ //$NON-NLS-2$
                + XDataWriter.END_OF_LINE);
    }

    public void writeByte(final byte b) throws IOException {
        this.bw.write("<" + XDataConstants.BYTE_TAG + ">" + Byte.toString(b) //$NON-NLS-1$ //$NON-NLS-2$
                + "</" + XDataConstants.BYTE_TAG + ">" //$NON-NLS-1$ //$NON-NLS-2$
                + XDataWriter.END_OF_LINE);
    }

    public void writeBoolean(final boolean b) throws IOException {
        this.bw.write("<" + XDataConstants.BOOLEAN_TAG + ">" //$NON-NLS-1$ //$NON-NLS-2$
                + Boolean.toString(b) + "</" + XDataConstants.BOOLEAN_TAG + ">" //$NON-NLS-1$ //$NON-NLS-2$
                + XDataWriter.END_OF_LINE);
    }

    public void writeString(final String s) throws IOException {
        this.bw.write("<" + XDataConstants.STRING_TAG + ">" //$NON-NLS-1$ //$NON-NLS-2$
                + XDataWriter.replaceSpecialCharacters(s) + "</" //$NON-NLS-1$
                + XDataConstants.STRING_TAG + ">" + XDataWriter.END_OF_LINE); //$NON-NLS-1$
    }

    public void writeOpeningGroup(final String groupName) throws IOException {
        this.bw.write("<" + XDataWriter.replaceSpecialCharacters(groupName)
                + ">" + XDataWriter.END_OF_LINE);
    }

    public void writeClosingGroup(final String groupName) throws IOException {
        this.bw.write("</" + XDataWriter.replaceSpecialCharacters(groupName)
                + ">" + XDataWriter.END_OF_LINE);
    }

    private void writeXHeader() throws IOException {
        this.bw.write(XDataConstants.X_HEADER + XDataWriter.END_OF_LINE);
    }

    private void writeOpeningDocTag() throws IOException {
        this.bw.write("<" + this.docTag + ">" + XDataWriter.END_OF_LINE); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private void writeClosingDocTag() throws IOException {
        this.bw.write("</" + this.docTag + ">"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private static String replaceSpecialCharacters(final String s) {
        String r = s;
        r = r.replace("&", "&amp;"); //$NON-NLS-1$ //$NON-NLS-2$
        r = r.replace("<", "&lt;"); //$NON-NLS-1$ //$NON-NLS-2$
        r = r.replace(">", "&gt;"); //$NON-NLS-1$ //$NON-NLS-2$
        r = r.replace("\"", "&quot;"); //$NON-NLS-1$ //$NON-NLS-2$
        r = r.replace("\'", "&apos;"); //$NON-NLS-1$ //$NON-NLS-2$
        r = r.replace("\r", ""); //$NON-NLS-1$ //$NON-NLS-2$
        return r.replace("\n", "&#xA;"); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
