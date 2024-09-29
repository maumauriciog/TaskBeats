@file:OptIn(DelicateCoroutinesApi::class)

package com.devspace.taskbeats

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private var categories = listOf<CategoryUiData>()
    private var tasks = listOf<TaskUiData>()

    private val dataBase by lazy {
        Room.databaseBuilder(
            applicationContext,
            TaskBeatsDataBase::class.java, "database-task"
        ).build()
    }
    private val categoryDAO: categoryDAO by lazy {
        dataBase.getCategoryDAO()
    }
    private val taskDAO: taskDAO by lazy {
        dataBase.getTaskDAO()
    }

    private val categoryAdapter = CategoryListAdapter()
    private val taskAdapter = TaskListAdapter()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rvCategory = findViewById<RecyclerView>(R.id.rv_categories)
        val rvTask = findViewById<RecyclerView>(R.id.rv_tasks)
        val fab = findViewById<FloatingActionButton>(R.id.fab)

        fab.setOnClickListener {
            showCreateUpdateTaskButton()
        }

        taskAdapter.setOnClickListener { task ->
            showCreateUpdateTaskButton(task)
        }

        categoryAdapter.setOnClickListener { selected ->
            if (selected.name == "+") {
                val createCategoryFrame = createCategorySheetFrame { categoryName ->
                    val vrCategoryEntity = categoryEntity(
                        name = categoryName,
                        isSelected = false
                    )
                    insertCategory(vrCategoryEntity)
                }
                createCategoryFrame.show(supportFragmentManager, "sheet")
            } else {
                val categoryTemp = categories.map { item ->
                    when {
                        item.name == selected.name && !item.isSelected -> item.copy(isSelected = true)
                        item.name == selected.name && item.isSelected -> item.copy(isSelected = false)
                        else -> item
                    }
                }
                val taskTemp = if (selected.name != "ALL") {
                    tasks.filter { it.category == selected.name }
                } else {
                    tasks
                }
                taskAdapter.submitList(taskTemp)
                categoryAdapter.submitList(categoryTemp)
            }
        }

        rvCategory.adapter = categoryAdapter
        getCategoryFromDb(categoryAdapter)

        rvTask.adapter = taskAdapter
        getTaskFromDb(taskAdapter)
    }

    private fun getCategoryFromDb(adapter: CategoryListAdapter) {
        GlobalScope.launch(Dispatchers.IO) {
            val getDbCat: List<categoryEntity> = categoryDAO.getAll()
            val listCat = getDbCat.map {
                CategoryUiData(name = it.name, isSelected = it.isSelected)
            }
                .toMutableList()
            listCat.add(
                CategoryUiData(name = "+", isSelected = false)
            )
            GlobalScope.launch(Dispatchers.Main) {
                categories = listCat
                adapter.submitList(listCat)
            }
        }
    }

    private fun getTaskFromDb(adapter: TaskListAdapter) {
        GlobalScope.launch(Dispatchers.IO) {
            val getTskDb: List<taskEntity> = taskDAO.getAllTask()
            val listTask: List<TaskUiData> = getTskDb.map {
                TaskUiData(id = it.id, name = it.name, category = it.category)
            }
            GlobalScope.launch(Dispatchers.Main) {
                tasks = listTask
                adapter.submitList(listTask)
            }
        }
    }

    private fun insertCategory(categoryEntity: categoryEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            categoryDAO.insert(categoryEntity)
            getCategoryFromDb(categoryAdapter)
        }
    }

    private fun insertTask(taskEntity: taskEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            taskDAO.insert(taskEntity)
            getTaskFromDb(taskAdapter)
        }
    }

    private fun showCreateUpdateTaskButton(taskUiData: TaskUiData? = null) {
        val createTaskSheet = createUpdateTaskSheetFrame(
            task = taskUiData,
            categoryList = categories,
            onClicked = { taskToBeCreate ->
                insertTask(
                    taskEntity(
                        name = taskToBeCreate.name,
                        category = taskToBeCreate.category
                    )
                )
            },
            onUpdateClicked = { taskToBeUpdate ->
                val taskEntityToBeInsert = taskEntity(
                    id = taskToBeUpdate.id,
                    name = taskToBeUpdate.name,
                    category = taskToBeUpdate.category
                )
                insertTask(taskEntityToBeInsert)
            }
        )
        createTaskSheet.show(supportFragmentManager, "createTask")
    }
}