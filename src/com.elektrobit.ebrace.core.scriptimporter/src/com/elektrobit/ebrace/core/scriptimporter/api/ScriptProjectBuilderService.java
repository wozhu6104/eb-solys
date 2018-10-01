/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.scriptimporter.api;

import java.io.File;
import java.util.List;

public interface ScriptProjectBuilderService
{
    public void addScriptBuildListener(ScriptProjectBuildListener listener);

    public void addUserScripts(List<File> userScripts);

    public void addPreinstalledScripts(List<File> preinstalledScripts);

    public void removeScriptBuildListener(ScriptProjectBuildListener listener);

    public void buildProject();

}
