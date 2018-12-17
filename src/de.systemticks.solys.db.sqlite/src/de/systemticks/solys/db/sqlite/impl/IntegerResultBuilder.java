package de.systemticks.solys.db.sqlite.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.systemticks.solys.db.sqlite.api.BaseEvent;
import de.systemticks.solys.db.sqlite.api.StatsItem;

public class IntegerResultBuilder implements TypedResultBuilder<Integer> {

	@Override
	public StatsItem<Integer> createStatsItem(ResultSet rs) {
		
        StatsItem<Integer> item = new StatsItem<>();
        try
        {
            item.setTimestamp( rs.getLong( 1 ) );
            item.setAverage( rs.getInt( 2 ) );
            item.setMaximum( rs.getInt( 3 ) );
            item.setMinimum( rs.getInt( 4 ) );
            item.setChannelId( rs.getInt( 5 ) );
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return item;

	}

	@Override
	public BaseEvent<Integer> createBaseEvent(ResultSet rs, boolean withChannel) {

		BaseEvent<Integer> event = new BaseEvent<>();

        try
        {
            event.setEventId( rs.getInt( 1 ) );
            event.setTimestamp( rs.getLong( 2 ) );
            event.setValue( rs.getInt( 3 ) );
            event.setChannelId( rs.getInt( 4 ) );
            if(withChannel) 
            {
            	event.setOrigin( rs.getString(5));
            }
            else 
            {
                event.setOrigin( "" );            	
            }

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return event;
	}


}
