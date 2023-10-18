package it.uniba.dib.sms22231.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.activities.TaskDetailActivity;
import it.uniba.dib.sms22231.adapters.RecyclerAdapter;
import it.uniba.dib.sms22231.config.TaskStatus;
import it.uniba.dib.sms22231.model.CardData;
import it.uniba.dib.sms22231.model.Task;
import it.uniba.dib.sms22231.service.TaskService;
import it.uniba.dib.sms22231.utility.RecyclerViewInterface;
import it.uniba.dib.sms22231.utility.TimeUtils;

public class TaskFragment extends Fragment implements RecyclerViewInterface {
    private final TaskService taskService = TaskService.getInstance();
    private RecyclerView taskRecycler;
    private FloatingActionButton addTaskButton;
    private TextView noTasksText;
    private ArrayList<CardData> cardDataArrayList;
    private String applicationId;
    private View view;
    private int caller;

    public TaskFragment(String applicationId, int caller) {
        super();
        this.applicationId = applicationId;
        this.caller = caller;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_task, container, false);


        taskRecycler = view.findViewById(R.id.taskRecycler);
        addTaskButton = view.findViewById(R.id.addTaskButton);
        noTasksText = view.findViewById(R.id.noTaskText);

        if (caller == 4) {
            addTaskButton.setVisibility(View.VISIBLE);
        } else {
            addTaskButton.setVisibility(View.GONE);
        }

        addTask();
        fillTaskFragment();

        return view;
    }

    private void fillTaskFragment() {
        taskService.getTasksByApplicationId(applicationId).subscribe(tasks -> {
            cardDataArrayList = new ArrayList<>();
            for (Task t : tasks) {
                String date = TimeUtils.getTimeFromDate(t.dueDate, false);
                CardData cardData = new CardData(t.title, date, t.id, null);
                cardDataArrayList.add(cardData);
            }
            if (cardDataArrayList.isEmpty()) {
                noTasksText.setVisibility(View.VISIBLE);
            } else {
                RecyclerAdapter recyclerAdapter = new RecyclerAdapter(cardDataArrayList, getContext(), this);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                taskRecycler.setLayoutManager(linearLayoutManager);
                taskRecycler.setAdapter(recyclerAdapter);
                SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.refreshTask);
                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        swipeRefreshLayout.setRefreshing(false);
                        fillTaskFragment();
                    }
                });
            }
        });
    }

    private void addTask() {
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View customLayout = getLayoutInflater().inflate(R.layout.custom_alert_add_task, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.addTask)
                        .setView(customLayout)
                        .setNegativeButton(getString(R.string.Cancel), null)
                        .setPositiveButton(getString(R.string.save), null);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                DatePicker datePicker = alertDialog.findViewById(R.id.taskDatePicker);
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                datePicker.init(year, month, day, null);

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Task task = new Task();
                        EditText title = alertDialog.findViewById(R.id.taskTitleText);
                        task.title = title.getText().toString();
                        EditText description = alertDialog.findViewById(R.id.taskDescriptionText);
                        task.description = description.getText().toString();
                        task.applicationId = applicationId;

                        Spinner spinner = alertDialog.findViewById(R.id.taskStatusSpinner);
//                        if (spinner.getSelectedItem().equals(getString(R.string.openStatus))) {
//                            task.status = TaskStatus.open;
//                        } else if (spinner.getSelectedItem().equals(getString(R.string.closedStatus))) {
//                            task.status = TaskStatus.closed;
//                        } else if (spinner.getSelectedItem().equals(getString(R.string.inProgressStatus))) {
//                            task.status = TaskStatus.inProgress;
//                        }
                        task.status = TaskStatus.values()[spinner.getSelectedItemPosition()];

                        calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                        calendar.set(Calendar.MONTH, datePicker.getMonth());
                        calendar.set(Calendar.YEAR, datePicker.getYear());
                        long time = calendar.getTimeInMillis();
                        task.dueDate = new Date(time);

                        taskService.saveNewTask(task, isSuccessfully -> {
                            if (isSuccessfully) {
                                Toast.makeText(getContext(), R.string.success, Toast.LENGTH_SHORT).show();
                            }
                        });
                        alertDialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getContext(), TaskDetailActivity.class);
        String taskId = cardDataArrayList.get(position).getId();
        intent.putExtra("taskId", taskId);
        intent.putExtra("caller", caller);
        startActivity(intent);
    }
}