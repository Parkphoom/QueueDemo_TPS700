package com.example.queuedemo_tps700

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.youtube.player.YouTubePlayer
import com.example.queuedemo_tps700.R.raw.vdo

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val YoutubeDeveloperKey = "AIzaSyDUlf_QynE_2Ivcrv"
    val utubeID ="RjG61DoDzN8"
    private var YPlayer: YouTubePlayer? = null
    private val RECOVERY_DIALOG_REQUEST = 1
    private var uri: Uri? = null
    private var isContinuously = false
    private var mediaController: MediaController? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val fragment = Service_Fragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contaner, fragment)
        transaction.commit()

        val myVideoV = findViewById<View>(R.id.videoView) as VideoView
        myVideoV.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true
            mediaPlayer.start()
        }
        myVideoV.setVideoURI(Uri.parse("android.resource://$packageName/$vdo"))
        myVideoV.setMediaController(MediaController(this))
        myVideoV.requestFocus()
        myVideoV.start()





        //        val fragmentyoutube : YouTubePlayerFragment
//        fragmentyoutube = fragmentManager.findFragmentById(R.id.youtube_fragment_1) as YouTubePlayerFragment
//        fragmentyoutube.initialize(YoutubeDeveloperKey,
//            object : YouTubePlayer.OnInitializedListener {
//                override fun onInitializationSuccess(
//                    provider: YouTubePlayer.Provider,
//                    player: YouTubePlayer,
//                    wasRestored: Boolean
//                ) {
//                    Log.d("Detail", "YouTube Player onInitializationSuccess")
//                    // Don't do full screen
//                    player.setFullscreen(false)
//                    if (!wasRestored) {
//                        YPlayer = player
//                        YPlayer!!.setFullscreen(true)
//                        YPlayer!!.loadVideo("RjG61DoDzN8")
//                        YPlayer!!.play()
//                    }
//                }
//
//                override fun onInitializationFailure(
//                    provider: YouTubePlayer.Provider,
//                    youTubeInitializationResult: YouTubeInitializationResult
//                ) {
//                    Log.i("Detail", "Failed: $youTubeInitializationResult")
//                    if (youTubeInitializationResult.isUserRecoverableError) {
//                        youTubeInitializationResult.getErrorDialog(this@MainActivity,
//                            RECOVERY_DIALOG_REQUEST
//                        ).show()
//                    } else {
//                        PublicAction().Toastmessage(this@MainActivity,youTubeInitializationResult.toString())
//                    }
//                }
//            })

    }


    override fun onClick(v: View?) {
        when (v?.id) {
//            R.id.Settingbtn -> {
//                val intent = Intent(this, PrintActivity::class.java)
//                startActivity(intent)
//            }
//            R.id.scanICbtn -> {
//                val intent = Intent(this, MainICFragment::class.java)
//                startActivity(intent)
//            }
        }
    }




}