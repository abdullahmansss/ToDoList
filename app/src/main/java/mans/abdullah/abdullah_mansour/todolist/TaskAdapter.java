package mans.abdullah.abdullah_mansour.todolist;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mans.abdullah.abdullah_mansour.todolist.Database.TaskEntry;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    Context context;
    List<TaskEntry> taskEntries;
    String DATE_FORMAT = "dd/MM/yyy";
    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private OnItemClickListner mlistner;


    public TaskAdapter(Context context)
    {
        this.context = context;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item , parent , false);
        TaskViewHolder guestviewholder = new TaskViewHolder(view);
        return guestviewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position)
    {
        TaskEntry taskEntry = taskEntries.get(position);
        String task_info = taskEntry.getTask_info();
        String task_hour = taskEntry.getTask_hour();
        String task_minute = taskEntry.getTask_minute();
        String task_date = dateFormat.format(taskEntry.getTask_date());

        holder.task_info.setText(task_info);
        holder.task_hours.setText(task_hour);
        holder.task_minutes.setText(task_minute);
        holder.task_date.setText(task_date);
    }

    @Override
    public int getItemCount()
    {
        if (taskEntries == null) {
            return 0;
        }
        return taskEntries.size();
    }

    public List<TaskEntry> getTasks()
    {
        return taskEntries;
    }

    public void setTasks(List<TaskEntry> taskEntries2)
    {
        taskEntries = taskEntries2;
        notifyDataSetChanged();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView task_info,task_hours,task_minutes,task_date;

        public TaskViewHolder(View itemView) {
            super(itemView);

            task_info = (TextView) itemView.findViewById(R.id.task_info);
            task_hours = (TextView) itemView.findViewById(R.id.hours);
            task_minutes = (TextView) itemView.findViewById(R.id.minutes);
            task_date = (TextView) itemView.findViewById(R.id.task_date);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            if (mlistner != null)
            {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION)
                {
                    mlistner.OnClick(position);
                }
            }
        }
    }

    public interface OnItemClickListner
    {
        public void OnClick (int position);
    }

    public void setOnItemClickListner (OnItemClickListner onItemClickListner)
    {
        mlistner = onItemClickListner;
    }
}
