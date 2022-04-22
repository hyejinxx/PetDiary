package com.hyejin.petdiary.scenarios.main.schedulePost

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.hyejin.petdiary.R
import com.hyejin.petdiary.databinding.FragmentSchedulePostBinding
import com.hyejin.petdiary.extensions.showToast
import com.hyejin.petdiary.views.dialog.ScheduleDialog
import kotlinx.coroutines.flow.collect
import java.time.Month
import java.util.*

class SchedulePostFragment : Fragment() {

    val viewModel by viewModels<SchedulePostViewModel>()
    lateinit var binding : FragmentSchedulePostBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initBinding()
        initObserve()

        return binding.root
    }

    private fun initBinding(inflater: LayoutInflater = this.layoutInflater, container: ViewGroup? = null ){
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_schedule_post, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }
    private fun initObserve(){
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.event.collect { event ->
                when (event) {
                    SchedulePostViewModel.SchedulePostEvent.ShowScheduleDialog -> showScheduleDialog()
                    SchedulePostViewModel.SchedulePostEvent.ShowCalenderDialog -> calenderPickListener()
                    SchedulePostViewModel.SchedulePostEvent.PostSuccess -> showToast("일정이 추가되었습니다")
                    is SchedulePostViewModel.SchedulePostEvent.PostFailure -> showToast("일정 추가에 실패하였습니다")
                }
            }
        }
    }
    private fun showScheduleDialog(){
        val dialog = ScheduleDialog(requireContext())
        dialog.start(viewModel.date.value)
        dialog.setOnClickListener(object:ScheduleDialog.DialogOKCLickListener{
            override fun onOKClicked(schedule: String) {
                viewModel.schedule.value = schedule
                viewModel.addSchedule(
                    onSuccess = { showToast("일정이 추가되었습니다") },
                    onFailure = { showToast("일정 추가에 실패하였습니다") }
                )
            }
        })
    }
    //캘린더
    private fun calenderPickListener() {
        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { datePicker, year, month, dayOfMonth
                ->
                viewModel.changeDate(year, month, dayOfMonth, Month.of(month+1))
            }
        DatePickerDialog(
            requireContext(), R.style.DatePickerStyle,
            dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}



