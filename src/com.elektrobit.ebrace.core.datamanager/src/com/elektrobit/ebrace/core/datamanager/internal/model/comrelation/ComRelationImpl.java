/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datamanager.internal.model.comrelation;

import java.io.Serializable;

import com.elektrobit.ebrace.common.utils.IdNumberGenerator;
import com.elektrobit.ebrace.core.datamanager.internal.PropertiesImpl;
import com.elektrobit.ebsolys.core.targetdata.api.Properties;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

/**
 * TODO Enter class description here!
 * 
 * @author rage2903
 * @version $Revision: $ $Name: $
 */

public class ComRelationImpl implements ComRelation, Serializable
{
    private static final long serialVersionUID = 2908364267672446424L;
    private final TreeNode m_sender;
    private final TreeNode m_receiver;
    private final String m_name;
    private volatile int m_hashCode;
    private final Long uniqueModelElementID;
    private final PropertiesImpl m_properties;

    public static ComRelationImpl createInstance(ComRelationBuilder builder)
    {
        return new ComRelationImpl( builder );
    }

    private ComRelationImpl(ComRelationBuilder builder)
    {
        m_sender = builder.m_sender;
        m_receiver = builder.m_receiver;
        m_name = builder.m_name;
        uniqueModelElementID = IdNumberGenerator.getNextId( "ModelElement" );
        m_properties = new PropertiesImpl( this );
    }

    public static class ComRelationBuilder
    {
        private final TreeNode m_sender;
        private final TreeNode m_receiver;

        private String m_name = "";

        public ComRelationBuilder(TreeNode sender, TreeNode receiver)
        {
            m_sender = sender;
            m_receiver = receiver;

            if (!sender.getTreeLevel().equals( receiver.getTreeLevel() ))
            {
                throw new UnsupportedOperationException( "Couldn't build ComRelation. Level of sender and receiver are different!" );
            }
        }

        public ComRelationBuilder addName(String name)
        {
            m_name = name;
            return this;
        }
    }

    @Override
    public TreeNode getSender()
    {
        return m_sender;
    }

    @Override
    public TreeNode getReceiver()
    {
        return m_receiver;
    }

    @Override
    public String getName()
    {
        return m_name;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        }

        if (!(o instanceof ComRelationImpl))
        {
            return false;
        }

        ComRelationImpl relation = (ComRelationImpl)o;

        return m_sender.equals( relation.m_sender ) && m_receiver.equals( relation.m_receiver );
    }

    @Override
    public int hashCode()
    {
        int result = m_hashCode;

        if (result == 0)
        {
            result = 17;
            result = 31 * result + m_sender.hashCode();
            result = 31 * result + m_receiver.hashCode();
            m_hashCode = result;
        }

        return result;
    }

    @Override
    public String toString()
    {
        return m_sender.toString() + " --> " + m_receiver.toString();
    }

    @Override
    public long getUniqueModelElementID()
    {
        return uniqueModelElementID;
    }

    @Override
    public Properties getProperties()
    {
        return m_properties;
    }

    /**
     * Sets the property of the node.
     */
    public void setProperty(Object key, Object value, String description)
    {
        m_properties.put( key, value, description );
    }
}
