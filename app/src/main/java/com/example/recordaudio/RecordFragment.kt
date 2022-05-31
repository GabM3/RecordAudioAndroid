package com.example.recordaudio

import android.Manifest.permission
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.example.recordaudio.databinding.FragmentRecordBinding
import java.io.IOException

class RecordFragment : BaseFragment() {
    companion object {
        fun with() =
            RecordFragment().apply {
                /**
                https://www.youtube.com/watch?v=FjFr3_MyGmA&list=PLpZQVidZ65jPz-XIHdWi1iCra8TU9h_kU
                 */
            }
    }

    lateinit var mr: MediaRecorder

    private lateinit var viewBinding: FragmentRecordBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
        viewBinding.buttonPlay.isEnabled = true
        viewBinding.buttonRecord.isEnabled = true

        mr = MediaRecorder()

        viewBinding.buttonRecord.setOnClickListener {
            if (viewBinding.buttonRecord.text == "Record") {
                viewBinding.buttonRecord.text = "Stop"

                mr.apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    val path = "${mainActivity.externalCacheDir?.absolutePath}/"
                    val fileName = "audio_record"
                    setOutputFile("$path$fileName.mp3")

                    try {
                        prepare()
                    } catch (e: IOException) {
                        print(e.localizedMessage)
                    }
                    start()
                }

            } else {
                mr.stop()

                viewBinding.buttonRecord.text = "Record"

            }
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


}