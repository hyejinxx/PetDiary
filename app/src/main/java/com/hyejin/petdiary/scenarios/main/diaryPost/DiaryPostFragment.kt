package com.hyejin.petdiary.scenarios.main.diaryPost

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.hyejin.petdiary.R
import com.hyejin.petdiary.databinding.FragmentDiaryPostBinding
import com.hyejin.petdiary.extensions.showToast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.collect
import java.time.Month
import java.util.*

class DiaryPostFragment : Fragment() {

    val viewModel by viewModels<DiaryPostViewModel>()
    lateinit var binding : FragmentDiaryPostBinding

    // Firebase
    private val auth = Firebase.auth
    private val user = auth.currentUser!!.uid
    private val storage = Firebase.storage("gs://petdiary-9b80e.appspot.com/")
//    private var storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference


    //code
    private val REQUEST_CODE = 99

    private var imageUri :Uri? = null
    // 권한요청
    private fun requestPermission(): Boolean {
        val writePermission: Int = ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val readPermission: Int = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (writePermission == PackageManager.PERMISSION_GRANTED && readPermission == PackageManager.PERMISSION_GRANTED) {
            return true
        } else (return false)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    var isAllGranted = true
                    for (grant in grantResults) {
                        if (grant != PackageManager.PERMISSION_GRANTED) { //권한 거부
                            isAllGranted = false
                            break
                        }
                        if (isAllGranted) {
                        } //권한 동의
                        else { //권한 거부했을경우
                            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                                    requireActivity(),
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                ) ||
                                !ActivityCompat.shouldShowRequestPermissionRationale(
                                    requireActivity(),
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                )
                            ) {
                                requestPermission()
                            } else {
                                break
                            }
                        }
                    }
                }
            }
        }
    }
    private var getContent: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            lateinit var _imageUri : Uri
            result.data?.data?.let {
                _imageUri = it
                Glide.with(this)
                    .load(it)
                    .into(binding.diaryImage)
                binding.diaryImage.clipToOutline
            }
            imageUri = _imageUri
        }
    }
    private fun setImage(){
        if (requestPermission()) {
            var intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            intent.type = "image/*"
            getContent.launch(intent)
        } else {
            ActivityCompat.requestPermissions(
                (requireActivity()), arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_CODE
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initBinding()
        initObserve()
        initClickListener()
        initTextListener()
        getDiaryImage()
        return binding.root
    }
    private fun initBinding(inflater: LayoutInflater = this.layoutInflater, container: ViewGroup? = null){
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_diary_post, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

    }
    private fun initObserve(){
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.event.collect{ event ->
                when(event){
                    DiaryPostViewModel.DiaryPostEvent.ShowCalenderDialog -> calenderPickListener()
                    DiaryPostViewModel.DiaryPostEvent.SaveData -> saveData()
                    DiaryPostViewModel.DiaryPostEvent.PostSuccess -> showToast("저장 완료")
                    is DiaryPostViewModel.DiaryPostEvent.PostFailure -> showToast("저장 실패")
                }
            }
        }
    }
    fun initTextListener(){
        binding.diaryEditText.doAfterTextChanged {
            viewModel.diaryContent.value = it.toString()
        }
    }
    private fun initClickListener() {
        // 갤러리에서 이미지 불러오기
        binding.addImageBtn.setOnClickListener {
            setImage()
        }
    }
    private fun saveData(){
        viewModel.addDiaryContent(
            onSuccess = {showToast("저장")},
            onFailure = {showToast("실패")})
        addDiaryImage()
    }
    private fun addDiaryImage(){
        val fileName = user + viewModel.date.value.toString() +".png"
        imageUri?.let {
            storageRef.child(user).child(fileName).putFile(it)
                .addOnSuccessListener {
                    /*no-op*/
                }
                .addOnFailureListener{
                    showToast("사진 저장에 실패하였습니다.")
                }
        }
    }
    private fun getDiaryImage() {
        val fileName = user + viewModel.date.value.toString() +".png"
        storageRef.child(user).child(fileName).downloadUrl
            .addOnSuccessListener {
                Glide.with(this)
                    .load(it)
                    .into(binding.diaryImage)
                binding.diaryImage.clipToOutline
            }
            .addOnFailureListener {
                binding.diaryImage.setImageResource(R.drawable.img_cat7)
            }
    }
    // 캘린더
    private fun calenderPickListener(){
        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { datePicker, year, month, dayOfMonth
                ->
                viewModel.changeDate(year, month+1, dayOfMonth, monthText = Month.of(month+1))
                getDiaryImage()
//                initUI()
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
