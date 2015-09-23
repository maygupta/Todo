package com.groupon.maygupta.todo;


import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends AppCompatActivity {

    ListView listItems;
    ArrayList<Todo> currentTodosList;
    Todo currentTodo;
    TodoAdapter adapter;

    TodosDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = TodosDatabaseHelper.getInstance(this);

        populateArrayItems();
        listItems = (ListView) findViewById(R.id.lvListItems);
        listItems.setAdapter(adapter);


        // Creating a long click listener
        listItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Delete entry from the database
                databaseHelper.deleteTodo(currentTodosList.get(position));
                currentTodosList.remove(position);
                arrangeTodosByPriority();
                adapter.notifyDataSetChanged();

                return false;
            }
        });

        listItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDialog(view, false);
            }
        });
    }

    public void showDialog(View v, boolean isNewTodo) {
        FragmentManager manager = getFragmentManager();
        TodoFragment fragment = new TodoFragment();
        fragment.setDialogResult(new TodoFragment.OnDialogResult() {
            @Override
            public void finish(Todo todo, int position) {
                if (position == -1) {
                    currentTodosList.add(currentTodosList.size(), todo);
                    databaseHelper.addTodo(todo);
                } else {
                    currentTodo = currentTodosList.get(position);
                    currentTodo.text = todo.text;
                    currentTodo.dueDate = todo.dueDate;
                    currentTodo.priority = todo.priority;
                    databaseHelper.updateTodo(currentTodo);
                }
                arrangeTodosByPriority();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void delete(int position) {
                currentTodo = currentTodosList.get(position);
                databaseHelper.deleteTodo(currentTodo);
                currentTodosList.remove(position);
                arrangeTodosByPriority();
                adapter.notifyDataSetChanged();
            }
        });

        // If already existing todo then send data of todo to fragment
        if (isNewTodo == false) {
            Bundle args = new Bundle();
            int position = (int) v.getTag();
            args.putInt("position", position);
            currentTodo = currentTodosList.get(position);
            args.putString("text", currentTodo.text);
            args.putString("dueDate", currentTodo.dueDate);
            args.putString("priority", currentTodo.priority);
            fragment.setArguments(args);
        }

        fragment.show(manager, "TodoDialog");
    }

    public void populateArrayItems() {
        currentTodosList = new ArrayList<Todo>(databaseHelper.getAllTodos());
        arrangeTodosByPriority();
        adapter = new TodoAdapter(this, currentTodosList);
    }

    public void onClickAdd(View view) {
        showDialog(view, true);
    }

    public void arrangeTodosByPriority() {
        Collections.sort(currentTodosList, new Todo.TodoComparator());
    }

}
