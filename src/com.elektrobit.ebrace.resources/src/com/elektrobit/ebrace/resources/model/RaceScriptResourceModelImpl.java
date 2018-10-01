/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.resources.model;

import java.io.File;

import com.elektrobit.ebrace.core.interactor.api.resources.model.EditRight;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourcesFolder;
import com.elektrobit.ebrace.core.interactor.api.resources.model.script.RaceScriptResourceModel;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;
import com.elektrobit.ebrace.resources.api.ResourceChangedNotifier;
import com.elektrobit.ebrace.resources.api.model.BaseResourceModel;

public class RaceScriptResourceModelImpl extends BaseResourceModel implements RaceScriptResourceModel
{
    // FIXME rage2903 : Class should only contain RaceScriptInfo
    private RaceScriptInfo raceScript;
    private final File sourceFile;

    public RaceScriptResourceModelImpl(File sourceFile, String name, ResourcesFolder parentFolder,
            ResourceChangedNotifier resourceChangedNotifier)
    {
        super( name, parentFolder, EditRight.EDITABLE, resourceChangedNotifier );
        this.sourceFile = sourceFile;
    }

    public RaceScriptResourceModelImpl(File sourceFile, RaceScriptInfo scriptInfo, ResourcesFolder parentFolder,
            ResourceChangedNotifier resourceChangedNotifier)
    {
        super( scriptInfo.getName(), parentFolder, EditRight.EDITABLE, resourceChangedNotifier );
        this.sourceFile = sourceFile;
        this.raceScript = scriptInfo;
    }

    @Override
    public EditRight getEditRight()
    {
        if (raceScript.isRunning())
        {
            return EditRight.READ_ONLY;
        }
        else
        {
            return super.getEditRight();
        }
    }

    @Override
    public RaceScriptInfo getScriptInfo()
    {
        return raceScript;
    }

    @Override
    public File getSourceFile()
    {
        return sourceFile;
    }

    @Override
    public void updateScriptInfo(RaceScriptInfo currentScript)
    {
        raceScript = currentScript;
    }
}
