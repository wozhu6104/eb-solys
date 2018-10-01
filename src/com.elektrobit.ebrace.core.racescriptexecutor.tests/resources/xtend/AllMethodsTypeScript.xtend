import api.ScriptBase
import com.elektrobit.ebsolys.script.external.Execute
import com.elektrobit.ebsolys.script.external.Execute.ExecutionContext
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker
import java.util.List
import com.elektrobit.ebsolys.script.external.ScriptContext
import com.elektrobit.ebsolys.script.external.BeforeScript
import com.elektrobit.ebsolys.script.external.AfterScript

class AllMethodsTypeScript
{
	extension ScriptContext _scriptContext
    extension ScriptBase _scriptBase
     
    new (ScriptContext scriptContext) {
    	_scriptContext = scriptContext
        _scriptBase = new ScriptBase(_scriptContext)
    }
    
    @BeforeScript
	def setupScript() {
		// Your setup code
	}
	
    @Execute(context=ExecutionContext.GLOBAL, description="My description")
	def executeGlobal() {
	}
	
    @Execute(context=ExecutionContext.PRESELECTION, description="My description")
	def executeChannel(RuntimeEventChannel<?> channel) {
	}
	
    @Execute(context=ExecutionContext.PRESELECTION, description="My description")
	def executeEvents(List<RuntimeEvent<?>> channel) {
	}
	
    @Execute(context=ExecutionContext.PRESELECTION, description="My description")
	def executeTimeMarker(TimeMarker t) {
	}
	
    @Execute(context=ExecutionContext.PRESELECTION, description="My description")
	def executeWrongMethod(Integer t) {
	}
		
	@AfterScript
	def cleanup() {
	     // Your cleanup code
	}
}



