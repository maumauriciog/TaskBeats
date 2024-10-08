package com.devspace.taskbeats

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar

class createUpdateTaskSheetFrame(
    private val categoryList: List<categoryEntity>,
    private val task: TaskUiData? = null,
    private val onCreateClicked: (TaskUiData) -> Unit,
    private val onUpdateClicked: (TaskUiData) -> Unit,
    private val onDeleteClicked: (TaskUiData) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.create_update_task_frame, container, false)

        val spinner = view.findViewById<Spinner>(R.id.sp_category)
        val tv_title = view.findViewById<TextView>(R.id.tv_TitleTask)
        val bt_CrTask = view.findViewById<Button>(R.id.bv_crTask)
        val bt_DelTask = view.findViewById<Button>(R.id.bv_delTask)
        val tiv_TaskName = view.findViewById<TextView>(R.id.tiv_writeTask)

        var taskCategory: String? = null
        val categoriesListTemp = mutableListOf("Select Categorys...")
        categoriesListTemp.addAll(
            categoryList.map { it.name }
        )
        val categoryStr: List<String> = categoriesListTemp

        ArrayAdapter(
            requireActivity().baseContext,
            android.R.layout.simple_spinner_item,
            categoryStr.toList()
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                taskCategory = categoryStr.get(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        //significa que não há click no float action button
        if (task == null) {
            bt_DelTask.isVisible = false
            tv_title.setText(R.string.new_task)
            bt_CrTask.setText(R.string.bt_new_task)
        } else {
            bt_DelTask.isVisible = true
            tv_title.setText(R.string.update_task)
            bt_CrTask.setText(R.string.bt_update_task)
            tiv_TaskName.setText(R.string.update_new_task)
            tiv_TaskName.setText(task.name)

            val currentCategory = categoryList.first { it.name == task.category }
            val index = categoryList.indexOf(currentCategory)
            spinner.setSelection(index)
        }

        bt_DelTask.setOnClickListener {
            if (task != null) {
                onDeleteClicked.invoke(task)
                dismiss()
            } else {
                Log.d("createUpdateTaskSheetFrame", "task not found")
            }
        }

        bt_CrTask.setOnClickListener {
            val nameTask = tiv_TaskName.text.toString()
            if (taskCategory != "Select Categorys...") {
                if (task == null) {
                    onCreateClicked.invoke(
                        TaskUiData(
                            id = 0,
                            name = nameTask,
                            category = requireNotNull(taskCategory)
                        )
                    )
                    dismiss()
                } else {
                    onUpdateClicked.invoke(
                        TaskUiData(
                            id = task.id,
                            name = nameTask,
                            category = requireNotNull(taskCategory)
                        )
                    )
                    dismiss()
                }
            } else {
                Snackbar.make(
                    bt_CrTask,
                    "Please, write or selected the task and category...",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        return view
    }
}