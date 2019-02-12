package de.systemticks.solys.db.sqlite.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import lombok.Data;

@Data
public class GenericJsonEvent {

	Long timestamp;
	String channel;
	Integer channelId;
	Integer eventId; 
	Object value;
	JsonObject details;
	
    @Override
    public String toString()
    {
        return new Gson().toJson( this );
    }
	
}
