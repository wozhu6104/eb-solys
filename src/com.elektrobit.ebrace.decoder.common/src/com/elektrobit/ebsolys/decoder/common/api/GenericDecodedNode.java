/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.decoder.common.api;

import java.util.ArrayList;
import java.util.List;

import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNodeVisitor;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedTree;

public class GenericDecodedNode implements DecodedNode
{
    private String name;
    private String value;
    private final List<DecodedNode> children;
    private final DecodedNode parent;
    private final DecodedTree parentTree;
    private StringBuilder summaryValue;

    @Override
    public DecodedNode createChildNode(String name)
    {
        GenericDecodedNode genericDecodedTreeNode = new GenericDecodedNode( this.getParentTree(), this, name );
        return genericDecodedTreeNode;
    }

    @Override
    public DecodedNode createChildNode(String name, String value)
    {
        GenericDecodedNode genericDecodedTreeNode = new GenericDecodedNode( this.getParentTree(), this, name, value );
        return genericDecodedTreeNode;
    }

    public GenericDecodedNode(DecodedTree tree, DecodedNode parentNode, String name)
    {
        this.name = name;
        this.parent = parentNode;
        this.parentTree = tree;
        this.children = new ArrayList<DecodedNode>();
        if (this.parent != null)
        {
            this.parent.addToListOfChildTreeNodes( this );
        }
    }

    private GenericDecodedNode(DecodedTree tree, DecodedNode parentNode, String name, String value)
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
    public DecodedTree getParentTree()
    {
        return parentTree;
    }

    public DecodedNode getParent()
    {
        return parent;
    }

    @Override
    public void addToListOfChildTreeNodes(DecodedNode child)
    {
        this.children.add( child );
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

    @Override
    public void traverse(DecodedNode root, DecodedNodeVisitor visitHandler)
    {

        for (DecodedNode child : root.getChildren())
        {

            visitHandler.nodeVisited( child );

            if (child.getValue() == null && child.getName() != null && !child.getChildren().isEmpty())
            {

                traverse( child, visitHandler );
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
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((children == null) ? 0 : children.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        GenericDecodedNode other = (GenericDecodedNode)obj;
        if (children == null)
        {
            if (other.children != null)
            {
                return false;
            }
        }
        else if (!children.equals( other.children ))
        {
            return false;
        }
        if (name == null)
        {
            if (other.name != null)
            {
                return false;
            }
        }
        else if (!name.equals( other.name ))
        {
            return false;
        }
        if (value == null)
        {
            if (other.value != null)
            {
                return false;
            }
        }
        else if (!value.equals( other.value ))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "GenericDecodedNode [name=" + name + ", value=" + value + "]";
    }

}
