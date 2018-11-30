package de.systemticks.solys.db.sqlite.api;

import java.util.List;


public interface DataStorageAccess {

	public boolean openReadAndWrite(String id);

	public boolean openReadOnly(String id);
	
	public boolean shutDown();
	
	public boolean commit();
	
	/**
	 * Import a list of base events. Bulk imports are preferred due to performance reasons
	 * @param storage Defines the storage. Currently supported values are "cpu" and "mem"
	 * @param events The list of events to be stored
	 * @param class1 Defines the type of the value, the BaseEvent is carrying. Supported is Double and Integer
	 * @return
	 */
	public <T> boolean bulkImportBaseEvents(String storage, List<BaseEvent<T>> events, Class<T> class1);

	public <T> List<BaseEvent<T>> getAllEventsFromChannel(String storage, int channeldId, Class<T> class1);
	
	public <T> List<StatsItem<T>> getStatisticOverTime(String storage, int channeldId, int interval, Class<T> class1);
	
	public List<Channel> getAllChannels();
}
