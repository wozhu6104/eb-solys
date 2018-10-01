/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.dbusgraph.layout;

import java.util.HashMap;

public class ForceCache
{
    private HashMap<LayoutNode, Vector> m_Cache;

    private ForceCache()
    {
        setEnabled( false );
    }

    private void setEnabled(boolean inEnabled)
    {
        if (inEnabled && m_Cache == null)
        {
            m_Cache = new HashMap<LayoutNode, Vector>();
        }
        else
        {
            m_Cache = null;
        }
    }

    public void pushForceForPeer(final LayoutNode peerNode, final Vector force)
    {
        if (m_Cache != null)
        {
            m_Cache.put( peerNode, force );
        }
    }

    public final Vector getForceForVector(final LayoutNode peerNode)
    {
        if (m_Cache != null)
        {
            final Vector vect = m_Cache.get( peerNode );
            if (vect != null)
            {
                m_Cache.remove( peerNode );
            }
            return vect;
        }
        else
        {
            return null;
        }
    }
}
