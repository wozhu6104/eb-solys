import api.ScriptBase
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent
import com.elektrobit.ebsolys.script.external.Filter
import com.elektrobit.ebsolys.script.external.ScriptContext

class FilterScript
{

	extension ScriptContext _scriptContext
    extension ScriptBase _scriptBase
     
    new (ScriptContext scriptContext) {
    	_scriptContext = scriptContext
        _scriptBase = new ScriptBase(_scriptContext)
    }
	
    @Filter
	def findEventsBiggherThan10(RuntimeEvent<?> event) {
		(event.value as Integer) > 10
	}
}



