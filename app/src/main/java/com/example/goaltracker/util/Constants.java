package com.example.goaltracker.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constants {
    public static final String DATABASE_NAME = "app_db";

    //Goal DB Constants
    public static final String GOAL_TABLE_NAME = "goal_table";
    public static final String GOAL_ID_COLUMN_NAME = "goal_id_col";
    public static final String GOAL_NAME_COLUMN_NAME = "goal_name_col";
    public static final String GOAL_TARGET_COLUMN_NAME = "goal_target_col";
    public static final String GOAL_VALUE_COLUMN_NAME = "goal_value_col";
    public static final String GOAL_FREQUENCY_NAME = "goal_freq_col";

    //Record DB Constants
    public static final String REC_TABLE_NAME = "record_table";
    public static final String REC_GOAL_ID_COLUMN_NAME = "rec_goal_id_col";
    public static final String REC_DATE_COLUMN_NAME = "rec_date_col";
    public static final String REC_VALUE_COLUMN_NAME = "rec_value_col";

    //Frequency Constants
    public static final String DAILY = "daily";
    public static final String WEEKLY = "weekly";
    public static final String MONTHLY = "monthly";


    //Option for spinner in AddGoalActivity
    public static final List<String> MORE_THAN_OPTION = new ArrayList<String>(
            Arrays.asList("More than", "Less than"));

    //Passing intent from MainActivity -> AddRecordActivity
    public static final int MAIN_ADD_RECORD_REQUESTCODE = 1248;
    public static final String MAIN_ADD_RECORD_MSGNAME = "mainAddRecordMsg";

}
