package com.groupon.maygupta.todo;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;


/**
 * Created by maygupta on 9/21/15.
 */
public class TodoFragment extends DialogFragment {

    OnDialogResult mDialogResult;
    private View view;
    private boolean isNewTodo;
    private int position;
    Todo todo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            position = args.getInt("position");
            todo = new Todo(args.getString("text"),args.getString("dueDate"),args.getString("priority"));
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

        TextView todoTextView = (TextView) view.findViewById(R.id.add_todo_text);
        if (isNewTodo == false) {

            todoTextView.setText(todo.text);

            DatePicker datePickerView = (DatePicker) view.findViewById(R.id.add_todo_due_date);
            if (todo.isValidDate()) {
                datePickerView.updateDate(todo.getYear(), todo.getMonth() - 1, todo.getDate());
            }

            Spinner priorityView = (Spinner) view.findViewById(R.id.add_todo_priority);
            priorityView.setSelection(Todo.getPriorityIndex(todo.priority));

            Button btn = (Button) view.findViewById(R.id.delete);
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(new DeleteListener());
        } else {
            todoTextView.setFocusable(true);
            todoTextView.requestFocus();
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        return view;
    }

    /**
     * LISTENERS FOR ACTION BUTTONS
     **/
    private class SaveListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (mDialogResult != null) {
                TextView text = (TextView) view.findViewById(R.id.add_todo_text);
                DatePicker dueDate = (DatePicker) view.findViewById(R.id.add_todo_due_date);
                Spinner priority = (Spinner) view.findViewById(R.id.add_todo_priority);
                Todo todo = new Todo(text.getText().toString(), parseDatePicker(dueDate), priority.getSelectedItem().toString());
                mDialogResult.finish(todo , position );
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

    private class CancelListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            TodoFragment.this.dismiss();
        }
    }

    public void setDialogResult(OnDialogResult dialogResult) {
        mDialogResult = dialogResult;
    }

    private String parseDatePicker(DatePicker d) {
        return String.format("%d-%d-%d", d.getDayOfMonth(), d.getMonth()+1, d.getYear());
    }

    public interface OnDialogResult {
        void finish(Todo todo, int position);
        void delete(int position);
    }

}
