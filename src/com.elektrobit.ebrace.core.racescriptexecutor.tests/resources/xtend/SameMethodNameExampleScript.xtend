import api.ScriptBase
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker
import com.elektrobit.ebsolys.script.external.Execute
import com.elektrobit.ebsolys.script.external.Execute.ExecutionContext
import com.elektrobit.ebsolys.script.external.ScriptContext
import java.util.List

class SameMethodNameExampleScript {

	extension ScriptContext _scriptContext
    extension ScriptBase _scriptBase
     
    new (ScriptContext scriptContext) {
    	_scriptContext = scriptContext
        _scriptBase = new ScriptBase(_scriptContext)
    }

	@Execute(context=ExecutionContext.GLOBAL, description="")
	def executeScript() { 
		"Global".consolePrintln
	}

	@Execute(context=ExecutionContext.PRESELECTION, description="")
	def executeScript(List<RuntimeEvent<?>> events) {
		"List".consolePrintln
	}
	
	@Execute(context=ExecutionContext.PRESELECTION, description="")
	def executeScript(RuntimeEventChannel<?> channel) {
		"Channel".consolePrintln
	}
	
	@Execute(context=ExecutionContext.PRESELECTION, description="")
	def executeScript(TimeMarker marker) {
		"Marker".consolePrintln
	}
	
	@Execute(context=ExecutionContext.GLOBAL, description="")
	def  executeScriptCB() {
    	"Global".consolePrintln
	}
	
	@Execute(context=ExecutionContext.CALLBACK, description="")
	def  executeScriptCB(List<RuntimeEvent<?>> events) {
    	"Callback".consolePrintln
	}

}