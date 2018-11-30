package de.systemticks.solys.db.sqlite.impl;

import java.sql.ResultSet;

import de.systemticks.solys.db.sqlite.api.BaseEvent;
import de.systemticks.solys.db.sqlite.api.StatsItem;

public interface TypedResultBuilder<T>
{

    public StatsItem<T> createStatsItem(ResultSet rs);

    public BaseEvent<T> createBaseEvent(ResultSet rs);

}
