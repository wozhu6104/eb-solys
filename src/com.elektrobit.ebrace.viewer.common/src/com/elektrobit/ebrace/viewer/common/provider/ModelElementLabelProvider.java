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

import com.elektrobit.ebrace.viewer.common.ViewerCommonPlugin;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation;
import com.elektrobit.ebsolys.core.targetdata.api.structure.Tree;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

public class ModelElementLabelProvider extends ColumnLabelProvider
{
    Image comRelationImage = null;
    ResourceManager resourceManager = null;

    public ModelElementLabelProvider()
    {
        resourceManager = new LocalResourceManager( JFaceResources.getResources() );
    }

    @Override
    public String getText(Object element)
    {
        if (element instanceof Tree)
        {
            return ((Tree)element).getRootNode().getName();
        }
        else if (element instanceof TreeNode)
        {
            return ((TreeNode)element).getName();
        }
        else if (element instanceof ComRelation)
        {
            ComRelation comRel = (ComRelation)element;
            if (comRel.getName() != null && !comRel.getName().isEmpty())
            {
                return comRel.getName();
            }
            return comRel.getSender().getName() + "->" + comRel.getReceiver().getName();
        }
        return null;
    }

    @Override
    public Image getImage(Object element)
    {
        if (element instanceof Tree)
        {
            return resourceManager.createImage( ImageDescriptor
                    .createFromImage( new Image( null, ((Tree)element).getRootNode().getTreeLevel().getIconPath() ) ) );
        }
        else if (element instanceof TreeNode)
        {
            return resourceManager.createImage( ImageDescriptor
                    .createFromImage( new Image( null, ((TreeNode)element).getTreeLevel().getIconPath() ) ) );
        }
        else if (element instanceof ComRelation)
        {
            if (comRelationImage == null)
            {
                comRelationImage = ViewerCommonPlugin.getDefault().getImageDescriptor( "icons/dependency_graph.png" )
                        .createImage();
            }
            return comRelationImage;
        }
        return super.getImage( element );
    }

    @Override
    public void dispose()
    {
        super.dispose();
        if (resourceManager != null)
        {
            resourceManager.dispose();
        }
    }

}
