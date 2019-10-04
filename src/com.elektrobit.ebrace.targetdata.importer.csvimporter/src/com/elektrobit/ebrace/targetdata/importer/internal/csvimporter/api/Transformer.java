/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.importer.internal.csvimporter.api;

import com.elektrobit.ebrace.core.targetdata.api.json.JsonEvent;

public interface Transformer
{
    public JsonEvent transformEvent(String input);

    public void acquireMetaData(String hint, String path);
}
