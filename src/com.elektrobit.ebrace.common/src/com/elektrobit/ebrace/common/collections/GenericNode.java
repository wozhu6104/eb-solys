/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.common.collections;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a node of the Tree<T> class. The Node<T> is also a container, and can be thought of as instrumentation to
 * determine the location of the type T in the Tree<T>.
 */
public class GenericNode<T> implements Serializable
{
    private static final long serialVersionUID = 56546L;

    private volatile int m_hashCode;

    private GenericNode<T> m_parent;

    private T m_data;

    private List<GenericNode<T>> m_children = new ArrayList<GenericNode<T>>();

    /**
     * Default constructor.
     */
    public GenericNode()
    {
    }

    /**
     * Convenience constructor to create a Node<T> with an instance of T.
     * 
     * @param data
     *            an instance of T.
     */
    public GenericNode(T data)
    {
        setData( data );
    }

    /**
     * Returns true, if this node is a root node, else false.
     * 
     * @return true, if this node is a root node, else false.
     */
    public boolean isRoot()
    {
        return (m_parent == null);
    }

    /**
     * Returns true, if this node is a leaf node, else false.
     * 
     * @return true, if this node is a leaf node, else false.
     */
    public boolean isLeaf()
    {
        return m_children.isEmpty();
    }

    /**
     * Return the children of Node<T>. The Tree<T> is represented by a single root Node<T> whose children are
     * represented by a List<Node<T>>. Each of these Node<T> elements in the List can have children. The getChildren()
     * method will return the children of a Node<T>.
     * 
     * @return the children of Node<T>
     */
    public List<GenericNode<T>> getGenericChildren()
    {
        if (m_children == null)
        {
            return Collections.emptyList();
        }
        return new ArrayList<GenericNode<T>>( m_children );
    }

    /**
     * Sets the children of a Node<T> object. See docs for getChildren() for more information.
     * 
     * @param children
     *            the List<Node<T>> to set.
     */
    public void setGenericChildren(List<GenericNode<T>> children)
    {
        m_children = children;
    }

    /**
     * Returns the number of immediate children of this Node<T>.
     * 
     * @return the number of immediate children.
     */
    public int getNumberOfGenericChildren()
    {
        return getGenericChildren().size();
    }

    /**
     * Adds a child to the list of children for this Node<T>. The addition of the first child will create a new
     * List<Node<T>>.
     * 
     * @param child
     *            a Node<T> object to set.
     */
    public void addGenericChild(GenericNode<T> child)
    {
        child.setGenericParent( this );
        m_children.add( child );
    }

    /**
     * Inserts a Node<T> at the specified position in the child list. Will throw an ArrayIndexOutOfBoundsException if
     * the index does not exist.
     * 
     * @param index
     *            the position to insert at.
     * @param child
     *            the Node<T> object to insert.
     * @throws IndexOutOfBoundsException
     *             if thrown.
     */
    public void insertGenericChildAt(int index, GenericNode<T> child) throws IndexOutOfBoundsException
    {
        if (index == getNumberOfGenericChildren())
        {
            // this is really an append
            addGenericChild( child );
            return;
        }
        else
        {
            m_children.get( index ); // just to throw the exception, and stop here
            child.setGenericParent( this );
            m_children.add( index, child );
        }
    }

    /**
     * Remove the Node<T> element at index index of the List<Node<T>>.
     * 
     * @param index
     *            the index of the element to delete.
     * @throws IndexOutOfBoundsException
     *             if thrown.
     */
    public void removeGenericChildAt(int index) throws IndexOutOfBoundsException
    {
        m_children.get( index ).m_parent = null;
        m_children.remove( index );
    }

    /**
     * Returns the data.
     * 
     * @return the data.
     */
    public T getData()
    {
        return m_data;
    }

    /**
     * Sets the data.
     * 
     * @param data
     *            the data which should appended to this {@link GenericNode}
     */
    public void setData(T data)
    {
        m_data = data;
        m_hashCode = 0;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append( "{" ).append( getData().toString() ).append( ",[" );
        int i = 0;
        for (GenericNode<T> e : getGenericChildren())
        {
            if (i > 0)
            {
                sb.append( "," );
            }
            sb.append( e.getData().toString() );
            i++;
        }
        sb.append( "]" ).append( "}" );
        return sb.toString();
    }

    @Override
    public int hashCode()
    {
        int result = m_hashCode;
        if (result == 0)
        {
            result = 17;
            result = 31 * result + m_data.hashCode();
            m_hashCode = result;
        }
        return result;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }

        if (!(object instanceof GenericNode))
        {
            return false;
        }

        @SuppressWarnings("unchecked")
        GenericNode<Object> compareNode = (GenericNode<Object>)object;

        return m_data.equals( compareNode.m_data );
    }

    // //////////////////////////////////////////////////////////
    // //
    // Private methods //
    // //
    // //////////////////////////////////////////////////////////

    /**
     * Returns the parent node of this node. Null if is has no parent node.
     * 
     * @return Returns the parent node of this node.
     */
    public GenericNode<T> getGenericParent()
    {
        return m_parent;
    }

    /**
     * Sets the parent node of this node.
     * 
     * @param parent
     *            The new parent node.
     */
    private void setGenericParent(GenericNode<T> parent)
    {
        m_parent = parent;
    }

}
