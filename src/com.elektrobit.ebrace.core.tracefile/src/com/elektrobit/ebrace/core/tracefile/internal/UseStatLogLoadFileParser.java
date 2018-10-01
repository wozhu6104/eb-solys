/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.tracefile.internal;

import java.io.File;

import com.elektrobit.ebrace.common.utils.FileHelper;
import com.elektrobit.ebrace.dev.usestatlogsannotationloader.api.UseStatLogParamParser;

public class UseStatLogLoadFileParser implements UseStatLogParamParser
{

    @Override
    public String parse(Object[] args)
    {
        if (args.length == 1 || args[0] instanceof String)
        {
            String result = "";

            String filePath = (String)args[0];
            result += getFileExtensionText( filePath ) + " | ";
            result += getFileSizeText( filePath );

            return result;

        }
        return null;
    }

    private String getFileExtensionText(String filePath)
    {
        String fileExtension = FileHelper.getFileExtension( filePath );
        return "fileExtension:" + fileExtension;
    }

    private String getFileSizeText(String filePath)
    {
        File file = new File( filePath );
        return "fileSize:" + file.length() + " bytes";
    }
}
