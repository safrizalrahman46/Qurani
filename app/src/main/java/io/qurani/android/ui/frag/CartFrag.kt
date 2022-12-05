package io.qurani.android.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import io.qurani.android.R
import io.qurani.android.databinding.EmptyStateBinding
import io.qurani.android.databinding.FragCartBinding
import io.qurani.android.manager.App
import io.qurani.android.manager.ToastMaker
import io.qurani.android.manager.Utils
import io.qurani.android.manager.adapter.CartRvAdapter
import io.qurani.android.manager.listener.ItemCallback
import io.qurani.android.manager.net.observer.NetworkObserverFragment
import io.qurani.android.model.*
import io.qurani.android.model.view.PaymentRedirection
import io.qurani.android.presenter.Presenter
import io.qurani.android.presenterImpl.CartPresenterImpl
import io.qurani.android.ui.MainActivity
import io.qurani.android.ui.PaymentStatusActivity
import io.qurani.android.ui.frag.abs.EmptyState
import io.qurani.android.ui.widget.CouponDialog
import io.qurani.android.ui.widget.LoadingDialog
import java.lang.StringBuilder

class CartFrag : NetworkObserverFragment(), View.OnClickListener, ItemCallback<CouponValidation>,
    EmptyState, MainActivity.OnRefreshListener {

    private lateinit var mBinding: FragCartBinding
    private lateinit var mPresenter: Presenter.CartPresenter
    private lateinit var mLoadingDialog: LoadingDialog
    private lateinit var mAmounts: Amounts
    private var mCouponValidation: CouponValidation? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragCartBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.BACK

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.cart)
        initData()
    }

    private fun initData() {
        if (!App.isLoggedIn()) {
            onCartReceived(null)
            return
        }

        mLoadingDialog = LoadingDialog.instance
        mLoadingDialog.show(childFragmentManager, null)

        mPresenter = CartPresenterImpl(this)
        mPresenter.getCart()
    }

    fun onCartReceived(cart: Cart?) {
if (this::mLoadingDialog.isInitialized) {
            mLoadingDialog.dismiss()
        }

        if (cart != null) {

            mAmounts = cart.amounts

            initInfo(cart.amounts)

            mBinding.cartCheckoutBtn.isEnabled = cart.cartItems.isNotEmpty()

            if (cart.userGroup != null) {
                initUserGroup(cart)
            }

            initCartItems(cart.cartItems)

            mBinding.cartCheckoutBtn.setOnClickListener(this)
            mBinding.cartAddCouponBtn.setOnClickListener(this)
            mBinding.cartInfoContainer.visibility = View.VISIBLE
        } else {
            mBinding.cartInfoContainer.visibility = View.GONE
            mBinding.cartUserGroupContainer.root.visibility = View.GONE
            mBinding.cartItemsRv.visibility = View.GONE
            if (!App.isLoggedIn()) {
                showLoginState()
                return
            }
            showEmptyState()
        }
    }

    private fun initCartItems(cartItems: List<CartItem>) {
        mBinding.cartItemsRv.adapter = CartRvAdapter(cartItems, this)
    }

    private fun initInfo(amounts: Amounts) {
        mBinding.cartSubtotalValueTv.text =
            Utils.formatPrice(requireContext(), amounts.subTotal, false)
        mBinding.cartDiscountValueTv.text =
            Utils.formatPrice(requireContext(), amounts.totalDiscount, false)
        mBinding.cartTaxValueTv.text = Utils.formatPrice(requireContext(), amounts.taxPrice, false)
        mBinding.cartTotalValueTv.text = Utils.formatPrice(requireContext(), amounts.total)

        mBinding.cartTaxKeyTv.text = ("${getString(R.string.tax)} (${amounts.tax}%)")
    }

    private fun initUserGroup(cart: Cart) {
        val cartUserGroupContainer = mBinding.cartUserGroupContainer
        cartUserGroupContainer.HeaderInfoImg.setImageResource(R.drawable.ic_discount)
        cartUserGroupContainer.HeaderInfoTitleTv.text =
            ("${cart.userGroup!!.discount}% ${getString(R.string.user_group_discount)}")
        cartUserGroupContainer.HeaderInfoDescTv.text = StringBuilder(
            "${getString(R.string.you_are_part_of)} \"${cart.userGroup!!.name}\" ${
                getString(
                    R.string.user_group
                )
            }"
        ).toString()

        val params = mBinding.cartItemsRv.layoutParams as ConstraintLayout.LayoutParams
        params.bottomToTop = R.id.cart_user_group_container
        mBinding.cartItemsRv.requestLayout()

        mBinding.cartUserGroupContainer.root.visibility = View.VISIBLE
    }

    fun onCartRemoved(response: BaseResponse, position: Int) {
        if (context == null) return

        if (response.isSuccessful) {
            val adapter = mBinding.cartItemsRv.adapter as CartRvAdapter
            adapter.items.removeAt(position)
            adapter.notifyItemRemoved(position)

            if (adapter.itemCount == 0) {
                onCartReceived(null)
            }

        } else {
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                response.message,
                ToastMaker.Type.ERROR
            )
        }
    }

    fun removeItem(id: Int, position: Int) {
        mPresenter.removeFromCart(id, position)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cart_add_coupon_btn -> {
                val dialog = CouponDialog()
                dialog.setOnCouponAdded(this)
                dialog.show(childFragmentManager, null)
            }

            R.id.cart_checkout_btn -> {
                mLoadingDialog = LoadingDialog.instance
                mLoadingDialog.show(childFragmentManager, null)

                mPresenter.checkout(mCouponValidation?.coupon)
            }
        }
    }

    fun onRequestFailed() {
        mLoadingDialog.dismiss()
    }

    override fun onItem(item: CouponValidation, vararg args: Any) {
        mAmounts = item.amounts

        item.coupon.discountId = item.coupon.id
        mCouponValidation = item

        initInfo(item.amounts)
    }

    fun onCheckout(data: Data<Response>) {
        mLoadingDialog.dismiss()

        if (data.isSuccessful && !data.data!!.link.isNullOrEmpty()) {
//            val bundle = Bundle()
//            bundle.putParcelable(App.ORDER, data.data!!)
//
//            if (mAmounts.total == 0.0) {
//                startActivity(Intent(requireContext(), MainActivity::class.java))
//                activity?.finish()
//
//            } else {
            val redirection = PaymentRedirection()
            redirection.isNavDrawer = true
            redirection.position = MainActivity.SlideMenuItem.DASHBOARD.value()
            redirection.buttonTitle = getString(R.string.dashboard)
            PaymentStatusActivity.paymentRedirection = redirection
//
//                bundle.putParcelable(App.REDIRECTION, redirection)
//
//                val frag = ChargeAccountPaymentFrag()
//                frag.arguments = bundle
//                (activity as MainActivity).transact(frag)
//            }

            Utils.openLink(requireContext(), data.data!!.link)

        } else {
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                data.message,
                ToastMaker.Type.ERROR
            )
        }
    }

    fun showEmptyState() {
        showEmptyState(R.drawable.no_cart, R.string.no_cart, R.string.no_cart_desc)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.cartEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }

    override fun getRefreshListener(): MainActivity.OnRefreshListener? {
        return this
    }

    override fun refresh() {
        hideEmptyState()
        initData()
    }
}