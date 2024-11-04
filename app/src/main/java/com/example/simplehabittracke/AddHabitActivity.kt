package com.example.simplehabittracke

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button

class AddHabitActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val colorView = findViewById<View>(R.id.view3)

        colorView.setBackgroundColor(Color.MAGENTA)
        colorView.setOnClickListener {

        }

        val button = findViewById<Button>(R.id.save_new_habit)
        button.setOnClickListener {
            val intent = Intent();
            intent.putExtra("editTextValue", "value_here")
            intent.putExtra("colorValue", R.color.gold)
            setResult(RESULT_OK, intent);
            this.finish()
        }
    }
}