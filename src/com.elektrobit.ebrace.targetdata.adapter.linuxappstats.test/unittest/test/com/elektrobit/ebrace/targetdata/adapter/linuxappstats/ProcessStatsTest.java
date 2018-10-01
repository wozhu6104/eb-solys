/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.targetdata.adapter.linuxappstats;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext.SOURCE_TYPE;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

public class ProcessStatsTest
{

    private RuntimeEventAcceptor runtimeEventAcceptor;
    private AcceptMessageHelper acceptMessageHelper;
    private DataSourceContext context;

    @Before
    public void setup()
    {
        context = new DataSourceContext( SOURCE_TYPE.FILE, "test." );
        runtimeEventAcceptor = Mockito.mock( RuntimeEventAcceptor.class );
        acceptMessageHelper = new AcceptMessageHelper( runtimeEventAcceptor, context );
    }

    @Test
    public void wasChannelForProcessCreated()
    {
        acceptMessageHelper.acceptMeasuredMessage( "CC:23253658 PI:23243 PN:\"eclipse\" PT:128.0" );

        Mockito.verify( runtimeEventAcceptor ).createOrGetRuntimeEventChannel( Mockito.eq( context ),
                                                                               Mockito.eq( "cpu.prof.p:eclipse:23243" ),
                                                                               Mockito.eq( Unit.PERCENT ),
                                                                               Mockito.anyString() );
    }

    @Test
    public void wasPTAddedToChannel()
    {
        acceptMessageHelper.acceptMeasuredMessage( "CC:23253658 PI:23243 PN:\"eclipse\" PT:128.0" );
        Mockito.verify( runtimeEventAcceptor ).acceptEvent( 23253658, null, null, 128.0 );
    }

    @Test(expected = IllegalArgumentException.class)
    public void piValueWithoutTI()
    {
        acceptMessageHelper.acceptMeasuredMessage( "CC:23253658 PI:23243" );
    }

}
