package com.zeal.paymentassignment.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.zeal.paymentassignment.R
import com.zeal.paymentassignment.core.FlowDataObject
import com.zeal.paymentassignment.databinding.FragmentPrintReceiptBinding

class PrintReceiptFragment : Fragment() {
    val binding by lazy {
        FragmentPrintReceiptBinding.inflate(layoutInflater)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvAmountBeforeDiscountValue.text = FlowDataObject.getInstance().amount.toString()
        binding.tvDiscountValue.text = FlowDataObject.getInstance().discount.toString()
        binding.tvAmountAfterDiscountValue.text = FlowDataObject.getInstance().amountAfterDiscount.toString()

        binding.btnPrint.setOnClickListener {}

        binding.btnGoGreen.setOnClickListener {
            findNavController().navigate(R.id.action_printReceiptFragment_to_mainMenuFragment)
        }
    }

}