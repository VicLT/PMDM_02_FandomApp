package edu.victorlamas.fandom.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.victorlamas.fandom.R
import edu.victorlamas.fandom.databinding.ItemFandomBinding
import edu.victorlamas.fandom.models.Fandom

class FandomAdapter(
    private val fandomList: MutableList<Fandom>,
    private val listenerFav: (pos:Int) -> Unit,
    private val delFandom: (pos: Int) -> Unit,
    private val showFandom: (pos: Int) -> Unit
): RecyclerView.Adapter<FandomAdapter.FanViewHolder>() {

    // Devuelve el ViewHolder ya configurado
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : FanViewHolder {
        return FanViewHolder(ItemFandomBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).root
        )
    }

    // Pasa los objetos al ViewHolder
    override fun onBindViewHolder(holder: FanViewHolder, position: Int) {
        holder.bind(fandomList[position])
    }

    // Devuelve el tamaño de la fuente de datos
    override fun getItemCount(): Int {
        return fandomList.size
    }

    // Rellena cada una de las vistas que se inflarán para cada uno de los
    // elementos del RecyclerView
    inner class FanViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Localiza los elementos en la vista
        private val binding = ItemFandomBinding.bind(view)

        // Configura los datos del ítem en la vista
        fun bind(item: Fandom) {
            // Solo mostramos aquellos que su propiedad visible es true
            if (item.visible) {
                // Asigna los valores a los elementos de la vista
                binding.tvCharacterItem.text = item.name
                binding.tvTitleItem.text = item.universe
                binding.imgBtnFavItem.setImageState(
                    intArrayOf(R.attr.state_fav_on),
                    item.fav
                )

                // Cargar la imagen usando Glide
                Glide.with(this.itemView)
                    .load(item.image)
                    .circleCrop()
                    .into(binding.ivCharacterItem)

                // Listener de click en el botón de favoritos
                binding.imgBtnFavItem.setOnClickListener {
                    listenerFav(adapterPosition)
                }

                // Listener de click largo en el ítem completo para eliminarlo
                binding.cardViewItem.setOnLongClickListener {
                    delFandom(adapterPosition)
                    true
                }

                // Listener de click en el ítem para mostrar la información
                binding.cardViewItem.setOnClickListener {
                    showFandom(adapterPosition)
                }
            }
        }
    }
}