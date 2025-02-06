package edu.victorlamas.fandom

import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import edu.victorlamas.fandom.adapters.FandomAdapter
import edu.victorlamas.fandom.databinding.ActivityMainBinding
import edu.victorlamas.fandom.utils.deleteFilesOptions
import edu.victorlamas.fandom.utils.fandomMutableList
import edu.victorlamas.fandom.utils.readRawFile
import edu.victorlamas.fandom.utils.updateFilesOptions

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fandomAdapter: FandomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
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

        // Cargar la lista inicial de fandoms
        fandomMutableList = readRawFile(this)
        // Configurar el RecyclerView
        setupRecyclerView()
    }

    // Reiniciar todos los valores del RecyclerView al refrescar
    override fun onStart() {
        super.onStart()

        binding.swipeRefreshLayout.setOnRefreshListener {
            fandomMutableList.clear()
            deleteFilesOptions(this)
            fandomMutableList.addAll(readRawFile(this))
            fandomAdapter.notifyItemRangeChanged(0, fandomMutableList.size)
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    // Actualizar el adaptador por si se ha cambiado algún favorito desde ActivityDetail
    override fun onRestart() {
        super.onRestart()
        
        fandomAdapter.notifyItemRangeChanged(0, fandomMutableList.size)
    }

    private fun setupRecyclerView() {
        // Configura el adaptador
        fandomAdapter = FandomAdapter(
            fandomList = fandomMutableList,
            listenerFav = { pos ->
                // Actualiza la propiedad fav del item de la lista
                fandomMutableList[pos].fav = !fandomMutableList[pos].fav
                updateFilesOptions(this, R.string.filenameFavs)
                fandomAdapter.notifyItemChanged(pos)
            },
            delFandom = { pos ->
                val longClickSnackbar = movedSnackbar(
                    String.format(getString(
                        R.string.txt_delete),
                        fandomMutableList[pos].name)
                ).setAction(R.string.txt_doit) {
                    fandomMutableList[pos].visible = false
                    updateFilesOptions(this, R.string.filenameDeleted)
                    fandomMutableList.removeAt(pos)
                    fandomAdapter.notifyItemRemoved(pos)
                }
                longClickSnackbar.show()
            },
            showFandom = { pos ->
                DetailActivity.navigateToDetail(this, fandomMutableList[pos])
            }
        )

        binding.recyclerView.adapter = fandomAdapter
    }

    // Mueve todos los snackbar a la parte baja de la pantalla
    private fun movedSnackbar(textToShow: String): Snackbar {
        val snackbar = Snackbar.make(
            binding.root,
            textToShow,
            Snackbar.LENGTH_LONG
        )

        val params = CoordinatorLayout.LayoutParams(snackbar.view.layoutParams)
        params.gravity = Gravity.BOTTOM
        params.setMargins(0, 0, 0, -binding.root.paddingBottom)
        snackbar.view.layoutParams = params
        return snackbar
    }
}