/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.app.racescriptexecutor.impl.service;

import java.util.List;

import org.mockito.Mockito;

import com.elektrobit.ebrace.core.interactor.api.resources.model.script.RaceScriptResourceModel;
import com.elektrobit.ebrace.core.interactor.api.script.ScriptData;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;

public class ScriptTestHelper
{
    public static RaceScriptResourceModel getScriptModelByName(String scriptName,
            ResourcesModelManager resourcesModelManager)
    {
        List<RaceScriptResourceModel> allScriptModels = resourcesModelManager.getAllScripts();
        for (RaceScriptResourceModel model : allScriptModels)
        {
            if (model.getName().equals( scriptName ))
            {
                return model;
            }
        }
        return null;
    }

    public static ScriptData mockScriptData(String name, String xtendPath, String jarPath)
    {
        ScriptData mockedScriptData = Mockito.mock( ScriptData.class );
        Mockito.when( mockedScriptData.getName() ).thenReturn( name );
        Mockito.when( mockedScriptData.getSourcePath() ).thenReturn( xtendPath );
        Mockito.when( mockedScriptData.getJarPath() ).thenReturn( jarPath );
        return mockedScriptData;
    }
}
