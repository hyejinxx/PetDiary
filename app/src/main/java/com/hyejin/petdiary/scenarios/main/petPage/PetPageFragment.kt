package com.hyejin.petdiary.scenarios.main.petPage

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.hyejin.petdiary.R
import com.hyejin.petdiary.data.Birth
import com.hyejin.petdiary.extensions.showToast
import com.hyejin.petdiary.views.dialog.ModifyBirthDialog
import com.hyejin.petdiary.views.dialog.ModifyDialog
import com.hyejin.petdiary.views.dialog.ModifyGenderDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.hyejin.petdiary.databinding.FragmentPetPageBinding
import kotlinx.coroutines.flow.collect

class PetPageFragment : Fragment() {

    val viewModel by viewModels<PetPageViewModel>()
    lateinit var binding: FragmentPetPageBinding

    // Firebase
    private val auth = Firebase.auth
    private val user = auth.currentUser!!.uid
    private val storage = Firebase.storage("gs://petdiary-9b80e.appspot.com/")
    //    private var storage = FirebaseStorage.getInstance()
    private var storageRef = storage.reference
    var fileName : String = user + ".png"

    // Code
    private val REQUEST_CODE =99

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
    // Image 콜백
    private var getContent: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let {
                Glide.with(this)
                    .load(it)
                    .into(binding.petPageImageView)
                binding.petPageImageView.clipToOutline
                saveImage(it)
            }
        }
    }
    private fun saveImage(uri: Uri){
        storageRef.child(user).child(fileName).putFile(uri)
            .addOnSuccessListener {
                /*no-op*/
            }
            .addOnFailureListener{
                showToast("사진 저장에 실패하였습니다.")
            }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initBinding()
        initObserves()
        initClickListener()
        initUI()
        return binding.root
    }
    private fun initBinding(inflater: LayoutInflater = this.layoutInflater, container: ViewGroup? = null){
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pet_page, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun initClickListener() {
        binding.petPageImageBtn.setOnClickListener {
            setImage()
        }
    }
    private fun initObserves(){
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.event.collect{  event ->
                when(event){
                    PetPageViewModel.PetPageEvent.ShowDialog -> showDialog()
                }
            }
        }
    }

    private fun initUI(){
        storageRef.child(user).child(fileName).downloadUrl
            .addOnSuccessListener {  value ->
                Glide.with(this)
                    .load(value)
                    .into(binding.petPageImageView)
                binding.petPageImageView.clipToOutline
            }
            .addOnFailureListener {
                binding.petPageImageView.setImageResource(R.drawable.img_cat)
            }
    }
    private fun showDialog() {
        val index = viewModel.dataIndex.value
        if (index == 3){
            val dialog = ModifyBirthDialog(requireContext())
            dialog.start()
            dialog.setOnClickListener(object:ModifyBirthDialog.DialogOKCLickListener{
                override fun onOKClicked(birth: Birth) {
                    modifyData(3, birth)
                }
            })
        }else if (index == 2){
            val dialog = ModifyGenderDialog(requireContext())
            dialog.start()
            dialog.setOnClickListener(object: ModifyGenderDialog.DialogOKCLickListener{
                override fun onOKClicked(gender: String) {
                    modifyData(2, gender)
                }
            })
        }
        else{
            val dialog = ModifyDialog(requireContext())
            dialog.start(index)
            dialog.setOnClickListener(object:ModifyDialog.DialogOKCLickListener{
                override fun onOKClicked(adaptedData: String?) {
                    adaptedData?.let {
                        modifyData(index, adaptedData)
                    }?: context?.showToast("변경 사항이 없습니다.")
                }
            })
        }
    }
    private fun modifyData(index : Int, data : Any){
        val choosedData = arrayOf("name", "age", "gender", "birth", "weight")
        viewModel.changePetData(choosedData[index], data)
    }

}