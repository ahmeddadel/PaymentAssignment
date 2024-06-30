package com.zeal.paymentassignment.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager.MATCH_DEFAULT_ONLY
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.zeal.paymentassignment.R
import com.zeal.paymentassignment.core.DialogHelper
import com.zeal.paymentassignment.core.FlowDataObject
import com.zeal.paymentassignment.databinding.FragmentSwipeFragment2Binding

class SwipeCardFragment : Fragment() {

    companion object {
        private const val ACTION_APPLY_DISCOUNT =
            "com.zeal.ACTION_APPLY_DISCOUNT" // Action to apply discount
        private const val TRANSACTION_AMOUNT = "transaction_amount" // Transaction amount
        private const val CARD_ID = "card_id" // Card ID
        private const val DISCOUNT_AMOUNT = "discount_amount" // Discount amount
        private const val DISCOUNT_CATEGORY = "com.zeal.loyaltyapplication" // Discount category
    }

    private val binding by lazy {
        FragmentSwipeFragment2Binding.inflate(layoutInflater)
    }

    private lateinit var reqResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        DialogHelper.showPanDialog(requireContext(), { cardId ->
            FlowDataObject.getInstance().pan = cardId
            val intent = Intent(ACTION_APPLY_DISCOUNT)
            intent.addCategory(DISCOUNT_CATEGORY)
            intent.putExtra(TRANSACTION_AMOUNT, FlowDataObject.getInstance().amount)
            intent.putExtra(CARD_ID, cardId)
            if (requireActivity().packageManager.resolveActivity(
                    intent,
                    MATCH_DEFAULT_ONLY
                ) != null
            ) { // Check if discount application is available
                reqResultLauncher.launch(intent)
            } else {
                onError("No discount application found")
            }

        }) {
            onError("Operation Cancelled")
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reqResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    if (result.data != null) {
                        val discountAmount: Float =
                            result.data!!.getFloatExtra(DISCOUNT_AMOUNT, 0.0f)
                        val transactionAmountAfterDiscount: Float =
                            result.data!!.getFloatExtra(TRANSACTION_AMOUNT, 0.0f)

                        FlowDataObject.getInstance().discount = discountAmount
                        FlowDataObject.getInstance().amountAfterDiscount =
                            transactionAmountAfterDiscount

                        if (transactionAmountAfterDiscount > 0) {
                            contactBank()
                        } else {
                            findNavController().navigate(R.id.action_swipeCardFragment_to_printReceiptFragment)
                        }
                    } else {
                        onError("No data received")
                    }
                } else {
                    onError("Loyalty application failed")
                }
            }
    }

    private fun contactBank() {
        Thread {
            DialogHelper.showLoadingDialog(requireActivity(), "Sending Transaction to The Bank\n(${FlowDataObject.getInstance().amountAfterDiscount} USD)")
            Thread.sleep(2000)
            DialogHelper.showLoadingDialog(requireActivity(), "Receiving Bank Response")
            Thread.sleep(1000)
            DialogHelper.hideLoading(requireActivity())

            requireActivity().runOnUiThread {
                findNavController().navigate(R.id.action_swipeCardFragment_to_printReceiptFragment)
            }
        }.start()
    }

    private fun onError(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_swipeCardFragment_to_mainMenuFragment)
    }
}