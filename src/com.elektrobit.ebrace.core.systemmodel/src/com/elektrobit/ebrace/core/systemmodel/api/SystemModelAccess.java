/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.systemmodel.api;

import java.io.FileNotFoundException;

public interface SystemModelAccess
{
    public SystemModel initFromFile(String absolutePathToInputModel) throws FileNotFoundException;

    public String generate(SystemModel model, ViewModelGenerator generator);

    public SystemModelNode addNode(SystemModel model, SystemModelNode node);

    public SystemModelEdge addEdge(SystemModel model, SystemModelEdge edge);

    public void addSystemModelChangedListener(SystemModelChangedListener listener);

    public void removeSystemModelChangedListener(SystemModelChangedListener listener);
}
