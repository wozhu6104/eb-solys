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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences;
import com.elektrobit.ebrace.core.preferences.api.UserInteractionPreferences;
import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences.ANALYSIS_TIMESPAN_CHANGE_REASON;

public class ChartShiftKeyListener implements KeyListener
{
    private static final int SMALL_TIMESPAN_JUMP_FACTOR = 10;
    private static final int BIG_TIMESPAN_JUMP_FACTOR = 1;
    private final AnalysisTimespanPreferences analysisTimespanPreferences = new GenericOSGIServiceTracker<AnalysisTimespanPreferences>( AnalysisTimespanPreferences.class )
            .getService();
    private final UserInteractionPreferences userInteractionPreferences = new GenericOSGIServiceTracker<UserInteractionPreferences>( UserInteractionPreferences.class )
            .getService();

    @Override
    public void keyPressed(KeyEvent e)
    {
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        if (userInteractionPreferences.isLiveMode())
            return;

        boolean shiftPressed = (e.stateMask & SWT.SHIFT) != 0;
        int jumpFactor = shiftPressed ? BIG_TIMESPAN_JUMP_FACTOR : SMALL_TIMESPAN_JUMP_FACTOR;
        long jumpTime = analysisTimespanPreferences.getAnalysisTimespanLength() / jumpFactor;
        long timespanEnd = analysisTimespanPreferences.getAnalysisTimespanEnd();

        switch (e.keyCode)
        {
            case SWT.ARROW_LEFT :
                analysisTimespanPreferences.setAnalysisTimespanEnd( timespanEnd - jumpTime,
                                                                    ANALYSIS_TIMESPAN_CHANGE_REASON.USER_SHIFT );
                break;
            case SWT.ARROW_RIGHT :
                analysisTimespanPreferences.setAnalysisTimespanEnd( timespanEnd + jumpTime,
                                                                    ANALYSIS_TIMESPAN_CHANGE_REASON.USER_SHIFT );
                break;
        }
    }
}
