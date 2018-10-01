/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.provider;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

public class FromToNodeComRelationLabelProvider extends ColumnLabelProvider
{
    private final boolean isFrom;
    private ResourceManager resourceManager = null;

    public FromToNodeComRelationLabelProvider(boolean isFrom)
    {
        resourceManager = new LocalResourceManager( JFaceResources.getResources() );
        this.isFrom = isFrom;
    }

    @Override
    public String getText(Object element)
    {
        if (element instanceof ComRelation)
        {
            ComRelation cr = (ComRelation)element;
            TreeNode node = cr.getSender();
            if (!isFrom)
            {
                node = cr.getReceiver();
            }
            return node.getName();
        }
        return null;
    };

    @Override
    public org.eclipse.swt.graphics.Image getImage(Object element)
    {
        if (element instanceof ComRelation)
        {
            ComRelation cr = (ComRelation)element;
            TreeNode node = cr.getSender();
            if (!isFrom)
            {
                node = cr.getReceiver();
            }
            return resourceManager.createImage( ImageDescriptor
                    .createFromImage( new Image( null, node.getTreeLevel().getIconPath() ) ) );
        }
        return null;
    };

    @Override
    public void dispose()
    {
        if (resourceManager != null)
            resourceManager.dispose();
        super.dispose();
    }
}
