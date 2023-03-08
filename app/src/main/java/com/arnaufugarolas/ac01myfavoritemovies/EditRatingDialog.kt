package com.arnaufugarolas.ac01myfavoritemovies

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.arnaufugarolas.ac01myfavoritemovies.dataClass.Movie
import com.arnaufugarolas.ac01myfavoritemovies.databinding.EditRatingBinding

class EditRatingDialog(private val movie: Movie) :
    DialogFragment() {
    private var mCallback: EditRatingListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = EditRatingBinding.inflate(inflater, container, false)

        binding.ETDataDialog.setText(movie.myScore.toString())

        binding.ButtonSubmitDialog.setOnClickListener {
            val rating = binding.ETDataDialog.text.toString().toInt()
            (activity as EditRatingListener).onEditRating(movie, rating)
            this.dismiss()
        }

        binding.ButtonCancelDialog.setOnClickListener {
            this.dismiss()
        }

        return binding.root.rootView
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
