/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.file.impl;

import java.io.File;

import com.elektrobit.ebrace.core.tracefile.api.LoadFileService;

public class FileLoadRunner
{
    private final LoadFileService loadFileService;

    public FileLoadRunner(final LoadFileService loadFileService)
    {
        this.loadFileService = loadFileService;
    }

    public boolean paramsOk(final String pathToFile)
    {
        return pathToFile != null && new File( pathToFile ).isFile();
    }

    public boolean run(final String pathToFile)
    {
        return loadFileService.loadFile( pathToFile );
    }

}
