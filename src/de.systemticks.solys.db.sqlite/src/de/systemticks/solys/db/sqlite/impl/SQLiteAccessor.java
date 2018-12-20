package de.systemticks.solys.db.sqlite.impl;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.osgi.service.component.annotations.Component;

import de.systemticks.solys.db.sqlite.api.BaseEvent;
import de.systemticks.solys.db.sqlite.api.Channel;
import de.systemticks.solys.db.sqlite.api.DataStorageAccess;
import de.systemticks.solys.db.sqlite.api.StatsItem;

@Component
public class SQLiteAccessor implements DataStorageAccess {

	private Connection connection;
	HashMap<String, Integer> channelMap = new HashMap<>();
	private int channelId = 1;

	private boolean open(String name) {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + name);
			connection.setAutoCommit(false);
			return true;
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	private boolean close() {
		try {
			connection.commit();
			connection.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean executeSingleStatement(CharSequence sqlStmt) {

		System.out.println("Executing SQL: " + sqlStmt);

		Statement stmt;
		try {
			stmt = connection.createStatement();
			stmt.executeUpdate(sqlStmt.toString());
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	@Override
	public boolean openReadAndWrite(String id) {

		if (!id.equals(":memory:")) {
			File f = new File(id);
			if (f.exists()) {
				f.delete();
			}
		}

		if (!open(id)) {
			return false;
		}

		executeSingleStatement(SQLHelper.createTableForChannelMapping("channels"));

		return true;
	}

	@Override
	public boolean shutDown() {
		return close();
	}

//	@Override
//	public boolean bulkImportAnyBaseEvents(List<BaseEvent<?>> events) {
//
//		for (BaseEvent<?> e : events) {
//
//			int cId = handleChannelName(e);
//
//			String stmt = SQLHelper.insertValueIntoEventTable(e.getOrigin() + "_" + cId).toString();
//			PreparedStatement ps;
//			try {
//				ps = connection.prepareStatement(stmt);
//				ps.setInt(1, e.getEventId());
//				ps.setLong(2, e.getTimestamp());
//				if (e.getValue() instanceof Double) {
//					ps.setDouble(3, (double) e.getValue());
//					ps.executeUpdate();
//					ps.close();
//				} else if (e.getValue() instanceof Integer) {
//					ps.setInt(3, (int) e.getValue());
//					ps.executeUpdate();
//					ps.close();
//				} else {
//					// drop it
//				}
//
//			} catch (SQLException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//				return false;
//			}
//		}
//
//		return true;
//	}

	@Override
	public boolean bulkImportAnyBaseEvents(List<BaseEvent<?>> events) {
		
		try {
			Statement stmt = connection.createStatement();
			
			for (BaseEvent<?> e : events) {			
				int cId = handleChannelName(e);				
				stmt.addBatch(SQLHelper.insertValueIntoEventTableUnprepared(
						e.getOrigin() + "_" + cId, e.getEventId(), e.getTimestamp(), e.getValue()).toString());				
			}
			
			stmt.executeBatch();
			stmt.close();
									
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}

	
	private int handleChannelName(BaseEvent<?> e) {
		if (!channelMap.containsKey(e.getChannelname())) {

			// Event with a new channel arrived
			// Need to create a new ChannelId
			// Create a new table, that carries events for this channel
			// Update the Channel Mapping Table

			channelId += 1;
			channelMap.put(e.getChannelname(), channelId);

			executeSingleStatement(SQLHelper.createTableForAnyEvents(e.getOrigin(), channelId,
					e.getValue()));

			executeSingleStatement(SQLHelper.insertIntoChannelMappingTable(channelId, e.getChannelname(), e.getOrigin(),
					e.getValue().getClass().getSimpleName()));
		}

		return channelMap.get(e.getChannelname());
	}

	@Override
	public boolean commit() {
		try {
			connection.commit();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean backup(String filename) {
		
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("backup to "+filename);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	
	@Override
	public <T> List<BaseEvent<T>> getMaxEventsFromAllChannels(String storage, Class<T> class1) {
		return createBaseEventsFromQuery(SQLHelper.createMaxFromAllChannels(storage),
				TypeResultBuilderFactory.create(class1));
	}

	@Override
	public <T> List<BaseEvent<T>> getAllEventsFromChannel(String storage, int cId, Class<T> class1) {
		return createBaseEventsFromQuery(SQLHelper.createAllEventsFromChannel(storage, cId),
				TypeResultBuilderFactory.create(class1));
	}

	@Override
	public <T> List<BaseEvent<T>> getAllEventsFromChannel(String storage, int channeldId, long fromTimestamp,
			long toTimestamp, Class<T> class1) {

		return createBaseEventsFromQuery(
				SQLHelper.createAllEventsFromChannel(storage, channeldId, fromTimestamp, toTimestamp),
				TypeResultBuilderFactory.create(class1));

	}

	@Override
	public <T> List<BaseEvent<T>> getEventsAtTimestamp(String storage, long timestamp, Class<T> class1) {
		return createBaseEventsFromQuery(SQLHelper.createEventsAtTimestamp(storage, timestamp),
				TypeResultBuilderFactory.create(class1));
	}

	private <T> List<BaseEvent<T>> createBaseEventsFromQuery(CharSequence query, TypedResultBuilder<T> builder) {

		List<BaseEvent<T>> result = new ArrayList<>();

		System.out.println(query);

		if (builder == null) {
			return result;
		}

		Statement stmt;
		try {
			stmt = connection.createStatement();
			stmt.setFetchSize(100);

			ResultSet rs = stmt.executeQuery(query.toString());
			boolean withChannel = (rs.getMetaData().getColumnCount() == 5) ? true : false;

			while (rs.next()) {
				result.add(builder.createBaseEvent(rs, withChannel));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;

	}

	@Override
	public boolean openReadOnly(String id) {
		return open(id);
	}

	@Override
	public <T> List<StatsItem<T>> getStatisticOverTime(String storage, int cId, int interval, Class<T> class1) {

		List<StatsItem<T>> result = new ArrayList<>();

		TypedResultBuilder<T> builder = TypeResultBuilderFactory.create(class1);

		if (builder == null) {
			return result;
		}

		String query = SQLHelper.createStatistics(storage, cId, interval).toString();

		System.out.println(query);

		Statement stmt;
		try {
			stmt = connection.createStatement();
			stmt.setFetchSize(100);

			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				result.add(builder.createStatsItem(rs));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public List<Channel> getAllChannels() {

		List<Channel> result = new ArrayList<>();

		String query = SQLHelper.createAllChannels().toString();

		System.out.println(query);

		Statement stmt;
		try {
			stmt = connection.createStatement();
			stmt.setFetchSize(100);

			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				Channel c = new Channel();
				c.setId(rs.getInt(1));
				c.setName(rs.getString(2));
				c.setNature(rs.getString(3));
				c.setType(rs.getString(4));
				result.add(c);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;

	}


}
