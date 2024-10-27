package ma.ensa.projet.service


import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import ma.ensa.projet.R
import android.media.MediaPlayer
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import ma.ensa.projet.MainActivity

class MusicService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private val CHANNEL_ID = "music_playback_channel"
    private val NOTIFICATION_ID = 16

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
    private fun buildNotification(songTitle: String): Notification {
        // Intent pour revenir à l'activité principale
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, "music_playback_channel")
            .setContentTitle("Lecture en cours")
            .setContentText(songTitle)
            .setSmallIcon(R.drawable.ic_music) // Icône de votre notification
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val musicTitle = intent?.getStringExtra("musicTitle") ?: "Musique"
        val action = intent?.action

        when (action) {
            "ACTION_PAUSE" -> {
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.pause()
                    showMusicNotification("Musique mise en pause") // Mise à jour pour indiquer que la musique est en pause
                } else {
                    mediaPlayer?.start()
                    showMusicNotification("Musique en cours") // Mise à jour pour indiquer que la musique est en cours
                }
            }
            else -> {
                // Initialiser le lecteur multimédia et démarrer la musique
                mediaPlayer?.release()
                // Sélectionner un fichier de musique en fonction du titre
                val musicResId = when (musicTitle) {
                    "Alan_Walker_Faded" -> R.raw.music1 // Nom du fichier dans res/raw
                    "Alec_Benjamin_Let_Me_Down_Slowly_(Lyrics)" -> R.raw.music2
                    "Ed Sheeran_Shape of You" -> R.raw.music3
                    "Lil_Dicky_Freaky_Friday_(Lyrics)" -> R.raw.music4
                    "Now_United__hoops" -> R.raw.music5
                    "Sia__The_Greatest" -> R.raw.music6
                    "The_Chainsmokers_-_Don_t_Let_Me_Down" -> R.raw.music7
                    "Trevor_Daniel__Falling" -> R.raw.music8
                    else -> R.raw.music1
                }

                // Initialiser et démarrer le lecteur multimédia
                mediaPlayer = MediaPlayer.create(this, musicResId)
                showMusicNotification(musicResId.toString())

                mediaPlayer?.start()
                startForeground(1, buildNotification(musicTitle))
                showMusicNotification(musicTitle) // Indiquer que la musique est en cours

                mediaPlayer?.setOnCompletionListener {
                    stopSelf()
                }
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Music Playback"
            val descriptionText = "Channel for music playback notifications"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showMusicNotification(message: String) {
        val playIntent = Intent(this, MusicService::class.java).apply {
            putExtra("musicTitle", message)
            action = "ACTION_PLAY"
        }
        val playPendingIntent = PendingIntent.getService(
            this,
            0,
            playIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val pauseIntent = Intent(this, MusicService::class.java).apply {
            action = "ACTION_PAUSE"
        }
        val pausePendingIntent = PendingIntent.getService(
            this,
            1,
            pauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val intent = Intent(this, MusicService::class.java).apply {
            // Ajoutez des extras si nécessaire
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Ajoutez ici le flag
        )

        // Créez la notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Musique en cours")
            .setContentText(message)
            .addAction(R.drawable.ic_pause, "Pause",pausePendingIntent) // Bouton pause
            .addAction(R.drawable.ic_play, "Jouer", playPendingIntent) // Bouton jouer
            .setSmallIcon(R.drawable.ic_music)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Affichez la notification
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification)
    }
}