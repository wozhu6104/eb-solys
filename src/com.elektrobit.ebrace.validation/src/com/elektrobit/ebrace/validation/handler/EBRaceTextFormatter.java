/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.validation.handler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;

public class EBRaceTextFormatter extends SimpleFormatter
{
    private String calcDate(long millisecs)
    {

        SimpleDateFormat date_format = new SimpleDateFormat( "EEE dd. MMM HH:mm:ss z yyyy" );

        Date resultdate = new Date( millisecs );

        return date_format.format( resultdate );

    }

    @Override
    public synchronized String format(LogRecord rec)
    {
        StringBuilder buf = new StringBuilder();
        if (rec instanceof RuntimeEventLogRecord)
        {
            RuntimeEventLogRecord record = (RuntimeEventLogRecord)rec;
            RuntimeEvent<?> event = record.getEvent();
            buf.append( calcDate( rec.getMillis() ) );
            buf.append( "\n" );
            buf.append( "\t" );
            buf.append( "TYPE=" );
            buf.append( "\"IPC\"" );
            buf.append( "\n" );
            buf.append( "\t" );
            buf.append( "FROM=" );
            buf.append( ((ComRelation)event.getModelElement()).getSender().getName() );
            buf.append( "\n" );
            buf.append( "\t" );
            buf.append( "TO=" );
            buf.append( ((ComRelation)event.getModelElement()).getReceiver().getName() );
            buf.append( "\n" );
            buf.append( "\t" );
            buf.append( "INTERFACE=" );
            buf.append( ((ComRelation)event.getModelElement()).getReceiver().getParent().getName() );
            buf.append( "\n" );
            buf.append( "\t" );
            buf.append( "EVENT=" );
            buf.append( "" );
            buf.append( "\n" );
            buf.append( "\t" );
            buf.append( "METHOD=" );
            buf.append( "" );
            buf.append( "\n" );
            buf.append( "\t" );
            buf.append( "SERIAL=" );
            buf.append( "" );
            buf.append( "\n" );
        }

        return buf.toString();
    }

    @Override
    public synchronized String formatMessage(LogRecord record)
    {
        return super.formatMessage( record );
    }
}
