package com.arnaufugarolas.ac01myfavoritemovies

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.arnaufugarolas.ac01myfavoritemovies.dataClass.Movie

class EditRatingDialog(private val movie: Movie) :
    DialogFragment() {
    private var mCallback: EditRatingListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.edit_rating, container, false)
        val btnOk: Button = view.findViewById(R.id.ButtonSubmitDialog)
        val btnCancel: Button = view.findViewById(R.id.ButtonCancelDialog)
        val editTextData: EditText = view.findViewById(R.id.ETDataDialog)

        editTextData.setText(movie.myScore.toString())

        btnOk.setOnClickListener {
            mCallback?.onEditRating(movie, editTextData.text.toString().toInt())
            this.dismiss()
        }

        btnCancel.setOnClickListener {
            this.dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.9).toInt()

        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mCallback = activity as EditRatingListener
        } catch (e: ClassCastException) {
            Log.e("Dialog", "onAttach: ClassCastException: " + e.message)
        }
    }

}
