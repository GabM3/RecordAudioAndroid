package com.example.recordaudio

import android.Manifest.permission
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.recordaudio.databinding.FragmentRecordBinding
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*

@RequiresApi(Build.VERSION_CODES.S)
class RecordFragment : BaseFragment(), Timer.OnTimerTickListener {
    companion object {
        fun with() =
            RecordFragment().apply {

            }
    }

    private lateinit var recorder: MediaRecorder

    private lateinit var vibrator: Vibrator

    private var dirPath = ""
    private var fileName = ""
    private var isRecording = false
    private var isPaused = false

    private lateinit var timer: Timer

    private lateinit var viewBinding: FragmentRecordBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
        viewBinding.buttonPlay.isEnabled = true
        viewBinding.buttonRecord.isEnabled = true

        timer = Timer(this)
        vibrator = mainActivity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        //after checking for permission initialize recorder
        recorder = MediaRecorder()

        viewBinding.buttonRecord.setOnClickListener {
            when {
                isPaused -> resumeRecorder()
                isRecording -> pauseRecorder()
                else -> startRecording()
            }
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        }



        viewBinding.buttonPlay.setOnClickListener {
            val mp = MediaPlayer()
            mp.apply {
                val path = "${mainActivity.externalCacheDir?.absolutePath}/"
                val fileName = "audio_record"
                setDataSource("$path$fileName.mp3")
                prepare()
                start()
            }
        }
    }

    private fun resumeRecorder() {
        recorder.resume()
        isPaused = false
        viewBinding.buttonRecord.text = "Pause"
        timer.start()
    }

    private fun pauseRecorder() {
        recorder.pause()
        isPaused = true
        viewBinding.buttonRecord.text = "Record"
        timer.pause()
    }

    private fun startRecording() {


        dirPath = "${mainActivity.externalCacheDir?.absolutePath}/"
        val simpleDateFormat = SimpleDateFormat("yyyy.MM.DD_hh.mm.ss")
        val date = simpleDateFormat.format(Date())
        fileName = "audio_record_$date"

        recorder.apply {
            //record from microphone
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile("$dirPath$fileName.mp3")

            try {
                prepare()
            } catch (e: IOException) {
                print(e.localizedMessage)
            }
            start()
        }
        viewBinding.buttonRecord.text = "Stop"
        isPaused = false
        isRecording = true
        timer.start()
    }

    private fun stopRecorder() {
        timer.stop()
    }

    private fun checkPermission() {

        if (ActivityCompat.checkSelfPermission(
                this.requireContext(),
                permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this.mainActivity,
                arrayOf(
                    permission.RECORD_AUDIO,
                    permission.WRITE_EXTERNAL_STORAGE,
                    permission.READ_EXTERNAL_STORAGE
                ),
                111
            )
            //onPermssionResult enable button
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentRecordBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onTimerTick(duration: Duration) {
        // nice format for user
        val millis = (duration.toMillis() % 1000) / 10
        val seconds = (duration.toMillis() / 1000) % 60
        val minutes = duration.toMinutes() % 60

        val formatted = if (minutes > 0) {
            "%02d:%02d:%02d".format(minutes, seconds, millis)
        } else {
            "%02d:%02d".format(seconds, millis)
        }

        viewBinding.textViewTime.text = formatted
    }


}