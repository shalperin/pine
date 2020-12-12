package com.samhalperin.android.pine.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.samhalperin.android.pine.databinding.FragmentTimerBinding
import com.samhalperin.android.pine.entities.Timer
import com.samhalperin.android.pine.entities.isInPast
import com.samhalperin.android.pine.entities.timeLeft
import com.samhalperin.android.pine.viewmodels.TimerViewModel
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.concurrent.schedule


class TimerFragment : Fragment() {
    private val model : TimerViewModel by viewModels()
    private lateinit var binding: FragmentTimerBinding
    private var job: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setTimer.setOnClickListener(onSet)
        binding.reset.setOnClickListener(onReset)
    }

    override fun onStop() {
        super.onStop()
        job?.cancel()
    }

    override fun onResume() {
        super.onResume()
        model.currentTimer().observe(viewLifecycleOwner, Observer {timer-> onTimer(timer)})
    }

    fun onTimer(timer: Timer?) {
        if (timer == null) {
            job?.cancel()
            binding.clock.text = "00h 00m"
            binding.progress.visibility = View.GONE
        } else {
            job?.cancel()
            job = GlobalScope.launch(Dispatchers.Main) {
                binding.progress.visibility = View.VISIBLE
                while(!timer.isInPast()) {
                    binding.clock.text = timer.timeLeft()
                    delay(60000)
                }
                binding.progress.visibility = View.GONE
            }
        }
    }

    val onSet = object: View.OnClickListener {
        override fun onClick(p0: View?) {
            val NOT_SET = -999

            var year = NOT_SET
            var month = NOT_SET
            var day = NOT_SET
            var hour = NOT_SET
            var minute = NOT_SET

            val timeDialog = TimePickerDialog(
                requireContext(), TimePickerDialog.OnTimeSetListener{_, h, m ->
                    hour = h
                    minute = m

                    val fields = listOf(year, month, day, hour, minute)
                    if (!fields.contains(NOT_SET)) {
                        val t = Timer(LocalDateTime.of(year, month, day, hour, minute))
                        if (t.isInPast()) {
                            Toast.makeText(requireContext(), "Timer can not be in the past.", Toast.LENGTH_LONG)
                                .show()
                        } else {
                            model.setTimer(t)
                            Toast.makeText(requireContext(), "Timer set.", Toast.LENGTH_LONG)
                                .show()
                        }
                    }

                },
                LocalTime.now().hour,
                LocalTime.now().minute,
                false
            )

            val dateDialog = DatePickerDialog(requireContext()).apply {
                setOnDateSetListener(DatePickerDialog.OnDateSetListener{picker, y, m, d ->
                    year = y
                    month = m + 1
                    day = d
                    timeDialog.show()
                })
            }

            dateDialog.show()

        }
    }

   val onReset = View.OnClickListener { model.clearTimer() }
}
