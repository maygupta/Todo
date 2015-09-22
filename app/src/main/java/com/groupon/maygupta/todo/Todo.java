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

}
