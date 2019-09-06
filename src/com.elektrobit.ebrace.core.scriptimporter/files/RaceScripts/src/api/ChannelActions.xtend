package api

import com.elektrobit.ebsolys.script.external.ScriptContext
import com.elektrobit.ebsolys.script.external.Execute
import com.elektrobit.ebsolys.script.external.Execute.ExecutionContext
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel
import java.util.List


class ChannelActions
{
    extension ScriptContext _scriptContext
     
    new (ScriptContext scriptContext) {
        _scriptContext = scriptContext
    }


    @Execute(context=ExecutionContext.PRESELECTION, description="Remove Channel")
    def deleteChannel(RuntimeEventChannel<?> c) {
        c.removeChannel
    }

    @Execute(context=ExecutionContext.PRESELECTION, description="Remove Channels")
    def deleteChannels(List<RuntimeEventChannel<?>> channels) {
        channels.forEach[removeChannel]
    }
    
}

