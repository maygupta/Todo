package com.groupon.maygupta.todo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> todoItems;
    ArrayAdapter<String> itemsAdapter;
    ListView listItems;
    EditText textInput;
    List<Todo> currentTodos;
    private final int REQUEST_CODE = 20;

    TodosDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = TodosDatabaseHelper.getInstance(this);

        populateArrayItems();
        listItems = (ListView) findViewById(R.id.lvListItems);
        listItems.setAdapter(itemsAdapter);
        textInput = (EditText) findViewById(R.id.etEditText);

        // Creating a long click listener
        listItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                todoItems.remove(position);
                itemsAdapter.notifyDataSetChanged();

                // Delete entry from the database
                databaseHelper.deleteTodo(currentTodos.get(position));
                currentTodos.remove(position);

                return false;
            }
        });

        listItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, EditItemActivity.class);

                i.putExtra("item", todoItems.get(position));
                i.putExtra("position", position);
                i.putExtra("id", currentTodos.get(position).id);

                startActivityForResult(i, REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String item = data.getExtras().getString("item");
            int position = data.getIntExtra("position", 0);
            todoItems.set(position, item);
            itemsAdapter.notifyDataSetChanged();

            // Update the entry in database
            Todo currentTodo = currentTodos.get(position);
            currentTodo.text = item;
            databaseHelper.updateTodo(currentTodo);
        }
    }

    public void populateArrayItems() {
        currentTodos = databaseHelper.getAllTodos();

        todoItems = new ArrayList<String>();
        int index = 0;
        for (Todo todo: currentTodos) {
            todoItems.add(index, todo.text);
            index++;
        }

        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, todoItems);
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

    public void onAddItem(View view) {
        itemsAdapter.add(textInput.getText().toString());

        Todo newTodo = new Todo();
        newTodo.text = textInput.getText().toString();

        long id = databaseHelper.addTodo(newTodo);
        if ( id != -1 ) {
            newTodo.id = Long.toString(id);
            currentTodos.add(currentTodos.size(), newTodo);
        }

        textInput.setText("");
    }
}
