package com.goodcom.pos.dialog;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

public class EnterDialog {
	private Context context = null;
	private AlertDialog.Builder builder = null;
	private EditText editText = null;
	int sel = 0;

	public EnterDialog(Context context) {
		this.context = context;
		builder = new AlertDialog.Builder(context);
	}

	public void showEnterDialog(String title,
			final OnEnterListener onEnterListener) {
		editText = new EditText(this.context);
		builder.setTitle(title)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setView(editText)
				.setPositiveButton("ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						onEnterListener.onEnter(editText.getText().toString());
					}
				})
				.setNegativeButton("cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(
									DialogInterface dialogInterface, int i) {

							}
						}).show();
	}

	public void showConfirmDialog(String title, String message,
			final OnConfirmListener onConfirmListener) {
		builder.setTitle(title)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setMessage(message)
				.setPositiveButton( "ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						onConfirmListener.onConfirm();
					}
				})
				.setNegativeButton( "cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(
									DialogInterface dialogInterface, int i) {
								onConfirmListener.onCancel();
							}
						}).show();
	}

	public void showListChoseDialog(String title, String[] list,
			final OnChoseListener onChoseListener) {
		builder.setTitle(title)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setSingleChoiceItems(list, 0,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(
									DialogInterface dialogInterface, int i) {
								sel = i;
							}
						})
				.setPositiveButton("ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						onChoseListener.Chose(sel);
					}
				})
				.setNegativeButton("cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(
									DialogInterface dialogInterface, int i) {
								onChoseListener.Chose(-1);
							}
						}).show();
	}

	public void showListDialog(String title, String[] list) {
		builder.setTitle(title)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setItems(list, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {

					}
				})
				.setPositiveButton("ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {

					}
				}).show();
	}

	public interface OnEnterListener {
		void onEnter(String text);
	}

	public interface OnConfirmListener {
		void onConfirm();

		void onCancel();
	}

	public interface OnChoseListener {
		void Chose(int i);
	}
}
