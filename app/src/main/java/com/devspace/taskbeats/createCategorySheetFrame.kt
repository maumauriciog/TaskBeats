package com.devspace.taskbeats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText

class createCategorySheetFrame(private val onClicked: (String) -> Unit): BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.create_category_botton_frame, container, false)
        val tv_titleCat = view.findViewById<TextView>(R.id.tv_TitleNewCategory)
        val iv_writeCat = view.findViewById<TextInputEditText>(R.id.iv_writeCat)
        val bt_createCat = view.findViewById<Button>(R.id.bv_create)

        bt_createCat.setOnClickListener{
            val name = iv_writeCat.text.toString()
            onClicked.invoke(name)
            dismiss()
        }
        return view
    }
}