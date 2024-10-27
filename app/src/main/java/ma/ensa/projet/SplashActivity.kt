package ma.ensa.projet

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        // Délai pour passer à l'activité principale
        Handler().postDelayed({
            // Démarrer MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Terminer cette activité
        }, 3000)
    }
}