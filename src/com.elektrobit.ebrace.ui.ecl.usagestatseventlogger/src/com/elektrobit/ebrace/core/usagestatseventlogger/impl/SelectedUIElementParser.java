/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.usagestatseventlogger.impl;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.osgi.service.event.Event;

import com.elektrobit.ebrace.dev.usestatlogsannotationloader.api.UseStatLogParamParser;

public class SelectedUIElementParser implements UseStatLogParamParser
{

    @Override
    public String parse(Object[] args)
    {
        if (args.length == 1 && args[0] instanceof Event)
        {
            String elementID = extractElementID( extractSelectedElement( (Event)args[0] ) );
            return elementID;
        }
        return null;
    }

    private String extractElementID(Object selectedElement)
    {
        if (selectedElement instanceof MPart)
        {
            MPart mpart = (MPart)selectedElement;
            if (mpart.getElementId().equals( "org.eclipse.e4.ui.compatibility.editor" ))
            {
                return mpart.getTags().get( 1 );
            }
            else
            {
                return mpart.getElementId();
            }
        }
        return null;
    }

    private Object extractSelectedElement(Event event)
    {
        return event.getProperty( UIEvents.EventTags.ELEMENT );
    }

}
