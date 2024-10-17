package viewmodel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submissionawal_aplikasidicodingevent.R
import com.example.submissionawal_aplikasidicodingevent.databinding.CardItemVerticalBinding
import data.local.model.FavoriteEvent

class AdapterFavoriteEvent(
    private val onItemClick: ((Int?) -> Unit)? = null
) : ListAdapter<FavoriteEvent, AdapterFavoriteEvent.FavoriteEventViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteEventViewHolder {
        val binding =
            CardItemVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteEventViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: FavoriteEventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FavoriteEventViewHolder(
        private val binding: CardItemVerticalBinding,
        private val onItemClick: ((Int?) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: FavoriteEvent) {
            // Set teks judul dan deskripsi
            binding.titleEvent.text = event.name
            binding.descriptionEvent.text = event.description

            // Memuat gambar dengan Glide, menambahkan placeholder dan error image
            Glide.with(binding.imageEvent.context)
                .load(event.image)
                .placeholder(R.drawable.background_bangkit)  // Gambar placeholder saat loading
                .into(binding.imageEvent)

            // Mengatur click listener untuk item
            binding.root.setOnClickListener {
                onItemClick?.invoke(event.eventId)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<FavoriteEvent> =
            object : DiffUtil.ItemCallback<FavoriteEvent>() {
                override fun areItemsTheSame(oldItem: FavoriteEvent, newItem: FavoriteEvent): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: FavoriteEvent, newItem: FavoriteEvent): Boolean {
                    return oldItem == newItem
                }
            }
    }
}