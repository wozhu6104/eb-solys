/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.timemarker.listener;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Control;

public class TimeMarkerTranslateToControlMouseListener extends TimeMarkerChangeTimeSpanMouseListener
{
    public TimeMarkerTranslateToControlMouseListener(Control parent, int isHorizontal, boolean isLiveMode)
    {
        super( parent, isHorizontal, isLiveMode );
    }

    @Override
    protected int getCoordXOrYMousePositionForOrientation(MouseEvent mousePosition)
    {
        return (int)(xOffset + super.getCoordXOrYMousePositionForOrientation( mousePosition ));
    }
}
