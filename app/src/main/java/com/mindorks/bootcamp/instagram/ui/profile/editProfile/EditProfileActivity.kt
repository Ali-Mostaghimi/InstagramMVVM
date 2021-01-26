package com.mindorks.bootcamp.instagram.ui.profile.editProfile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.di.component.ActivityComponent
import com.mindorks.bootcamp.instagram.ui.base.BaseActivity
import com.mindorks.bootcamp.instagram.ui.photo.PhotoFragment
import com.mindorks.bootcamp.instagram.utils.common.GlideHelper
import com.mindorks.paracamera.Camera
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.io.FileNotFoundException
import java.lang.Exception
import javax.inject.Inject

class EditProfileActivity : BaseActivity<EditProfileViewModel>(),
    ChangePhotoBottomSheetDialog.ChangePhotoBottomSheetListener {
    companion object {
        const val REQUEST_GALLERY_IMG = 1001
        const val TAG = "EditProfileActivity"
        const val BottomSheet_TAG = "ChangePhotoBottomSheet"
        const val MYINFO_NAME_PARAM = "name"
        const val MYINFO_TAGLINE_PARAM = "tagline"
        const val MYINFO_PROFILE_PIC_URL_PARAM = "profilePicUrl"
    }

    @Inject
    lateinit var camera: Camera

    override fun provideLayoutId(): Int = R.layout.activity_edit_profile

    override fun injectDependencies(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun setupView(savedInstanceState: Bundle?) {
        editProfile_tv_changePhoto.setOnClickListener {
            ChangePhotoBottomSheetDialog().apply {
                show(supportFragmentManager, BottomSheet_TAG)
            }
        }

        editProfile_et_name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onNameChange(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        editProfile_et_bio.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onTaglineChange(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        editProfile_toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.itemConfirm -> {
                    it.setActionView(R.layout.progress_wheel)
                    viewModel.updateMyInfo()
                    true
                }
                else -> false
            }
        }

        editProfile_toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.loadingPage.observe(this, Observer {
            editProfile_pb_pageLoading.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.showContent.observe(this, Observer {
            editProfile_content_container.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.nameField.observe(this, Observer {
            if (it != editProfile_et_name.text.toString()) editProfile_et_name.setText(it)
        })

        viewModel.taglineField.observe(this, Observer {
            if (it != editProfile_et_bio.text.toString()) editProfile_et_bio.setText(it)
        })

        viewModel.imageProfile.observe(this, Observer {
            viewModel.loadingProfile.postValue(true)
            it?.run {
                if (this.url.isNotEmpty()) {
                    val glideRequest = Glide
                        .with(editProfile_iv_profile.context)
                        .load(GlideHelper.getProtectedUrl(url, headers))
                        .apply(RequestOptions.circleCropTransform())
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_profile_placeholder))

                    if (placeholderWidth > 0 && placeholderHeight > 0) {
                        val params = editProfile_iv_profile.layoutParams as ViewGroup.LayoutParams
                        params.width = placeholderWidth
                        params.height = placeholderHeight
                        editProfile_iv_profile.layoutParams = params
                        glideRequest
                            .apply(RequestOptions.overrideOf(placeholderWidth, placeholderHeight))
                            .apply(RequestOptions.placeholderOf(R.drawable.ic_profile_placeholder))
                    }
                    glideRequest.into(editProfile_iv_profile)
                }
            }
            viewModel.loadingProfile.postValue(false)
        })

        viewModel.updatedMyInfo.observe(this, Observer {
            val intent = Intent().apply {
                putExtra(MYINFO_NAME_PARAM, it.name)
                putExtra(MYINFO_PROFILE_PIC_URL_PARAM, it.profilePicUrl)
                putExtra(MYINFO_TAGLINE_PARAM, it.tagline)
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        })

        viewModel.myInfo.observe(this, Observer {
            viewModel.onFetchedMyInfo(it)
        })

        viewModel.email.observe(this, Observer {
            editProfile_et_email.setText(it)
        })

        viewModel.loadingProfile.observe(this, Observer {
            editProfile_pb_imageLoading.visibility = if (it) View.VISIBLE else View.GONE
        })

    }

    override fun onButtonClicked(option: ChangePhotoBottomSheetDialog.ChangePhotoOptions) {
        when (option) {
            ChangePhotoBottomSheetDialog.ChangePhotoOptions.CAMERA -> {
                try {
                    camera.takePicture()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            ChangePhotoBottomSheetDialog.ChangePhotoOptions.GALLERY -> {
                Intent(Intent.ACTION_PICK)
                    .apply {
                        type = "image/*"
                    }.run {
                        startActivityForResult(this, REQUEST_GALLERY_IMG)
                    }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PhotoFragment.REQUEST_GALLERY_IMG -> {
                    try {
                        data?.data?.let {
                            contentResolver?.openInputStream(it)?.run {
                                viewModel.onGalleryImageSelected(this)
                            }
                        } ?: showMessage(R.string.try_again)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                        showMessage(R.string.try_again)
                    }
                }
                Camera.REQUEST_TAKE_PHOTO -> {
                    viewModel.onCameraImageTaken { camera.cameraBitmapPath }
                }
            }
        }
    }
}