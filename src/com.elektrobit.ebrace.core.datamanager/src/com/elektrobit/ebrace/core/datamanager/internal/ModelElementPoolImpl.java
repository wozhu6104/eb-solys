/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datamanager.internal;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;
import com.elektrobit.ebsolys.core.targetdata.api.ModelElementPool;

@Component(immediate = true)
public class ModelElementPoolImpl implements ModelElementPool
{
    private final Map<Long, ModelElement> modelElementMap = new HashMap<Long, ModelElement>();

    public ModelElementPoolImpl()
    {
        insertModelElement( ModelElement.NULL_MODEL_ELEMENT );
    }

    @Override
    public void insertModelElement(ModelElement modelElementToStore)
    {
        if (modelElementToStore != null)
        {
            modelElementMap.put( modelElementToStore.getUniqueModelElementID(), modelElementToStore );
        }
    }

    @Override
    public ModelElement getModelElementWithID(long modelElementID)
    {
        return modelElementMap.get( modelElementID );
    }

    @Override
    public void performReset()
    {
        modelElementMap.clear();
    }
}
