import api.ScriptBase
import com.elektrobit.ebrace.script.external.ScriptContext
import com.elektrobit.ebrace.script.external.Execute
import com.elektrobit.ebrace.script.external.Execute.ExecutionContext


class MyScript
{

	extension ScriptContext _scriptContext
    extension ScriptBase _scriptBase
     
    new (ScriptContext raceScriptContext) {
    	_scriptContext = ScriptContext
        _scriptBase = new ScriptBase(_scriptContext)
    }

	
    /**
	 * Add a meaningful content to the description tag to describe the feature, which is executed by this script
	 * The content of the description tag will be used in all UI widgets where the script can be invoked
	 * If the content is empty, then the classname.methodname will be used instead
	 */
    @Execute(context=ExecutionContext.GLOBAL, description="")
	def execute() {
    	// Your script code
	}


}