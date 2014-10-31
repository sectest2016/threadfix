////////////////////////////////////////////////////////////////////////
//
//     Copyright (c) 2009-2014 Denim Group, Ltd.
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
package com.denimgroup.threadfix.framework.impl.dotNetWebForm;

import com.denimgroup.threadfix.CollectionUtils;
import com.denimgroup.threadfix.framework.engine.AbstractEndpoint;
import com.denimgroup.threadfix.logging.SanitizedLogger;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.*;

import static com.denimgroup.threadfix.CollectionUtils.*;

/**
 * Created by mac on 9/4/14.
 */
public class WebFormsEndpoint extends AbstractEndpoint {

    private static final SanitizedLogger LOG = new SanitizedLogger(WebFormsEndpoint.class);

    final AspxParser   aspxParser;
    final AspxCsParser aspxCsParser;
    final File         aspxRoot;
    final String       urlPath;

    Map<String, List<Integer>> map = newMap();

    public WebFormsEndpoint(File aspxRoot, AspxParser aspxParser, AspxCsParser aspxCsParser) {
        if (!(aspxParser.aspName + ".cs").equals(aspxCsParser.aspName)) {
            throw new IllegalArgumentException("Invalid aspx mappings pairs passed to WebFormsEndpoint constructor: " +
                    aspxParser.aspName + " and " + aspxCsParser.aspName);
        }

        this.aspxParser = aspxParser;
        this.aspxCsParser = aspxCsParser;
        this.aspxRoot = aspxRoot;

        this.urlPath = calculateUrlPath();

        collectParameters();
    }

    private String calculateUrlPath() {
        String aspxFilePath = aspxParser.file.getAbsolutePath();
        String aspxRootPath = aspxRoot.getAbsolutePath();

        if (aspxFilePath.startsWith(aspxRootPath)) {
            return aspxFilePath.substring(aspxRootPath.length());
        } else {
            String error = "AspxFilePath didn't start with aspxRoot : " +
                    aspxFilePath +
                    " didn't start with " +
                    aspxRootPath;
            LOG.error(error);
            assert false : error;
            return aspxParser.aspName;
        }
    }

    private void collectParameters() {

        if (!aspxCsParser.lineNumberToParametersMap.containsKey(0)) {
            aspxCsParser.lineNumberToParametersMap.put(0, CollectionUtils.<String>set());
        }

        aspxCsParser.lineNumberToParametersMap.get(0).addAll(aspxParser.parameters);

        for (Map.Entry<Integer, Set<String>> entry : aspxCsParser.lineNumberToParametersMap.entrySet()) {
            for (String key : entry.getValue()) {
                if (!map.containsKey(key)) {
                    map.put(key, new ArrayList<Integer>());
                }

                map.get(key).add(entry.getKey());
            }
        }

        for (List<Integer> integers : map.values()) {
            Collections.sort(integers);
        }
    }

    @Nonnull
    @Override
    protected List<String> getLintLine() {
        return list();
    }

    @Nonnull
    @Override
    public Set<String> getParameters() {
        return map.keySet();
    }

    @Nonnull
    @Override
    public Set<String> getHttpMethods() {
        return set("GET");
    }

    @Nonnull
    @Override
    public String getUrlPath() {
        return urlPath;
    }

    @Nonnull
    @Override
    public String getFilePath() {
        return aspxCsParser.aspName;
    }

    @Override
    public int getStartingLineNumber() {
        return 0;
    }

    @Override
    public int getLineNumberForParameter(String parameter) {
        return map.containsKey(parameter) ? map.get(parameter).get(0) : -1;
    }

    @Override
    public boolean matchesLineNumber(int lineNumber) {
        return true;
    }
}
