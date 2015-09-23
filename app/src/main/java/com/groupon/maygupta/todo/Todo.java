package com.groupon.maygupta.todo;

import java.util.ArrayList;

/**
 * Created by maygupta on 9/21/15.
 * Model for Todos
 */
public class Todo {
    public String id;
    public String text;
    public String dueDate;
    public String priority;

    public Todo(String text, String dueDate, String priority) {
        this.text = text;
        this.dueDate = dueDate;
        this.priority = priority;
    }

    public static int getPriorityIndex(String priority) {
        switch (priority) {
            case "High": return 0;
            case "Medium": return 1;
            case "Low": return 2;
        }
        return -1;
    }

}
