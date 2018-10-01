/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.decoder.protobuf.model;

import java.util.ArrayList;
import java.util.List;

import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNodeVisitor;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedTree;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(exclude = {"children", "parent", "parentTree"})
@EqualsAndHashCode(exclude = {"summaryValue", "parentTree", "parent"})
public class DefaultMessageDecodedNode implements DecodedNode
{
    private String name;
    private String value;
    private final List<DecodedNode> children;
    private final DefaultMessageDecodedNode parent;
    private final DefaultMessageDecodedTree parentTree;
    private StringBuilder summaryValue;

    @Override
    public DecodedNode createChildNode(String name)
    {
        DefaultMessageDecodedNode dBusMessageDecodedTreeNode = new DefaultMessageDecodedNode( this.getParentTree(),
                                                                                              this,
                                                                                              name );
        return dBusMessageDecodedTreeNode;
    }

    @Override
    public DecodedNode createChildNode(String name, String value)
    {
        DefaultMessageDecodedNode dBusMessageDecodedTreeNode = new DefaultMessageDecodedNode( this.getParentTree(),
                                                                                              this,
                                                                                              name,
                                                                                              value );

        return dBusMessageDecodedTreeNode;
    }

    public DefaultMessageDecodedNode(DecodedTree result, DecodedNode decodedNode, String name)
    {
        this.name = name;
        this.parent = (DefaultMessageDecodedNode)decodedNode;
        this.parentTree = (DefaultMessageDecodedTree)result;
        this.children = new ArrayList<DecodedNode>();
        if (this.parent != null)
        {
            this.parent.addToListOfChildTreeNodes( this );
        }
    }

    public DefaultMessageDecodedNode(DecodedTree tree, DecodedNode parentNode, String name, String value)
    {
        this( tree, parentNode, name );
        this.value = value;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String getValue()
    {
        return this.value;
    }

    @Override
    public DefaultMessageDecodedTree getParentTree()
    {
        return parentTree;
    }

    public DefaultMessageDecodedNode getParent()
    {
        return parent;
    }

    @Override
    public void addToListOfChildTreeNodes(DecodedNode child)
    {
        if (!children.contains( child ))
        {
            this.children.add( child );
        }
    }

    @Override
    public List<DecodedNode> getChildren()
    {
        return children;
    }

    @Override
    public void setValue(String value)
    {
        this.value = value;
    }

    @Override
    public String getSummaryValue()
    {
        if (summaryValue == null)
        {
            summaryValue = new StringBuilder();
            summaryValue.append( "[" );
            if (this.getValue() != null && this.getChildren().isEmpty())
            {
                summaryValue.append( this.getValue() );
            }
            else
            {
                summarizeValue( this );
            }
            summaryValue.append( "]" );
        }
        return summaryValue.toString();
    }

    private void summarizeValue(DecodedNode node)
    {
        for (DecodedNode child : node.getChildren())
        {
            int index = node.getChildren().indexOf( child );

            if (child.getValue() != null)
            {
                summaryValue.append( child.getName() );
                summaryValue.append( "=" );
                summaryValue.append( child.getValue() );
                setKomma( node.getChildren(), index );
            }

            else
            {
                if (child.getValue() == null && child.getName() != null)
                {
                    summaryValue.append( child.getName() );
                    summaryValue.append( ":" );
                    if (!child.getChildren().isEmpty())
                    {
                        summaryValue.append( "[" );
                        summarizeValue( child );
                        summaryValue.append( "]" );
                        setKomma( node.getChildren(), index );
                    }
                }
            }
        }
    }

    private void setKomma(List<DecodedNode> list, int index)
    {
        if (index < list.size() - 1)
        {
            summaryValue.append( "," );
        }
    }

    @Override
    public DecodedNode getParentNode()
    {
        return this.parent;
    }

    @Override
    public void traverse(DecodedNode root, DecodedNodeVisitor visitHandler)
    {
    }

}
