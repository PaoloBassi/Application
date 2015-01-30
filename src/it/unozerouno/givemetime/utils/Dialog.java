package it.unozerouno.givemetime.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * class for handling Dialog messages 
 * @author paolo
 *
 */
public class Dialog {

	/**
	 * popup a dialog to inform the user about a required payment
	 * @param context
	 * @param title : title of the Dialog. It's an int, so it's required a string.xml reference
	 * @param msg : message of the Dialog. It's an int, so it's required a string.xml reference
	 */
	public static void paymentDialog(Context context, int title, int msg) {
		new AlertDialog.Builder(context)
		.setTitle(title)
		.setMessage(msg)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				
			}
		}).show();
	}
	
}
