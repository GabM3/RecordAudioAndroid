package com.example.recordaudio

import android.Manifest.permission
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.example.recordaudio.databinding.FragmentRecordBinding
import java.io.File
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

    private lateinit var amplitudes: ArrayList<Float>
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
        viewBinding.btnRecord.isEnabled = true
        //after checking for permission initialize recorder
        recorder = MediaRecorder()
        timer = Timer(this)
        vibrator = mainActivity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator


        viewBinding.btnRecord.setOnClickListener {
            when {
                isPaused -> resumeRecorder()
                isRecording -> pauseRecorder()
                else -> startRecording()
            }
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        }

/*

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
*/
        viewBinding.btnList.setOnClickListener {
            Toast.makeText(requireContext(), "list todo", Toast.LENGTH_LONG).show()
        }

        viewBinding.btnDone.setOnClickListener {
            stopRecorder()
            Toast.makeText(requireContext(), "record saved todo", Toast.LENGTH_LONG).show()
        }

        viewBinding.btnDelete.setOnClickListener {
            if (isRecording) {
                stopRecorder()
                File("$dirPath$fileName.mp3")
            }

        }

        viewBinding.btnDelete.isClickable = false
        viewBinding.btnDone.isVisible = false
    }

    private fun resumeRecorder() {
        recorder.resume()
        isPaused = false
        timer.start()
    }

    private fun pauseRecorder() {
        recorder.pause()
        isPaused = true
        timer.pause()
    }

    private fun startRecording() {
        recorder = MediaRecorder()
        recorder.apply {

            //record from microphone
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

            dirPath = "${mainActivity.externalCacheDir?.absolutePath}/"
            val simpleDateFormat = SimpleDateFormat("yyyy.MM.DD_hh.mm.ss")
            val date = simpleDateFormat.format(Date())

            fileName = "audio_record_$date"
            setOutputFile("$dirPath$fileName.mp3")

            try {
                prepare()
            } catch (e: IOException) {
                print(e.localizedMessage)
            }
            start()
        }

        viewBinding.btnRecord.setImageResource(R.drawable.ic_pause_24)
        isPaused = false
        isRecording = true
        timer.start()

        viewBinding.btnDelete.isClickable = true

        viewBinding.btnList.isVisible = false
        viewBinding.btnDone.isVisible = true
    }

    private fun stopRecorder() {
        timer.stop()

        recorder.apply {
            stop()
            release()
        }

        isPaused = false
        isRecording = false

        viewBinding.btnList.isVisible = true
        viewBinding.btnDone.isVisible = false
        viewBinding.btnDelete.isClickable = false
        viewBinding.btnDelete.setImageResource(R.drawable.ic_delete)
        viewBinding.btnRecord.setImageResource(R.drawable.ic_record)


        viewBinding.textViewTime.text = "00:00:00"

        amplitudes = viewBinding.waveFormView.clear()
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
            //onPermissionResult enable button
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

        viewBinding.waveFormView.addAmplitude(amp = recorder.maxAmplitude.toFloat())
    }


}