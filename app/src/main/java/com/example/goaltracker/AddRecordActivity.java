package com.example.goaltracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.goaltracker.adapter.GoalRViewAdapter_AddRecord;

import com.example.goaltracker.exception.DateAfterTodayException;
import com.example.goaltracker.exception.DateFormatInvalidException;
import com.example.goaltracker.exception.DuplicateRecordException;
import com.example.goaltracker.model.Goal;
import com.example.goaltracker.model.GoalViewModel;
import com.example.goaltracker.model.Record;
import com.example.goaltracker.model.RecordViewModel;
import com.example.goaltracker.util.Constants;
import com.example.goaltracker.util.DateTimeHandler;
import com.google.android.material.snackbar.Snackbar;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddRecordActivity extends AppCompatActivity  {

    private static final String TAG = "Add Record Activity";

    private GoalViewModel goalViewModel;
    private RecordViewModel recordViewModel;

    private GoalRViewAdapter_AddRecord goalRViewAdapter;
    private RecyclerView recyclerView;

    private Button calendarButton;
    private Button saveButton;

    private EditText calendarEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_record);

        //Set up Recycler View
        recyclerView = findViewById(R.id.add_record_act_rview);
        goalRViewAdapter = new GoalRViewAdapter_AddRecord(this);
        recyclerView.setAdapter(goalRViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Setup goal viewmodel
        goalViewModel = ViewModelProviders.of(this)
                .get(GoalViewModel.class);
        goalViewModel.getAllGoal().observe(this, new Observer<List<Goal>>() {
            @Override
            public void onChanged(List<Goal> goals) {
                goalRViewAdapter.setGoalList(goals);
            }
        });

        //Setup RecordViewModel
        recordViewModel = ViewModelProviders.of(this)
                .get(RecordViewModel.class);
        recordViewModel.getAllRecord().observe(this, new Observer<List<Record>>() {
            @Override
            public void onChanged(List<Record> records) {
                //No UI change, so method is empty
            }
        });

        //Setup From Calendar Button
        final DatePickerDialog datePickerDialogFrom = new DatePickerDialog(this);
        datePickerDialogFrom.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //Plus one is because month start from 0
                String datePicked = DateTimeHandler.getCalendarText(year, month + 1, dayOfMonth);

                calendarEditText.setText(datePicked);
                try {
                    DateTimeHandler.verifyDate(datePicked);
                } catch (DateAfterTodayException e) {
                    showUserDateAfterTodayException(recyclerView);
                } catch (DateFormatInvalidException e) {
                    showUserDateFormatInvalidException(recyclerView);
                }

            }
        });
        calendarButton = findViewById(R.id.activity_add_record_calendar_from);
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialogFrom.show();
            }
        });

        //Setup From Edit Text, prefill with today date
        calendarEditText =findViewById(R.id.activity_add_record_from);
        calendarEditText.setText(DateTimeHandler.getCalendarText(DateTimeHandler.NOW));
        calendarEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    EditText castedView = (EditText) v;
                    if (!castedView.getText().toString().isEmpty()) {
                        String date = castedView.getText().toString();

                        try {
                            DateTimeHandler.verifyDate(date);
                            datePickerDialogFrom.updateDate(
                                    DateTimeHandler.getYear(date),
                                    DateTimeHandler.getMonth(date) - 1,
                                    DateTimeHandler.getDate(date)
                            );
                        } catch (DateAfterTodayException e) {
                            showUserDateAfterTodayException(recyclerView);
                        } catch (DateFormatInvalidException e) {
                            showUserDateFormatInvalidException(recyclerView);
                        }
                    }
                }
            }
        });




        //Setup Save Button
        saveButton = findViewById(R.id.activity_add_record_saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRecord();
            }
        });


    }

    //TODO: Implement snackbar when saving, go back to the previous activity
    /**
     * When saving the file, it only detect which goal is filled. Only those got filled will be input to database
     */
    private void saveRecord() {

        double value;
        Record record;
        Goal goal;

        if (!goalRViewAdapter.getValues().isEmpty() && calendarEditText.getText() != null) {

            String dateString = calendarEditText.getText().toString().trim();
            List<Record> records = new ArrayList<>();

            try {
                DateTimeHandler.verifyDate(dateString);
                long date = DateTimeHandler.stringToLongDate(calendarEditText.getText().toString().trim());

                for (Map.Entry<Goal, Double> entry : goalRViewAdapter.getValues().entrySet()) {
                    goal = entry.getKey();
                    value = entry.getValue();
                    record = new Record(goal.getId(), date, value);
                    recordViewModel.checkDuplicate(record);
                    records.add(record);
                }

                for (Record validRecord : records) {
                    recordViewModel.insert(validRecord);
                }

                Intent intent = getIntent();
                intent.putExtra(Constants.MAIN_ADD_RECORD_MSGNAME, "Record saved Successfully");
                setResult(RESULT_OK, intent);
                finish();

            } catch (DateAfterTodayException e) {
                showUserDateAfterTodayException(recyclerView);
            } catch (DateFormatInvalidException e) {
                showUserDateFormatInvalidException(recyclerView);
            } catch (DuplicateRecordException e) {
                Snackbar.make(recyclerView, "Duplicate record found! No record is saved.", Snackbar.LENGTH_SHORT)
                        .show();
            }
        } else {
            Snackbar.make(recyclerView, "Cannot have all empty value. No data to save!", Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = getIntent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    //Below Code is for exception handling
    private void showUserDateAfterTodayException(View v) {
        calendarEditText.setError("Cannot pick a day after today");
        Snackbar.make(v, "Cannot pick a day after today", Snackbar.LENGTH_SHORT)
                .show();
    }

    private void showUserDateFormatInvalidException(View v) {
        calendarEditText.setError("Date format is invalid");
        Snackbar.make(v, "Date format is invalid", Snackbar.LENGTH_SHORT)
                .show();
    }






}