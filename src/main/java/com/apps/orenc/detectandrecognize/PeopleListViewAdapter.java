package com.apps.orenc.detectandrecognize;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by orenc on 6/5/15.
 */
public class PeopleListViewAdapter extends ArrayAdapter<Person> {

    private static final String TAG = "PeopleListViewAdapter";

    public PeopleListViewAdapter(Context context, List<Person> persons) {
        super(context, R.layout.list_view_item_person, persons);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if(convertView == null) {

            // Inflate the GridView item layout.
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_view_item_person, parent, false);

            // Initialize the view holder.
            viewHolder = new ViewHolder();
            viewHolder.mPersonPictureImageView = (ImageView) convertView.findViewById(R.id.person_picture_image_view);
            viewHolder.mPersonNameTextView = (TextView) convertView.findViewById(R.id.person_name_text_view);
            viewHolder.mPersonEmailTextView = (TextView) convertView.findViewById(R.id.person_email_text_view);
            convertView.setTag(viewHolder);
        }
        else {
            // Recycle the already inflated view.
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Update the item view.
        Person person = getItem(position);
        viewHolder.mPersonPictureImageView.setImageBitmap(BitmapCodec.decode(person.getPicture()));
        String name =
                (person.getFirstName() == null ? "" : person.getFirstName()) + " "
                 + (person.getLastName() == null ? "" : person.getLastName());
        viewHolder.mPersonNameTextView.setText(name);
        viewHolder.mPersonEmailTextView.setText(person.getEmail());



        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    private static class ViewHolder {

        private ImageView mPersonPictureImageView;
        private TextView mPersonNameTextView;
        private TextView mPersonEmailTextView;

    }


}
