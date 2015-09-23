package com.groupon.maygupta.todo;


import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ListView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    ListView listItems;
    ArrayList<Todo> currentTodosList;
    Todo currentTodo;
    private final int REQUEST_CODE = 20;
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
            public void finish(String text, String dueDate, String priority, int position) {
                if (position == -1) {
                    Todo newTodo = new Todo(text, dueDate, priority);
                    currentTodosList.add(currentTodosList.size(), newTodo);
                    databaseHelper.addTodo(newTodo);
                } else {
                    currentTodo = currentTodosList.get(position);
                    currentTodo.text = text;
                    currentTodo.dueDate = dueDate;
                    currentTodo.priority = priority;
                    databaseHelper.updateTodo(currentTodo);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void delete(int position) {
                currentTodo = currentTodosList.get(position);
                databaseHelper.deleteTodo(currentTodo);
                currentTodosList.remove(position);
                adapter.notifyDataSetChanged();
            }
        });

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
        adapter = new TodoAdapter(this, currentTodosList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickAdd(View view) {
        showDialog(view, true);
    }

}
