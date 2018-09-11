package mans.abdullah.abdullah_mansour.todolist;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.provider.AlarmClock;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mans.abdullah.abdullah_mansour.todolist.Database.AppDatabase;
import mans.abdullah.abdullah_mansour.todolist.Database.TaskEntry;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnItemClickListner
{
    FloatingActionButton time;
    static Button add_task, back;
    static EditText task_info;
    static ImageView set_task_time;
    static TextView task_time_hours,task_time_minutes;
    AppDatabase mDb;
    static String infoo,hour,minute;
    static int taskID;
    static int hoursoday,minutesofday;


    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDb = AppDatabase.getInstance(getApplicationContext());

        time = (FloatingActionButton) findViewById(R.id.add_list);
        recyclerView = (RecyclerView) findViewById(R.id.todolist_recycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

        taskAdapter = new TaskAdapter(getApplicationContext());
        taskAdapter.setOnItemClickListner(MainActivity.this);

        recyclerView.setAdapter(taskAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir)
            {
                // Here is where you'll implement swipe to delete
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int position = viewHolder.getAdapterPosition();
                        List<TaskEntry> tasks = taskAdapter.getTasks();
                        mDb.taskDao().deleteTask(tasks.get(position));
                    }
                });
            }
        }).attachToRecyclerView(recyclerView);

        setupViewModel();

        time.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showCustomDialog();
            }
        });
    }

    private void showCustomDialogUpdate(final String i, final String h, final String m, final int id)
    {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        add_task = (Button) dialog.findViewById(R.id.add_task_btn);
        back = (Button) dialog.findViewById(R.id.back_btn);

        task_info = (EditText) dialog.findViewById(R.id.task_txt);
        task_time_hours = (TextView) dialog.findViewById(R.id.task_time_hours);
        task_time_minutes = (TextView) dialog.findViewById(R.id.task_time_minutes);
        set_task_time = (ImageView) dialog.findViewById(R.id.set_time_image);

        add_task.setText("Update Task");

        task_info.setText(i);
        task_time_hours.setText(h);
        task_time_minutes.setText(m);

        final View layout = getLayoutInflater().inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setTextColor(Color.WHITE);
        text.setText("Please enter valid data");
        CardView lyt_card = (CardView) layout.findViewById(R.id.lyt_card);
        lyt_card.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));

        final Toast toast = new Toast(getApplicationContext());

        set_task_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });

        add_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String info = task_info.getText().toString();
                String hours = task_time_hours.getText().toString();

                if (info.length() == 0 || hours.length() == 0)
                {
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
                else
                {
                    String info2,hour2,minute2;
                    Date date2;
                    info2 = task_info.getText().toString();
                    hour2 = task_time_hours.getText().toString();
                    minute2 = task_time_minutes.getText().toString();
                    date2 = new Date();

                    final TaskEntry task = new TaskEntry(info2,hour2,minute2,date2);
                    AppExecutors.getInstance().diskIO().execute(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            task.setId(id);
                            mDb.taskDao().updateTask(task);
                        }
                    });
                    dialog.dismiss();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                toast.cancel();
            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void setupViewModel()
    {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getTasks().observe(this, new Observer<List<TaskEntry>>()
        {
            @Override
            public void onChanged(@Nullable List<TaskEntry> taskEntries) {
                //Log.d(TAG, "Updating list of tasks from LiveData in ViewModel");
                taskAdapter.setTasks(taskEntries);
            }
        });
    }

    private void showCustomDialog()
    {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        add_task = (Button) dialog.findViewById(R.id.add_task_btn);
        back = (Button) dialog.findViewById(R.id.back_btn);

        task_info = (EditText) dialog.findViewById(R.id.task_txt);
        task_time_hours = (TextView) dialog.findViewById(R.id.task_time_hours);
        task_time_minutes = (TextView) dialog.findViewById(R.id.task_time_minutes);
        set_task_time = (ImageView) dialog.findViewById(R.id.set_time_image);

        final View layout = getLayoutInflater().inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setTextColor(Color.WHITE);
        text.setText("Please enter valid data");
        CardView lyt_card = (CardView) layout.findViewById(R.id.lyt_card);
        lyt_card.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));

        final Toast toast = new Toast(getApplicationContext());

        set_task_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });

        add_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String info = task_info.getText().toString();
                String hours = task_time_hours.getText().toString();

                if (info.length() == 0 || hours.length() == 0)
                {
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
                else
                {
                    final String infoo,hour,minute;
                    Date date;
                    infoo = task_info.getText().toString();
                    hour = task_time_hours.getText().toString();
                    minute = task_time_minutes.getText().toString();
                    date = new Date();

                    final TaskEntry task = new TaskEntry(infoo,hour,minute,date);
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void run()
                        {
                            mDb.taskDao().insertTask(task);
                            createAlarm(infoo,hoursoday,minutesofday);
                        }
                    });
                    dialog.dismiss();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
                toast.cancel();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public void OnClick(int position)
    {
        TaskEntry taskEntry = taskAdapter.getTasks().get(position);

        infoo = taskEntry.getTask_info();
        hour = taskEntry.getTask_hour();
        minute = taskEntry.getTask_minute();
        taskID = taskEntry.getId();

        showCustomDialogUpdate(infoo,hour,minute,taskID);
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));

            // Create a new instance of TimePickerDialog and return it
            return timePickerDialog;
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
        {
            // Do something with the time chosen by the user
            task_time_hours.setText("" + hourOfDay);
            task_time_minutes.setText("" + minute);
            hoursoday = hourOfDay;
            minutesofday = minute;

        }
    }

    public void createAlarm(String message, int hour, int minutes) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_MESSAGE, message)
                .putExtra(AlarmClock.EXTRA_HOUR, hour)
                .putExtra(AlarmClock.EXTRA_MINUTES, minutes);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
