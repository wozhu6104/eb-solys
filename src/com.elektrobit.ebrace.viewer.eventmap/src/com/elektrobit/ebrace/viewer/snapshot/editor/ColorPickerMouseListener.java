/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.snapshot.editor;

import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.elektrobit.ebrace.core.interactor.api.channelcolor.ChannelColorUseCase;
import com.elektrobit.ebrace.viewer.common.provider.ChannelValueProvider;
import com.elektrobit.ebrace.viewer.common.swt.ColorDialogHandler;

public class ColorPickerMouseListener implements MouseListener
{
    private static final int LEFT_MOUSE_BUTTON = 1;
    private final Tree tree;
    private final ResourceManager resourceManager;
    private final ChannelColorUseCase channelColorUseCase;

    ColorPickerMouseListener(Tree tree, ResourceManager resourceManager, ChannelColorUseCase channelColorUseCase)
    {
        this.tree = tree;
        this.resourceManager = resourceManager;
        this.channelColorUseCase = channelColorUseCase;
    }

    @Override
    public void mouseDown(MouseEvent mouseEvent)
    {
        if (mouseEvent.button == LEFT_MOUSE_BUTTON)
        {
            TreeItem item = tree.getItem( new Point( mouseEvent.x, mouseEvent.y ) );
            if (item != null && mouseInRange( mouseEvent ))
            {
                if (item.getData() instanceof ChannelValueProvider)
                {
                    ChannelValueProvider channelValueProvider = (ChannelValueProvider)item.getData();
                    Color color = getColorFromChooser();
                    if (color != null)
                    {
                        channelColorUseCase.setColorForChannel( channelValueProvider.getRuntimeEventChannel(),
                                                                color.getRed(),
                                                                color.getGreen(),
                                                                color.getBlue() );
                    }
                }

            }
        }
    }

    private Color getColorFromChooser()
    {
        ColorDialogHandler colorDialogHandler = new ColorDialogHandler( resourceManager );
        return colorDialogHandler.getSelectedColor();
    }

    private boolean mouseInRange(MouseEvent mouseEvent)
    {
        return (mouseEvent.x <= 10) && (mouseEvent.x >= 0);
    }

    @Override
    public void mouseDoubleClick(MouseEvent e)
    {
    }

    @Override
    public void mouseUp(MouseEvent e)
    {
    }

}
