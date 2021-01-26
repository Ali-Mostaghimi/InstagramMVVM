package com.mindorks.bootcamp.instagram.ui.profile.editProfile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mindorks.bootcamp.instagram.R
import kotlinx.android.synthetic.main.fragment_changephoto.*
import java.lang.ClassCastException

class ChangePhotoBottomSheetDialog : BottomSheetDialogFragment() {
    lateinit var mListener: ChangePhotoBottomSheetListener

    enum class ChangePhotoOptions {
        GALLERY, CAMERA
    }

    interface ChangePhotoBottomSheetListener {
        fun onButtonClicked(option: ChangePhotoOptions)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as ChangePhotoBottomSheetListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement ChangePhotoBottomSheetListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_changephoto, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changePhoto_view_camera.setOnClickListener {
            mListener?.let { it.onButtonClicked(ChangePhotoOptions.CAMERA) }
            dismiss()
        }
        changePhoto_view_gallery.setOnClickListener {
            mListener?.let { it.onButtonClicked(ChangePhotoOptions.GALLERY) }
            dismiss()
        }
    }
}