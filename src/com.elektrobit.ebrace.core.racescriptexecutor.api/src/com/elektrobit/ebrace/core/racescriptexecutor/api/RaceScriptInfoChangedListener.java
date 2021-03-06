/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.racescriptexecutor.api;

import java.util.List;

import com.elektrobit.ebrace.core.interactor.api.resources.model.script.RaceScript;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;

public interface RaceScriptInfoChangedListener
{
    public void scriptInfoChanged(RaceScript script);

    public void filterMethodsChanged(RaceScript script, List<RaceScriptMethod> filterMethods);
}
