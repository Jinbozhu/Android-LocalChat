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
        TextView textContent = (TextView) singleMessageContainer.findViewById(R.id.textContent);
        TextView textStatus = (TextView) singleMessageContainer.findViewById(R.id.textStatus);
        textContent.setText(element.content);
        textStatus.setText(element.status);

//        Button b = (Button) singleMessageContainer.findViewById(R.id.chatButton);
//        // Sets a listener for the button, and a tag for the button as well.
//        b.setTag(new Integer(position));
//        b.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Reacts to a button press.
//                // Gets the integer tag of the button.
//                String s = v.getTag().toString();
//                int duration = Toast.LENGTH_SHORT;
//                Toast toast = Toast.makeText(context, s, duration);
//                toast.show();
//                // Let's remove the list item.
//                int i = Integer.parseInt(s);
//                arrayList.remove(i);
//                my.notifyDataSetChanged();
//            }
//        });

        return singleMessageContainer;
    }
}
