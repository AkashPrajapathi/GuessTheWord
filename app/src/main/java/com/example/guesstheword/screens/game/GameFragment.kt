package com.example.guesstheword.screens.game

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.guesstheword.R
import com.example.guesstheword.databinding.GameFragmentBinding


class GameFragment : Fragment() {

    private lateinit var binding: GameFragmentBinding

    private lateinit var gameViewModel: GameViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.game_fragment,
            container,
            false
        )

        gameViewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        binding.gameViewModel = gameViewModel
        binding.setLifecycleOwner(this)

        gameViewModel.eventGameFinish.observe(viewLifecycleOwner) { isFinish ->

            if (isFinish) {
                val currentScorfe = gameViewModel.score.value ?: 0
                val action = GameFragmentDirections.actionGameToScore(currentScorfe)
                findNavController().navigate(action)
                gameViewModel.onGameFinishComplete()
            }

        }

        gameViewModel.eventBuzz.observe(viewLifecycleOwner) { buzzType ->

            if (buzzType != GameViewModel.BuzzType.NO_BUZZ) {
                buzz(buzzType.pattern)
                gameViewModel.onBuzzComplete()
            }
        }

        gameViewModel.currentTime.observe(viewLifecycleOwner) { newTime ->
            binding.timerText.text = DateUtils.formatElapsedTime(newTime)
        }

        return binding.root
    }

    private fun buzz(pattern: LongArray) {
        val buzzer = activity?.getSystemService<Vibrator>()

        buzzer?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                buzzer.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                //deprecated in API 26
                buzzer.vibrate(pattern, -1)
            }
        }
    }

}