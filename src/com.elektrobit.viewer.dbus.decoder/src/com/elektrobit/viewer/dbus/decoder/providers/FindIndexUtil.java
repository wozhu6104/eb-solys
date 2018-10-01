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

public class FindIndexUtil
{
    public static int isFirst(DecodedNode node)
    {
        DecodedNode rootNode = node.getParentTree().getRootNode();
        DecodedNode rootParent = node.getParentNode();
        DecodedNode result = null;
        if (rootParent.equals( rootNode ))
        {
            return rootNode.getChildren().indexOf( node );
        }
        while (!rootParent.equals( rootNode ))
        {
            result = rootParent;
            rootParent = rootParent.getParentNode();
        }
        return rootNode.getChildren().indexOf( result );
    }
}
