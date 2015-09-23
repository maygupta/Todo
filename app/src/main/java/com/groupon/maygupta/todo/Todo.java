package com.groupon.maygupta.todo;

import java.util.Comparator;

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

    public boolean isValidDate() {
        return dueDate.split("-").length == 3;
    }

    public int getDate() {
        return Integer.parseInt(dueDate.split("-")[0]);
    }

    public int getMonth() {
        return Integer.parseInt(dueDate.split("-")[1]);
    }

    public int getYear() {
        return Integer.parseInt(dueDate.split("-")[2]);
    }

    public static class TodoComparator implements Comparator<Todo> {

        @Override
        public int compare(Todo lhs,
                           Todo rhs) {
            if(lhs.priority.equals(rhs.priority)) return 0;
            if(lhs.priority.equals("High")) return -1;
            if(lhs.priority.equals("Low")) return 1;

            if(rhs.priority.equals("High")) return 1;
            return -1;
        }

    }

}
