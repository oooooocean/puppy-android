package tools

import android.app.Dialog
import android.content.Context
import android.view.View
import com.example.puppy_android.R

class ProgressDialog {
    companion object {
        fun progressDialog(context: Context): Dialog {
            val dialog = Dialog(context, R.style.dialog_style)
            val inflate = View.inflate(context, R.layout.simple_progress_dialog, null)
            dialog.setContentView(inflate)
            return dialog
        }
    }
}

class SimpleLoadingHelper: Loading {
    lateinit var dialog: Dialog

    override fun showLoading(context: Context, cancelable: Boolean) {
        if (!::dialog.isInitialized) dialog = ProgressDialog.progressDialog(context)
        dialog.setCancelable(cancelable)
        dialog.show()
    }

    override fun dismissLoading() {
        dialog.dismiss()
    }
}