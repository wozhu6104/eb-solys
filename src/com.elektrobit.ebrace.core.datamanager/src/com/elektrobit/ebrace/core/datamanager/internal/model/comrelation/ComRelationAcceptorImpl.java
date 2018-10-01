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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.core.datamanager.internal.PropertiesImpl;
import com.elektrobit.ebrace.core.datamanager.internal.model.comrelation.ComRelationImpl.ComRelationBuilder;
import com.elektrobit.ebrace.core.datamanager.internal.model.structure.TreeNodeImpl;
import com.elektrobit.ebsolys.core.targetdata.api.ModelElementPool;
import com.elektrobit.ebsolys.core.targetdata.api.Properties;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationChangedListener;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationProvider;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ClearChunkDataListener;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

/**
 * This class provides a implementation of the {@link ComRelationAcceptor}.
 * 
 * This class is implemented as Singleton-Pattern.
 * 
 * @author rage2903
 * @version 11.05
 */
@Component(immediate = true)
public final class ComRelationAcceptorImpl implements ComRelationAcceptor, ComRelationProvider, ClearChunkDataListener
{
    private final static Logger LOG = Logger.getLogger( ComRelationAcceptorImpl.class );

    private final List<ComRelation> comRelations = new ArrayList<ComRelation>();

    private final ConcurrentHashMap<TreeNode, List<ComRelation>> comRelationMap = new ConcurrentHashMap<TreeNode, List<ComRelation>>();

    private final List<ComRelationChangedListener> comRelationChangedListeners = new CopyOnWriteArrayList<ComRelationChangedListener>();

    private ModelElementPool modelElementPool;

    public ComRelationAcceptorImpl()
    {
    }

    @Reference
    public void bindModelElementPool(ModelElementPool modelElementPool)
    {
        this.modelElementPool = modelElementPool;
    }

    public void unbindModelElementPool(ModelElementPool modelElementPool)
    {
        this.modelElementPool = null;
    }

    @Override
    public ComRelation[] getComRelations(TreeNode node)
    {
        List<ComRelation> comRelationList = comRelationMap.get( node );
        if (comRelationList == null)
        {
            return new ComRelation[0];
        }

        ComRelation comRelationArray[] = new ComRelation[comRelationList.size()];
        return comRelationList.toArray( comRelationArray );
    }

    @Override
    public Set<ComRelation> getComRelations()
    {
        Set<ComRelation> comRelations = new HashSet<ComRelation>();
        Collection<List<ComRelation>> comrelationLists = comRelationMap.values();
        for (List<ComRelation> nextComRelations : comrelationLists)
        {
            comRelations.addAll( nextComRelations );
        }
        return comRelations;
    }

    @Override
    public Properties getComRelationsProperties(ComRelation comRelation)
    {
        return comRelation.getProperties();
    }

    @Override
    public ComRelation createOrGetComRelation(TreeNode sender, TreeNode receiver, String name)
    {
        ComRelation comRelation = ComRelationImpl
                .createInstance( new ComRelationBuilder( sender, receiver ).addName( name ) );

        modelElementPool.insertModelElement( comRelation );

        if (comRelations.contains( comRelation ))
        {
            LOG.debug( "ComRelation " + comRelation + " already available. " );
            // ComRelation already available
            return comRelation;
        }

        comRelations.add( comRelation );

        addComRelationToSenderList( comRelation );
        addComRelationToReceiverList( comRelation );

        // notify listener
        for (ComRelationChangedListener listener : comRelationChangedListeners)
        {
            listener.added( comRelation );
        }

        // update upper layers recursively
        TreeNode parentSender = sender.getParent();
        TreeNode parentReceiver = receiver.getParent();
        if (parentSender != null && parentReceiver != null)
        {
            if (getComRelation( parentSender, parentReceiver ) == null)
            {
                addComRelation( parentSender,
                                parentReceiver,
                                parentSender.getName() + "->" + parentReceiver.getName() );
            }
        }

        return comRelation;
    }

    @Override
    public ComRelation addComRelation(TreeNode sender, TreeNode receiver, String name)
    {
        ComRelation comRelation = ComRelationImpl
                .createInstance( new ComRelationBuilder( sender, receiver ).addName( name ) );

        modelElementPool.insertModelElement( comRelation );

        if (comRelations.contains( comRelation ))
        {
            LOG.debug( "ComRelation " + comRelation + " already available. " );
            // ComRelation already available
            return null;
        }

        comRelations.add( comRelation );

        addComRelationToSenderList( comRelation );
        addComRelationToReceiverList( comRelation );

        // notify listener
        for (ComRelationChangedListener listener : comRelationChangedListeners)
        {
            listener.added( comRelation );
        }

        // update upper layers recursively
        TreeNode parentSender = sender.getParent();
        TreeNode parentReceiver = receiver.getParent();
        if (parentSender != null && parentReceiver != null)
        {
            if (getComRelation( parentSender, parentReceiver ) == null)
            {
                addComRelation( parentSender,
                                parentReceiver,
                                parentSender.getName() + "->" + parentReceiver.getName() );
            }
        }

        return comRelation;
    }

    @Override
    public void removeComRelation(ComRelation comRelation)
    {
        if (comRelation == null)
        {
            return;
        }

        comRelations.remove( comRelation );
        List<ComRelation> recvComRelations = comRelationMap.get( comRelation.getReceiver() );
        if (recvComRelations != null)
        {
            recvComRelations.remove( comRelation );
        }

        List<ComRelation> sendComRelations = comRelationMap.get( comRelation.getSender() );
        if (sendComRelations != null)
        {
            sendComRelations.remove( comRelation );
        }

        for (ComRelationChangedListener listener : comRelationChangedListeners)
        {
            listener.removed( comRelation );
        }

        // update upper layers
        TreeNodeImpl senderImpl = (TreeNodeImpl)comRelation.getSender();
        TreeNodeImpl receiverImpl = (TreeNodeImpl)comRelation.getReceiver();

        TreeNodeImpl parentSenderImpl = (TreeNodeImpl)senderImpl.getParent();
        TreeNodeImpl parentReceiverImpl = (TreeNodeImpl)receiverImpl.getParent();

        if (parentSenderImpl == null || parentReceiverImpl == null)
        {
            // sender or receiver haven't a parent node
            return;
        }

        if (countComRelationsOfChildren( parentSenderImpl, parentReceiverImpl ) == 0)
        {
            ComRelation parentComRelation = getComRelation( parentSenderImpl, parentReceiverImpl );
            if (parentComRelation != null)
            {
                removeComRelation( getComRelation( parentSenderImpl, parentReceiverImpl ) );
            }
            else
            {
                LOG.warn( "Couldn't find parent ComRelation. " );
            }
        }
    }

    /**
     * Counts number of {@link ComRelation} of children of the given sender and receiver {@link TreeNode}. Sender and
     * receiver mustn't be null!
     * 
     * @param sender
     * @param receiver
     * @return Number of {@link ComRelation}s of children of the given {@link TreeNode}s.
     * @throws NullPointerException
     *             if sender or receiver {@link TreeNode} is null.
     */
    private int countComRelationsOfChildren(TreeNode sender, TreeNode receiver)
    {
        if (sender == null || receiver == null)
        {
            throw new NullPointerException();
        }
        int num = 0;
        TreeNodeImpl senderImpl = (TreeNodeImpl)sender;
        TreeNodeImpl receiverImpl = (TreeNodeImpl)receiver;
        for (TreeNode senderChild : senderImpl.getChildren())
        {
            List<ComRelation> senderChildComRelationList = comRelationMap.get( senderChild );

            if (senderChildComRelationList == null)
            {
                // sender has no ComRelations
                continue;
            }

            for (TreeNode receiverChild : receiverImpl.getChildren())
            {
                for (ComRelation cr : senderChildComRelationList)
                {
                    if (cr.getReceiver().equals( receiverChild ))
                    {
                        num++;
                    }
                }
            }
        }

        return num;
    }

    @Override
    public void doComRelation(ComRelation comRelation)
    {
        if (!comRelations.contains( comRelation ))
        {
            LOG.error( "ERROR: ComRelation not available. Please add ComRelation first!" );
            throw new UnsupportedOperationException();
        }

        PropertiesImpl properties = (PropertiesImpl)comRelation.getProperties();
        Long numOfCalls = (Long)properties.getValue( "NumberOfCalls" );

        if (numOfCalls == null)
        {
            numOfCalls = new Long( 0 );
        }
        numOfCalls++;

        properties.put( "NumberOfCalls", numOfCalls, "Number of calls" );

        // notify listener
        for (ComRelationChangedListener listener : comRelationChangedListeners)
        {
            listener.done( comRelation );
        }

        // call parent edge recursively
        ComRelation parentComRelation = getComRelation( ((TreeNodeImpl)comRelation.getSender()).getParent(),
                                                        ((TreeNodeImpl)comRelation.getReceiver()).getParent() );
        if (parentComRelation != null)
        {
            doComRelation( parentComRelation );
        }

    }

    @Override
    public void undoComRelation(ComRelation comRelation)
    {
        if (!comRelations.contains( comRelation ))
        {
            LOG.error( "ERROR: ComRelation not available. Please add ComRelation first!" );
            throw new UnsupportedOperationException();
        }

        PropertiesImpl properties = (PropertiesImpl)comRelation.getProperties();
        Long numOfCalls = (Long)properties.getValue( "NumberOfCalls" );

        if (numOfCalls < 1)
        {
            LOG.warn( "WARN: Couldn't undo this call, because it never happened!" );
        }
        else
        {
            numOfCalls--;
            properties.put( "NumberOfCalls", numOfCalls, "Number of calls" );
        }

        // notify listener
        for (ComRelationChangedListener listener : comRelationChangedListeners)
        {
            listener.undone( comRelation );
        }
    }

    @Override
    public boolean addProperty(ComRelation comRelation, Object key, Object value, String description)
    {
        if (comRelation == null)
        {
            LOG.warn( "Couldn't add property to ComRelation, which is null!" );
            LOG.debug( "PropertyKey was: " + key );
            LOG.debug( "PropertyValue was: " + value );
            return false;
        }

        if (key == null)
        {
            LOG.warn( "Couldn't add property with key which is null!" );
            LOG.debug( "PropertyKey was: " + key );
            LOG.debug( "PropertyValue was: " + value );
            return false;
        }

        if (!(comRelation instanceof ComRelationImpl))
        {
            LOG.error( "Couldn't add properties for a ComRelation which isn't instance of ComRelationImpl!" );
            return false;
        }

        ComRelationImpl comRelationImpl = (ComRelationImpl)comRelation;

        if (comRelationImpl.getProperties().getKeys().contains( key ))
        {
            LOG.warn( "Couldn't not add a properties key, which is already there!" );
            return false;
        }

        comRelationImpl.setProperty( key, value, description );
        return true;
    }

    @Override
    public boolean changeProperty(ComRelation comRelation, Object key, Object value, String description)
    {
        if (comRelation == null)
        {
            LOG.warn( "Couldn't change property to ComRelation, which is null!" );
            LOG.debug( "PropertyKey was: " + key );
            LOG.debug( "PropertyValue was: " + value );
            return false;
        }

        if (key == null)
        {
            LOG.warn( "Couldn't change property with key which is null!" );
            LOG.debug( "PropertyKey was: " + key );
            LOG.debug( "PropertyValue was: " + value );
            return false;
        }

        if (!(comRelation instanceof ComRelationImpl))
        {
            LOG.error( "Couldn't add properties for a ComRelation which isn't instance of ComRelationImpl!" );
            return false;
        }

        ComRelationImpl comRelationImpl = (ComRelationImpl)comRelation;

        if (!(comRelationImpl.getProperties().getKeys().contains( key )))
        {
            LOG.warn( "Couldn't not add a properties key, which is already there!" );
            return false;
        }

        comRelationImpl.setProperty( key, value, description );
        return true;
    }

    @Override
    public void removeProperty(ComRelation comRelation, Object key)
    {
        if (comRelation == null)
        {
            LOG.warn( "Couldn't remove property of ComRelation, which is null!" );
            return;
        }
        if (key == null)
        {
            LOG.warn( "Couldn't remove property with key, which is null!" );
            return;
        }
        if (!(comRelation instanceof ComRelationImpl))
        {
            LOG.error( "Couldn't remove properties for a ComRelation which isn't instance of ComRelationImpl!" );
            return;
        }
        PropertiesImpl properties = (PropertiesImpl)comRelation.getProperties();
        properties.remove( key );
    }

    @Override
    public void addComRelationChangedListener(ComRelationChangedListener listener)
    {
        comRelationChangedListeners.add( listener );
    }

    @Override
    public void removeComRelationChangedListener(ComRelationChangedListener listener)
    {
        comRelationChangedListeners.remove( listener );
    }

    @Override
    public List<ComRelation> getChildrenComRelationsRecusivly(ComRelation startComRelation)
    {
        List<ComRelation> inputComRelations = new ArrayList<ComRelation>();
        inputComRelations.add( startComRelation );

        List<ComRelation> resultComRelations = new ArrayList<ComRelation>();
        resultComRelations.add( startComRelation );

        while (!inputComRelations.isEmpty())
        {
            inputComRelations.addAll( getChildrenComRelations( inputComRelations.get( 0 ) ) );
            resultComRelations.addAll( getChildrenComRelations( inputComRelations.get( 0 ) ) );
            inputComRelations.remove( 0 );
        }

        return resultComRelations;
    }

    @Override
    public List<ComRelation> getChildrenComRelationsRecusivly(List<ComRelation> startComRelations)
    {
        Set<ComRelation> comRelationsFromAllLevels = new HashSet<ComRelation>();
        for (ComRelation comRelation : startComRelations)
        {
            List<ComRelation> childComRelations = getChildrenComRelationsRecusivly( comRelation );
            comRelationsFromAllLevels.addAll( childComRelations );
        }
        List<ComRelation> comRelationsList = new ArrayList<ComRelation>( comRelationsFromAllLevels );
        return comRelationsList;
    }

    @Override
    public List<TreeNode> getConnectedTreeNodes(TreeNode treeNode)
    {
        ComRelation[] connectedComRelations = getComRelations( treeNode );
        Set<TreeNode> connectedTreeNodes = new HashSet<TreeNode>();
        for (ComRelation comRelation : connectedComRelations)
        {
            connectedTreeNodes.add( comRelation.getSender() );
            connectedTreeNodes.add( comRelation.getReceiver() );
        }
        List<TreeNode> resultList = new ArrayList<TreeNode>( connectedTreeNodes );
        return resultList;
    }

    @Override
    public List<TreeNode> getConnectedReceivers(TreeNode treeNode)
    {
        ComRelation[] connectedComRelations = getComRelations( treeNode );
        Set<TreeNode> connectedTreeNodes = new HashSet<TreeNode>();
        for (ComRelation comRelation : connectedComRelations)
        {
            connectedTreeNodes.add( comRelation.getReceiver() );
        }
        connectedTreeNodes.remove( treeNode );
        List<TreeNode> resultList = new ArrayList<TreeNode>( connectedTreeNodes );
        return resultList;
    }

    private List<ComRelation> getChildrenComRelations(ComRelation startComRelation)
    {
        List<TreeNode> senderChildren = new ArrayList<TreeNode>();
        List<TreeNode> receiverChildren = new ArrayList<TreeNode>();
        senderChildren.addAll( startComRelation.getSender().getChildren() );
        receiverChildren.addAll( startComRelation.getReceiver().getChildren() );

        List<ComRelation> childrenComRelation = new ArrayList<ComRelation>();
        for (TreeNode nextSenderNode : senderChildren)
        {
            for (ComRelation nextComRelation : getComRelations( nextSenderNode ))
            {
                for (TreeNode nextReceiver : receiverChildren)
                {
                    if (nextComRelation.getSender().equals( nextSenderNode )
                            && nextComRelation.getReceiver().equals( nextReceiver ))
                    {
                        childrenComRelation.add( nextComRelation );
                    }
                }
            }
        }
        return childrenComRelation;
    }

    // //////////////////////////////////
    // //
    // Private Methods //
    // //
    // //////////////////////////////////

    /**
     * Adds a {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation} to its own sender.
     * 
     * @param comRelation
     *            the {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation} that should be added to
     *            its sender list
     */
    private void addComRelationToSenderList(ComRelation comRelation)
    {
        // Add ComRelation to sender
        List<ComRelation> comRelationList = null;
        if (!comRelationMap.containsKey( comRelation.getSender() ))
        {
            comRelationList = new ArrayList<ComRelation>();
            comRelationList.add( comRelation );
            comRelationMap.put( comRelation.getSender(), comRelationList );
        }
        else
        {
            comRelationList = comRelationMap.get( comRelation.getSender() );

            if (!comRelationList.contains( comRelation ))
            {
                comRelationList.add( comRelation );
            }
        }
    }

    /**
     * Adds a {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation} to its own receiver.
     * 
     * @param comRelation
     *            the {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation} that should be added to
     *            its receiver
     */
    private void addComRelationToReceiverList(ComRelation comRelation)
    {
        // Add ComRelation to sender
        List<ComRelation> comRelationList = null;
        if (!comRelationMap.containsKey( comRelation.getReceiver() ))
        {
            comRelationList = new ArrayList<ComRelation>();
            comRelationList.add( comRelation );
            comRelationMap.put( comRelation.getReceiver(), comRelationList );
        }
        else
        {
            comRelationList = comRelationMap.get( comRelation.getReceiver() );

            if (!comRelationList.contains( comRelation ))
            {
                comRelationList.add( comRelation );
            }
        }
    }

    /**
     * Returns the ComRelation with the given sender, receiver and name and null, if the ComRelation is not available.
     * 
     * @param sender
     *            Sender of the ComRelation as TreeNode
     * @param receiver
     *            Receiver of the ComRelation as TreeNode
     * @param name
     *            Name of the ComRelation as String
     * @return the ComRelation with the given sender, receiver and name and null, if the ComRelation is not available.
     */
    private ComRelation getComRelation(TreeNode sender, TreeNode receiver)
    {
        List<ComRelation> sendersComRelationList = comRelationMap.get( sender );
        if (sendersComRelationList == null || sendersComRelationList.isEmpty())
        {
            return null;
        }

        for (ComRelation cr : sendersComRelationList)
        {
            if (receiver.equals( cr.getReceiver() ))
            {
                return cr;
            }
        }

        return null;
    }

    @Override
    public void onClearChunkData()
    {
        modelElementPool.performReset();
        comRelationMap.clear();
        comRelations.clear();

    }

}
