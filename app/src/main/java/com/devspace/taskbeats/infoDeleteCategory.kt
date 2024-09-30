package com.devspace.taskbeats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class infoDeleteCategory(
    private val title: String,
    private val description: String,
    private val btnText: String,
    private val onClicked: () -> Unit
) : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.info_delete_category, container, false)

        val tv_TitleInfo = view.findViewById<TextView>(R.id.tv_TitleInfo_delete)
        val tv_TextInfo = view.findViewById<TextView>(R.id.tv_TitleNewCategory)
        val bv_Info = view.findViewById<Button>(R.id.bt_info)

        tv_TitleInfo.text = title
        tv_TextInfo.text = description
        bv_Info.text = btnText

        bv_Info.setOnClickListener{
            onClicked.invoke()
            dismiss()
        }
        return view
    }
}