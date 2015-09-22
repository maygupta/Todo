package com.groupon.maygupta.todo;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import android.widget.DatePicker;
import android.widget.TextView;

import org.w3c.dom.Text;


public class MainActivity extends FragmentActivity implements DatePickerDialog.OnDateSetListener {

    ListView listItems;
    EditText textInput;
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
        textInput = (EditText) findViewById(R.id.etEditText);

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
                Intent i = new Intent(MainActivity.this, EditItemActivity.class);

                i.putExtra("item", currentTodosList.get(position).text);
                i.putExtra("position", position);
                i.putExtra("id", currentTodosList.get(position).id);

                startActivityForResult(i, REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String item = data.getExtras().getString("item");
            int position = data.getIntExtra("position", 0);

            Todo currentTodo = currentTodosList.get(position);
            currentTodo.text = item;

            // Update the entry in database
            databaseHelper.updateTodo(currentTodo);

            adapter.notifyDataSetChanged();
        }
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

    public void onAddItem(View view) {
        Todo newTodo = new Todo(textInput.getText().toString(), "");

        long id = databaseHelper.addTodo(newTodo);
        if ( id != -1 ) {
            newTodo.id = Long.toString(id);
            currentTodosList.add(currentTodosList.size(), newTodo);
            adapter.notifyDataSetChanged();
        }

        textInput.setText("");
    }

    public void showDatePickerDialog(View v) {
        int position = (int) v.getTag();
        currentTodo = currentTodosList.get(position);
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(this.getFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        currentTodo.dueDate = String.format("%d-%d-%d", day, month + 1, year);
        databaseHelper.updateTodo(currentTodo);
        adapter.notifyDataSetChanged();
    }

}
