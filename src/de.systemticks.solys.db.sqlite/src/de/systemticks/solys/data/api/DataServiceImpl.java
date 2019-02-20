package de.systemticks.solys.data.api;

import java.util.List;

import org.apache.thrift.TException;

import de.systemticks.solys.db.sqlite.api.DataStorageAccess;

public class DataServiceImpl implements DataServiceInterface.Iface {

	private final DataStorageAccess access;

	public DataServiceImpl(DataStorageAccess _access) 
	{
		access = _access;
	}
	
	@Override
	public List<String> getAllEventsFromChannel(String storage, int channelId, long fromTimestamp, long toTimestamp)
			throws TException {
		return access.getAllEventsFromChannel(storage, channelId, fromTimestamp, toTimestamp);
	}

	@Override
	public List<String> getStatisticOverTime(String storage, int channelId, int interval) throws TException {
		return access.getStatisticOverTime(storage, channelId, interval);
	}

	@Override
	public boolean open(String dbName) throws TException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean openReadOnly(String dbName) throws TException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean close() throws TException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public List<String> getAllChannels() throws TException {
		// TODO Auto-generated method stub
		return access.getAllChannels();
	}

	@Override
	public List<String> getEventsBySQL(String storage, int channelId, String query) throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getEventsStartingFromId(String storage, int channelId, int eventId, List<String> details)
			throws TException {
		return access.getAllEventsFromChannel(storage, channelId, eventId, details);
	}

}
