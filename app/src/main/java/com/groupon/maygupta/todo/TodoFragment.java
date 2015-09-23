package com.groupon.maygupta.todo;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by maygupta on 9/21/15.
 */
public class TodoFragment extends DialogFragment {

    OnDialogResult mDialogResult;
    private View view;
    private boolean isNewTodo;
    private int position;
    private String text;
    private String dueDateStr;
    private String priority;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            position = args.getInt("position");
            text = args.getString("text");
            dueDateStr = args.getString("dueDate");
            priority = args.getString("priority");
            isNewTodo = false;
        } else {
            isNewTodo = true;
            position = -1;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.item_dialog, null);
        view.findViewById(R.id.save).setOnClickListener(new SaveListener());
        view.findViewById(R.id.cancel).setOnClickListener(new CancelListener());

        if (isNewTodo == false) {
            TextView todoTextView = (TextView) view.findViewById(R.id.add_todo_text);
            todoTextView.setText(text);

            DatePicker datePickerView = (DatePicker) view.findViewById(R.id.add_todo_due_date);
            if (isValidDate()) {
                datePickerView.updateDate(getYear(), getMonth() - 1, getDate());
            }

            Spinner priorityView = (Spinner) view.findViewById(R.id.add_todo_priority);
            priorityView.setSelection(Todo.getPriorityIndex(priority));

            Button btn = (Button) view.findViewById(R.id.delete);
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(new DeleteListener());
        }

        return view;
    }

    public boolean isValidDate() {
        return dueDateStr.split("-").length == 3;
    }

    public int getDate() {
        return Integer.parseInt(dueDateStr.split("-")[0]);
    }

    public int getMonth() {
        return Integer.parseInt(dueDateStr.split("-")[1]);
    }

    public int getYear() {
        return Integer.parseInt(dueDateStr.split("-")[2]);
    }

    private class SaveListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (mDialogResult != null) {
                TextView text = (TextView) view.findViewById(R.id.add_todo_text);
                DatePicker dueDate = (DatePicker) view.findViewById(R.id.add_todo_due_date);
                Spinner priority = (Spinner) view.findViewById(R.id.add_todo_priority);
                mDialogResult.finish(text.getText().toString(), parseDatePicker(dueDate), priority.getSelectedItem().toString() , position );
            }
            TodoFragment.this.dismiss();
        }
    }

    private class DeleteListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (mDialogResult != null) {
                mDialogResult.delete(position);
            }
            TodoFragment.this.dismiss();
        }
    }

    private String parseDatePicker(DatePicker d) {
        return String.format("%d-%d-%d", d.getDayOfMonth(), d.getMonth()+1, d.getYear());
    }

    private class CancelListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            TodoFragment.this.dismiss();
        }
    }

    public void setDialogResult(OnDialogResult dialogResult) {
        mDialogResult = dialogResult;
    }

    public interface OnDialogResult {
        void finish(String text, String dueDate, String priority, int position);
        void delete(int position);
    }

}
