package com.examples.foodwavesadmin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.examples.foodwavesadmin.adapter.DeliveryAdapter
import com.examples.foodwavesadmin.databinding.ActivityOutForDeliveryBinding
import com.examples.foodwavesadmin.model.OrderDetails
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class outForDeliveryActivity : AppCompatActivity() {
    private  val binding: ActivityOutForDeliveryBinding by lazy {
        ActivityOutForDeliveryBinding.inflate(layoutInflater)
    }

    private lateinit var database: FirebaseDatabase
    private  var listOfCompleteOrderList: ArrayList<OrderDetails> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.backButton.setOnClickListener {
            finish()
        }

        // retrieve and display completed order
        retrieveCompletedOrderDetails()


    }

    private fun retrieveCompletedOrderDetails() {
        //initialise firebase database
        database= FirebaseDatabase.getInstance()
        val completeOrderReference=database.reference.child("CompletedOrder")
            .orderByChild("currentTime")
        completeOrderReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear the list before populating it with new data
                listOfCompleteOrderList.clear()
                for (orderSnapshot in snapshot.children){
                   val completeOrder=orderSnapshot.getValue(OrderDetails::class.java)
                    completeOrder?.let {
                        listOfCompleteOrderList.add(it)
                    }
                }
                //reverse the list to display latest order first
                listOfCompleteOrderList.reverse()

                setDataIntoRecyclerView()
            }



            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun setDataIntoRecyclerView() {
        // initialise list to hold customers name and payment status
        val customerName=mutableListOf<String>()
        val moneyStatus=mutableListOf<Boolean>()

        for(order in listOfCompleteOrderList){
            order.userName?.let {
                customerName.add(it)
            }
            moneyStatus.add(order.paymentReceived)
        }
        val adapter= DeliveryAdapter(customerName,moneyStatus)
        binding.outForDeliveryRecyclerView.adapter=adapter
        binding.outForDeliveryRecyclerView.layoutManager= LinearLayoutManager(this)

    }
}