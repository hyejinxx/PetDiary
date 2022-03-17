package com.example.petdiary.Fragment

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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.petdiary.R
import com.example.petdiary.data.DiaryContents
import com.example.petdiary.data.ImageURI
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class DiaryPost : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private val REQUEST_CODE =99
    var uriPhoto : Uri? = null
    val auth = Firebase.auth
    var user = auth.currentUser?.uid
    val db = FirebaseFirestore.getInstance()
    private var storage : FirebaseStorage? = null

    lateinit var imageView: ImageView


    lateinit var dateArray : ArrayList<Int>
    lateinit var date : String

    fun requestPermission(): Boolean {
        var writePermission: Int = ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        var readPermission: Int = ContextCompat.checkSelfPermission(
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_diary_post, container, false)
        arguments?.let {
            dateArray = it.getIntegerArrayList("date") as ArrayList<Int>
        }


        //반려동물 사진
        imageView = view.findViewById(R.id.diaryImage)
        storage = FirebaseStorage.getInstance()

//        var getContent: ActivityResultLauncher<Intent> = registerForActivityResult(
//            ActivityResultContracts.StartActivityForResult()
//        ) { result: ActivityResult ->
//            if (result.resultCode == AppCompatActivity.RESULT_OK) {
//                uriPhoto = result.data?.data
//                image.setImageURI(uriPhoto)
//            }
//        }

        imageView.setOnClickListener{
            if (requestPermission()) {
                var intent = Intent(Intent.ACTION_GET_CONTENT)

                intent.type = MediaStore.Images.Media.CONTENT_TYPE
                intent.type = "image/*"
                startActivityForResult(intent, REQUEST_CODE)
            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    REQUEST_CODE
                )
            }
        }

        var diaryEditText : EditText = view.findViewById(R.id.diaryText)
        var diary : String = diaryEditText.text.toString()

        var btnSave : Button = view.findViewById(R.id.btn_save)
        btnSave.setOnClickListener {
            SaveDiary(user, dateArray, diary)
        }
        return view
    }

    fun SaveDiary(user : String?,dateArray : ArrayList<Int>, content : String){
        date = dateArray.toString()
        var data = DiaryContents(dateArray, content)
        db.collection("UsersData").document(user!!).collection("Diary").document(date).set(data)
            .addOnSuccessListener {
                Toast.makeText(context, "저장완료", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                Toast.makeText(context, "저장실패", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        date = dateArray.toString()

        if (resultCode == REQUEST_CODE){
            uriPhoto = data?.data
            imageView.setImageURI(uriPhoto)

            var uri = ImageURI(uriPhoto)
            var filename : String = "profile" + date + ".jpg"
            var storageRef = storage?.reference?.child("images")?.child(user!!)?.child(filename)

            storageRef?.putFile(uriPhoto!!)?.addOnSuccessListener {
                db.collection("UsersData").document(user!!).collection("Diary").document(date).set(uri)
            }

        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ScadulePost.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DiaryPost().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
