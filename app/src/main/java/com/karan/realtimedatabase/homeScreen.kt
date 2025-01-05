package com.karan.realtimedatabase

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog.show
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.karan.realtimedatabase.databinding.CustomDialogboxBinding
import com.karan.realtimedatabase.databinding.FragmentHomeScreenBinding
import io.github.jan.supabase.SupabaseClient

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [homeScreen.newInstance] factory method to
 * create an instance of this fragment.
 */
class homeScreen : Fragment(), Recycler_btn {

    lateinit var binding: FragmentHomeScreenBinding
    var dbRefrence: DatabaseReference =
        FirebaseDatabase.getInstance().reference //database initialization and decleration
    var array = ArrayList<Items>()
    var recyclerAdapter = recyclerAdapter(array, this)

    //Supabase

    var PICK_IMAGE_REQUEST = 1
    var PERMISSION_REQUEST_CODE = 100
    var MANAGE_EXTERNAL_STORAGE_REQUEST_CODE = 101
    lateinit var supabaseClient: SupabaseClient

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        supabaseClient = (requireContext() as MyApplication).supabaseClient
        checkAndRequestPermissions()

        dbRefrence.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                Log.e(TAG,"snapshot ${snapshot.value}")
                val items: Items? = snapshot.getValue(Items::class.java)
                items?.id = snapshot.key
                if (items != null) {
                    array.add(items)
                    recyclerAdapter.notifyDataSetChanged()
                }

            }

            override fun onChildChanged(
                snapshot: DataSnapshot, previousChildName: String?
            ) { //used for update
                var items: Items? = snapshot.getValue(Items::class.java)
                items?.id = snapshot.key
                if (items != null) {
                    array.forEachIndexed { index, items ->
                        if (items.id == items.id) {
                            array[index] = items
                            return@forEachIndexed
                        }
                    }
                    recyclerAdapter.notifyDataSetChanged()
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                var items: Items? = snapshot.getValue(Items::class.java)
                items?.id = snapshot.key
                if (items != null) {
                    array.remove(items)
                    recyclerAdapter.notifyDataSetChanged()
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


    }

    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {

                } else {
                    requestManageExternalStoragePermission()
                }
            } else {
                if (ContextCompat.checkSelfPermission(
                        requireContext(), Manifest.permission.MANAGE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestManageExternalStoragePermission()
                }
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun requestManageExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                startActivityForResult(intent, PERMISSION_REQUEST_CODE)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(requireContext(), "Activity not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {                          //Android 10 below
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(requireContext(), "Permission granted", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(requireContext(), "Permission granted", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            MANAGE_EXTERNAL_STORAGE_REQUEST_CODE -> {             //Android 10 above
                if (Environment.isExternalStorageManager()) {
                    Toast.makeText(requireContext(), "Full storage granted", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(requireContext(), "Full storage not granted", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        Toast.makeText(this, "result code: ${requestCode}", Toast.LENGTH_SHORT).show()
//        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST) {
//            data?.data?.let { uri ->
//                binding..setImageURI(uri)
//                uploadImageToSupabase(uri)
//            }
//        }




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeScreenBinding.inflate(layoutInflater)
        var linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerList.layoutManager = linearLayoutManager
        binding.recyclerList.adapter = recyclerAdapter
        binding.recyclerList.setHasFixedSize(true)
        binding.btnFab.setOnClickListener {
            var dialogboxBinding = CustomDialogboxBinding.inflate(layoutInflater)
            Dialog(requireContext()).apply {
                setContentView(dialogboxBinding.root)
                window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
                dialogboxBinding.btnSave.setOnClickListener {

                    if (dialogboxBinding.etName.text.toString().trim().isNullOrEmpty()) {
                        dialogboxBinding.etName.error = "Enter the name"
                    } else if (dialogboxBinding.etclass.text.toString().trim().isNullOrEmpty()) {
                        dialogboxBinding.etclass.error = "Enter the class"
                    } else if (dialogboxBinding.etNumber.text.toString().trim().isNullOrEmpty()) {
                        dialogboxBinding.etNumber.error = "Enter the number"
                    } else {
                        var items = Items(
                            "",
                            dialogboxBinding.etName.text.toString(),
                            dialogboxBinding.etclass.text.toString(),
                            dialogboxBinding.etNumber.text.toString().toInt(),
//                            dialogboxBinding.setImage.background.toString()
                            dialogboxBinding.setImage.setOnClickListener {
                                val intent = Intent(
                                    Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                                )
                                startActivityForResult(intent, PICK_IMAGE_REQUEST)
                            }.toString()


                        )
                        dbRefrence.push().setValue(items).addOnCompleteListener {
                            Toast.makeText(requireContext(), "Menu ADD", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener { err ->
                                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT)
                                    .show()
                            }


                        recyclerAdapter.notifyDataSetChanged()
                        dismiss()
                    }


                    show()
                }
            }
        }


        return binding.root
    }


    override fun update_data(items: Items, position: Int) {
        val dialogboxBinding = CustomDialogboxBinding.inflate(layoutInflater)
        val update_dialog = Dialog(requireContext()).apply {
            setContentView(dialogboxBinding.root)
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            show()
        }
        val old_name: String = array[position].name.toString()
        val old_class: String = array[position].Etclass.toString()
        val old_number: String = array[position].number.toString().toInt().toString()
        var dialog = dialogboxBinding
        dialog.etName.setText(old_name)
        dialog.etclass.setText(old_class)
        dialog.etNumber.setText(old_number)

        dialog.btnSave.setOnClickListener {
            val updated_itesms = Items(
                "",
                dialogboxBinding.etName.text.toString(),
                dialogboxBinding.etclass.text.toString(),
                dialogboxBinding.etNumber.text.toString().toInt()
            )
            dbRefrence.child(items.id ?: "").setValue(updated_itesms).addOnCompleteListener {

            }.addOnFailureListener {}

            recyclerAdapter.notifyDataSetChanged()
            update_dialog.dismiss()
        }
    }

    override fun delete_data(items: Items, position: Int) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("You want to Delete")
            setPositiveButton("yes") { _, _ ->
                dbRefrence.child(items.id ?: "").removeValue()
            }
            recyclerAdapter.notifyDataSetChanged()
            setNegativeButton("No")

            { _, _ ->

            }
            setCancelable(false)
        }.show()
    }

    override fun click(items: Items, position: Int) {
        var bundle = Bundle()
        bundle.putString("name", array[position].name)
        bundle.putString("class", array[position].Etclass)
        bundle.putString("number", array[position].number.toString().toInt().toString())
        findNavController().navigate(R.id.action_homeScreen_to_detail_screen, bundle)
    }


}
