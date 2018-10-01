/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.viewer.dbus.decoder.providers;

import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedTree;

public class DecodedModelLabelProvider extends DecodedNodeLabelProvider
{
    @Override
    public String getText(Object element)
    {
        if (element instanceof DecodedTree)
        {
            DecodedTree tree = (DecodedTree)element;
            return tree.getRootNode().getName();
        }
        else if (element instanceof DecodedNode)
        {
            return ((DecodedNode)element).getName();
        }
        return null;
    }
}
