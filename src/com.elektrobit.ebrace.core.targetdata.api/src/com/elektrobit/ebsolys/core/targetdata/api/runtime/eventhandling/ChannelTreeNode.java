/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(exclude = {"children"})
public class ChannelTreeNode
{
    @Getter
    @Setter
    private ChannelTreeNode parent;

    @Getter
    @Setter
    private RuntimeEventChannel<?> runtimeEventChannel;

    @Getter
    private final String nodeName;

    @Getter
    private final String fullName;

    private final List<ChannelTreeNode> children = new ArrayList<>();

    public ChannelTreeNode(String nodeName, ChannelTreeNode parent, String fullName)
    {
        this.nodeName = nodeName;
        this.parent = parent;
        this.fullName = fullName;
    }

    public ChannelTreeNode(String name)
    {
        this( name, null, name );
    }

    public void addChild(ChannelTreeNode childNode)
    {
        children.add( childNode );
    }

    public void addChildren(List<ChannelTreeNode> childNodes)
    {
        children.addAll( childNodes );
    }

    public void replaceChild(ChannelTreeNode newChild, ChannelTreeNode oldChild)
    {
        int index = children.indexOf( oldChild );
        children.set( index, newChild );
    }

    public void remove(ChannelTreeNode childNode)
    {
        children.remove( childNode );
    }

    public void removeAllChildren()
    {
        children.clear();
    }

    public ChannelTreeNode getChildAt(int index)
    {
        return children.get( index );
    }

    public int getChildCount()
    {
        return children.size();
    }

    public boolean hasChildren()
    {
        return children.size() > 0;
    }

    public List<ChannelTreeNode> getChildren()
    {
        return Collections.unmodifiableList( children );
    }

    public boolean isLeaf()
    {
        return children.size() == 0;
    }

    @Override
    public String toString()
    {
        return "ChannelTreeNode [" + fullName + ", leaf=" + isLeaf() + ", nodeName=" + nodeName + "]";
    }
}
