package com.groupon.maygupta.todo;

/**
 * Created by maygupta on 9/21/15.
 * Model for Todos
 */
public class Todo {
    public String id;
    public String text;
    public String dueDate;

    public Todo(String text, String dueDate) {
        this.text = text;
        this.dueDate = dueDate;
    }

    // Due date is stored in ddmmyyyy format in database

    public int getDate() {
        return Integer.parseInt(dueDate.substring(0, 2));
    }

    public int getMonth() {
        return Integer.parseInt(dueDate.substring(2, 4));
    }

    public int getYear() {
        return Integer.parseInt(dueDate.substring(4));
    }
}
