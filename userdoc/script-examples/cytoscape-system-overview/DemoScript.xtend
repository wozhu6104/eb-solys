

import api.ScriptBase
import com.elektrobit.ebsolys.script.external.ScriptContext
import com.elektrobit.ebsolys.script.external.Execute
import com.elektrobit.ebsolys.script.external.Execute.ExecutionContext
import com.elektrobit.ebsolys.script.external.UIResourcesContext.CHART_TYPE
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit

class DemoScript
{

	extension ScriptContext _scriptContext
    extension ScriptBase _scriptBase
    
     
    new (ScriptContext scriptContext) {
    	_scriptContext = scriptContext
        _scriptBase = new ScriptBase(_scriptContext)
    }

	
    /**
	 * Add a meaningful content to the description tag to describe the feature, which is executed by this script
	 * The content of the description tag will be used in all UI widgets where the script can be invoked
	 * If the content is empty, then the classname.methodname will be used instead
	 */
    @Execute(context=ExecutionContext.GLOBAL, description="")
	def execute(String channelName) {
    	val relevantChannels = allChannels.filter[name.contains(channelName)].toList
    	val chartChannels = relevantChannels.filter[#[Unit.COUNT, Unit.KILOBYTE, Unit.PERCENT].contains(unit)].toList
    	if(chartChannels.size > 0){
	    	val chart = createOrGetChart(channelName, CHART_TYPE.LINE_CHART)
    		chart.add(chartChannels)
    	}
    	val tableChannels = relevantChannels.filter[#[Unit.TEXT, Unit.BOOLEAN].contains(unit)].toList
    	if(tableChannels.size > 0){
	    	val table = createOrGetTable("Logs")
    		table.add(tableChannels)
    	}
	}


}



