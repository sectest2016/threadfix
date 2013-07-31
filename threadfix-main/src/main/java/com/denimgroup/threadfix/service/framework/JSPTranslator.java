////////////////////////////////////////////////////////////////////////
//
//     Copyright (c) 2009-2013 Denim Group, Ltd.
//
//     The contents of this file are subject to the Mozilla Public License
//     Version 2.0 (the "License"); you may not use this file except in
//     compliance with the License. You may obtain a copy of the License at
//     http://www.mozilla.org/MPL/
//
//     Software distributed under the License is distributed on an "AS IS"
//     basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
//     License for the specific language governing rights and limitations
//     under the License.
//
//     The Original Code is ThreadFix.
//
//     The Initial Developer of the Original Code is Denim Group, Ltd.
//     Portions created by Denim Group, Ltd. are Copyright (C)
//     Denim Group, Ltd. All Rights Reserved.
//
//     Contributor(s): Denim Group, Ltd.
//
////////////////////////////////////////////////////////////////////////
package com.denimgroup.threadfix.service.framework;

import com.denimgroup.threadfix.data.entities.Finding;
import com.denimgroup.threadfix.data.entities.Scan;
import com.denimgroup.threadfix.service.SanitizedLogger;
import com.denimgroup.threadfix.service.merge.ScanMergeConfiguration;

// TODO incorporate include parsing
public class JSPTranslator extends AbstractPathUrlTranslator {
	
	private final SanitizedLogger log = new SanitizedLogger("JSPTranslator");
	
	public JSPTranslator(ScanMergeConfiguration scanMergeConfiguration,
			Scan scan) {
		super(scanMergeConfiguration, scan);
		
		filePathRoot = CommonPathFinder.findOrParseProjectRoot(scan, ".jsp");
		urlPathRoot = CommonPathFinder.findOrParseUrlPath(scan);
		
		if ((urlPathRoot == null || urlPathRoot.isEmpty()) && filePathRoot != null) {
			urlPathRoot = filePathRoot;
		}
		
		scan.setFilePathRoot(filePathRoot);
		scan.setUrlPathRoot(urlPathRoot);
		
		log.info("Using JSP URL - Path translator.");
		log.info("Calculated filesystem root: " + filePathRoot);
		log.info("Calculated url path root: " + urlPathRoot);
	}

	@Override
	public String getFileName(Finding finding) {
		switch (scanMergeConfiguration.getSourceCodeAccessLevel()) {
			case FULL: return getFileNameWithSourceCodeDefault(finding);
			default:   return getFileNameDefault(finding);
		}
	}

	@Override
	public String getUrlPath(Finding finding) {
		return getUrlPathDefault(finding);
	}
	
}
