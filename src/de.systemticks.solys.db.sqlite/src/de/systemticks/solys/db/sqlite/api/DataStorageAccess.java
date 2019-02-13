package de.systemticks.solys.db.sqlite.api;

import java.util.List;


public interface DataStorageAccess {

	public boolean openReadAndWrite(String id);

	public boolean openReadOnly(String id);
	
	public boolean shutDown();
	
	public boolean commit();
	
	public boolean backup(String filename);
	
	public boolean bulkImportAnyBaseEvents(List<GenericJsonEvent> events);
		
	// New methods return JSON
	public int createChannel(String fullname, FieldMapping fieldMapping);
	
	public List<String> getAllChannels();

	public List<String> getAllEventsFromChannel(String storage, int channeldId, long fromTimestamp, long toTimestamp);

	public List<String> getStatisticOverTime(String storage, int cId, int interval);
	
	// Legacy
//	public <T> List<BaseEvent<T>> getEventsAtTimestamp(String storage, long timestamp, Class<T> class1);
//
//	public <T> List<BaseEvent<T>> getAllEventsFromChannel(String storage, int channeldId, Class<T> class1);
//
//	public <T> List<BaseEvent<T>> getMaxEventsFromAllChannels(String storage, Class<T> class1);

}
