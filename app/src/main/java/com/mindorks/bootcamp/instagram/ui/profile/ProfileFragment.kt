package com.mindorks.bootcamp.instagram.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.di.component.FragmentComponent
import com.mindorks.bootcamp.instagram.ui.base.BaseFragment
import com.mindorks.bootcamp.instagram.ui.login.LoginActivity
import com.mindorks.bootcamp.instagram.ui.photo.PhotoViewModel
import com.mindorks.bootcamp.instagram.ui.profile.editProfile.EditProfileActivity
import com.mindorks.bootcamp.instagram.utils.common.GlideHelper
import com.mindorks.bootcamp.instagram.utils.common.Status
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.item_view_post.view.*

class ProfileFragment : BaseFragment<ProfileViewModel>() {

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
        profile_tv_editProfile.setOnClickListener {
            viewModel.onEditProfile()
        }

        profile_tv_logout.setOnClickListener {
            viewModel.onLogout()
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.loading.observe(this, Observer {
            profile_pb_loading.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.loggedOut.observe(this, Observer {
            if (it.status == Status.SUCCESS) {
                startActivity(Intent(activity, LoginActivity::class.java))
                activity?.finish()
            }
        })

        viewModel.lunchEditProfile.observe(this, Observer {
            startActivity(Intent(activity, EditProfileActivity::class.java))
        })

        viewModel.name.observe(this, Observer {
            profile_tv_name.text = it
        })

        viewModel.tagline.observe(this, Observer {
            profile_tv_bio.text = it
        })

        viewModel.profileImage.observe(this, Observer {
            it?.run {
                val glideRequest = Glide
                    .with(profile_iv_profile.context)
                    .load(GlideHelper.getProtectedUrl(url, headers))
                    .apply(RequestOptions.circleCropTransform())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_profile_placeholder))

                if (placeholderWidth > 0 && placeholderHeight > 0) {
                    val params = profile_iv_profile.layoutParams as ViewGroup.LayoutParams
                    params.width = placeholderWidth
                    params.height = placeholderHeight
                    profile_iv_profile.layoutParams = params
                    glideRequest
                        .apply(RequestOptions.overrideOf(placeholderWidth, placeholderHeight))
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_photo))
                }
                glideRequest.into(profile_iv_profile)
            }
        })


    }
}