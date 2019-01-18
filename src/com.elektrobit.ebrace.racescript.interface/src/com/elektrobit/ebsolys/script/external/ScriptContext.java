/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.script.external;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.SortedSet;

import com.elektrobit.ebrace.core.systemmodel.api.SystemModel;
import com.elektrobit.ebrace.core.systemmodel.api.SystemModelAccess;
import com.elektrobit.ebrace.core.systemmodel.api.SystemModelChangedListener;
import com.elektrobit.ebrace.core.systemmodel.api.ViewModelGenerator;
import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedTree;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventTag;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.STimeSegment;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;

public interface ScriptContext extends UIResourcesContext
{
    /**
     * Load a file into EB solys.
     * 
     * @param path
     *            Path to file that should be loaded including filename and extension.
     * @return true if loading was successful, otherwise false.
     */
    public boolean loadFile(String path);

    /**
     * Clears all data related to loaded files in EB solys.
     */
    public void clearAllData();

    /**
     * Create a new connection that can be later used to connect to target.
     * 
     * @deprecated Use createOrGetConnection instead
     * @param name
     *            Name of connection visible in the UI
     * @param host
     *            Host name or IP address of the target
     * @param port
     *            Port to connect to at target (port of Target Agent)
     * @param saveDataToFile
     *            if true, incoming data will be saved in the recordings folder
     * @return
     */
    @Deprecated
    public SConnection createConnection(String name, String host, int port, boolean saveDataToFile);

    /**
     * Try to retrieve an existing connection. If not available then create a new connection. The connection can be
     * later used to connect to target.
     * 
     * @param name
     *            Name of connection visible in the UI
     * @param host
     *            Host name or IP address of the target
     * @param port
     *            Port to connect to at target (port of Target Agent)
     * @param saveDataToFile
     *            if true, incoming data will be saved in the recordings folder
     * @return
     */
    public SConnection createOrGetConnection(String name, String host, int port, boolean saveDataToFile);

    /**
     * Retrieve an existing connection that can be later used to connect to target.
     * 
     * @param name
     *            Name of connection visible in the UI
     * @param host
     *            Host name or IP address of the target
     * @param port
     *            Port to connect to at target (port of Target Agent)
     * @return
     */
    public SConnection getConnection(String name, String host, int port);

    /**
     * Connect to target described by {@code connection}.
     * 
     * @param connection
     *            Connection object created by {@link #createConnection(String, String, int, boolean) createConnection}
     * @return true if connection was established, otherwise false
     */
    public boolean connectToTarget(SConnection connection);

    /**
     * Shuts down connection that has been previously established using {@code connection} model
     * 
     * @param connection
     */
    public void disconnectFromTarget(SConnection connection);

    /**
     * Shuts down all open connection
     */
    public void disconnectAllTargets();

    /**
     * TIMEMARKERS
     */

    /**
     * Gets all timemarkers, sorted by time-stamp (ascending)
     * 
     * @return All timemarkers
     */
    public SortedSet<TimeMarker> getAllTimemarkers();

    /**
     * Gets all timemarkers, sorted by time-stamp (ascending) starting from a certain time-stamp
     * 
     * @param timestamp
     *            The given time-stamp
     * @return All timemarkers
     */
    public SortedSet<TimeMarker> getTimemarkersFrom(long timestamp);

    /**
     * Gets all timemarkers, sorted by time-stamp (ascending) between two time-stamps
     * 
     * @param fromTimestamp
     *            The starting time-stamp
     * @param toTimestamp
     *            The ending time-stamp
     * @return All timemarkers
     */
    public SortedSet<TimeMarker> getTimemarkersBetween(long fromTimestamp, long toTimestamp);

    /**
     * Creates a new timemarker at a certain time-stamp. The timemarker's name is auto-generated
     * 
     * @param timestamp
     *            The given time-stamp
     * @return The created timemarker
     */
    public TimeMarker createTimemarker(long timestamp);

    /**
     * Creates a new timemarker at a certain time-stamp with a given name
     * 
     * @param timestamp
     *            The given time-stamp
     * @param name
     *            The name of the marker
     * @return The created timemarker
     */
    public TimeMarker createTimemarker(long timestamp, String name);

    /**
     * Removes all timemarkers
     * 
     * @return true if operation was successful, false otherwise
     */
    public void removeAllTimemarkers();

    /**
     * Removes a certain timemarker
     * 
     * @param timemarker
     *            The timemarker, that should be removed
     * @return true if operation was successful, false otherwise
     */
    public void removeTimemarker(TimeMarker marker);

    /**
     * Removes all timemarkers that begin with a certain prefix
     * 
     * @param string
     *            The prefix of the timemarkers
     */
    public void removeAllTimemarkersWithPrefix(String prefix);

    /**
     * CHANNELS
     */

    /**
     * Gets all channels
     * 
     * @return All channels. Set can be empty, if no channel exists
     */

    /**
     * Makes all views to jump to this Time Marker
     * 
     * @param timeMarker
     *            Time Marker to jump to
     */
    public void jumpTo(TimeMarker timeMarker);

    public List<RuntimeEventChannel<?>> getAllChannels();

    /**
     * Creates a new channel or gets the one with the given name. The Channel must have a unit, and it can only contain
     * values of a data type of the unit.
     * 
     * @param name
     *            The channel name (mandatory, cannot be null)
     * @param unit
     *            Unit of the channel. Use predefined unit e.g. <i>Unit.PERCENT</i> or create your own with
     *            <i>Unit.createCustomUnit(String name, Class<E> dataType)</i>
     * @param description
     *            a short description (optional, can be null)
     * @return The new created channel or the one with the given name
     */
    public <T> RuntimeEventChannel<T> createOrGetChannel(String name, Unit<T> unit, String description);

    /**
     * Creates a new channel or gets the one with the given name. The Channel must have a unit, and it can only contain
     * values of a data type of the unit.
     * 
     * @param name
     *            The channel name (mandatory, cannot be null)
     * @param unit
     *            Unit of the channel. Use predefined unit e.g. <i>Unit.PERCENT</i> or create your own with
     *            <i>Unit.createCustomUnit(String name, Class<E> dataType)</i>
     * @param description
     *            a short description (optional, can be null)
     * @param valueColumns
     *            A value column describes which part of a json value shall be visualized in a table. E.g. if a event
     *            looks like this {"key1":"val1", "key2", "val2"} and the value column is {"key2"}, only key2 is visible
     *            as a value column in the UI table.
     * @return The new created channel or the one with the given name
     */
    public <T> RuntimeEventChannel<T> createOrGetChannel(String name, Unit<T> unit, String description,
            List<String> valueColumns);

    /**
     * * Creates a new channel or gets the one with the given name. The Channel must have a unit, and it can only
     * contain values of a data type of the unit. Channel color will be always set to (r,g,b) values.
     * 
     * @param name
     *            The channel name (mandatory, cannot be null)
     * @param unit
     *            Unit of the channel. Use predefined unit e.g. <i>Unit.PERCENT</i> or create your own with
     *            <i>Unit.createCustomUnit(String name, Class<E> dataType)</i>
     * @param description
     *            a short description (optional, can be null)
     * @param r
     *            Red component of channel's color
     * @param g
     *            Green component of channel's color
     * @param b
     *            Blue component of channel's color
     * @return The new created channel. Null if the channel already exists
     */
    public <T> RuntimeEventChannel<T> createOrGetChannel(String name, Unit<T> unit, String description, int r, int g,
            int b);

    /**
     * Creates a new channel or gets the one with the given name. The Channel must have a unit, and it can only contain
     * values of a data type of the unit. Channel color will be always set to rgb value.
     * 
     * @param name
     *            The channel name (mandatory, cannot be null)
     * @param unit
     *            Unit of the channel. Use predefined unit e.g. <i>Unit.PERCENT</i> or create your own with
     *            <i>Unit.createCustomUnit(String name, Class<E> dataType)</i>
     * @param description
     *            a short description (optional, can be null)
     * @param rgb
     *            Array with length 3 that contains red, green and blue components of color
     * @return
     */
    public <T> RuntimeEventChannel<T> createOrGetChannel(String name, Unit<T> unit, String description, int[] rgb);

    /**
     * Remove a runtime channel and all of its events.
     * 
     * @param channel
     *            The channel to be removed
     */
    public <T> void removeChannel(RuntimeEventChannel<T> channel);

    /**
     * Adds a new event to the given channel. The event object is created internally
     * 
     * @param channel
     *            The channel where the event should be added
     * @param timestamp
     *            The time-stamp of the internal created event
     * @param value
     *            The value of the internal created event
     * @param modelElement
     *            Can be linked to a model element if required. Set to null if not applicable
     * @return true if operation was successful, false otherwise
     */
    public <T> void addEventToChannel(RuntimeEventChannel<T> channel, long timestamp, T value,
            ModelElement modelElement);

    /**
     * Adds a new runtime event to a channel
     * 
     * @param channel
     *            The channel where the event should be added
     * @param timestamp
     *            The time-stamp of added event
     * @param value
     *            The value. Must be of same type as the channel type
     */
    public <T> void addEvent(RuntimeEventChannel<T> channel, long timestamp, T value);

    /**
     * Gets all events from a certain channel
     * 
     * @param channel
     *            The given channel
     * @return All events. List can be empty if channel has no events
     */
    public List<RuntimeEvent<?>> getEvents(RuntimeEventChannel<?> channel);

    /**
     * Gets all events from a certain channels
     * 
     * @param channels
     *            The given channels
     * @return All events. List can be empty if channels have no events
     */
    public List<RuntimeEvent<?>> getAllEventsFromChannels(List<RuntimeEventChannel<?>> channels);

    /**
     * Returns first RuntimeEvent of {@code channel} after the {@code timeMarker}. This event corresponds to visible
     * value of {@code channel}, e.g. in chart
     * 
     * @param channel
     *            to get RuntimeEvent from
     * @param timeMarker
     *            to indicate timestamp
     * @return First RuntimeEvent of {@code channel} after {@code timeMarker} or NULL, when timestamp of
     *         {@code timeMarker} is not after the first and before the last event of {@code channel}.
     */
    public RuntimeEvent<?> getEventAt(RuntimeEventChannel<?> channel, TimeMarker timeMarker);

    /**
     * Set a color of channel that will be displayed in UI
     * 
     * @param channel
     *            Channel whose color should be set
     * @param r
     *            Red component of color
     * @param g
     *            Green component of color
     * @param b
     *            Blue component of color
     */
    public void setColorOfChannel(RuntimeEventChannel<?> channel, int r, int g, int b);

    /**
     * @deprecated Replaced by {@link #setColorOfChannel(RuntimeEventChannel<?>, int, int, int)}
     */
    @Deprecated
    public void setColorOfChanel(RuntimeEventChannel<?> channel, int r, int g, int b);

    /**
     * Set a color of channel that will be displayed in UI
     * 
     * @param channel
     *            Channel whose color should be set
     * @param rgb
     *            Array with length 3 that contains red, green and blue components of color
     */
    public void setColorOfChannel(RuntimeEventChannel<?> channel, int[] rgb);

    /**
     * @deprecated Replaced by {@link #setColorOfChannel(RuntimeEventChannel<?>, int[])}
     */
    @Deprecated
    public void setColorOfChanel(RuntimeEventChannel<?> channel, int[] rgb);

    /**
     * Return current color of channel
     * 
     * @param channel
     * @return color of channel
     */
    public int[] getColorOfChannel(RuntimeEventChannel<?> channel);

    /**
     * Adds a tag and a tag description to a RuntimeEvent. Predefined tags can be found in RuntimeEventTag. Tag will be
     * shown in Tables as icons. Tag name and description will be shown as tool tip.
     * 
     * @param event
     *            The event, that shall be tagged.
     * @param tag
     *            The tag type, e.g. WARNING, ERROR or some custom tag.
     * @param tagDescription
     *            The description of the tag, e.g. 'Wrong used API'.
     * @return Returns the modified RuntimeEvent.
     */
    public RuntimeEvent<?> setTag(RuntimeEvent<?> event, RuntimeEventTag tag, String tagDescription);

    /**
     * Clears a tag and its description from a RuntimeEvent.
     * 
     * @param event
     *            The event, that shall be cleared.
     * @return Returns the modified RuntimeEvent.
     */
    public RuntimeEvent<?> clearTag(RuntimeEvent<?> event);

    /**
     * COMMON
     */

    /**
     * Prints a certain text into the console without line-feed
     * 
     * @param textThe
     *            given text
     */
    public void consolePrint(String text);

    /**
     * Prints a certain text into the console with line-feed
     * 
     * @param textThe
     *            given text
     */
    public void consolePrintln(String text);

    /**
     * Decodes the value of a RuntimeEvent with the registered DecoderService.
     * 
     * @param runtimeEventToDecode
     *            The RuntimeEvent, which shall be decoded.
     * @return Returns the decoded RuntimeEvent, if a DecoderService was registered, null otherwise.
     */
    public DecodedRuntimeEvent decode(RuntimeEvent<?> runtimeEventToDecode);

    /**
     * This method checks a given trace string (Format: MESSAGETYPE_MessageCallName, e.g. CALL_CreateLocationInput)
     * against the Franca contract of a given interface name (full qualified name).
     * 
     * @param fullQualifiedInterface
     *            The interface name (full qualified), which contains the Franca contract.
     * @param trace
     *            The communication message, that should be checked against the contract in the format
     *            MESSAGETYPE_MessageCallName).
     * @return true, if this was a valid call and false otherwise.
     */
    public boolean isValidFrancaTrace(String fullQualifiedInterface, List<String> trace);

    /**
     * Converts a given String, that was converted by {@link #convertToString(DecodedTree)} to a DecodedTree.
     * 
     * @param The
     *            String representation of the DecodedTree.
     * 
     * @return The given String as DecodedTree, null if the format was wrong.
     * 
     * @see #convertToString(DecodedTree)
     */
    public DecodedTree convertFromString(String decodedTreeAsString);

    /**
     * Converts a given decodeTree to a String representation.
     * 
     * @param The
     *            DecodedTree, that shall be converted.
     * 
     * @return The given DecodedTree as String.
     * 
     * @see #convertFromString(String)
     */
    public String convertToString(DecodedTree decodedTree);

    /**
     * Search a tree from a given start node for a key and return the DecodedNode with the first occurrence of the key
     * 
     * @param startNode
     *            Root tree element or sub tree element of decoded tree
     * @param key
     *            The key = DecodedNode name to search for
     * @return DecodedNode or null if not found
     */
    public DecodedNode getFirstNode(DecodedNode startNode, String key);

    /**
     * Search a tree for a key from a given start node and return all DecodedNodes with the key
     * 
     * @param startNode
     *            Root tree element or sub tree element of decoded tree
     * @param key
     *            The key = DecodedNode name to search for
     * @return List of DecodedNode
     */
    public List<DecodedNode> getNodes(DecodedNode startNode, String key);

    /**
     * Search a tree from a given start node for a key and return the value of the DecodedNode with the first occurrence
     * of the key
     * 
     * @param startNode
     *            Root tree element or sub tree element of decoded tree
     * @param key
     *            The key = DecodedNode name to search for
     * @return Value of the DecodedNode or null if not found
     */
    public String getFirstValue(DecodedNode startNode, String key);

    /**
     * Search a tree from a given start node for a key and return all DecodedNode values with the key
     * 
     * @param startNode
     *            Root tree element or sub tree element of decoded tree
     * @param key
     *            The key = DecodedNode name to search for
     * @return A list of all DecodedNode values
     */
    public List<String> getValues(DecodedNode startNode, String key);

    /**
     * Search within the decodedRuntimeEvents decoded tree from for a key and return the DecodedNode with the first
     * occurrence of the key
     * 
     * @param decodedRuntimeEvent
     *            decodedRuntimeEvent whose DecodedTree is searched within for the key
     * @param key
     *            The key = DecodedNode name to search for
     * @return DecodedNode or null if not found
     */
    public DecodedNode getFirstNode(DecodedRuntimeEvent decodedRuntimeEvent, String key);

    /**
     * Search decodedRuntimeEvents decoded tree for a key and return all DecodedNodes with the occurrence of the key
     * 
     * @param decodedRuntimeEvent
     *            decodedRuntimeEvent whose DecodedTree is searched within for the key
     * @param key
     *            The key = DecodedNode name to search for
     * @return List of DecodedNode
     */
    public List<DecodedNode> getNodes(DecodedRuntimeEvent decodedRuntimeEvent, String key);

    /**
     * Search within the decodedRuntimeEvents decoded tree for a key and return the value of the DecodedNode with the
     * first occurrence of the key
     * 
     * @param decodedRuntimeEvent
     *            decodedRuntimeEvent whose DecodedTree is searched within for the key
     * @param key
     *            The key = DecodedNode name to search for
     * @return Value of the DecodedNode or null if not found
     */
    public String getFirstValue(DecodedRuntimeEvent decodedRuntimeEvent, String key);

    /**
     * Search within the decodedRuntimeEvents decoded tree for a key and return all DecodedNode values with the key
     * 
     * @param decodedRuntimeEvent
     *            decodedRuntimeEvent whose DecodedTree is searched within for the key
     * @param key
     *            The key = DecodedNode name to search for
     * @return A list of all DecodedNode values
     */
    public List<String> getValues(DecodedRuntimeEvent decodedRuntimeEvent, String key);

    /**
     * Check if a key exists in a decoded tree
     * 
     * @param startNode
     *            Root tree element or sub tree element of decoded tree
     * @param key
     *            The key = DecodedNode name to search for
     * @return true if a key exists else false
     */
    public boolean keyExists(DecodedNode startNode, String key);

    /**
     * Check if a key exists in the decoded tree of the given decodedRuntimeEvent
     * 
     * @param decodedRuntimeEvent
     *            decodedRuntimeEvent whose DecodedTree is searched within for the key
     * @param key
     *            The key = DecodedNode name to search for
     * @return true if a key exists else false
     */
    public boolean keyExists(DecodedRuntimeEvent decodedRuntimeEvent, String key);

    /**
     * Get number of occurrence of a key in a decoded tree
     * 
     * @param startNode
     *            Root tree element or sub tree element of decoded tree
     * @param key
     *            The key = DecodedNode name to search for
     * @return number of occurrences
     */
    public int numberOfKeys(DecodedNode startNode, String key);

    /**
     * Get number of occurrences of a key in the decodedRuntimeEvents decoded tree
     * 
     * @param decodedRuntimeEvent
     *            decodedRuntimeEvent whose DecodedTree is searched within for the key
     * @param key
     *            The key = DecodedNode name to search for
     * @return number of occurrences
     */
    public int numberOfKeys(DecodedRuntimeEvent decodedRuntimeEvent, String key);

    /**
     * Pop Up a message box to inform user about some information
     * 
     * @param message
     *            message to be shown in window
     */
    public void messageBoxInfo(String message);

    /**
     * Pop Up a message box to inform user about some warnings
     * 
     * @param message
     *            message to be shown in window
     */
    public void messageBoxWarning(String message);

    /**
     * Pop Up a message box to inform user about some errors
     * 
     * @param message
     *            message to be shown in window
     */
    public void messageBoxError(String message);

    /**
     * TIMESEGMENTS
     */

    /**
     * Creates a new TimeSegmentChannel if not exists, otherwise it returns the existing TimeSegmentChannel.
     * TimeSegmentChannel name is used as identifier. If existing name with different description is used, then the
     * existing TimeSegmentChannel is returned.
     * 
     * @param name
     *            Name of this TimeSegmentChannel.
     * @param description
     *            Description of this TimeSegmentChannel.
     * @return Returns the created TimeSegmentChannel.
     */
    public RuntimeEventChannel<STimeSegment> createOrGetTimeSegmentChannel(String name, String description);

    /**
     * Adds a new TimeSegment to the given TimeSegmentChannel. TimeSegmentChannel must be created before.
     * 
     * @param timeSegmentChannel
     *            The TimeSegmentChannel the TimeSegment shall be added.
     * @param timeSegment
     *            The TimeSegment that shall be added.
     * @return Returns the TimeSegmentChannel.
     */
    public STimeSegment add(RuntimeEventChannel<STimeSegment> timeSegmentChannel, RuntimeEvent<?> startEvent,
            RuntimeEvent<?> endEvent);

    /**
     * Adds a new TimeSegment to the given TimeSegmentChannel. TimeSegmentChannel must be created before.
     * 
     * @param timeSegmentChannel
     *            The TimeSegmentChannel the TimeSegment shall be added.
     * @param timeSegment
     *            The TimeSegment that shall be added.
     * @param label
     *            The label that shall be shown.
     * @return Returns the TimeSegmentChannel.
     */
    public STimeSegment add(RuntimeEventChannel<STimeSegment> timeSegmentChannel, RuntimeEvent<?> startEvent,
            RuntimeEvent<?> endEvent, String label);

    /**
     * Returns all TimeSegments of the given Channel.
     * 
     * @param timeSegmentChannel
     * @return
     */
    public List<STimeSegment> getTimeSegments(RuntimeEventChannel<STimeSegment> timeSegmentChannel);

    /**
     * Set a color of timeSegment that will be displayed in UI
     * 
     * @param timeSegment
     *            timeSegment whose color should be set
     * @param r
     *            Red component of color
     * @param g
     *            Green component of color
     * @param b
     *            Blue component of color
     */
    public void setColor(STimeSegment timeSegment, int r, int g, int b);

    /**
     * Set a color of timeSegment that will be displayed in UI
     * 
     * @param timeSegment
     *            timeSegment whose color should be set
     * @param rgb
     *            Array with length 3 that contains red, green and blue components of color
     */
    public void setColor(STimeSegment timeSegment, int[] rgb);

    /**
     * Renders a Plant UML text as SVG image. Plant UML syntax is described here: http://plantuml.com
     * 
     * @param plantUmlText
     *            Text that shall be rendered. Text must be correct Plant UML syntax, see http://plantuml.com
     * @param pathToImage
     *            Path to the rendered image. Be carefully, image is overridden if exists. Relative path is relative to
     *            EB solys executable folder.
     * @return Returns true, if image was created successfully, false otherwise. If Plant UML syntax was wrong, image is
     *         still generated.
     */
    public boolean plantUmlToSVG(String plantUmlText, String pathToImage);

    /**
     * Renders a Plant UML text as PNG image. Plant UML syntax is described here: http://plantuml.com
     * 
     * @param plantUmlText
     *            Text that shall be rendered. Text must be correct Plant UML syntax, see http://plantuml.com
     * @param pathToImage
     *            Path to the rendered image. Be carefully, image is overridden if exists. Relative path is relative to
     *            EB solys executable folder.
     * @return Returns true, if image was created successfully, false otherwise. If Plant UML syntax was wrong, image is
     *         still generated.
     */
    public boolean plantUmlToPNG(String plantUmlText, String pathToImage);

    /**
     * Get value of command line parameter that EB solys was started with (e.g. key=value).
     * 
     * @param key
     *            Name of the parameter
     * @return value of the parameter or null if not defined
     */
    public String getParameter(String key);

    /**
     * Query if value of parameter with specific name has set when EB solys was started.
     * 
     * @param key
     *            Name of parameter
     * @return true if value of parameter has been set (e.g. key=value), otherwise false
     */
    public boolean hasParameter(String key);

    /**
     * Adds an event based on a JSON object string containing at least the tags uptime, channel, summary and value
     * 
     * uptime: the milliseconds since system start or microseconds if an 'u' character is attached, e.g.
     * "uptime":"3000u" <br>
     * channel: the channel name <br>
     * summary: event summary <br>
     * value: value (can also be a json object) <br>
     * edge: for structured events, e.g.
     * "edge":{"source":"service1.module3.class1","destination":"service2.module3.class1","type":"request"} <br>
     * duration: duration following the same rules as uptime, to create time segments
     * 
     * @param jsonEvent
     *            the event
     */
    public void addJsonEvent(String jsonEvent);

    public SystemModel initSystemModelFromFile(String path) throws FileNotFoundException;

    public String getInputModelRepresentation(SystemModel model, ViewModelGenerator generator);

    public SystemModelAccess addSystemModelChangedListener(SystemModelChangedListener listener);
}
