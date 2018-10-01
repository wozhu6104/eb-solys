/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.genivi.targetadapter.dbus.impl;

import java.util.List;

import com.elektrobit.ebsolys.core.targetdata.api.Properties;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

public class GenericTreeHelper
{

    public static TreeNode getFirstChildTreeNodeByParentNodeProperty(TreeNode root, Object propertyKey,
            Object desiredPropertyValue)
    {
        if (root != null)
        {
            List<TreeNode> children = root.getChildren();
            if (children != null)
            {
                for (TreeNode node : children)
                {
                    Object value = GenericTreeHelper.getNodePropertyValueObjectByPropertyKey( node, propertyKey );
                    if (value != null)
                    {
                        if (value.equals( desiredPropertyValue ))
                        {
                            return node;
                        }
                    }
                    else
                    {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    public static void setNodeProperty(StructureAcceptor structureAcceptor, TreeNode node, Object propertyKey,
            Object newPropertyValue, String description)
    {
        if (GenericTreeHelper.getNodePropertyValueObjectByPropertyKey( node, propertyKey ) != null)
        {
            structureAcceptor.changeStructureProperty( node, propertyKey, newPropertyValue, description );
        }
        else
        {
            structureAcceptor.addStructureProperty( node, propertyKey, newPropertyValue, description );
        }
    }

    public static Object getNodePropertyValueObjectByPropertyKey(TreeNode theNode, Object propertyKey)
    {
        if (theNode != null)
        {
            Properties properties = theNode.getProperties();
            if (properties != null)
            {
                return properties.getValue( propertyKey );
            }
        }

        return null;
    }

}
