package net.rodrigobrito.firetodolist.data;

import android.provider.BaseColumns;

/**
 * Created by rodrigo on 07/09/16.
 */
public final class TaskContract {

    public TaskContract() {}

    public static abstract class TaskEntry implements BaseColumns{
        public static final String TABLE_NAME = "task";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DECRTIPTION = "descritption";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_DONE = "done";
    }
}
