package viewmodel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submissionawal_aplikasidicodingevent.R
import com.example.submissionawal_aplikasidicodingevent.databinding.CardItemVerticalBinding
import data.remote.response.ListEventsItem
import utils.ListEventsDiffUtil

class AdapterVerticalEvent(
    private val onItemClick: ((Int?) -> Unit)? = null
) : ListAdapter<ListEventsItem, AdapterVerticalEvent.VerticalEventViewHolder>(ListEventsDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerticalEventViewHolder {
        val binding =
            CardItemVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VerticalEventViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: VerticalEventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class VerticalEventViewHolder(
        private val binding: CardItemVerticalBinding,
        private val onItemClick: ((Int?) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: ListEventsItem) {
            // Mengisi teks dengan nama event dan deskripsi
            binding.titleEvent.text = event.name
            binding.descriptionEvent.text = event.summary

            // Memuat gambar dengan Glide, menambahkan placeholder dan error image
            Glide.with(binding.imageEvent.context)
                .load(event.imageLogo)
                .placeholder(R.drawable.background_bangkit)  // Gambar placeholder saat loading
                .into(binding.imageEvent)

            // Mengatur click listener untuk item
            binding.root.setOnClickListener {
                onItemClick?.invoke(event.id)
            }
        }
    }
}