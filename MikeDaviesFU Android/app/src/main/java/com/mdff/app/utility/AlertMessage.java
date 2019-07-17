package com.mdff.app.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class AlertMessage extends DialogFragment {
private String message; static private String okString,title;

/* The activity that creates an instance of this dialog fragment must
 * implement this interface in order to receive event callbacks.
 * Each method passes the DialogFragment in case the host needs to query it. */
public interface NoticeDialogListenerWithoutView {
    public void onDialogPositiveWithoutViewClick(DialogFragment dialog);

}

// Use this instance of the interface to deliver action events
NoticeDialogListenerWithoutView mListener;

// Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
@Override
public void onAttach(Activity activity) {
    super.onAttach(activity);
    // Verify that the host activity implements the callback interface
    try {
        // Instantiate the NoticeDialogListener so we can send events to the host
        mListener = (NoticeDialogListenerWithoutView) activity;
    } catch (ClassCastException e) {
        // The activity doesn't implement the interface, throw exception
        throw new ClassCastException(activity.toString()
                + " must implement NoticeDialogListener");
    }
}
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
		builder.setTitle(title);
		builder.setMessage(getArguments().getString("message"));
		builder.setPositiveButton(okString,new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				mListener.onDialogPositiveWithoutViewClick(AlertMessage.this);
				
			}
		});

		Dialog dialog=builder.create();
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}
	public static  AlertMessage newInstance(String message, String ok,String t) {
		okString=ok;
		title=t;
		AlertMessage f = new AlertMessage();
	    Bundle args = new Bundle();
	    args.putString("message", message);
	    f.setArguments(args);
	    return f;
	}



}
