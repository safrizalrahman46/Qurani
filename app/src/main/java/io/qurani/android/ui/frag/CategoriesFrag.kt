package io.qurani.android.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.qurani.android.R
import io.qurani.android.databinding.FragCategoriesBinding
import io.qurani.android.manager.App
import io.qurani.android.manager.adapter.CategoriesRvAdapter
import io.qurani.android.manager.adapter.TrendingCategoriesRvAdapter
import io.qurani.android.manager.listener.ItemClickListener
import io.qurani.android.manager.listener.OnItemClickListener
import io.qurani.android.manager.net.observer.NetworkObserverFragment
import io.qurani.android.model.*
import io.qurani.android.model.view.CategoryView
import io.qurani.android.presenter.Presenter
import io.qurani.android.presenterImpl.CategoriesPresenterImpl
import io.qurani.android.ui.MainActivity

class CategoriesFrag : io.qurani.android.manager.net.observer.NetworkObserverFragment(),
    io.qurani.android.manager.listener.OnItemClickListener {

    private lateinit var mBinding: FragCategoriesBinding
    private lateinit var mPresneter: Presenter.CategoriesPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragCategoriesBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.NAV

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.categories)
        (activity as MainActivity).setContainer(false)

        mPresneter = CategoriesPresenterImpl(this)
        mPresneter.getTrendingCategories()
        mPresneter.getCategories()
    }

    fun onTrendingCategoriesRecevied(data: Data<Count<Category>>) {
        val items = data.data!!.items
        mBinding.categoriesTrendingProgressBar.visibility = View.GONE

        val linearLayoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

        val adapter = TrendingCategoriesRvAdapter(items, requireActivity().windowManager)
        mBinding.categoriesTrendingRv.layoutManager = linearLayoutManager
        mBinding.categoriesTrendingRv.adapter = adapter
        mBinding.categoriesTrendingRv.addOnItemTouchListener(
            io.qurani.android.manager.listener.ItemClickListener(
                mBinding.categoriesTrendingRv,
                this
            )
        )
    }

    fun onCategoriesRecevied(data: Data<Count<Category>>) {
        val items = data.data!!.items
        mBinding.categoriesProgressBar.visibility = View.GONE

        val groups = ArrayList<CategoryView>()

        for (item in items) {
            item.subCategories.let {
                val categoryView =
                    CategoryView(item.title, it)
                categoryView.catTitle = item.title
                categoryView.count = item.count
                categoryView.icon = item.icon
                categoryView.id = item.id
                groups.add(categoryView)
            }
        }

        mBinding.categoriesExpandableRv.adapter =
            CategoriesRvAdapter(groups, activity as MainActivity)
    }

    override fun onClick(view: View?, position: Int, id: Int) {
        val category =
            (mBinding.categoriesTrendingRv.adapter as TrendingCategoriesRvAdapter).items[position] as Category

        val bundle = Bundle()
        bundle.putParcelable(io.qurani.android.manager.App.CATEGORY, category)

        val categoryFrag = CategoryFrag()
        categoryFrag.arguments = bundle

        (activity as MainActivity).transact(categoryFrag)
    }

    override fun onLongClick(view: View?, position: Int, id: Int) {
    }
}