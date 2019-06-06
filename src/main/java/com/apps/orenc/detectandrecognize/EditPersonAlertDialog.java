package com.apps.orenc.detectandrecognize;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by orenc on 6/7/15.
 *
 * Alert dialog for editing a person.
 */
public class EditPersonAlertDialog extends DialogFragment {

    private static final String TAG = "EditPersonAlertDialog";

    private Person mPerson;

    public interface OnEditPersonAlertDialogClickListener {
        public void onEditPersonAlertDialogPositiveClick();
    }

    public void setPerson(Person person) {
        mPerson = person;
    }

    public void setOnEditPersonAlertDialogClickListener(OnEditPersonAlertDialogClickListener listener) {
        mListener = listener;
    }

    private OnEditPersonAlertDialogClickListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View root = inflater.inflate(R.layout.dialog_edit_person, null);

        // Build the dialog UI.
        builder.setView(root)
                .setPositiveButton(R.string.save_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // If object that using this doesn't registered to its listener, throw an exception.
                        if(mListener == null) {
                            throw new NullPointerException(
                                    TAG + " does not received appropriate listener");
                        }
                        // 1. Update member person.
                        updatePerson(root);
                        // 2. Call listener method in order to update the application with the updated person.
                        mListener.onEditPersonAlertDialogPositiveClick();
                    }
                })
                .setNegativeButton(R.string.cancel_button, null);

        setUiFields(root);

        return builder.create();
    }

    private void setUiFields(View root) {

        if(mPerson.getPicture() != null){
            ((ImageView) root.findViewById(R.id.edit_person_dialog_picture)).
                    setImageBitmap(BitmapCodec.decode(mPerson.getPicture()));
        }

        if(mPerson.getFirstName() != null) {
            ((EditText) root.findViewById(R.id.edit_person_dialog_first_name)).
                    setText(mPerson.getFirstName());
        }

        if(mPerson.getLastName() != null) {
            ((EditText) root.findViewById(R.id.edit_person_dialog_last_name)).
                    setText(mPerson.getLastName());
        }

        if(mPerson.getEmail() != null) {
            ((EditText) root.findViewById(R.id.edit_person_dialog_email)).
                    setText(mPerson.getEmail());
        }

        if(mPerson.getPhone() != null) {
            ((EditText) root.findViewById(R.id.edit_person_dialog_phone)).
                    setText(mPerson.getPhone());
        }

        if(mPerson.getGeneral() != null) {
            ((EditText) root.findViewById(R.id.edit_person_dialog_general)).
                    setText(mPerson.getGeneral());
        }
    }

    private void updatePerson(View root) {

        // update the person member from the updated UI components.

        String firstName = ((EditText) root.findViewById(R.id.edit_person_dialog_first_name)).getText().toString();
        String lastName = ((EditText) root.findViewById(R.id.edit_person_dialog_last_name)).getText().toString();
        String email = ((EditText) root.findViewById(R.id.edit_person_dialog_email)).getText().toString();
        String phone = ((EditText) root.findViewById(R.id.edit_person_dialog_phone)).getText().toString();
        String general = ((EditText) root.findViewById(R.id.edit_person_dialog_general)).getText().toString();

        if (!firstName.equals("")) {
            mPerson.setFirstName(firstName);
        }

        if (!lastName.equals("")) {
            mPerson.setLastName(lastName);
        }

        if (!email.equals("")) {
            mPerson.setEmail(email);
        }

        if (!phone.equals("")) {
            mPerson.setPhone(phone);
        }

        if (!general.equals("")) {
            mPerson.setGeneral(general);
        }
    }
}