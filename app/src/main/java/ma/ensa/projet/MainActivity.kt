package ma.ensa.projet

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ma.ensa.projet.adapter.MusicAdapter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Configurer la Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val musicList = listOf("Alan_Walker_Faded", "Alec_Benjamin_Let_Me_Down_Slowly_(Lyrics)", "Ed Sheeran_Shape of You","Lil_Dicky_Freaky_Friday_(Lyrics)","Now_United__hoops","Sia__The_Greatest","The_Chainsmokers_-_Don_t_Let_Me_Down","Trevor_Daniel__Falling") // Liste de musiques
        val adapter = MusicAdapter(this, musicList)
        recyclerView.adapter = adapter

    }
}