/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.ui.ecl.file;

import java.util.List;

import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionUseCase;

public class ImportAnotherFileHandler extends BaseLoadFileHandler
{
    private String[] types;
    private String[] extensions;

    @Override
    protected void init(OpenFileInteractionUseCase useCase)
    {
        List<List<String>> anotherFilesTypesAndExtensions = useCase.getAnotherFilesTypesAndExtensions();
        List<String> types = anotherFilesTypesAndExtensions.get( 0 );
        List<String> extensions = anotherFilesTypesAndExtensions.get( 1 );

        this.types = types.toArray( new String[types.size()] );
        this.extensions = extensions.toArray( new String[extensions.size()] );
    }

    @Override
    protected String[] getFileExtentions()
    {
        return extensions;
    }

    @Override
    protected String getFileDialogTitle()
    {
        return "Open File";
    }

    @Override
    protected String[] getFileExtentionNames()
    {
        return types;
    }
}
