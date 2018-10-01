/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.chartengine.internal.handler;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;

import com.elektrobit.ebrace.viewer.snapshot.editor.ChannelsSnapshotDecoderComposite;

public class ClearSelectionInLegendMouseListener implements MouseListener
{
    private final ChannelsSnapshotDecoderComposite channelsSnapshotDecoderComposite;

    public ClearSelectionInLegendMouseListener(ChannelsSnapshotDecoderComposite channelsSnapshotDecoderComposite)
    {
        this.channelsSnapshotDecoderComposite = channelsSnapshotDecoderComposite;
    }

    @Override
    public void mouseDoubleClick(MouseEvent e)
    {
    }

    @Override
    public void mouseDown(MouseEvent e)
    {
        channelsSnapshotDecoderComposite.getTreeViewerOfSnapshot().setSelection( null );
    }

    @Override
    public void mouseUp(MouseEvent e)
    {
    }
}
