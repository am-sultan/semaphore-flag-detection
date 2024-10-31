package com.athallahriq.projectskripsi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class AlphabetItem(val letter: String, val imageResId: Int)

class AlphabetAdapter(private val alphabetList: List<AlphabetItem>) : RecyclerView.Adapter<AlphabetAdapter.AlphabetViewHolder>() {

    class AlphabetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val alphabetText: TextView = view.findViewById(R.id.txt_alphabet)
        val imageView: ImageView = view.findViewById(R.id.img_semaphore_alphabet)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlphabetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.alphabet_list, parent, false)
        return AlphabetViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlphabetViewHolder, position: Int) {
        val item = alphabetList[position]
        holder.alphabetText.text = item.letter
        holder.imageView.setImageResource(item.imageResId)
    }

    override fun getItemCount(): Int {
        return alphabetList.size
    }
}
