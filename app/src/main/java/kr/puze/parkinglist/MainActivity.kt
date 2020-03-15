package kr.puze.parkinglist

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ChildEventListener
import kotlin.collections.ArrayList
import android.media.RingtoneManager

@SuppressLint("LongLogTag")
class MainActivity : AppCompatActivity() {

    companion object{
        lateinit var recyclerAdapter: RecyclerAdapter
        val firebaseDatabase = FirebaseDatabase.getInstance()
        var myRef = firebaseDatabase.reference.child("data")
        var pushRef = firebaseDatabase.reference.child("push")
        lateinit var context: Context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this@MainActivity
        val item = ArrayList<RecyclerData>()
        item.add(RecyclerData("key","번호", "차 번호", false))
        recyclerAdapter = RecyclerAdapter(item, this@MainActivity)
        recycler.adapter = recyclerAdapter
        recyclerAdapter.notifyDataSetChanged()

//        val valueEventListener = object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val list = ArrayList()
//                for (ds in dataSnapshot.children) {
//                    val uid = ds.key
//                    list.add(uid)
//                }
//
//                //Do what you need to do with your list
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                Log.d(FragmentActivity.TAG, databaseError.message) //Don't ignore errors!
//            }
//        }


        myRef.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    val data = dataSnapshot.getValue<Data>(Data::class.java)
                    Log.d("LOGTAG, myRef onChildAdded:", data.toString())
                    item.add(RecyclerData(dataSnapshot.key.toString(),data!!.phone!!, data.car!!, false))
                    recyclerAdapter.notifyDataSetChanged()
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}

                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                    val data = dataSnapshot.getValue<Data>(Data::class.java)
                    Log.d("LOGTAG, myRef onChildRemoved:", data.toString())
                }

                override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}

                override fun onCancelled(databaseError: DatabaseError) {}
            })

        pushRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val data = dataSnapshot.getValue<String>(String::class.java)
                Log.d("LOGTAG, onChildAdded:", data.toString())
                playSound()
                recyclerAdapter.findItem(data!!)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                val data = dataSnapshot.getValue<String>(String::class.java)
                Log.d("LOGTAG, onChildChanged:", data.toString())
                playSound()
                recyclerAdapter.findItem(data!!)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.getValue<String>(String::class.java)
                Log.d("LOGTAG, onChildRemoved:", data.toString())
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
                val data = dataSnapshot.getValue<String>(String::class.java)
                Log.d("LOGTAG, onChildMoved:", data.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        button.setOnClickListener {
            myRef.push().setValue(Data(edit_phone.text.toString(), edit_car.text.toString()))
            edit_phone.setText("")
            edit_car.setText("")
        }
    }

    fun removeItem(key: String) {
        myRef.child(key).removeValue()
    }

    fun notification(car: String){
        Toast.makeText(context, "차량번호 $car 출차합니다.", Toast.LENGTH_LONG).show()
    }

    fun playSound(){
        val ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(applicationContext, RingtoneManager.TYPE_NOTIFICATION)
        val ringtone = RingtoneManager.getRingtone(applicationContext, ringtoneUri)
        ringtone.play()
    }

    @IgnoreExtraProperties
    data class Data(
        var phone: String? = "",
        var car: String? = ""
    )
}
