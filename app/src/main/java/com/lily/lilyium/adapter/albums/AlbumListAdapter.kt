import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.lily.lilyium.api.model.Album
import com.lily.lilyium.adapter.albums.AlbumListViewHolder
import com.lily.lilyium.databinding.GridItemBinding
import com.lily.lilyium.databinding.ListItemBinding

import com.squareup.picasso.Picasso

class AlbumListAdapter :
    ListAdapter<Album, AlbumListViewHolder>(object : DiffUtil.ItemCallback<Album>() {

        override fun areItemsTheSame(oldItem: Album, newItem: Album) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Album, newItem: Album) =
            oldItem == newItem
    }) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumListViewHolder {
            val binding = GridItemBinding.inflate(LayoutInflater.from(parent.context))
            return AlbumListViewHolder(binding)
        }

    override fun onBindViewHolder(holder: AlbumListViewHolder, position: Int) {
        val album = getItem(position)
//        holder.textView.text = album.title
        val imageUrl = "http://192.168.1.9:4533/rest/getCoverArt.view?id=${album.id}&u=asuka&p=5463728190&v=1.12.0&c=lilyium"
//        val imageUrl = "http://100.114.211.103:4533/rest/getCoverArt.view?id=${album.id}&u=asuka&p=5463728190&v=1.12.0&c=lilyium"
        //            This from the getAlbumList
        Log.d("ON_BIND", "Track object: $album")
        Picasso.get()
            .load(imageUrl)
            .resize(300, 300)
            .into(holder.imageView)
    }
}

