/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.analysisTimespan;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.analysisTimespan.AnalysisTimespanInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.analysisTimespan.AnalysisTimespanInteractionUseCase;
import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences;

public class AnalysisTimespanInteractionUseCaseImpl implements AnalysisTimespanInteractionUseCase
{
    public static int ANALYSIS_TIMESPAN_SECONDS_MIN = 1;
    public static int ANALYSIS_TIMESPAN_SECONDS_MAX = 5 * 60;
    private static long ANALYSIS_TIMESPAN_USSECONDS_STEP = 1000000;
    private static long SECONDS_TO_USEC = 1000000;

    private AnalysisTimespanInteractionCallback callback;
    private final AnalysisTimespanPreferences service;

    public AnalysisTimespanInteractionUseCaseImpl(AnalysisTimespanInteractionCallback callback,
            AnalysisTimespanPreferences service)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "callback", callback );
        RangeCheckUtils.assertReferenceParameterNotNull( "service", service );
        this.callback = callback;
        this.service = service;
    }

    @Override
    public void increaseAnalysisTimespan()
    {
        long length = service.getAnalysisTimespanLength();
        length += ANALYSIS_TIMESPAN_USSECONDS_STEP;
        length = Math.min( length, ANALYSIS_TIMESPAN_SECONDS_MAX * SECONDS_TO_USEC );
        setTimespanLengthIfChanged( length );
    }

    private void setTimespanLengthIfChanged(long uSecLength)
    {
        long analysisTimespanLength = service.getAnalysisTimespanLength();
        if (analysisTimespanLength != uSecLength)
        {
            service.setAnalysisTimespanLength( uSecLength );
        }
    }

    @Override
    public void decreaseAnalysisTimespan()
    {
        long length = service.getAnalysisTimespanLength();
        length -= ANALYSIS_TIMESPAN_USSECONDS_STEP;
        length = Math.max( length, ANALYSIS_TIMESPAN_SECONDS_MIN * SECONDS_TO_USEC );
        setTimespanLengthIfChanged( length );
    }

    @Override
    public void setAnalysisTimespan(String textSeconds)
    {
        textSeconds = trimEndCharacterIfAny( 's', textSeconds );
        textSeconds = trimEndCharacterIfAny( ' ', textSeconds );
        Integer result = parseInt( textSeconds );
        if (result != null)
        {
            setAnalysisTimespan( result );
        }
    }

    private String trimEndCharacterIfAny(char characterToTrim, String string)
    {
        if (string.isEmpty())
        {
            return string;
        }
        char lastChar = string.charAt( string.length() - 1 );
        if (lastChar == characterToTrim)
        {
            return string.substring( 0, string.length() - 1 );
        }
        else
        {
            return string;
        }
    }

    private Integer parseInt(String text)
    {
        Integer result = null;

        try
        {
            result = Integer.valueOf( text );
        }
        catch (NumberFormatException e)
        {
            if (callback != null)
            {
                callback.onAnalysisTimespanTextInputInvalidFormat();
            }
        }
        return result;
    }

    private void setAnalysisTimespan(int seconds)
    {
        int secondsInRange = seconds > ANALYSIS_TIMESPAN_SECONDS_MAX ? ANALYSIS_TIMESPAN_SECONDS_MAX : seconds;
        secondsInRange = secondsInRange < ANALYSIS_TIMESPAN_SECONDS_MIN
                ? ANALYSIS_TIMESPAN_SECONDS_MIN
                : secondsInRange;

        if (seconds != secondsInRange && callback != null)
        {
            callback.onAnalysisTimespanTextInputOutOfRange( ANALYSIS_TIMESPAN_SECONDS_MIN,
                                                            ANALYSIS_TIMESPAN_SECONDS_MAX );
        }
        setTimespanLengthIfChanged( secondsInRange * SECONDS_TO_USEC );
    }

    @Override
    public void unregister()
    {
        callback = null;
    }
}
