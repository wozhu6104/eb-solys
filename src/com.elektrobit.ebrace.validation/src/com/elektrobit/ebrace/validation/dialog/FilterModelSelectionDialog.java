/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.validation.dialog;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog;

public class FilterModelSelectionDialog extends FilteredResourcesSelectionDialog
{
    IContainer container;

    public FilterModelSelectionDialog(Shell shell, boolean multi, IContainer container, int typesMask)
    {
        super( shell, multi, container, typesMask );
        this.container = container;
        setListSelectionLabelDecorator( new ILabelDecorator()
        {

            @Override
            public void removeListener(ILabelProviderListener listener)
            {
            }

            @Override
            public boolean isLabelProperty(Object element, String property)
            {
                return true;
            }

            @Override
            public void dispose()
            {
            }

            @Override
            public void addListener(ILabelProviderListener listener)
            {
            }

            @Override
            public String decorateText(String text, Object element)
            {
                if (element instanceof IFile)
                {
                    String suffix = ((IFile)element).getProject().getName();
                    if (!text.endsWith( suffix ))
                    {
                        return text + " - " + suffix;
                    }
                    return text;
                }
                return text;
            }

            @Override
            public Image decorateImage(Image image, Object element)
            {
                return null;
            }
        } );
        setDetailsLabelProvider( new ILabelProvider()
        {

            @Override
            public void removeListener(ILabelProviderListener listener)
            {
            }

            @Override
            public boolean isLabelProperty(Object element, String property)
            {
                return false;
            }

            @Override
            public void dispose()
            {
            }

            @Override
            public void addListener(ILabelProviderListener listener)
            {
            }

            @Override
            public String getText(Object element)
            {
                return element instanceof IFile ? ((IFile)element).getLocation().toString() : "";
            }

            @Override
            public Image getImage(Object element)
            {
                return null;
            }
        } );
    }

    protected class DanaModelResourceFilter extends FilteredResourcesSelectionDialog.ResourceFilter
    {
        public DanaModelResourceFilter(IContainer container)
        {
            super( container, false, IResource.FILE );
        }

        @Override
        public boolean matchItem(Object item)
        {
            return item instanceof IFile && "di".equals( ((IFile)item).getFileExtension() ) && super.matchItem( item );
        }
    }

    @Override
    protected ItemsFilter createFilter()
    {
        return new DanaModelResourceFilter( container );
    }
}
