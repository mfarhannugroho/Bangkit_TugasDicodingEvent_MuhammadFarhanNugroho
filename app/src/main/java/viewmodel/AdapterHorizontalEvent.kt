package viewmodel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.example.submissionawal_aplikasidicodingevent.R
import com.example.submissionawal_aplikasidicodingevent.databinding.CardItemHorizontalBinding
import data.remote.response.ListEventsItem
import utils.ListEventsDiffUtil

class AdapterHorizontalEvent(
    private val onItemClick: ((Int?) -> Unit)? = null
) : ListAdapter<ListEventsItem, AdapterHorizontalEvent.HorizontalEventViewHolder>(ListEventsDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalEventViewHolder {
        val binding =
            CardItemHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HorizontalEventViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: HorizontalEventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class HorizontalEventViewHolder(
        private val binding: CardItemHorizontalBinding,
        private val onItemClick: ((Int?) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: ListEventsItem) {
            binding.titleEvent.text = event.name

            // Memuat gambar dengan Glide, menambahkan placeholder dan error image
            Glide.with(binding.imageEvent.context)
                .load(event.imageLogo)
                .placeholder(R.drawable.background_bangkit)  // Gambar placeholder saat loading
                .into(binding.imageEvent)

            binding.root.setOnClickListener {
                onItemClick?.invoke(event.id)
            }
        }
    }
}