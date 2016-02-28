package com.example.feeling.homework3;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by shobhit on 1/24/16.
 * Copied from Prof. Luca class code
 */
public class MyAdapter extends ArrayAdapter<ListElement> {
    int resource;
    Context context;

    TextView textContent;
    TextView textStatus;


    public MyAdapter(Context _context, int _resource, List<ListElement> items) {
        super(_context, _resource, items);
        resource = _resource;
        context = _context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout singleMessageContainer;
        ListElement element = getItem(position);

        // Inflate a new view if necessary.
        if (convertView == null) {
            singleMessageContainer = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
            vi.inflate(resource, singleMessageContainer, true);
        } else {
            singleMessageContainer = (LinearLayout) convertView;
        }

        // Fills in the view.
        textContent = (TextView) singleMessageContainer.findViewById(R.id.textContent);
        textStatus = (TextView) singleMessageContainer.findViewById(R.id.textStatus);
        textContent.setText(element.content);
        textStatus.setText(element.status);
        if (element.self) {
            singleMessageContainer.setGravity(View.FOCUS_RIGHT);
        }

        return singleMessageContainer;
    }
}
