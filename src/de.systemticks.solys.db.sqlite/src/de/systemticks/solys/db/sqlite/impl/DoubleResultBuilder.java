package de.systemticks.solys.db.sqlite.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.systemticks.solys.db.sqlite.api.BaseEvent;
import de.systemticks.solys.db.sqlite.api.StatsItem;

public class DoubleResultBuilder implements TypedResultBuilder<Double>
{

    @Override
    public StatsItem<Double> createStatsItem(ResultSet rs)
    {

        StatsItem<Double> item = new StatsItem<>();
        try
        {
            item.setTimestamp( rs.getLong( 1 ) );
            item.setAverage( rs.getDouble( 2 ) );
            item.setMaximum( rs.getDouble( 3 ) );
            item.setMinimum( rs.getDouble( 4 ) );
            item.setChannelId( rs.getInt( 5 ) );
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return item;
    }

    @Override
    public BaseEvent<Double> createBaseEvent(ResultSet rs, boolean withChannel)
    {

        BaseEvent<Double> event = new BaseEvent<>();

        try
        {
            event.setEventId( rs.getInt( 1 ) );
            event.setTimestamp( rs.getLong( 2 ) );
            event.setValue( rs.getDouble( 3 ) );
            event.setChannelId( rs.getInt( 4 ) );
            if(withChannel) 
            {
            	event.setChannelName( rs.getString(5));
            }
            else 
            {
                event.setChannelName( "" );            	
            }

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return event;

    }

}
