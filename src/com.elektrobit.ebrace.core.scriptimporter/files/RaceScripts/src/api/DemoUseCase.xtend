package api

import api.ScriptBase
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ProtoMessageValue
import com.elektrobit.ebsolys.script.external.Execute
import com.elektrobit.ebsolys.script.external.Execute.ExecutionContext
import com.elektrobit.ebsolys.script.external.ScriptContext
import com.elektrobit.ebsolys.script.external.UIResourcesContext.CHART_TYPE
import java.util.List
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent

class DemoUseCase 
{

	extension ScriptContext _scriptContext
    extension ScriptBase _scriptBase
     
    new (ScriptContext scriptContext) {
    	_scriptContext = scriptContext
        _scriptBase = new ScriptBase(_scriptContext)
    }

	var List<RuntimeEventChannel<?>> activeProcessChannels
	val ACTIVITY_THRESHOLD = 1.0
	
    @Execute(context=ExecutionContext.GLOBAL, description="Create Some Charts, Tables and Markers")
	def execute() {
		createSomeMarkers
		createTraceTables
    	activeProcessChannels = filterActiveProcesses
		createMarkerForCPUSystemPeak
		createChartForCPULoadSystem
    	createChartForCPULoad
    	createChartForProcessActivity
	}
	
	def createChartForProcessActivity() {
		val timeSegmentChannels = newArrayList
		activeProcessChannels.forEach[
			val processName = name.split("\\.").get(2)
			val timeSegmentChannel = createOrGetTimeSegmentChannel("activity." + processName, "")
			events.tokenizeAboveThreshold(ACTIVITY_THRESHOLD).forEach[
				timeSegmentChannel.add(it.head, it.last)
			]
			timeSegmentChannels.add(timeSegmentChannel)
		]
		val chart = createOrGetTimelineView("CPU Activity")
    	chart.add(timeSegmentChannels)
	}
	
	def List<List<RuntimeEvent<Double>>> tokenizeAboveThreshold(List<RuntimeEvent<?>> events, double threshold){
		val List<List<RuntimeEvent<Double>>> returnListOfLists = newArrayList
		
		var List<RuntimeEvent<Double>> overThresholdEvents = newArrayList
		var lastValue = 0.0
		for(event : events){
			val currentValue = event.value as Double
			if( lastValue <= threshold && currentValue > threshold)  // start adding
			{
				overThresholdEvents = newArrayList
			}
			else if (lastValue > threshold && currentValue <= threshold) // stop adding
			{
				returnListOfLists.add(overThresholdEvents)
			}
			if(currentValue >= threshold){
				overThresholdEvents.add(event as RuntimeEvent<Double>)
			}
			lastValue = currentValue
		}
		
		returnListOfLists
	}

	/**
	 * This method will create a time marker whenever a dbus message was found that contains the text 'StartGuidance'.
	 */
	def createSomeMarkers() {
		getChannel("demo-solys-data.bin.trace.dbus.sessionbus").events.filter[(value as ProtoMessageValue).summary.contains("StartGuidance")].forEach[
			if(decode.getFirstValue("type").equals("DBUS_MSG_TYPE_METHOD_CALL")) {
				createTimemarker(timestamp, "START_GUIDANCE")
			}
		] 
	}

	/**
	 * This method will create a table that contains events from all kinds of trace channels.
	 */
	def createTraceTables() {
		val t = createOrGetTable("Traces")
		t.add(getChannelsByPrefix('trace'))
	} 
	
	/**
	 * This method will retrieve the highest CPU load value and creates a corresponding time marker.
	 * The Tables and Charts will be centered to the new time marker by calling _jumpTo_ at the end.
	 */
	def createMarkerForCPUSystemPeak() {
		getChannel("demo-solys-data.bin.cpu.system").events.sortBy[value as Double].last.createTimemarker("CPU Load Peak").jumpTo
	}
	
	/**
	 * This method will create a chart for the CPU load of the overall system (summarized all singular process cpu values).
	 */
	def createChartForCPULoadSystem() {
		createOrGetChart("CPU Load System", CHART_TYPE.LINE_CHART).add(getChannel("demo-solys-data.bin.cpu.system"))
	}

	/**
	 * This method creates a chart, that contains all processes which at least consume more than 1.0% of CPU at a certain time.
	 */
	def createChartForCPULoad() {
		
    	val chart = createOrGetChart("CPU Load", CHART_TYPE.LINE_CHART)
    	chart.add(activeProcessChannels)
	}
	
	def List<RuntimeEventChannel<?>> filterActiveProcesses() {
		val channels = newHashSet()
    	getChannelsByPrefix("demo-solys-data.bin.cpu.proc.").allEventsFromChannels.groupByTimestamp.forEach[
    		channels.addAll(filter[value as Double > ACTIVITY_THRESHOLD].map[runtimeEventChannel])
    	]
		channels.toList
	}
}
