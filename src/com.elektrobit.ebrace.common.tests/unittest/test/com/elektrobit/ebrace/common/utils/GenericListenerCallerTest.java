/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.common.utils;

import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.common.utils.GenericListenerCaller;
import com.elektrobit.ebrace.common.utils.GenericListenerCaller.Notifier;

public class GenericListenerCallerTest
{
    public interface Listener
    {
        public void onListenerNotified();
    }

    @Test
    public void testRegisterAndNotify() throws Exception
    {
        GenericListenerCaller<Listener> sutListenerCaller = new GenericListenerCaller<Listener>();
        Listener mockedListener = Mockito.mock( Listener.class );
        sutListenerCaller.add( mockedListener );
        triggerNotify( sutListenerCaller );
        Mockito.verify( mockedListener, Mockito.times( 1 ) ).onListenerNotified();
    }

    @Test
    public void testRegisterUnregisterNotify() throws Exception
    {
        GenericListenerCaller<Listener> sutListenerCaller = new GenericListenerCaller<Listener>();
        Listener mockedListener = Mockito.mock( Listener.class );
        sutListenerCaller.add( mockedListener );
        sutListenerCaller.remove( mockedListener );
        triggerNotify( sutListenerCaller );
        Mockito.verifyNoMoreInteractions( mockedListener );
    }

    @Test
    public void testUnregisterDuringNotify() throws Exception
    {
        final GenericListenerCaller<Listener> sutListenerCaller = new GenericListenerCaller<Listener>();
        Listener mockedListener1 = Mockito.mock( Listener.class );
        Listener mockedListener2 = Mockito.mock( Listener.class );
        Listener listenerToBeUnregistered = new Listener()
        {
            @Override
            public void onListenerNotified()
            {
                sutListenerCaller.remove( this );
            }
        };

        sutListenerCaller.add( listenerToBeUnregistered );
        sutListenerCaller.add( mockedListener1 );
        sutListenerCaller.add( mockedListener2 );

        triggerNotify( sutListenerCaller );
        Mockito.verify( mockedListener1, Mockito.times( 1 ) ).onListenerNotified();
        Mockito.verify( mockedListener2, Mockito.times( 1 ) ).onListenerNotified();
    }

    private void triggerNotify(GenericListenerCaller<Listener> sutListenerCaller)
    {
        sutListenerCaller.notifyListeners( new Notifier<GenericListenerCallerTest.Listener>()
        {
            @Override
            public void notify(Listener listener)
            {
                listener.onListenerNotified();
            }
        } );
    }
}
