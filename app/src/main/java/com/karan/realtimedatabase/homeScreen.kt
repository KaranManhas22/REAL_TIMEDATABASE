package com.karan.realtimedatabase

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog.show
import android.nfc.Tag
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.karan.realtimedatabase.databinding.CustomDialogboxBinding
import com.karan.realtimedatabase.databinding.FragmentHomeScreenBinding

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
    var dbRefrence: DatabaseReference = FirebaseDatabase.getInstance().reference
    var array = ArrayList<Items>()
    var recyclerAdapter = recyclerAdapter(array, this)

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
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
                            dialogboxBinding.etNumber.text.toString().toInt()
                        )
                        dbRefrence.push().setValue(items).addOnCompleteListener {
                            Toast.makeText(requireContext(), "Menu ADD", Toast.LENGTH_SHORT).show()
                        }
                            .addOnFailureListener { err ->
                                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT)
                                    .show()
                            }

                        recyclerAdapter.notifyDataSetChanged()
                        dismiss()
                    }

                }
                show()
            }
        }
        return binding.root
    }


    override fun update_data(items: Items, position: Int) {
        val dialogboxBinding = CustomDialogboxBinding.inflate(layoutInflater)
        val update_dialog = Dialog(requireContext()).apply {
            setContentView(dialogboxBinding.root)
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
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

            }
                .addOnFailureListener {
                }

            recyclerAdapter.notifyDataSetChanged()
            update_dialog.dismiss()
        }
    }

    override fun delete_data(items: Items, position: Int) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("You want to Delete")
            setPositiveButton("yes")
            { _, _ ->
                dbRefrence.child(items.id ?: "").removeValue()
            }
            recyclerAdapter.notifyDataSetChanged()
            setNegativeButton("No")

            { _, _ ->

            }
            setCancelable(false)
        }
            .show()
    }

    override fun click(items: Items, position: Int) {
        var bundle = Bundle()
        bundle.putString("name", array[position].name)
        bundle.putString("class", array[position].Etclass)
        bundle.putString("number", array[position].number.toString().toInt().toString())
        findNavController().navigate(R.id.action_homeScreen_to_detail_screen, bundle)
    }
}
