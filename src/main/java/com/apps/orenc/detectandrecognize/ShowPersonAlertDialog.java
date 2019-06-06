package com.apps.orenc.detectandrecognize;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by orenc on 6/6/15.
 *
 * Alert dialog that shows the selected person details.
 */
public class ShowPersonAlertDialog extends DialogFragment {

    private static final String TAG = "ShowPersonAlertDialog";

    // The selected person.
    private Person mPerson;

    // Set selected person.
    public void setPerson(Person person) {
        mPerson = person;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View root = inflater.inflate(R.layout.dialog_show_person, null);

        // Build the dialog UI.
        builder.setView(root)
                .setPositiveButton(R.string.ok_button, null);

        setUiFields(root);

        return builder.create();
    }

    // Set the UI fields according to the selected person.
    private void setUiFields(View root) {

        if(mPerson.getPicture() != null){
            ((ImageView) root.findViewById(R.id.show_person_dialog_picture)).
                    setImageBitmap(BitmapCodec.decode(mPerson.getPicture()));
        }

        if(mPerson.getFirstName() != null) {
            ((TextView) root.findViewById(R.id.show_person_dialog_first_name)).
                    setText(mPerson.getFirstName());
        }

        if(mPerson.getLastName() != null) {
            ((TextView) root.findViewById(R.id.show_person_dialog_last_name)).
                    setText(mPerson.getLastName());
        }

        if(mPerson.getEmail() != null) {
            ((TextView) root.findViewById(R.id.show_person_dialog_email)).
                    setText(mPerson.getEmail());
        }

        if(mPerson.getPhone() != null) {
            ((TextView) root.findViewById(R.id.show_person_dialog_phone)).
                    setText(mPerson.getPhone());
        }

        if(mPerson.getGeneral() != null) {
            ((TextView) root.findViewById(R.id.show_person_dialog_general)).
                    setText(mPerson.getGeneral());
        }
    }
}
