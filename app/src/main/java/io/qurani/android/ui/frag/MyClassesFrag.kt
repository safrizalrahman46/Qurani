package io.qurani.android.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.qurani.android.R
import io.qurani.android.databinding.EmptyStateBinding
import io.qurani.android.databinding.RvBinding
import io.qurani.android.manager.App
import io.qurani.android.manager.ToastMaker
import io.qurani.android.manager.Utils
import io.qurani.android.manager.adapter.MyClassesRvAdapter
import io.qurani.android.manager.listener.ItemClickListener
import io.qurani.android.manager.listener.OnItemClickListener
import io.qurani.android.manager.net.observer.NetworkObserverFragment
import io.qurani.android.model.Course
import io.qurani.android.model.MyClasses
import io.qurani.android.presenterImpl.MyClassesPresenterImpl
import io.qurani.android.ui.MainActivity
import io.qurani.android.ui.frag.abs.EmptyState
import java.io.Serializable
import java.lang.StringBuilder

class MyClassesFrag : io.qurani.android.manager.net.observer.NetworkObserverFragment(),
    io.qurani.android.manager.listener.OnItemClickListener, EmptyState,
    MainActivity.OnRefreshListener {

    private lateinit var mBinding: RvBinding
    private lateinit var mType: Type

    enum class Type : Serializable {
        PURCHASED,
        MY_CLASSES,
        ORGANIZATION,
        INVITED
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = RvBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mBinding.rvContainer.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )

        if (!io.qurani.android.manager.App.isLoggedIn()) {
            showLoginState()
            return
        }

        mBinding.rvEmptyState.root.visibility = View.INVISIBLE
        mType = requireArguments().getSerializable(io.qurani.android.manager.App.SELECTION_TYPE) as Type

        val presenter = MyClassesPresenterImpl(this)
        if (io.qurani.android.manager.App.loggedInUser!!.isUser()) {
            if (mType == Type.PURCHASED) {
                presenter.getPurchased()
            } else {
                presenter.getOrganizations()
            }

        } else {
            presenter.getMyClasses()
        }
    }

    fun onPurchasedReceived(items: List<Course>, purchased: Boolean) {
        mBinding.rvProgressBar.visibility = View.INVISIBLE
        if (items.isNotEmpty()) {
            mBinding.rv.layoutManager = LinearLayoutManager(requireContext())
            mBinding.rv.adapter = MyClassesRvAdapter(items, purchased)
            mBinding.rv.addOnItemTouchListener(
                io.qurani.android.manager.listener.ItemClickListener(
                    mBinding.rv,
                    this
                )
            )
        } else {
            showEmptyState()
        }
    }

    fun onMyClassesReceived(myClasses: MyClasses) {
        when (mType) {
            Type.PURCHASED -> {
                if (myClasses.purchases.isNotEmpty()) {
                    onPurchasedReceived(myClasses.purchases, true)
                } else {
                    showEmptyState()
                }
            }

            Type.ORGANIZATION -> {
                if (myClasses.organizations.isNotEmpty()) {
                    onPurchasedReceived(myClasses.organizations, false)
                } else {
                    showEmptyState()
                }
            }

            Type.MY_CLASSES -> {
                if (myClasses.myClasses.isNotEmpty()) {
                    onPurchasedReceived(myClasses.myClasses, false)
                } else {
                    showEmptyState()
                }
            }

            Type.INVITED -> {
                if (myClasses.invitations.isNotEmpty()) {
                    onPurchasedReceived(myClasses.invitations, false)
                } else {
                    showEmptyState()
                }
            }
        }
    }

    override fun onClick(view: View?, position: Int, id: Int) {
        val course = (mBinding.rv.adapter as MyClassesRvAdapter).items[position]

        if (course.expired) {
            val builder = StringBuilder()
                .append(getString(R.string.your_access_expired_on))
                .append(" ")
                .append(Utils.getDateFromTimestamp(course.expiresOn!!))

            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                builder.toString(),
                ToastMaker.Type.ERROR
            )
            return
        }

        val bundle = Bundle()
        bundle.putParcelable(io.qurani.android.manager.App.COURSE, course)

        val frag: Fragment = if (mType == Type.PURCHASED) {
            CourseDetailsFrag()
        } else {
            CourseOverviewFrag()
        }

        frag.arguments = bundle
        (activity as MainActivity).transact(frag)
    }

    override fun onLongClick(view: View?, position: Int, id: Int) {
    }

    fun showEmptyState() {
        var desc = 0

        when (mType) {
            Type.PURCHASED -> {
                desc = R.string.purchase_no_courses
            }

            Type.ORGANIZATION -> {
                desc = R.string.no_courses_organization
            }

            Type.MY_CLASSES -> {
                desc = R.string.no_courses_class
            }

            Type.INVITED -> {
                desc = R.string.no_courses_invited
            }
        }

        showEmptyState(R.drawable.no_course, R.string.no_courses, desc)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.rvEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }

    override fun getRefreshListener(): MainActivity.OnRefreshListener? {
        return this
    }

    override fun refresh() {
        hideEmptyState()
        init()
    }
}