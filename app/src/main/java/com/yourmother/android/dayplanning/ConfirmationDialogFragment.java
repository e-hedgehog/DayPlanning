package com.yourmother.android.dayplanning;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.io.Serializable;

public class ConfirmationDialogFragment extends DialogFragment {

    private static final String TAG = "Confirmation";

    private static final String ARG_LISTENER = "confirmListener";
    private static final String ARG_MESSAGE = "message";

    interface OnConfirmListener extends Serializable {
        void onPositiveButtonPressed();
    }

    public static ConfirmationDialogFragment newInstance(String message, OnConfirmListener listener) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_LISTENER, listener);
        args.putString(ARG_MESSAGE, message);

        ConfirmationDialogFragment fragment = new ConfirmationDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        OnConfirmListener mListener = (OnConfirmListener)
                getArguments().getSerializable(ARG_LISTENER);
        String message = getArguments().getString(ARG_MESSAGE);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.confirm_dialog_title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    if (mListener != null)
                        mListener.onPositiveButtonPressed();
                })
                .setNegativeButton(android.R.string.no,
                        (dialog, which) -> Log.i(TAG, "Pressed negative button"))
                .create();
    }
}
