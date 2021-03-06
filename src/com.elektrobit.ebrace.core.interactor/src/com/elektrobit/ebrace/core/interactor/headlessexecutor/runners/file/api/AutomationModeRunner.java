/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.file.api;

import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;

public interface AutomationModeRunner
{
    public boolean paramsOk(String dataSource, RaceScriptInfo script, String methodName);

    public boolean run(String dataSource, RaceScriptInfo script, String methodName);
}
