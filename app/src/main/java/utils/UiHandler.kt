package utils

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

object UiHandler {

    fun showLoading(isLoading: Boolean, progressBar: ProgressBar, recyclerView: RecyclerView) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    fun handleError(
        isError: Boolean,
        message: String?,
        errorTextView: TextView,
        refreshButton: MaterialButton,
        recyclerView: RecyclerView,
        onRetry: () -> Unit
    ) {
        if (isError) {
            errorTextView.visibility = View.VISIBLE
            errorTextView.text = message ?: "Terjadi kesalahan"
            refreshButton.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE // Sembunyikan RecyclerView saat ada kesalahan
            refreshButton.setOnClickListener {
                onRetry()
            }
        } else {
            errorTextView.visibility = View.GONE
            refreshButton.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE // Tampilkan RecyclerView jika tidak ada kesalahan
        }
    }
}