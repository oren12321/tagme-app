package com.apps.orenc.detectandrecognize;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by orenc on 6/7/15.
 *
 * Alert dialog for adding new person to the database.
 */
public class AddPersonAlertDialog extends DialogFragment {

    private static final String TAG = "AddPersonAlertDialog";

    private Bitmap mNewPicture;

    public interface OnAddPersonAlertDialogClickListener {
        void onAddPersonAlertDialogPositiveClick(Person newPerson);
    }

    public void setOnAddPersonAlertDialogClickListener(OnAddPersonAlertDialogClickListener listener) {
        mListener = listener;
    }

    public void setBitmap(Bitmap bitmap) {
        mNewPicture = bitmap;
    }

    private OnAddPersonAlertDialogClickListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View root = inflater.inflate(R.layout.dialog_edit_person, null);

        // Build the dialog UI.
        builder.setView(root)
                .setPositiveButton(R.string.add_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // If object that using this doesn't registered to its listener, throw an exception.
                        if(mListener == null) {
                            throw new NullPointerException(
                                    TAG + " does not received appropriate listener");
                        }
                        // 1. Update member person.
                        Person newPerson = initializePerson(root);
                        // 2. Call listener method in order to update the application with the updated person.
                        mListener.onAddPersonAlertDialogPositiveClick(newPerson);
                    }
                })
                .setNegativeButton(R.string.cancel_button, null);

        setImageView(root);

        return builder.create();
    }

    private void setImageView(View root) {
        ((ImageView) root.findViewById(R.id.edit_person_dialog_picture)).setImageBitmap(mNewPicture);
    }

    private Person initializePerson(View root) {

        // update the person member from the updated UI components.
        Person person = new Person();

        String firstName = ((EditText) root.findViewById(R.id.edit_person_dialog_first_name)).getText().toString();
        String lastName = ((EditText) root.findViewById(R.id.edit_person_dialog_last_name)).getText().toString();
        String email = ((EditText) root.findViewById(R.id.edit_person_dialog_email)).getText().toString();
        String phone = ((EditText) root.findViewById(R.id.edit_person_dialog_phone)).getText().toString();
        String general = ((EditText) root.findViewById(R.id.edit_person_dialog_general)).getText().toString();

        ImageView iv = (ImageView) root.findViewById(R.id.edit_person_dialog_picture);
        iv.buildDrawingCache();

        person.setPicture(BitmapCodec.encode(iv.getDrawingCache()));

        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setEmail(email);
        person.setPhone(phone);
        person.setGeneral(general);

        return person;
    }
}
