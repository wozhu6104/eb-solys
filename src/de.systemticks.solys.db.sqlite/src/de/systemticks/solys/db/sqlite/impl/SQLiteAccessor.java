package de.systemticks.solys.db.sqlite.impl;

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
public class SQLiteAccessor implements DataStorageAccess
{

    private Connection connection;
    HashMap<String, Integer> channelMap = new HashMap<>();
    private int channelId = 1;

    private boolean open(String name)
    {
        try
        {
            Class.forName( "org.sqlite.JDBC" );
            connection = DriverManager.getConnection( "jdbc:sqlite:" + name );
            connection.setAutoCommit( false );
            return true;
        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
            return false;
        }

    }

    private boolean close()
    {
        try
        {
            connection.commit();
            connection.close();
            return true;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    private boolean executeSingleStatement(CharSequence sqlStmt)
    {

        System.out.println( "Executing SQL: " + sqlStmt );

        Statement stmt;
        try
        {
            stmt = connection.createStatement();
            stmt.executeUpdate( sqlStmt.toString() );
            stmt.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public boolean openReadAndWrite(String id)
    {

        if (!open( id ))
        {
            return false;
        }

        executeSingleStatement( SQLHelper.dropTable( "cpu" ) );
        executeSingleStatement( SQLHelper.createTableForDoubleEvents( "cpu" ) );
        executeSingleStatement( SQLHelper.createIndex( "cpu", "eId" ) );
        executeSingleStatement( SQLHelper.createIndex( "cpu", "eChannelId" ) );

        executeSingleStatement( SQLHelper.dropTable( "mem" ) );
        executeSingleStatement( SQLHelper.createTableForIntEvents( "mem" ) );
        executeSingleStatement( SQLHelper.createIndex( "mem", "eId" ) );
        executeSingleStatement( SQLHelper.createIndex( "mem", "eChannelId" ) );

        executeSingleStatement( SQLHelper.dropTable( "channels" ) );
        executeSingleStatement( SQLHelper.createTableForChannelMapping( "channels" ) );
        executeSingleStatement( SQLHelper.createIndex( "channels", "cId" ) );

        return true;
    }

    @Override
    public boolean shutDown()
    {
        return close();
    }

    @Override
    public <T> boolean bulkImportBaseEvents(String storage, List<BaseEvent<T>> events, Class<T> class1)
    {

        try
        {
            String stmt = "INSERT INTO " + storage + " (eId, eTimestamp, eValue, eChannelId) values " + "(?, ?, ?, ?)";

            PreparedStatement ps = connection.prepareStatement( stmt );

            for (BaseEvent<T> e : events)
            {

                int cId = handleChannelName( e.getChannelName() );
                ps.setInt( 1, e.getEventId() );
                ps.setLong( 2, e.getTimestamp() );
                // FIXME - No good implementation
                if (class1.getName().equals( "java.lang.Double" ))
                {
                    ps.setDouble( 3, (Double)class1.cast( e.getValue() ) );
                }
                else if (class1.getName().equals( "java.lang.Integer" ))
                {
                    ps.setInt( 3, (Integer)class1.cast( e.getValue() ) );
                }
                ps.setInt( 4, cId );
                ps.addBatch();
            }

            ps.executeBatch();
            ps.close();

        }
        catch (SQLException e1)
        {
            e1.printStackTrace();
            return false;
        }

        return true;
    }

    private int handleChannelName(String channelName)
    {

        if (!channelMap.containsKey( channelName ))
        {
            channelId += 1;
            channelMap.put( channelName, channelId );
            executeSingleStatement( "INSERT INTO channels (cId, cName) values (" + channelId + ", '" + channelName
                    + "')" );
        }

        return channelMap.get( channelName );

    }

    @Override
    public boolean commit()
    {
        try
        {
            connection.commit();
            return true;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public <T> List<BaseEvent<T>> getAllEventsFromChannel(String storage, int cId, Class<T> class1)
    {

        List<BaseEvent<T>> result = new ArrayList<>();

        String query = SQLHelper.createAllEventsFromChannel( storage, cId ).toString();

        TypedResultBuilder<T> builder = TypeResultBuilderFactory.create( class1 );

        if (builder == null)
        {
            return result;
        }

        System.out.println( query );

        Statement stmt;
        try
        {
            stmt = connection.createStatement();
            stmt.setFetchSize( 100 );

            ResultSet rs = stmt.executeQuery( query );

            while (rs.next())
            {
                result.add( builder.createBaseEvent( rs ) );
            }

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public boolean openReadOnly(String id)
    {
        return open( id );
    }

    @Override
    public <T> List<StatsItem<T>> getStatisticOverTime(String storage, int cId, int interval, Class<T> class1)
    {

        List<StatsItem<T>> result = new ArrayList<>();

        TypedResultBuilder<T> builder = TypeResultBuilderFactory.create( class1 );

        if (builder == null)
        {
            return result;
        }

        String query = SQLHelper.createStatistics( storage, cId, interval ).toString();

        System.out.println( query );

        Statement stmt;
        try
        {
            stmt = connection.createStatement();
            stmt.setFetchSize( 100 );

            ResultSet rs = stmt.executeQuery( query );

            while (rs.next())
            {
                result.add( builder.createStatsItem( rs ) );
            }

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public List<Channel> getAllChannels()
    {

        List<Channel> result = new ArrayList<>();

        String query = SQLHelper.createAllChannels().toString();

        System.out.println( query );

        Statement stmt;
        try
        {
            stmt = connection.createStatement();
            stmt.setFetchSize( 100 );

            ResultSet rs = stmt.executeQuery( query );

            while (rs.next())
            {
                Channel c = new Channel();
                c.setId( rs.getInt( 1 ) );
                c.setName( rs.getString( 2 ) );
                result.add( c );
            }

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return result;

    }
}
