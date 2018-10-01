/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.runtimeeventloggertable.editor;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;

public class RuntimeEventTableMouseListener implements MouseListener, MouseTrackListener
{
    private final RuntimeEventLoggerTableEditor parent;

    public RuntimeEventTableMouseListener(RuntimeEventLoggerTableEditor parent)
    {
        this.parent = parent;
    }

    @Override
    public void mouseDown(MouseEvent e)
    {
        parent.toggleSelectedResources();
    }

    @Override
    public void mouseDoubleClick(MouseEvent e)
    {
        parent.toggleDecoderComposite();
    }

    @Override
    public void mouseEnter(MouseEvent e)
    {
        parent.getTable().setFocus();
    }

    @Override
    public void mouseHover(MouseEvent e)
    {
    }

    @Override
    public void mouseExit(MouseEvent e)
    {
    }

    @Override
    public void mouseUp(MouseEvent e)
    {
    }

}
