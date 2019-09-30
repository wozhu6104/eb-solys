package api

import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimebasedObject
import com.elektrobit.ebsolys.script.external.ScriptContext
import java.io.File
import java.util.List

class ScriptBase {

	// Allows the access to the extension methods from the native API
	extension ScriptContext ctx

	/**
       Initialize Context
    */
	new(ScriptContext _ctx) {
		ctx = _ctx
	}

	/**
     * SCRIPT API - BASICS
     */
	/**
     * Gets all channel names
     */
	def getAllChannelNames() {
		allChannels.map[name]
	}

	/**
       * Gets all channel names, which start with a certain prefix
       @param prefix Given channel prefix 
    */
	def getChannelNamesByPrefix(String prefix) {
		allChannelNames.filter[startsWith(prefix)]
	}

	/**
       * Gets a certain channel by a given channel name
       @param channelName Given channel name
    */
	def getChannel(String channelName) {
		allChannels.filter[name.equals(channelName)].head
	}

	/**
       * Gets a list of channels by given channel names
       @param channelNames Given list of channel names
       */
	def getChannels(List<String> channelNames) {
		allChannels.filter[channelNames.contains(name)].toList
	}

	/**
       * Gets a list of channels by a given prefix
       @param preifx  Given channel prefix
    */
	def getChannelsByPrefix(String prefix) {
		prefix.channelNamesByPrefix.toList.channels
	}

	/**
	 * Filters all events from a certain channel name
	 * @param events A list of events
	 * @param channel The channel name
	 */
	def filterByChannelName(List<RuntimeEvent<?>> events, String channel) {
		events.filter[runtimeEventChannel.name.equals(channel)]
	}

	/**
	 * Filters all events from channel names with a certain prefix
	 * @param events A list of events
	 * @param channel The channel name prefix
	 */
	def filterByChannelNamePrefix(List<RuntimeEvent<?>> events, String prefix) {
		events.filter[runtimeEventChannel.name.startsWith(prefix)]
	}

	/**
	 * Gets the Channel Name from a certain event
	 * @param event An event
	 * @return The channel name of the event
	 */
	def getChannelName(RuntimeEvent<?> event) {
		event.runtimeEventChannel.name
	}

	/**
	 * Returns all channels which match the given regular expression
	 * @param regex The regular expression as String
	 * @return All channels matching the regular expression
	 */
	def getChannelsByRegex(String regex) {
		allChannels.filter[name.matches(regex)]
	}

	/**
	 * Get a timemarker by a given name
	 * @param mname The timemarker name
	 * @return The timemarker
	 */
	def getFirstTimemarker(String mname) {
		allTimemarkers.filter[name.equals(mname)].head 
	}

	/**
     * Creates a timemarker based on a given event
     * @param event The event from which the timemarker is created
     * @param name The timemarker name
     * @return A new timemarker
     */
	def createTimemarker(RuntimeEvent<?> event, String name) {
		event.timestamp.createTimemarker(name)
	}

	/**
     * SCRIPT API - UTILITY 
     */

	/**
     * Filters all events values that matches a certain regular expression
     */
	def like(Iterable<RuntimeEvent<?>> events, String regex) {
		events.filter[matchRegex(regex)]
	}

	/**
     * Filters checks if an event value matches a certain regular expression
     */
	def matchRegex(RuntimeEvent<?> event, String regex) {
		event.value.toString.matches(regex)
	}

	/**
	 *  Filters all events values that starts wuth a given prefix
	 */
	def startWith(Iterable<RuntimeEvent<?>> events, String starts) {
		events.filter[matchPrefix(starts)]
	}

	/**
     * Filters checks if an event value starts with a certain prefix
     */
	def matchPrefix(RuntimeEvent<?> event, String starts) {
		event.value.toString.startsWith(starts)
	}

	
	def < (TimebasedObject to1, TimebasedObject to2) {
		to1.timestamp < to2.timestamp
	}

	def > (TimebasedObject to1, TimebasedObject to2) {
		to1.timestamp > to2.timestamp
	}

	def == (TimebasedObject to1, TimebasedObject to2) {
		to1.timestamp == to2.timestamp
	}

	def <= (TimebasedObject to1, TimebasedObject to2) {
		to1.timestamp <= to2.timestamp
	}

	def >= (TimebasedObject to1, TimebasedObject to2) {
		to1.timestamp >= to2.timestamp
	}
	
	def - (TimebasedObject to1, TimebasedObject to2) {
		to1.timestamp - to2.timestamp
	}	
	
	/**
     * Filters a list of events whose time-stamps are higher than the time-stamp of the given TimebasedObject
     * @param evts The list of events
     * @param tm The given TimebasedObject
     * @return  The filtered events
     */
	def after(List<RuntimeEvent<?>> evts, TimebasedObject t) {
		evts.filter[it >= t]
	}
	

	/**
     * Filter all events between two time TimebasedObjects
     * @param evts The list of events
     * @param t1 The first TimebasedObject
     * @param t2 The second TimebasedObject
     * @return  The filtered events
    */
	def between(List<RuntimeEvent<?>> events, TimebasedObject t1, TimebasedObject t2) {
		events.filter[it >= t1 && it <= t2]
	}

	/**
     * Filters a list of events whose time-stamps are lower than the time-stamp of the given TimebasedObject
     * @param evts The list of events
     * @param tm The given TimebasedObject
     * @return  The filtered events
     */
	def before(List<RuntimeEvent<?>> evts, TimebasedObject t) {
		evts.filter[it <= t]
	}

	/**
     * Gets a list of timemarkers by a given prefix
     * @param preifx  Given timemarker prefix
     * @return A list of timemarkers
     */
	def getTimemarkersByPrefix(String prefix) {
		allTimemarkers.filter[name.startsWith(prefix)]
	}
	
	/**
	 * Get a timemarker by a given name
	 * @param mname The timemarker name
	 * @return The timemarker
	 */
	def getTimemarkers(String mname) {
		allTimemarkers.filter[name.equals(mname)]
	}
	
	/**
	 * Retrieve the decoded node from a runtime event by a given json path expression
	 * No wild-card or filter expressions are supported for now.
	 * Only full path names with array indices are supported
	 * If you need the value of the node, you need to call extract('$.').value
	 * @param event The given runtime event
	 * @param path The json path expression, e.g. '$.a.b.c' or '$.a[1].b.c[0]' 
	 * 
	 */
	def extract(RuntimeEvent<?> event, String path)
	{
		val p = path.replaceAll('\\[\\s*(\\d+)\\s*\\]', '\\.\\<$1\\>')
		
		if(path.startsWith('$.'))
		{
			event.decode.decodedTree.rootNode.extract(p.substring(2))			
		}
		else
		{
			event.decode.decodedTree.rootNode.extract(p)						
		}				
	}

	def extract(DecodedNode node, String path)
	{
		val tokens = path.split('\\.')
		node.jsonPathResult(tokens.head, tokens.tail)				
	}


	private def DecodedNode jsonPathResult(DecodedNode node, String head, Iterable<String> tail) 
	{						
		val nextNode = node.children.filter[name.equals(head)].head
				
		if(nextNode !== null)
		{			
			if(!tail.empty)
			{
				return jsonPathResult(nextNode, tail.head, tail.tail)
			}
			else 
			{
				return nextNode
			}	
		}
		else 
		{
			return null
		}		
	}
	
	/**
	 * Shows Plant UML string in a HTML View. 
	 * Note: Content of HTML view is overridden without warning. 
	 * 
	 * @param plantUmlText Plant UML string that shall be shown.
	 * @param htmlViewName Name of HTML view the shows the Plant UML image.    
	 */
	def showPlantUmlInHtmlViewSVG(String plantUmlText, String htmlViewName) {
		var pathToSVG = htmlViewName.toLowerCase
		pathToSVG = pathToSVG.replaceAll("\\s","")
		pathToSVG += "-plantuml.svg"
		
		new File(pathToSVG).deleteOnExit()
		
		plantUmlText.plantUmlToSVG(pathToSVG)		
		
		val htmlView = createOrGetHtmlView(htmlViewName)
		htmlView.content = 
		'''
			<html><object data="«pathToSVG»" type="image/svg+xml"/></html>
		'''
	}
	
	def showPlantUmlInHtmlView(String plantUmlText, String htmlViewName) {
		var pathToPNG = htmlViewName.toLowerCase
		pathToPNG = pathToPNG.replaceAll("\\s","")
		pathToPNG += "-plantuml.png"
		
		new File(pathToPNG).deleteOnExit()
		
		plantUmlText.plantUmlToPNG(pathToPNG)		
		
		val htmlView = createOrGetHtmlView(htmlViewName)
		htmlView.content = 
		'''
		<html>
		<head>
			<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
			<script type="text/javascript">
				function reloadImg() {
					d = new Date();
					$("#imageId").attr("src", "«pathToPNG»?"+d.getTime());		
				}
			</script>
		</head>
		<body>
			<img id="imageId" />
			<script type="text/javascript">reloadImg()</script>
		</body>
		</html>
		'''
	}
	
	/**
       * This will return a list of lists of runtime events, grouped by time-stamp
       */
	def groupByTimestamp(Iterable<RuntimeEvent<?>> runtimeEvents) {
		val sortedByTimestamp = runtimeEvents.sortBy[timestamp]
		val List<List<RuntimeEvent<?>>> allList = newArrayList()
		allList.add(newArrayList());
		sortedByTimestamp.reduce[p1, p2|allList.last.add(p1)
			if (p1.timestamp != p2.timestamp) {
				allList.add(newArrayList())
			} p2]
		allList.last.add(sortedByTimestamp.last)
		allList
	}
		
	/**
	 * Splits a plain list of elements into a list of list of elements (a given number of consecutive elements)
	 * e.g. [1,2,3,4,5,6], 2 will become [[1,2],[3,4],[5,6]]
	 * e.g. [1,2,3,4,5,6], 3 will become [[1,2,3],[4,5,6]]
	 * e.g. [1,2,3,4,5,6,7], 2 will become [[1,2],[3,4],[5,6]]
	 * @param elements The list of elements, that should be divided into a list of list of elements
	 * @param number The number of elements in each sub-list
	 * @return A list of list of elements. 
	 * 		If size of elements.size % number != 0, input size will be reduced that elements.size % number == 0 is true. 
	 */
	def <T> List<List<T>> groupByNumber(List<T> elements, int number) {
		val result = newArrayList()
		
		var size = elements.size
		if(elements.size % number != 0) 
			size -= elements.size % number
		
		for(var i = 0; i < size; i += number) {
			result.add( elements.subList(i , i + number))
		}		
	 	result
	}
}
