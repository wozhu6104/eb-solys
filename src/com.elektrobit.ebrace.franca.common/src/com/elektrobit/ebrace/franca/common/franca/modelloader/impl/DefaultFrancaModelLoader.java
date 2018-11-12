/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.franca.common.franca.modelloader.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.franca.core.dsl.FrancaIDLHelpers;
import org.franca.core.franca.FModel;

import com.elektrobit.ebrace.common.utils.FileHelper;
import com.elektrobit.ebrace.franca.common.franca.modelloader.api.FrancaModelLoader;

public class DefaultFrancaModelLoader implements FrancaModelLoader
{
    private List<FModel> loadedModels = null;
    private final String pathToFolder;

    public DefaultFrancaModelLoader(String pathToFolder)
    {
        this.pathToFolder = pathToFolder;
    }

    private List<FModel> loadModelsForPath(String pathToFolder)
    {
        List<FModel> models = new ArrayList<FModel>();
        FileScanner fScanner = new FileScanner();
        File root = getRootDirectory( pathToFolder );
        List<String> francaModels = fScanner.getFiles( root, pathToFolder );

        // Load all Franca Models and sort them to solve the dependency chain
        for (int i = 0; i < francaModels.size(); i++)
        {
            File francaFile = new File( francaModels.get( i ) );

            URI rootURI = URI.createURI( "file:/" );
            URI model = URI.createFileURI( francaFile.getAbsolutePath() );

            FModel loadedModel = FrancaIDLHelpers.instance().loadModel( model, rootURI );

            models.add( loadedModel );
        }

        return models;
    }

    private File getRootDirectory(String pathToFolder)
    {
        String correctPathToFolder = pathToFolder;
        if (!pathToFolder.startsWith( "/" ))
        {
            correctPathToFolder = "/" + correctPathToFolder;
        }

        return new File( FileHelper.locateFileInBundle( "com.elektrobit.ebrace.franca.common", correctPathToFolder ) );
    }

    @Override
    public List<FModel> getLoadedModels()
    {
        if (loadedModels == null)
        {
            loadedModels = loadModelsForPath( pathToFolder );
        }

        return loadedModels;
    }
}
