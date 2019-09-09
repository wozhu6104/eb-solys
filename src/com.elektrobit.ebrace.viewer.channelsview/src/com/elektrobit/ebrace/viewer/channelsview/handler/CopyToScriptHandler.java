/*******************************************************************************
 * Copyright (C) 2019 systemticks GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.channelsview.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ChannelTreeNode;

public class CopyToScriptHandler extends AbstractHandler
{

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        IStructuredSelection selection = ((IStructuredSelection)HandlerUtil.getCurrentSelection( event ));

        ChannelTreeNode node = (ChannelTreeNode)selection.getFirstElement();

        if (node != null)
        {
            String textToCopy;

            if (!node.hasChildren())
            {
                textToCopy = "'" + node.getFullName() + "'.channel";
            }
            else
            {
                textToCopy = "'" + node.getFullName() + "'.channelsByPrefix";
            }

            Clipboard clipboard = new Clipboard( Display.getCurrent() );
            TextTransfer textTransfer = TextTransfer.getInstance();
            Transfer[] transfers = new Transfer[]{textTransfer};
            Object[] data = new Object[]{textToCopy};
            clipboard.setContents( data, transfers );
            clipboard.dispose();
        }

        return null;
    }

}
