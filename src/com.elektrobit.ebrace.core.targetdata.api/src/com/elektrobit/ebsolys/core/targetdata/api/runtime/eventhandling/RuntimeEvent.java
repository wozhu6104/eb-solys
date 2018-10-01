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

import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;

/**
 * This interface is supposed to gain access a certain Runtime event.
 * 
 * A runtime event is typed and cooperates with the interfaces RuntimeEventAcceptor and RuntimeEventChannel.
 * 
 * The RuntimeEventChannel is supposed as the instance which transfers the Event. However the RuntimeEventChannel
 * interface has no method which actually receives a RuntimeEvent. This is the task of the RuntimeAcceptorInterface.
 * 
 * Hence a RuntimeEvent is typed it can be handled by a RuntimeChannel with the matching type only!
 * 
 * A runtime event encapsulates the properties as follow:
 * <ul>
 * <li>A time stamp which describes the creation time of this event.</li>
 * <li>The model element which belongs to this event.</li>
 * <li>The Value which belongs to this event.</li>
 * <li>The RuntimeEventChannel which belongs to the Runtime event.</li>
 * </ul>
 * 
 * @param <T>
 *            The type of this runtime event.
 * 
 * @see RuntimeEventAcceptor
 * @see #RuntimeEventAcceptor.acceptEvent(RuntimeEventChannel, Object, T)
 * 
 * @author pedu2501@elektrobit.com
 * @version 12.06
 */
public interface RuntimeEvent<T> extends TimebasedObject
{
    /**
     * Gets the time stamp of this Runtime Event. The time stamp is in units milliseconds since January, 1st, 1970 UTC
     * 
     * @return The time stamp of this runtime event.
     */
    @Override
    public long getTimestamp();

    /**
     * Get the model element which belongs to this event. This is the model event object which has been used as it has
     * been accepted by the method RuntimeEventAcceptor.acceptEvent(RuntimeEventChannel<T>, Object modelElement, T)
     * 
     * @return The model element of this runtime event.
     */
    public ModelElement getModelElement();

    /**
     * Get the value which belongs to this event. This is the value which has been used as it has been accepted by the
     * method RuntimeEventAcceptor.acceptEvent(RuntimeEventChannel<T>, Object modelElement, T)
     * 
     * @return The value of this runtime event.
     */
    public T getValue();

    /**
     * Get the textual summary of the value.
     * 
     * @return Summary.
     */
    public String getSummary();

    /**
     * Gets the RuntimeEventChannel which is used to transfer this Event. If you want to obtain all RuntimeEventChannels
     * the use #RuntimeEventProvider.
     * 
     * @return The RuntimeEventChannel object of this RuntimeEvent.
     */
    public RuntimeEventChannel<T> getRuntimeEventChannel();

    /**
     * @return Returns RuntimeEventTag of RuntimeEvent if set, else null.
     */
    public RuntimeEventTag getTag();

    /**
     * @return Returns a description RuntimeEventTag of RuntimeEvent if set, else a empty String.
     */
    public String getTagDescription();

    /**
     * @return Returns false if getTag is null, else true.
     */
    public boolean isTagged();
}
