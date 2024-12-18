package com.example.eatttttttttt

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var contentLayout: FrameLayout
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var preferencesAdapter: ArrayAdapter<String>
    private val mainScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        contentLayout = findViewById(R.id.content_layout)
        sharedPreferences = getSharedPreferences("MealPreferences", MODE_PRIVATE)

        val preferencesButton: Button = findViewById(R.id.preferences_button)
        val recommendMealButton: Button = findViewById(R.id.recommend_meal_button)
        val lotteryButton: Button = findViewById(R.id.lottery_button)

        preferencesButton.setOnClickListener { showPreferencesLayout() }
        recommendMealButton.setOnClickListener { showRecommendMealLayout() }
        lotteryButton.setOnClickListener { showLotteryLayout() }

        // 預設顯示偏好設定頁面
        showPreferencesLayout()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel()
    }

    private fun showPreferencesLayout() {
        val preferencesLayout = layoutInflater.inflate(R.layout.preferences_layout, contentLayout, false)
        contentLayout.removeAllViews()
        contentLayout.addView(preferencesLayout)

        val preferencesList: ListView = preferencesLayout.findViewById(R.id.preferences_list)
        val inputEditText: EditText = preferencesLayout.findViewById(R.id.input_edittext)
        inputEditText.hint = "請輸入您的食物偏好！"
        val addButton: Button = preferencesLayout.findViewById(R.id.add_button) // 新增的按鈕

        preferencesAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, loadPreferences())
        preferencesList.adapter = preferencesAdapter

        addButton.setOnClickListener {
            val inputText = inputEditText.text.toString()
            if (inputText.isNotEmpty()) {
                preferencesAdapter.add(inputText)
                savePreferences()
                inputEditText.text.clear()
            } else {
                Toast.makeText(this, "請輸入內容", Toast.LENGTH_SHORT).show()
            }
        }

        preferencesList.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = preferencesAdapter.getItem(position)
            if (selectedItem != null) {
                preferencesAdapter.remove(selectedItem)
                savePreferences()
                Toast.makeText(this, "已刪除：$selectedItem", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadPreferences(): MutableList<String> {
        val preferences = sharedPreferences.getStringSet("preferences", setOf()) ?: setOf()
        return preferences.toMutableList()
    }

    private fun savePreferences() {
        val editor = sharedPreferences.edit()
        val preferencesSet = preferencesAdapter.allItems.toSet()
        editor.putStringSet("preferences", preferencesSet)
        editor.apply()
    }

    private val ArrayAdapter<String>.allItems: List<String>
        get() {
            val items = mutableListOf<String>()
            for (i in 0 until count) {
                items.add(getItem(i) ?: "")
            }
            return items
        }

    private fun showRecommendMealLayout() {
        val recommendMealLayout = layoutInflater.inflate(R.layout.recommend_meal_layout, contentLayout, false)
        contentLayout.removeAllViews()
        contentLayout.addView(recommendMealLayout)

        val chatInputEditText: EditText = recommendMealLayout.findViewById(R.id.chat_input_edittext)
        val recommendationTextView: TextView = recommendMealLayout.findViewById(R.id.recommendation_textview)
        val recommendButton: Button = recommendMealLayout.findViewById(R.id.recommend_button)

        chatInputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val userInput = chatInputEditText.text.toString()
                if (userInput.isNotEmpty()) {
                    recommendMeal(userInput) { response ->
                        recommendationTextView.text = response
                    }
                    chatInputEditText.text.clear()
                } else {
                    Toast.makeText(this, "請輸入內容", Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                false
            }
        }

        recommendButton.setOnClickListener {
            recommendMeal("") { response ->
                recommendationTextView.text = response
            }
        }
    }

    private fun recommendMeal(userInput: String, callback: (String) -> Unit) {
        val preferences = loadPreferences()
        if (preferences.isNotEmpty()) {
            val prompt = "根據以下偏好推薦一個餐點：${preferences.joinToString(", ")}"
            mainScope.launch {
                try {
                    val response = withContext(Dispatchers.IO) {
                        ChatGPT.chat(prompt)
                    }
                    callback(response)
                } catch (e: Exception) {
                    callback("發生錯誤，請重試。"+e.message)
                }
            }
        } else {
            callback("目前沒有偏好設定，請先添加您的食物偏好。")
        }
    }

    private fun showLotteryLayout() {
        val lotteryLayout = layoutInflater.inflate(R.layout.lottery_layout, contentLayout, false)
        contentLayout.removeAllViews()
        contentLayout.addView(lotteryLayout)

        val diceImageView: ImageView = lotteryLayout.findViewById(R.id.dice_imageview)
        val mealEditTexts: Array<EditText> = arrayOf(
            lotteryLayout.findViewById(R.id.meal1_edittext),
            lotteryLayout.findViewById(R.id.meal2_edittext),
            lotteryLayout.findViewById(R.id.meal3_edittext),
            lotteryLayout.findViewById(R.id.meal4_edittext),
            lotteryLayout.findViewById(R.id.meal5_edittext),
            lotteryLayout.findViewById(R.id.meal6_edittext)
        )
        val rollButton: Button = lotteryLayout.findViewById(R.id.roll_button)

        rollButton.setOnClickListener {
            val handler = Handler(Looper.getMainLooper())
            var finalDiceNumber = 1 // 用於保存最終的骰子點數

            val runnable = object : Runnable {
                @SuppressLint("DiscouragedApi")
                override fun run() {
                    val randomNumber = Random.nextInt(1, 7)
                    finalDiceNumber = randomNumber // 更新最終的骰子點數
                    diceImageView.setImageResource(resources.getIdentifier("dice$randomNumber", "drawable", packageName))
                    handler.postDelayed(this, 100) // 每 100 毫秒切換一次骰子圖片
                }
            }

            handler.postDelayed(runnable, 0) // 立即開始動畫

            handler.postDelayed({
                handler.removeCallbacks(runnable) // 2 秒後停止動畫
                // 使用最終的骰子點數
                diceImageView.setImageResource(resources.getIdentifier("dice$finalDiceNumber", "drawable", packageName))
                val selectedMeal = mealEditTexts[finalDiceNumber - 1].text.toString()
                if (selectedMeal.isNotEmpty()) {
                    Toast.makeText(this, "您抽到了：$selectedMeal", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "請輸入第 $finalDiceNumber 個餐點名稱", Toast.LENGTH_SHORT).show()
                }
            }, 2000)
        }
    }
}