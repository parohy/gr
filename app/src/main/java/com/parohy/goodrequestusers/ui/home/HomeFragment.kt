package com.parohy.goodrequestusers.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.parohy.goodrequestusers.common.BaseFragment
import com.parohy.goodrequestusers.R
import com.parohy.goodrequestusers.api.model.User
import com.parohy.goodrequestusers.diComponent
import com.parohy.goodrequestusers.ui.home.adapter.OnItemClickListener
import com.parohy.goodrequestusers.ui.home.adapter.UserListAdapter
import com.parohy.goodrequestusers.pattern.withFactory
import com.parohy.goodrequestusers.ui.detail.DetailFragmentArgs
import kotlinx.android.synthetic.main.error_layout.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.loading_view.*
import javax.inject.Inject

class HomeFragment: BaseFragment(), OnItemClickListener<User> {
    companion object {
        private const val TAG = "HomeFragment"
    }

    override val fragmentLayout: Int = R.layout.fragment_home

    private val adapter: UserListAdapter by lazy {
        UserListAdapter(requireContext(), this)
    }

    @Inject
    lateinit var vmFactory: HomeViewModel.Factory
    private val vm: HomeViewModel by viewModels { withFactory(vmFactory) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().application.diComponent().injectHomeFragment(this)

        val llm = LinearLayoutManager(requireContext())
        homeUserList.layoutManager = llm
        homeUserList.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val lastIndex = llm.findLastVisibleItemPosition()
                if (lastIndex >= 0 && lastIndex == adapter.itemCount - 1)
                    vm.loadPage()
            }
        })

        homePullRefresh.setOnRefreshListener { vm.refresh() }

        errorRetry.setOnClickListener { vm.retry() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        vm.state.observe(viewLifecycleOwner, Observer { state ->
            errorGroup.visibility = View.GONE
            loadingView.visibility = View.GONE

            if (state.loading) {
                loadingView.visibility = View.VISIBLE
                homeUserList.visibility = View.INVISIBLE
                return@Observer
            }

            homePullRefresh.isRefreshing = false

            if (!showError(state.error)) {
                homeUserList.visibility = View.VISIBLE
                updateData(state)
            }

            showSilentError(state.silentError)
        })
    }

    private fun showError(throwable: Throwable?): Boolean {
        if (throwable != null) {
            errorGroup.visibility = View.VISIBLE
            homeUserList.visibility = View.INVISIBLE
            errorMessage.text = throwable.message ?: "Unknown error"
            Log.e(TAG, errorMessage.text.toString())
            return true
        }
        return false
    }

    private fun showSilentError(throwable: Throwable?) {
        if (throwable != null) {
            Snackbar.make(requireView(), throwable.message ?: "Unknown error", Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.retry)) { vm.retry() }
                .show()
            Log.e(TAG, throwable.toString())
        }
    }

    private fun updateData(state: HomeViewState) {
        homeUserList.visibility = View.VISIBLE

        if (!homeUserList.hasAdapter())
            homeUserList.adapter = adapter


        if (state.refresh)
            adapter.updateData(state.data)
        else
            adapter.insertData(state.data)
    }

    override fun onClick(item: User) {
        findNavController().navigate(R.id.toDetail, DetailFragmentArgs(item.id).toBundle())
    }

    private fun RecyclerView.hasAdapter(): Boolean = adapter != null
    private fun RecyclerView.hasLayoutManager(): Boolean = layoutManager != null
}