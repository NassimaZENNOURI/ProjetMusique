package ma.ensa.projet.adapter

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ma.ensa.projet.R
import ma.ensa.projet.service.MusicService

class MusicAdapter(private val context: Context, private val musicList: List<String>) :
    RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

    private var currentlyPlaying: String? = null // Variable pour suivre la musique actuellement jouée


    // Méthode pour afficher la notification
    private fun showMusicNotification(musicTitle: String, isPlaying: Boolean) {
        val playIntent = Intent(context, MusicService::class.java).apply {
            action = if (isPlaying) "ACTION_PAUSE" else "ACTION_PLAY"
            putExtra("musicTitle", musicTitle)
        }
        val playPendingIntent = PendingIntent.getService(
            context,
            0,
            playIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, "music_playback_channel")
            .setSmallIcon(R.drawable.ic_music) // Remplacez par votre icône
            .setContentTitle("Lecture de musique")
            .setContentText(musicTitle)
            .addAction(
                if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play,
                if (isPlaying) "Pause" else "Jouer",
                playPendingIntent
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java)
        notificationManager?.notify(1, builder.build()) // Utilisez un ID unique pour la notification
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_music, parent, false)
        return MusicViewHolder(view)
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val musicTitle = musicList[position]
        holder.tvMusicTitle.text = musicTitle

        // Initialiser le bouton avec l'état actuel
        holder.btnPlayPause.setImageResource(
            if (isPlaying(musicTitle)) R.drawable.ic_pause else R.drawable.ic_play
        )
        holder.btnPlayPause.setOnClickListener {
            // Logique pour démarrer ou arrêter la musique
            val intent = Intent(context, MusicService::class.java)
            intent.putExtra("musicTitle", musicTitle)

            if (isPlaying(musicTitle)) {
                // Si la musique est en cours, on l'arrête
                context.stopService(intent)
                currentlyPlaying = null // Réinitialiser l'état
            } else {
                // Sinon, on la joue
                ContextCompat.startForegroundService(context, intent)
                currentlyPlaying = musicTitle // Mettre à jour la musique actuellement jouée
            }
            // Mettre à jour l'icône après l'action
            holder.btnPlayPause.setImageResource(
                if (isPlaying(musicTitle)) R.drawable.ic_pause else R.drawable.ic_play
            )
        }
        holder.itemView.setOnClickListener {
            // Démarrer le service
            val intent = Intent(context, MusicService::class.java).apply {
                putExtra("musicTitle", musicTitle) // Envoie le titre au service
            }
            ContextCompat.startForegroundService(context, intent)
            showMusicNotification(musicTitle, true) // Indiquer que la musique est en cours
        }
    }

    override fun getItemCount(): Int = musicList.size

    // Vérifie si la musique est en cours de lecture
    private fun isPlaying(musicTitle: String): Boolean {
        return musicTitle == currentlyPlaying
    }

    class MusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMusicTitle: TextView = itemView.findViewById(R.id.tvMusicTitle)
        val btnPlayPause: ImageView = itemView.findViewById(R.id.btnPlayPause)
    }
}
