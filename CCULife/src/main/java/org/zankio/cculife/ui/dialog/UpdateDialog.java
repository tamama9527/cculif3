package org.zankio.cculife.ui.dialog;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import org.zankio.cculife.R;
public class UpdateDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle argv = getArguments();
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.new_version)
                .setMessage(String.format("v%s\n%s", argv.getString("version"), argv.getString("description")))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
    }

    @Override
    public void onDestroyView() {
        UpdateDialog.this.getActivity().finish();
        super.onDestroyView();
    }
}
