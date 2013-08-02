/*
 * $Id$
 *
 * This file is part of the iText (R) project.
 * Copyright (c) 1998-2013 1T3XT BVBA
 * Authors: Bruno Lowagie, Paulo Soares, et al.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3
 * as published by the Free Software Foundation with the addition of the
 * following permission added to Section 15 as permitted in Section 7(a):
 * FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY 1T3XT,
 * 1T3XT DISCLAIMS THE WARRANTY OF NON INFRINGEMENT OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA, 02110-1301 USA, or download the license from the following URL:
 * http://itextpdf.com/terms-of-use/
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License.
 *
 * In accordance with Section 7(b) of the GNU Affero General Public License,
 * a covered work must retain the producer line in every PDF that is created
 * or manipulated using iText.
 *
 * You can be released from the requirements of the license by purchasing
 * a commercial license. Buying such a license is mandatory as soon as you
 * develop commercial activities involving the iText software without
 * disclosing the source code of your own applications.
 * These activities include: offering paid services to customers as an ASP,
 * serving PDFs on the fly in a web application, shipping iText with a closed
 * source product.
 *
 * For more information, please contact iText Software Corp. at this
 * address: sales@itextpdf.com
 */
package com.itextpdf.text.xml.xmp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import com.itextpdf.text.Version;
import com.itextpdf.text.pdf.PdfDate;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfString;
import com.itextpdf.xmp.*;
import com.itextpdf.xmp.options.PropertyOptions;
import com.itextpdf.xmp.options.SerializeOptions;

/**
 * With this class you can create an Xmp Stream that can be used for adding
 * Metadata to a PDF Dictionary. Remark that this class doesn't cover the
 * complete XMP specification.
 */
public class XmpWriter {

	/** A possible charset for the XMP. */
	public static final String UTF8 = "UTF-8";
	/** A possible charset for the XMP. */
	public static final String UTF16 = "UTF-16";
	/** A possible charset for the XMP. */
	public static final String UTF16BE = "UTF-16BE";
	/** A possible charset for the XMP. */
	public static final String UTF16LE = "UTF-16LE";

	/** String used to fill the extra space. */
	public static final String EXTRASPACE = "                                                                                                   \n";

	/** You can add some extra space in the XMP packet; 1 unit in this variable represents 100 spaces and a newline. */
	//protected int extraSpace;

	/** The writer to which you can write bytes for the XMP stream. */
	//protected OutputStreamWriter writer;

	/** The about string that goes into the rdf:Description tags. */
	//protected String about;

	/**
	 * Processing Instruction required at the start of an XMP stream
	 * @since iText 2.1.6
	 */
	public static final String XPACKET_PI_BEGIN = "<?xpacket begin=\"\uFEFF\" id=\"W5M0MpCehiHzreSzNTczkc9d\"?>\n";

	/**
	 * Processing Instruction required at the end of an XMP stream for XMP streams that can be updated
	 * @since iText 2.1.6
	 */
	public static final String XPACKET_PI_END_W = "<?xpacket end=\"w\"?>";

	/**
	 * Processing Instruction required at the end of an XMP stream for XMP streams that are read only
	 * @since iText 2.1.6
	 */
	public static final String XPACKET_PI_END_R = "<?xpacket end=\"r\"?>";

    protected XMPMeta xmpMeta;
    protected OutputStream xmpOs;
    protected SerializeOptions serializeOptions;

	/**
	 * Creates an XmpWriter.
	 * @param os
	 * @param utfEncoding
	 * @param extraSpace
	 * @throws IOException
	 */
	public XmpWriter(OutputStream os, String utfEncoding, int extraSpace) throws IOException {
        xmpOs = os;
        serializeOptions = new SerializeOptions();
        if (UTF16BE.equals(utfEncoding) || UTF16.equals(utfEncoding))
            serializeOptions.setEncodeUTF16BE(true);
        else if (UTF16LE.equals(utfEncoding))
            serializeOptions.setEncodeUTF16LE(true);
        serializeOptions.setPadding(extraSpace);
        xmpMeta = XMPMetaFactory.create();
        xmpMeta.setObjectName(XMPConst.TAG_XMPMETA);
        xmpMeta.setObjectName("");
        try {
            xmpMeta.setProperty(XMPConst.NS_DC, DublinCoreProperties.FORMAT, "application/pdf");
            xmpMeta.setProperty(XMPConst.NS_PDF, PdfProperties.PRODUCER, Version.getInstance().getVersion());
        } catch (XMPException xmpExc) {}
	}

	/**
	 * Creates an XmpWriter.
	 * @param os
	 * @throws IOException
	 */
	public XmpWriter(OutputStream os) throws IOException {
		this(os, UTF8, 2000);
	}

    public XMPMeta getXmpMeta() {
        return xmpMeta;
    }

    /** Sets the XMP to read-only */
	public void setReadOnly() {
        serializeOptions.setReadOnlyPacket(true);
	}

	/**
	 * @param about The about to set.
	 */
	public void setAbout(String about) {
        xmpMeta.setObjectName(about);
	}

	/**
	 * Adds an rdf:Description.
	 * @param xmlns
	 * @param content
	 * @throws IOException
	 */
    @Deprecated
	public void addRdfDescription(String xmlns, String content) throws IOException {
        try {
            String str = "<rdf:RDF xmlns:rdf=\"" + XMPConst.NS_RDF + "\">" +
                    "<rdf:Description rdf:about=\"" + xmpMeta.getObjectName() +
                    "\" " +
                    xmlns +
                    ">" +
                    content +
                    "</rdf:Description></rdf:RDF>\n";
            XMPMeta extMeta = XMPMetaFactory.parseFromString(str);
            XMPUtils.appendProperties(extMeta, xmpMeta, true, true);
        } catch (XMPException xmpExc) {
            throw new IOException("XMP metadata updating failure!!!", xmpExc);
        }
	}

	/**
	 * Adds an rdf:Description.
	 * @param s
	 * @throws IOException
	 */
    @Deprecated
	public void addRdfDescription(XmpSchema s) throws IOException {
        try {
            String str = "<rdf:RDF xmlns:rdf=\"" + XMPConst.NS_RDF + "\">"+
                    "<rdf:Description rdf:about=\"" + xmpMeta.getObjectName() +
                    "\" " +
                    s.getXmlns() +
                    ">" +
                    s.toString() +
                    "</rdf:Description></rdf:RDF>\n";
            XMPMeta extMeta = XMPMetaFactory.parseFromString(str);
            XMPUtils.appendProperties(extMeta, xmpMeta, true, true);
        } catch (XMPException xmpExc) {
            throw new IOException("XMP metadata updating failure!!!", xmpExc);
        }
	}

	/**
	 * Flushes and closes the XmpWriter.
	 * @throws IOException
	 */
	public void close() throws IOException {
        try {
            XMPMetaFactory.serialize(xmpMeta, xmpOs, serializeOptions);
        } catch (XMPException xmpExc) {
            throw new IOException("XMP metadata updating failure!!!", xmpExc);
        }
	}

    /**
     * @deprecated
     * @param os
     * @param info
     * @param pdfXConformance
     * @throws IOException
     */
    public XmpWriter(OutputStream os, PdfDictionary info, int pdfXConformance) throws IOException {
        this(os, info);
        if (info != null) {
        	PdfName key;
        	PdfObject obj;
        	String value;
        	for (PdfName pdfName : info.getKeys()) {
        		key = pdfName;
        		obj = info.get(key);
        		if (obj == null)
        			continue;
        		if (!obj.isString())
        			continue;
        		value = ((PdfString)obj).toUnicodeString();
                try {
        		    addDocInfoProperty(key, value);
                } catch (XMPException xmpEcx) {
                    throw new IOException("XMP metadata updating failure!!!", xmpEcx);
                }
            }
        }
    }

    /**
     * @param os
     * @param info
     * @throws IOException
     */
    public XmpWriter(OutputStream os, PdfDictionary info) throws IOException {
        this(os);
        if (info != null) {
        	PdfName key;
        	PdfObject obj;
        	String value;
        	for (PdfName pdfName : info.getKeys()) {
        		key = pdfName;
        		obj = info.get(key);
        		if (obj == null)
        			continue;
        		if (!obj.isString())
        			continue;
        		value = ((PdfString)obj).toUnicodeString();
                try {
                    addDocInfoProperty(key, value);
                } catch (XMPException xmpEcx) {
                    throw new IOException("XMP metadata updating failure!!!", xmpEcx);
                }
        	}
        }
    }

    /**
     * @param os
     * @param info
     * @throws IOException
     * @since 5.0.1 (generic type in signature)
     */
    public XmpWriter(OutputStream os, Map<String, String> info) throws IOException {
        this(os);
        if (info != null) {
        	String key;
        	String value;
        	for (Map.Entry<String, String> entry: info.entrySet()) {
        		key = entry.getKey();
        		value = entry.getValue();
        		if (value == null)
        			continue;
                try {
        		    addDocInfoProperty(key, value);
                } catch (XMPException xmpEcx) {
                    throw new IOException("XMP metadata updating failure!!!", xmpEcx);
                }
            }
        }
    }

    public void addDocInfoProperty(Object key, String value) throws XMPException {
        if (key instanceof String)
            key = new PdfName((String)key);
        if (PdfName.TITLE.equals(key)) {
            xmpMeta.setLocalizedText(XMPConst.NS_DC, DublinCoreProperties.TITLE, XMPConst.X_DEFAULT, XMPConst.X_DEFAULT, value);
        } else if (PdfName.AUTHOR.equals(key)) {
            xmpMeta.appendArrayItem(XMPConst.NS_DC, DublinCoreProperties.CREATOR, new PropertyOptions(PropertyOptions.ARRAY_ORDERED), value, null);
        } else if (PdfName.SUBJECT.equals(key)) {
            xmpMeta.appendArrayItem(XMPConst.NS_DC, DublinCoreProperties.SUBJECT, new PropertyOptions(PropertyOptions.ARRAY), value, null);
            xmpMeta.setLocalizedText(XMPConst.NS_DC, DublinCoreProperties.DESCRIPTION, XMPConst.X_DEFAULT, XMPConst.X_DEFAULT, value);
        } else if (PdfName.KEYWORDS.equals(key)) {
            xmpMeta.setProperty(XMPConst.NS_PDF, PdfProperties.KEYWORDS, value);
        } else if (PdfName.PRODUCER.equals(key)) {
            xmpMeta.setProperty(XMPConst.NS_PDF, PdfProperties.PRODUCER, value);
        } else if (PdfName.CREATOR.equals(key)) {
            xmpMeta.setProperty(XMPConst.NS_XMP, XmpCoreProperties.CREATOR_TOOL, value);
        } else if (PdfName.CREATIONDATE.equals(key)) {
            xmpMeta.setProperty(XMPConst.NS_XMP, XmpCoreProperties.CREATE_DATE, PdfDate.getW3CDate(value));
        } else if (PdfName.MODDATE.equals(key)) {
            xmpMeta.setProperty(XMPConst.NS_XMP, XmpCoreProperties.MODIFY_DATE, PdfDate.getW3CDate(value));
        }
    }
}