package net.simplifiedcoding.androidmysqlsync;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Belal on 1/27/2017.
 */

public class NameAdapter extends ArrayAdapter<Name> {

    //storing all the names in the list
    private List<Name> names;

    //context object
    private Context context;

    //constructor
    public NameAdapter(Context context, int resource, List<Name> names) {
        super(context, resource, names);
        this.context = context;
        this.names = names;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //getting the layoutinflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //getting listview itmes
        View listViewItem = inflater.inflate(R.layout.names, null, true);
        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewName);
        ImageView imageViewStatus = (ImageView) listViewItem.findViewById(R.id.imageViewStatus);

        //getting the current name
        Name name = names.get(position);

        //setting the name to textview
        textViewName.setText(name.getName());

        //if the synced status is 0 displaying
        //queued icon
        //else displaying synced icon
        if (name.getStatus() == 0)
            imageViewStatus.setBackgroundResource(R.drawable.stopwatch);
        else
            imageViewStatus.setBackgroundResource(R.drawable.success);

        return listViewItem;
    }
}
