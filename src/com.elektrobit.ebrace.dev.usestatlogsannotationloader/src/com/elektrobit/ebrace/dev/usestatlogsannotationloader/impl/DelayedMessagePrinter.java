/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.dev.usestatlogsannotationloader.impl;

public class DelayedMessagePrinter implements Runnable
{
    private volatile boolean print = false;
    private volatile long value = 0;

    public void update(long value)
    {
        this.value = value;
        print = false;
    }

    @Override
    public void run()
    {
        while (!print)
        {
            print = true;
            try
            {
                Thread.sleep( 500 );
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        System.out.println( "LAST COMPLETE | " + value );
    }

}
