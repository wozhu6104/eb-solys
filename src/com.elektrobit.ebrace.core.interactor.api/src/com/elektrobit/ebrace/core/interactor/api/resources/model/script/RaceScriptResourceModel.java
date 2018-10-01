/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.api.resources.model.script;

import java.io.File;

import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;

public interface RaceScriptResourceModel extends ResourceModel
{
    public RaceScriptInfo getScriptInfo();

    public File getSourceFile();

    public void updateScriptInfo(RaceScriptInfo currentScript);
}
