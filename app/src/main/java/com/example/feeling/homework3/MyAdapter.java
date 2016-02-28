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
    ImageView imageView;

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
        imageView = (ImageView) newView.findViewById(R.id.imageView);

        LinearLayout singleMessageContainer
                = (LinearLayout) newView.findViewById(R.id.singleMessageContainer);

        if (element.self && element.delivered) {
            newView.setGravity(Gravity.RIGHT);
            singleMessageContainer.setBackgroundResource(R.drawable.right_bubble);
            imageView.setImageResource(R.drawable.double_tick);
        } else if (element.self && !element.delivered) {
            newView.setGravity(Gravity.RIGHT);
            singleMessageContainer.setBackgroundResource(R.drawable.right_bubble);
            imageView.setImageResource(android.R.color.transparent);
        } else {
            newView.setGravity(Gravity.LEFT);
            singleMessageContainer.setBackgroundResource(R.drawable.left_bubble);
        }

        return newView;
    }
}
