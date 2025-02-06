package edu.victorlamas.fandom

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import edu.victorlamas.fandom.databinding.ActivityDetailBinding
import edu.victorlamas.fandom.models.Fandom
import edu.victorlamas.fandom.utils.updateFilesOptions
import edu.victorlamas.fandom.utils.fandomMutableList

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        // Obtiene el item desde el Intent. Si es nulo, cierra la actividad
        val fandom: Fandom?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            fandom = intent.getParcelableExtra(EXTRA_ITEM, Fandom::class.java)
        } else {
            @Suppress("DEPRECATION")
            fandom = intent.getParcelableExtra(EXTRA_ITEM) as? Fandom
        }

        if (fandom == null) {
            finish()
            return
        } else {
            setupDetailView(fandom)
            updateFavorites(fandom)
        }
    }

    // Inicia el DetailActivity pasando un objeto fandom como parámetro
    companion object {
        private const val EXTRA_ITEM = "ITEM"
        fun navigateToDetail(activity: Activity, fandom: Fandom) {
            activity.startActivity(Intent(activity, DetailActivity::class.java).apply {
                putExtra(EXTRA_ITEM, fandom)
            })
        }
    }

    // Configura la vista con los datos del item
    private fun setupDetailView(fandom: Fandom) {
        binding.tvDescriptionDetail.text = fandom.description
        binding.tvLinkDetail.text = String.format(
            getString(R.string.txt_more_info),
            fandom.info
        )
        binding.btnFavoriteDetail.setImageState(
            intArrayOf(R.attr.state_fav_on),
            fandom.fav
        )

        // Carga la imagen
        Glide.with(this)
            .load(fandom.image)
            .fitCenter()
            .transform(RoundedCorners(20))
            .into(binding.ivDetail)

        // Configura el click en el enlace URL
        binding.tvLinkDetail.setOnClickListener {
            Intent(Intent.ACTION_VIEW, Uri.parse(fandom.info)).apply {
                if (this.resolveActivity((packageManager)) != null) {
                    startActivity(this)
                }
            }
        }

        // Configura la Toolbar y habilita el botón de "volver"
        setSupportActionBar(binding.mToolbarDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = fandom.name
    }

    // Configura el botón de favoritos
    private fun updateFavorites(fandom: Fandom) {
        binding.btnFavoriteDetail.setOnClickListener {
            // Actualiza la propiedad fav del item de la lista
            fandomMutableList.find { it.id == fandom.id }?.let {
                it.fav = !it.fav
            }
            // Actualiza el estado del botón de favoritos
            binding.btnFavoriteDetail.setImageState(
                intArrayOf(R.attr.state_fav_on),
                fandomMutableList.find { it.id == fandom.id }?.fav ?: false
            )
            // Actualiza el fichero de favoritos
            updateFilesOptions(this, R.string.filenameFavs)
        }
    }
}