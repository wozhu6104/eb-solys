/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.resources;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionType;
import com.elektrobit.ebrace.viewer.resources.connection.ConnectionTypeSelector;

public class ConnectionTypeSelectorTest
{

    private class TestType1 implements ConnectionType
    {

        @Override
        public String getName()
        {
            return "type 1";
        }

        @Override
        public String getExtension()
        {
            return ".type1";
        }

        @Override
        public int getDefaultPort()
        {
            return 1234;
        }

    }

    private class TestType2 implements ConnectionType
    {

        @Override
        public String getName()
        {
            return "type 2";
        }

        @Override
        public String getExtension()
        {
            return ".type2";
        }

        @Override
        public int getDefaultPort()
        {
            return 1234;
        }

    }

    private final List<ConnectionType> testTypes = new ArrayList<ConnectionType>();

    @Before
    public void setUp() throws Exception
    {
        testTypes.add( new TestType1() );
        testTypes.add( new TestType2() );
    }

    @Test
    public void isCorrectConnectionTypeSelected()
    {
        ConnectionTypeSelector tested = new ConnectionTypeSelector( testTypes, new SelectionListener()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }
        } );
        // this will not work
        tested.initializeMap( null );
        tested.selectByType( new TestType2() );
        assertEquals( TestType2.class, tested.selectedType().getClass() );
    }

}
