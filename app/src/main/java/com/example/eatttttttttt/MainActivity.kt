package com.example.eatttttttttt

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random
import android.view.inputmethod.EditorInfo

class MainActivity : AppCompatActivity() {

    private lateinit var contentLayout: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        contentLayout = findViewById(R.id.content_layout)

        val preferencesButton: Button = findViewById(R.id.preferences_button)
        val recommendMealButton: Button = findViewById(R.id.recommend_meal_button)
        val lotteryButton: Button = findViewById(R.id.lottery_button)

        preferencesButton.setOnClickListener { showPreferencesLayout() }
        recommendMealButton.setOnClickListener { showRecommendMealLayout() }
        lotteryButton.setOnClickListener { showLotteryLayout() }

        // 預設顯示偏好設定頁面
        showPreferencesLayout()
    }

    private fun showPreferencesLayout() {
        val preferencesLayout = layoutInflater.inflate(R.layout.preferences_layout, contentLayout, false)
        contentLayout.removeAllViews()
        contentLayout.addView(preferencesLayout)

        val preferencesList: ListView = preferencesLayout.findViewById(R.id.preferences_list)
        val inputEditText: EditText = preferencesLayout.findViewById(R.id.input_edittext)
        inputEditText.hint = "請輸入您的食物偏好！"
        val preferencesAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
        preferencesList.adapter = preferencesAdapter

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val inputText = inputEditText.text.toString()
                preferencesAdapter.add(inputText)
                inputEditText.text.clear()
                true
            } else {
                false
            }
        }
    }

    private fun showRecommendMealLayout() {
        val recommendMealLayout = layoutInflater.inflate(R.layout.recommend_meal_layout, contentLayout, false)
        contentLayout.removeAllViews()
        contentLayout.addView(recommendMealLayout)

        //  ChatGPT API
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
            val runnable = object : Runnable {
                @SuppressLint("DiscouragedApi")
                override fun run() {
                    val randomNumber = Random.nextInt(1, 7)
                    diceImageView.setImageResource(resources.getIdentifier("dice$randomNumber", "drawable", packageName))
                    handler.postDelayed(this, 100) // 每 100 毫秒切換一次骰子圖片
                }
            }

            handler.postDelayed(runnable, 0) // 立即開始動畫

            handler.postDelayed({
                handler.removeCallbacks(runnable) // 2 秒後停止動畫
                val finalDiceNumber = Random.nextInt(1, 7) // 獲取最終骰子點數
                diceImageView.setImageResource(resources.getIdentifier("dice$finalDiceNumber", "drawable", packageName)) // 顯示最終骰子點數
                val selectedMeal = mealEditTexts[finalDiceNumber - 1].text.toString()
                Toast.makeText(this, "您抽到了：$selectedMeal", Toast.LENGTH_SHORT).show()
            }, 2000)
        }
    }
}
