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

public class ThreadStatsTest
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
    public void wasChannelForProcessCreated() throws Exception
    {
        acceptMessageHelper.acceptMeasuredMessage( "CC:23253658 PI:23243 PN:\"eclipse\" PT:128.0" );
        acceptMessageHelper
                .acceptMeasuredMessage( "CC:23253658 PI:23243 TI:23243 TN:\"eclipse\" TP:2475072 TC:95513 TT:0.0" );

        Mockito.verify( runtimeEventAcceptor ).createOrGetRuntimeEventChannel( Mockito.eq( context ),
                                                                               Mockito.eq( "cpu.prof.p:eclipse:23243" ),
                                                                               Mockito.eq( Unit.PERCENT ),
                                                                               Mockito.anyString() );
        Mockito.verify( runtimeEventAcceptor ).createOrGetRuntimeEventChannel( Mockito.eq( context ),
                                                                               Mockito.eq( "cpu.prof.p:eclipse:23243.t:eclipse:23243" ),
                                                                               Mockito.eq( Unit.PERCENT ),
                                                                               Mockito.anyString() );

        Mockito.verify( runtimeEventAcceptor ).createOrGetRuntimeEventChannel( Mockito.eq( context ),
                                                                               Mockito.eq( "mem.prof.p:eclipse:23243.t:eclipse:23243" ),
                                                                               Mockito.eq( Unit.KILOBYTE ),
                                                                               Mockito.anyString() );

        Mockito.verify( runtimeEventAcceptor ).createOrGetRuntimeEventChannel( Mockito.eq( context ),
                                                                               Mockito.eq( "mem.count.p:eclipse:23243.t:eclipse:23243" ),
                                                                               Mockito.eq( Unit.COUNT ),
                                                                               Mockito.anyString() );
    }

    @Test
    public void wasPTAddedToChannel() throws Exception
    {
        acceptMessageHelper.acceptMeasuredMessage( "CC:23253658 PI:23243 PN:\"eclipse\" PT:128.0" );
        acceptMessageHelper
                .acceptMeasuredMessage( "CC:23253658 PI:23243 TI:23243 TN:\"eclipse\" TP:2475072 TC:95513 TT:0.0" );

        Mockito.verify( runtimeEventAcceptor ).acceptEvent( 23253658, null, null, 128.0 );
        Mockito.verify( runtimeEventAcceptor ).acceptEvent( 23253658, null, null, 0.0 );
        Mockito.verify( runtimeEventAcceptor ).acceptEvent( 23253658, null, null, 2417L );
        Mockito.verify( runtimeEventAcceptor ).acceptEvent( 23253658, null, null, 95513L );
    }

    @Test
    public void messageRejectedIfNoProcessNameAvailable() throws Exception
    {
        acceptMessageHelper
                .acceptMeasuredMessage( "CC:23253658 PI:23243 TI:23243 TN:\"eclipse\" TP:2475072 TC:95513 TT:0.0" );
        acceptMessageHelper.acceptMeasuredMessage( "CC:23253658 PI:23243 PN:\"eclipse\" PT:128.0" );

        Mockito.verify( runtimeEventAcceptor, Mockito.times( 0 ) )
                .createOrGetRuntimeEventChannel( Mockito.eq( context ),
                                                 Mockito.eq( "cpu.prof.p:eclipse:23243.t:eclipse:23243" ),
                                                 Mockito.eq( Unit.PERCENT ),
                                                 Mockito.anyString() );

        Mockito.verify( runtimeEventAcceptor, Mockito.times( 1 ) )
                .createOrGetRuntimeEventChannel( Mockito.eq( context ),
                                                 Mockito.eq( "cpu.prof.p:eclipse:23243" ),
                                                 Mockito.eq( Unit.PERCENT ),
                                                 Mockito.anyString() );

        Mockito.verify( runtimeEventAcceptor, Mockito.times( 0 ) ).acceptEvent( 23253658, null, null, 0.0 );
        Mockito.verify( runtimeEventAcceptor, Mockito.times( 0 ) ).acceptEvent( 23253658, null, null, 2475072 );
        Mockito.verify( runtimeEventAcceptor, Mockito.times( 0 ) ).acceptEvent( 23253658, null, null, 95513 );
        Mockito.verify( runtimeEventAcceptor, Mockito.times( 1 ) ).acceptEvent( 23253658, null, null, 128.0 );
    }

}
