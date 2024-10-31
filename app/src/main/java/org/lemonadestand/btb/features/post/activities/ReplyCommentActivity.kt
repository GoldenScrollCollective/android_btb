package org.lemonadestand.btb.features.post.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.R
import org.lemonadestand.btb.utils.Utils
import org.lemonadestand.btb.databinding.ActivityReplyCommentBinding
import org.lemonadestand.btb.constants.handleCommonResponse
import org.lemonadestand.btb.features.common.models.body.AddCommentBody
import org.lemonadestand.btb.features.common.models.body.ShareStoryUser
import org.lemonadestand.btb.features.login.models.User
import org.lemonadestand.btb.features.post.models.Post
import org.lemonadestand.btb.mvvm.factory.CommonViewModelFactory
import org.lemonadestand.btb.mvvm.repository.HomeRepository
import org.lemonadestand.btb.mvvm.viewmodel.HomeViewModel
import org.lemonadestand.btb.singleton.Singleton

class ReplyCommentActivity : AppCompatActivity() {

    lateinit var viewModel: HomeViewModel
    private lateinit var mBinding: ActivityReplyCommentBinding
    private var currentUser: User? = null

    private var post: Post? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reply_comment)
        mBinding = ActivityReplyCommentBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        getData()
        setOnClicks()
        setUpViewModel()
    }

    private fun getData() {
        val bundle = intent.extras
        if (bundle != null) {
            val jsonData = bundle.getString("reply_data")
            val gson = Gson()
            post = gson.fromJson(jsonData, Post::class.java)

            Log.e("replayModel", post.toString())
        }
        currentUser = Utils.getUser(this)
    }

    private fun setOnClicks() {
        mBinding.icBack.setOnClickListener { onBackPressed() }

        mBinding.btnSave.setOnClickListener {

            if (mBinding.htmlEditor.text.toString()
                    .isEmpty() || mBinding.htmlEditor.text.toString() == "<p><br></p>"
            ) {
                Toast.makeText(this, "Please write a message.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val requestBody = AddCommentBody(
                uniq_id = "",
                resource = "user/${currentUser!!.uniqId}",
                html = mBinding.htmlEditor.text,
                created = "",
                parent_id = "${post?.uniq_id}",
                modified = "",
                by_user_id = "",

                user = ShareStoryUser(id = "", name = "")
            )
            viewModel.addComment(requestBody)
        }
    }

    private fun setUpViewModel() {

        val repository = HomeRepository()
        val viewModelProviderFactory =
            CommonViewModelFactory((this).application, repository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[HomeViewModel::class.java]


        viewModel.liveError.observe(this) {
            Singleton.handleResponse(response = it, this, "ReplyCommentActivity")
            ProgressDialogUtil.dismissProgressDialog()
        }

        viewModel.commonResponse.observe(this) {
            handleCommonResponse(this, it)
            ProgressDialogUtil.dismissProgressDialog()
            finish()
        }


        viewModel.isLoading.observe(this) {
            Log.e("value==>", it.toString())
            if (it) {
                ProgressDialogUtil.showProgressDialog(this)
            } else {
                ProgressDialogUtil.dismissProgressDialog()
            }
        }

        viewModel.noInternet.observe(this) {
            Toast.makeText(this, " $it", Toast.LENGTH_SHORT).show()
            ProgressDialogUtil.dismissProgressDialog()
        }
    }
}