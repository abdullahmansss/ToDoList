package mans.abdullah.abdullah_mansour.todolist.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "task")
public class TaskEntry
{
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String task_info;
    private String task_hour;
    private String task_minute;
    @ColumnInfo(name = "updated_at")
    private Date task_date;

    @Ignore
    public TaskEntry(String task_info, String task_hour, String task_minute, Date task_date) {
        this.task_info = task_info;
        this.task_hour = task_hour;
        this.task_minute = task_minute;
        this.task_date = task_date;
    }

    public TaskEntry(int id, String task_info, String task_hour, String task_minute, Date task_date) {
        this.id = id;
        this.task_info = task_info;
        this.task_hour = task_hour;
        this.task_minute = task_minute;
        this.task_date = task_date;
    }

    public String getTask_hour() {
        return task_hour;
    }

    public void setTask_hour(String task_hour) {
        this.task_hour = task_hour;
    }

    public String getTask_minute() {
        return task_minute;
    }

    public void setTask_minute(String task_minute) {
        this.task_minute = task_minute;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTask_info() {
        return task_info;
    }

    public void setTask_info(String task_info) {
        this.task_info = task_info;
    }

    public Date getTask_date() {
        return task_date;
    }

    public void setTask_date(Date task_date) {
        this.task_date = task_date;
    }
}
