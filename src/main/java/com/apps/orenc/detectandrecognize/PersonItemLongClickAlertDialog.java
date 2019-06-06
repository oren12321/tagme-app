package com.apps.orenc.detectandrecognize;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by orenc on 6/6/15.
 *
 * An alert dialog that pops out when performing long click on list view item.
 */
public class PersonItemLongClickAlertDialog extends DialogFragment {

    private static final String TAG = "PersonItemLongClickAlertDialog";

    // Interface for the listener who wants to handle this dialog selected operations.
    public interface OnPersonItemAlertDialogClickListener {
        void onShowOperationClick();
        void onEditOperationClick();
        void onRemoveOperationClick();
    }

    // Member listener that will be initialize by the class that implements its interface.
    private OnPersonItemAlertDialogClickListener mListener;

    // Method for setting the listener.
    public void setOnPersonItemAlertDialogListener(OnPersonItemAlertDialogClickListener listener) {
        mListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Take the dialog UI builder.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set its title and its operations.
        builder.setTitle(R.string.choose_opration_text)
                .setItems(R.array.operations_array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // If object that using this doesn't registered to its listener, throw an exception.
                        if(mListener == null) {
                            throw new NullPointerException(
                                    TAG + " does not received appropriate listener");
                        }
                        // Handle the selection with the correct function.
                        switch (which) {
                            case 0:
                                mListener.onShowOperationClick();
                                break;
                            case 1:
                                mListener.onEditOperationClick();
                                break;
                            case 2:
                                mListener.onRemoveOperationClick();
                                break;
                        }
                    }
                });

        // Create the dialog.
        return builder.create();
    }
}
