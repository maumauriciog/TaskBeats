@file:OptIn(DelicateCoroutinesApi::class)

package com.devspace.taskbeats

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Global
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private var categories = listOf<CategoryUiData>()
    private var categoriesEntits = listOf<categoryEntity>()
    private var tasks = listOf<TaskUiData>()

    private lateinit var rvCategory: RecyclerView
    private lateinit var ctnLinearLayoutEmpty: LinearLayout
    private lateinit var fab: FloatingActionButton

    private val categoryAdapter = CategoryListAdapter()
    private val taskAdapter = TaskListAdapter()

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

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvCategory = findViewById(R.id.rv_categories)
        ctnLinearLayoutEmpty = findViewById(R.id.ll_emptyCategory)
        fab = findViewById(R.id.fab)
        val rvTask = findViewById<RecyclerView>(R.id.rv_tasks)
        val bvEmptyCategory = findViewById<Button>(R.id.bv_create_empty)

        bvEmptyCategory.setOnClickListener {
            showCreateCategoryBottSheet()
        }

        fab.setOnClickListener {
            showCreateUpdateTaskButton()
        }

        taskAdapter.setOnClickListener { task ->
            showCreateUpdateTaskButton(task)
        }

        categoryAdapter.setOnLongClickerListener { categoryToDelete ->
            if (categoryToDelete.name != "+" && categoryToDelete.name != "All") {
                val title: String = this.getString(R.string.title_about_category)
                val description: String = this.getString(R.string.info_about_category)
                val btnText: String = this.getString(R.string.btnDel_about_category)

                showInfoDialog(
                    title,
                    description,
                    btnText
                ) {
                    val categoryEntityToBeDelete = categoryEntity(
                        categoryToDelete.name,
                        categoryToDelete.isSelected
                    )
                    deleteCategory(categoryEntityToBeDelete)
                }
            }
        }

        categoryAdapter.setOnClickListener { selected ->
            if (selected.name == "+") {
                showCreateCategoryBottSheet()
            } else {
                val categoryTemp = categories.map { item ->
                    when {
                        item.name == selected.name && item.isSelected -> item.copy(isSelected = true)
                        item.name == selected.name && !item.isSelected -> item.copy(isSelected = true)
                        item.name != selected.name && item.isSelected -> item.copy(isSelected = false)
                        else -> item
                    }
                }
                if (selected.name != "All") {
                    filterTaskByCategoryName(selected.name)
                } else {
                    getTaskFromDb()
                }
                categoryAdapter.submitList(categoryTemp)
            }
        }

        rvCategory.adapter = categoryAdapter
        getCategoryFromDb()

        rvTask.adapter = taskAdapter
        getTaskFromDb()
    }

    private fun getCategoryFromDb() {
        GlobalScope.launch(Dispatchers.IO) {
            val getDbCat: List<categoryEntity> = categoryDAO.getAll()
            categoriesEntits = getDbCat
            GlobalScope.launch(Dispatchers.Main){
                if (categoriesEntits.isEmpty()) {
                    ctnLinearLayoutEmpty.isVisible = true
                    fab.isVisible = false
                    rvCategory.isVisible = false
                } else {
                    ctnLinearLayoutEmpty.isVisible = false
                    fab.isVisible = true
                    rvCategory.isVisible = true
                }
            }

            val listCat = getDbCat.map {
                CategoryUiData(name = it.name, isSelected = it.isSelected)
            }
                .toMutableList()
            listCat.add(
                CategoryUiData(name = "+", isSelected = false)
            )

            //criando uma variável, adicionando
            val categoryAddAll = mutableListOf(
                CategoryUiData(
                    name = "All",
                    isSelected = true
                )
            )
            categoryAddAll.addAll(listCat)
            GlobalScope.launch(Dispatchers.Main) {
                categories = categoryAddAll
                categoryAdapter.submitList(categories)
            }
        }
    }

    private fun getTaskFromDb() {
        GlobalScope.launch(Dispatchers.IO) {
            val getTskDb: List<taskEntity> = taskDAO.getAllTask()
            val listTask: List<TaskUiData> = getTskDb.map {
                TaskUiData(id = it.id, name = it.name, category = it.category)
            }
            GlobalScope.launch(Dispatchers.Main) {
                tasks = listTask
                taskAdapter.submitList(listTask)
            }
        }
    }

    private fun insertCategory(categoryEntity: categoryEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            categoryDAO.insert(categoryEntity)
            getCategoryFromDb()
        }
    }

    private fun insertTask(taskEntity: taskEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            taskDAO.insert(taskEntity)
            getTaskFromDb()
        }
    }

    private fun deleteTask(taskEntity: taskEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            taskDAO.delete(taskEntity)
            getTaskFromDb()
        }
    }

    private fun showInfoDialog(
        title: String,
        description: String,
        btnText: String,
        onClick: () -> Unit
    ) {
        val infoBottSheet = infoDeleteCategory(
            title = title,
            description = description,
            btnText = btnText,
            onClick
        )
        infoBottSheet.show(supportFragmentManager, "infoBottSheet")
    }

    private fun deleteCategory(categoryEntity: categoryEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            val deleteAllCategoryByTask = taskDAO.getAllByCategoryName(categoryEntity.name)
            taskDAO.deleteAll(deleteAllCategoryByTask)
            categoryDAO.delete(categoryEntity)
            getCategoryFromDb()
            getTaskFromDb()
        }
    }

    private fun filterTaskByCategoryName(category: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val taskFromDB: List<taskEntity> = taskDAO.getAllByCategoryName(category)
            val tasksUi: List<TaskUiData> = taskFromDB.map {
                TaskUiData(
                    id = it.id,
                    name = it.name,
                    category = it.category
                )
            }
            GlobalScope.launch(Dispatchers.Main) {
                taskAdapter.submitList(tasksUi)
            }
        }
    }

    private fun showCreateUpdateTaskButton(taskUiData: TaskUiData? = null) {
        val createTaskSheet = createUpdateTaskSheetFrame(
            task = taskUiData,
            categoryList = categoriesEntits,
            onCreateClicked = { taskToBeCreate ->
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
            },
            onDeleteClicked = { taskDelete ->
                val taskToBeDelete = taskEntity(
                    id = taskDelete.id,
                    name = taskDelete.name,
                    category = taskDelete.category
                )
                deleteTask(taskToBeDelete)
            }
        )
        createTaskSheet.show(supportFragmentManager, "createTask")
    }

    private fun showCreateCategoryBottSheet() {
        val createCategoryFrame = createCategorySheetFrame { categoryName ->
            val vrCategoryEntity = categoryEntity(
                name = categoryName,
                isSelected = false
            )
            insertCategory(vrCategoryEntity)
        }
        createCategoryFrame.show(supportFragmentManager, "sheet")
    }

}