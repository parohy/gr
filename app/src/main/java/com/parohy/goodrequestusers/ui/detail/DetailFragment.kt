package com.parohy.goodrequestusers.ui.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.distinctUntilChanged
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.parohy.goodrequestusers.R
import com.parohy.goodrequestusers.api.model.User
import com.parohy.goodrequestusers.common.BaseFragment
import com.parohy.goodrequestusers.diComponent
import com.parohy.goodrequestusers.pattern.withFactory
import kotlinx.android.synthetic.main.error_layout.*
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.loading_view.*
import javax.inject.Inject

class DetailFragment: BaseFragment() {
    override val fragmentLayout: Int = R.layout.fragment_detail
    private val args: DetailFragmentArgs by navArgs()

    @Inject
    lateinit var factory: DetailViewModel.Factory
    private val vm: DetailViewModel by viewModels { withFactory(factory) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        detailGroup.setOnRefreshListener {
            if (!detailGroup.isRefreshing)
                vm.loadUser(args.userId)
        }

        errorRetry.setOnClickListener { vm.retry() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        requireActivity().application.diComponent().injectDetailFragment(this)

        vm.state.distinctUntilChanged().observe(viewLifecycleOwner, Observer { state ->
            errorGroup.visibility = View.GONE
            loadingView.visibility = View.GONE

            if (state.loading) {
                loadingView.visibility = View.VISIBLE
                return@Observer
            }

            detailGroup.isRefreshing = false

            showSilentError(state.silentError, getString(R.string.retry)) { vm.retry() }

            if (!showError(state.error)) {
                detailGroup.visibility = View.VISIBLE
                updateData(state.data)
            }
        })

        vm.loadUser(args.userId)
    }

    private fun updateData(user: User?) {
        if (user == null) return

        Glide.with(this)
            .load(user.avatar)
            .into(detailAvatar)

        detailId.text = user.id.toString()
        detailName.text = getString(R.string.user_name_format, user.name, user.surname)
        detailEmail.text = user.email
    }

    private fun showError(throwable: Throwable?): Boolean {
        if (throwable != null) {
            errorGroup.visibility = View.VISIBLE
            detailGroup.visibility = View.INVISIBLE

            errorMessage.text = throwable.message ?: "Unknown error"
            return true
        }

        return false
    }

    private fun showSilentError(
        throwable: Throwable?,
        actionName: String? = null,
        action: (() -> Unit)? = null
    ) {
        if (throwable != null) {
            val snackbar =
                Snackbar.make(
                    requireView(),
                    throwable.message ?: "Unknown error",
                    Snackbar.LENGTH_LONG
                )
            if (!actionName.isNullOrEmpty() && action != null)
                snackbar.setAction(actionName) { action.invoke() }
            snackbar.show()
        }
    }
}