package com.athallahriq.projectskripsi

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.athallahriq.projectskripsi.databinding.ActivityAlphabetBinding

class AlphabetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlphabetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAlphabetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val alphabetList = ('a'..'z').map { letter ->
            val resourceName = letter.toString()
            val resId = resources.getIdentifier(resourceName, "drawable", packageName)
            AlphabetItem(letter.toString().toUpperCase(), resId)
        }

        val adapter = AlphabetAdapter(alphabetList)

        binding.recyclerAlphabet.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerAlphabet.adapter = adapter
    }
}
