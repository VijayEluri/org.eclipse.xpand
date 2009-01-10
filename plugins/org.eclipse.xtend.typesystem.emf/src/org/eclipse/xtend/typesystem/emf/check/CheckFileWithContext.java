/*******************************************************************************
 * Copyright (c) 2005-2009 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.xtend.typesystem.emf.check;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.xtend.check.CheckUtils;


public class CheckFileWithContext {

	private String fileName;
	
	private List<String> importedEPackageNsUris = new ArrayList<String>();

	public CheckFileWithContext(String checkFileName) {
		this.fileName = normalizeFileName(checkFileName);
	}

	public String getFileName() {
		return fileName;
	}

	public List<String> getImportedEPackageNsUris() {
		return importedEPackageNsUris;
	}

	public void addImportedEPackageNsUri(String importedEPackageNsUri) {
		importedEPackageNsUris.add(importedEPackageNsUri);
	}
	
	private String normalizeFileName(String checkFileName) { 
		if(checkFileName.toLowerCase().endsWith(CheckUtils.FILE_EXTENSION)) {
			checkFileName = checkFileName.substring(0, checkFileName.length() - CheckUtils.FILE_EXTENSION.length() - 1);
		}
		return checkFileName;
	}
}
