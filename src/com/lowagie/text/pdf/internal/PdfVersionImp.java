/*
 * $Id$
 *
 * Copyright 2006 Bruno Lowagie
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the License.
 *
 * The Original Code is 'iText, a free JAVA-PDF library'.
 *
 * The Initial Developer of the Original Code is Bruno Lowagie. Portions created by
 * the Initial Developer are Copyright (C) 1999, 2000, 2001, 2002 by Bruno Lowagie.
 * All Rights Reserved.
 * Co-Developer of the code is Paulo Soares. Portions created by the Co-Developer
 * are Copyright (C) 2000, 2001, 2002 by Paulo Soares. All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 *
 * Alternatively, the contents of this file may be used under the terms of the
 * LGPL license (the "GNU LIBRARY GENERAL PUBLIC LICENSE"), in which case the
 * provisions of LGPL are applicable instead of those above.  If you wish to
 * allow use of your version of this file only under the terms of the LGPL
 * License and not to allow others to use your version of this file under
 * the MPL, indicate your decision by deleting the provisions above and
 * replace them with the notice and other provisions required by the LGPL.
 * If you do not delete the provisions above, a recipient may use your version
 * of this file under either the MPL or the GNU LIBRARY GENERAL PUBLIC LICENSE.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the MPL as stated above or under the terms of the GNU
 * Library General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library general Public License for more
 * details.
 *
 * If you didn't download this code from the following link, you should check if
 * you aren't using an obsolete version:
 * http://www.lowagie.com/iText/
 */

package com.lowagie.text.pdf.internal;

import java.io.IOException;

import com.lowagie.text.DocWriter;
import com.lowagie.text.pdf.OutputStreamCounter;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.interfaces.PdfVersion;

/**
 * Stores the PDF version information,
 * knows how to write a PDF Header,
 * and how to add the version to the catalog (if necessary).
 */

public class PdfVersionImp implements PdfVersion {

	/** Indicates if the header was already written. */
	protected boolean headerWasWritten = false;
	/** Indicates if we are working in append mode. */
	protected boolean appendmode = false;
	/** The version that was or will be written to the header. */
	protected char header_version = VERSION_1_4;
	/** The version that will be written to the catalog. */
	protected PdfName catalog_version = null;
	
	/**
	 * @see com.lowagie.text.pdf.interfaces.PdfVersion#setPdfVersion(char)
	 */
	public void setPdfVersion(char version) {
		if (headerWasWritten || appendmode) {
			setPdfVersion(getVersionAsName(version));
		}
		else {
			this.header_version = version;
		}
	}
	
	/**
	 * @see com.lowagie.text.pdf.interfaces.PdfVersion#setAtLeastPdfVersion(char)
	 */
	public void setAtLeastPdfVersion(char version) {
		if (version > header_version) {
			setPdfVersion(version);
		}
	}
	
	/**
	 * @see com.lowagie.text.pdf.interfaces.PdfVersion#setPdfVersion(com.lowagie.text.pdf.PdfName)
	 */
	public void setPdfVersion(PdfName version) {
		if (catalog_version == null || catalog_version.compareTo(version) < 0) {
			this.catalog_version = version;
		}
	}
	
	/**
	 * Sets the append mode.
	 */
	public void setAppendmode(boolean appendmode) {
		this.appendmode = appendmode;
	}
	
	/**
	 * Writes the header to the OutputStreamCounter.
	 * @throws IOException 
	 */
	public void writeHeader(OutputStreamCounter os) throws IOException {
		if (appendmode) {
			os.write(HEADER[0]);
		}
		else {
			os.write(HEADER[1]);
			os.write(getVersionAsByteArray(header_version));
			os.write(HEADER[2]);
			headerWasWritten = true;
		}
	}
	
	/**
	 * Returns the PDF version as a name.
	 * @param version	the version character.
	 */
	public PdfName getVersionAsName(char version) {
		switch(version) {
		case VERSION_1_2:
			return PDF_VERSION_1_2;
		case VERSION_1_3:
			return PDF_VERSION_1_3;
		case VERSION_1_4:
			return PDF_VERSION_1_4;
		case VERSION_1_5:
			return PDF_VERSION_1_5;
		case VERSION_1_6:
			return PDF_VERSION_1_6;
		case VERSION_1_7:
			return PDF_VERSION_1_7;
		default:
			return PDF_VERSION_1_4;
		}
	}
	
	/**
	 * Returns the version as a byte[].
	 * @param version the version character
	 */
	public byte[] getVersionAsByteArray(char version) {
		return DocWriter.getISOBytes(getVersionAsName(version).toString().substring(1));
	}

	/** Adds the version to the Catalog dictionary. */
	public void addToCatalog(PdfDictionary catalog) {
		if(catalog_version != null) {
			catalog.put(PdfName.VERSION, catalog_version);
		}
	}
}