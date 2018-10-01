/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.franca.common;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.franca.core.dsl.FrancaIDLHelpers;
import org.franca.core.franca.FModel;

import com.elektrobit.ebrace.franca.common.franca.modelloader.impl.FileScanner;

public class FrancaModelLoaderHelper
{
    public static List<FModel> loadFrancaDefaultModels()
    {
        List<FModel> models = new ArrayList<FModel>();
        FileScanner fScanner = new FileScanner();
        File root = new File( "../com.elektrobit.ebrace.franca.common/franca" );
        List<String> francaModels = fScanner.getFiles( root, "default" );

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
}
