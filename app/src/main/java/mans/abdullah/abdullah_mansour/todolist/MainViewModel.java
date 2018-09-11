package mans.abdullah.abdullah_mansour.todolist;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mans.abdullah.abdullah_mansour.todolist.Database.AppDatabase;
import mans.abdullah.abdullah_mansour.todolist.Database.TaskEntry;

public class MainViewModel extends AndroidViewModel {
    private LiveData<List<TaskEntry>> tasks;

    public MainViewModel(@NonNull Application application) {
        super(application);

        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        //Log.d(TAG, "Actively retrieving the tasks from the DataBase");
        tasks = database.taskDao().loadAllTasks();
    }

    public LiveData<List<TaskEntry>> getTasks()
    {
        return tasks;
    }
}