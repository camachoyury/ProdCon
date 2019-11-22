package com.supay.jallpa.view

import android.content.Context
import android.widget.TextView
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.supay.core.Seller
import com.supay.jallpa.R
import kotlinx.android.synthetic.main.bottom_sheet.*
import android.content.Intent
import android.net.Uri


class ActionBottomDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {

    private var mListener: ItemClickListener? = null
    lateinit var seller: Seller

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        seller = (arguments!!.getSerializable(
            TAG
        ) as Seller?)!!

        editTextProduct.text = seller.product
        editTextName.text = seller.name
        editTextPhone.text = seller.phone
        editTextComments.text = seller.obs

        editTextPhone.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:${Uri.encode(seller.phone)}")
            startActivity(callIntent) }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ItemClickListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement ItemClickListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onClick(view: View) {
        val tvSelected = view as TextView
        mListener!!.onItemClick(tvSelected.text.toString())
        dismiss()
    }

    interface ItemClickListener {
        fun onItemClick(item: String)
    }

    companion object {

        val TAG = "SELLER"

        fun newInstance(seller: Seller) = ActionBottomDialogFragment().apply {
            arguments = Bundle().apply {
                putSerializable(TAG, seller)
            }
        }
    }
}