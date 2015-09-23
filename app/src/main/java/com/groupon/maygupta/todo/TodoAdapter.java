package com.groupon.maygupta.todo;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by maygupta on 9/21/15.
 */
public class TodoAdapter extends ArrayAdapter<Todo> {

    public TodoAdapter(Context context, ArrayList<Todo> todos) {
        super(context, 0, todos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Todo todo = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        TextView itemText = (TextView) convertView.findViewById(R.id.itemText);
        TextView itemDueDate = (TextView) convertView.findViewById(R.id.itemDueDate);
        TextView itemPriority = (TextView) convertView.findViewById(R.id.itemPriority);
        itemDueDate.setTag(position);

        itemText.setText(todo.text);
        if (todo.dueDate != null) {
            itemDueDate.setText(todo.dueDate);
        }
        if (todo.priority != null) {
            itemPriority.setText(todo.priority);

            switch (todo.priority) {
                case Todo.PRIORITY_HIGH: itemPriority.setTextColor(Color.RED); break;
                case Todo.PRIORITY_MEDIUM: itemPriority.setTextColor(Color.rgb(236,141,22)); break;
                case Todo.PRIORITY_LOW: itemPriority.setTextColor(Color.rgb(110,216,10)); break;
            }
        }
        convertView.setTag(position);
        return convertView;
    }

}
