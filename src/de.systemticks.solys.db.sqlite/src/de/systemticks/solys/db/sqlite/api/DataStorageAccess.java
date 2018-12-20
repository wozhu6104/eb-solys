package de.systemticks.solys.db.sqlite.api;

import java.util.List;


public interface DataStorageAccess {

	public boolean openReadAndWrite(String id);

	public boolean openReadOnly(String id);
	
	public boolean shutDown();
	
	public boolean commit();
	
	public boolean bulkImportAnyBaseEvents(List<BaseEvent<?>> events);

	public <T> List<BaseEvent<T>> getEventsAtTimestamp(String storage, long timestamp, Class<T> class1);

	public <T> List<BaseEvent<T>> getAllEventsFromChannel(String storage, int channeldId, Class<T> class1);

	public <T> List<BaseEvent<T>> getAllEventsFromChannel(String storage, int channeldId, long fromTimestamp, long toTimestamp, Class<T> class1 );

	public <T> List<BaseEvent<T>> getMaxEventsFromAllChannels(String storage, Class<T> class1);

	public <T> List<StatsItem<T>> getStatisticOverTime(String storage, int channeldId, int interval, Class<T> class1);
	
	public List<Channel> getAllChannels();
	
	public boolean backup(String filename);
}
