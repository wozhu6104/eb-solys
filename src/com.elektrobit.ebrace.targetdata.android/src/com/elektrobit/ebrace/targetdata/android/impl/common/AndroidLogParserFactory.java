/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.android.impl.common;

import java.util.regex.Pattern;

import com.elektrobit.ebrace.targetdata.adapter.androidlog.AndroidLogTAProto.OutputFormat;
import com.elektrobit.ebrace.targetdata.android.impl.importer.AndroidLogLineParserAbstract;
import com.elektrobit.ebrace.targetdata.android.impl.importer.AndroidLogLineParserLong;
import com.elektrobit.ebrace.targetdata.android.impl.importer.AndroidLogLineParserThreadtime;
import com.elektrobit.ebrace.targetdata.android.impl.importer.AndroidLogLineParserTime;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;

public class AndroidLogParserFactory
{

    // e.g. 05-25 11:10:24.157 I/SystemServer( 1346): Backup Service
    private final static String ANDROID_LOG_PATTERN_TIME = "(\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}.\\d{3}):?(\\s+\\w/.*)";
    // e.g. 05-25 11:10:24.157 1346 1346 I SystemServer: Backup Service
    private final static String ANDROID_LOG_PATTERN_THREADTIME = "(\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}.\\d{3})\\s+(\\d*)\\s+(\\d*)\\s+(\\w)\\s+(.*?:)\\s+(.*)";
    // e.g. [ 05-25 11:10:24.266 1346: 1346 D/BackupManagerService ]
    private final static String ANDROID_LOG_PATTERN_LONG = "\\W\\s+(\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}.\\d{3})(\\s+\\d*:\\s+\\d*\\s+\\w/.*)\\s+\\W";

    public static AndroidLogLineParserAbstract createParser(OutputFormat format, RuntimeEventAcceptor acceptor)
    {

        if (format.equals( OutputFormat.THREADTIME ))
        {
            return new AndroidLogLineParserThreadtime( ANDROID_LOG_PATTERN_THREADTIME, acceptor );
        }
        else if (format.equals( OutputFormat.TIME ))
        {
            return new AndroidLogLineParserTime( ANDROID_LOG_PATTERN_TIME, acceptor );
        }
        else if (format.equals( OutputFormat.LONG ))
        {
            return new AndroidLogLineParserLong( ANDROID_LOG_PATTERN_LONG, acceptor );
        }

        else
        {
            return null;
        }

    }

    public static AndroidLogLineParserAbstract createParser(String line, RuntimeEventAcceptor runtimeEventAcceptor)
    {

        if (Pattern.compile( ANDROID_LOG_PATTERN_TIME ).matcher( line ).matches())
        {
            return new AndroidLogLineParserTime( ANDROID_LOG_PATTERN_TIME, runtimeEventAcceptor );
        }
        else if (Pattern.compile( ANDROID_LOG_PATTERN_THREADTIME ).matcher( line ).matches())
        {
            return new AndroidLogLineParserThreadtime( ANDROID_LOG_PATTERN_THREADTIME, runtimeEventAcceptor );
        }
        else if (Pattern.compile( ANDROID_LOG_PATTERN_LONG ).matcher( line ).matches())
        {
            return new AndroidLogLineParserLong( ANDROID_LOG_PATTERN_LONG, runtimeEventAcceptor );
        }
        else
        {
            return null;
        }

    }

}
