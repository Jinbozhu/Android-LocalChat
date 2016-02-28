package com.example.feeling.homework3;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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

    TextView msgText;
    TextView nicknameText;

    public MyAdapter(Context _context, int _resource, List<ListElement> items) {
        super(_context, _resource, items);
        resource = _resource;
        context = _context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout newView;
        ListElement element = getItem(position);

        // Inflate a new view if necessary.
        if (convertView == null) {
            newView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
            vi.inflate(resource, newView, true);
        } else {
            newView = (LinearLayout) convertView;
        }

        // Fills in the view.
        msgText = (TextView) newView.findViewById(R.id.msgText);
        nicknameText = (TextView) newView.findViewById(R.id.nicknameText);
        msgText.setText(element.message);
        nicknameText.setText(element.nickname);

        LinearLayout singleMessageContainer
                = (LinearLayout) newView.findViewById(R.id.singleMessageContainer);

        if (element.self && !element.delivered) {
            newView.setGravity(Gravity.END);
            singleMessageContainer.setBackgroundResource(R.drawable.right_gray);
            nicknameText.setGravity(Gravity.END);
        } else if (element.self && element.delivered) {
            newView.setGravity(Gravity.END);
            singleMessageContainer.setBackgroundResource(R.drawable.right_green);
            nicknameText.setGravity(Gravity.END);
        } else {
            newView.setGravity(Gravity.START);
            singleMessageContainer.setBackgroundResource(R.drawable.left_gray);
            nicknameText.setGravity(Gravity.START);
        }

        return newView;
    }
}
