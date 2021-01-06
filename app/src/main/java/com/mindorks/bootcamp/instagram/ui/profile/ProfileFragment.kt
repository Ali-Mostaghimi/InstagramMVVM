package com.mindorks.bootcamp.instagram.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.di.component.FragmentComponent
import com.mindorks.bootcamp.instagram.ui.base.BaseFragment
import com.mindorks.bootcamp.instagram.ui.photo.PhotoViewModel
import com.mindorks.bootcamp.instagram.ui.profile.editProfile.EditProfileActivity
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment: BaseFragment<PhotoViewModel>() {

    companion object {
        const val TAG = "ProfileFragment"

        fun newInstance(): ProfileFragment {
            val args = Bundle()
            val fragment = ProfileFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_profile

    override fun injectDependencies(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }

    override fun setupView(view: View) {
        profile_tv_editProfile.setOnClickListener{
            startActivity(Intent(activity, EditProfileActivity::class.java))
        }
    }
}