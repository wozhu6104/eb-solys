import api.ScriptBase
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent
import com.elektrobit.ebsolys.script.external.Filter
import com.elektrobit.ebsolys.script.external.ScriptContext
import java.util.List

class FilterMethodsScript
{

	extension ScriptContext _scriptContext
    extension ScriptBase _scriptBase
     
    new (ScriptContext scriptContext) {
    	_scriptContext = scriptContext
        _scriptBase = new ScriptBase(_scriptContext)
    }
	
    @Filter( description="")
	def filterMethodOk1(RuntimeEvent<?> event) {
    	return true;    
	} 
	
	@Filter( description="")
	def filterMethodOk2(RuntimeEvent<?> event) {
    	return new Boolean(true);      
	}
	
	@Filter( description="")
	def filterMethodNoReturnType(RuntimeEvent<?> event) {
	}
	
	@Filter( description="")
	def filterMethodNoParam() {
    	return new Boolean(true);      
	}  
	 
	@Filter( description="")
	def filterMethodWrongParam(List<RuntimeEvent<?>> events) {
    	return new Boolean(true);       
	}


}



