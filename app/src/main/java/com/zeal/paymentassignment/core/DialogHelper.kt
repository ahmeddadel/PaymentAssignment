package com.zeal.paymentassignment.core

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.zeal.paymentassignment.R
import com.zeal.paymentassignment.databinding.CardNumberDialogBinding
import com.zeal.paymentassignment.databinding.LoadingDialog2Binding
import java.util.function.Consumer

object DialogHelper {

    private var loadingDialog: Dialog? = null;

    fun showPanDialog(activity: Activity, onDone: Consumer<String>, onClose: Runnable) =
        activity.apply {
            val dialogView = CardNumberDialogBinding.inflate(LayoutInflater.from(this))
            val dialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
                .setView(dialogView.root)
                .setTitle("Enter Amount and Card ID")
                .create()

            dialog.setCanceledOnTouchOutside(false) // Prevent dialog from closing on outside touch
            dialog.setCancelable(false) // Prevent dialog from closing on back press

            dialogView.btnSubmit.setOnClickListener {
                handleSubmit(dialogView.editTextCardId.text.toString(), onDone, dialog)
            }
            dialogView.editTextCardId.setOnEditorActionListener { _, actionId, keyEvent ->
                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent?.keyCode == KeyEvent.KEYCODE_ENTER) {
                    handleSubmit(dialogView.editTextCardId.text.toString(), onDone, dialog)
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }

            dialogView.btnCancel.setOnClickListener {
                onClose.run()
                dialog.dismiss()
            }

            dialog.show()
            Handler(Looper.getMainLooper()).postDelayed({
                showSoftKeyboard(this@apply, dialogView.editTextCardId)
            }, 250) // to let the dialog show first
        }

    private fun handleSubmit(cardId: String, onDone: Consumer<String>, dialog: AlertDialog) {
        if (cardId.isNotEmpty()) {
            onDone.accept(cardId)
            dialog.dismiss()
        } else {
            Toast.makeText(dialog.context, "Please enter valid inputs", Toast.LENGTH_LONG).show()
        }
    }

    private fun showSoftKeyboard(context: Context, view: View) {
        view.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    fun showLoadingDialog(activity: Activity, title: String) {
        activity.runOnUiThread {
            val dialogView = LoadingDialog2Binding.inflate(LayoutInflater.from(activity))
            dialogView.tvContent.text = title;
            val dialogBuilder = AlertDialog.Builder(activity)
                .setView(dialogView.root)
                .setTitle("Loading")

            loadingDialog?.dismiss();
            loadingDialog = dialogBuilder.show()

        }
    }

    fun hideLoading(activity: Activity) {
        activity.runOnUiThread {
            loadingDialog?.cancel()
        }
    }
}