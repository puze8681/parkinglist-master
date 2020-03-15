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
        var myRef = firebaseDatabase.reference.child("list")
        var pushRef = firebaseDatabase.reference.child("push")
        val item = ArrayList<RecyclerData>()
        lateinit var context: Context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this@MainActivity
        recyclerAdapter = RecyclerAdapter(item, this@MainActivity)
        recycler.adapter = recyclerAdapter
        recyclerAdapter.notifyDataSetChanged()

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                item.clear()
                dataSnapshot.children.forEach{
                    it.getValue(Data::class.java)?.let { data ->
                        item.add(RecyclerData(data.phone, data.car, false))
                    }
                }
                recyclerAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        myRef.addValueEventListener(valueEventListener)

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
            item.add(RecyclerData(edit_phone.text.toString(), edit_car.text.toString(), false))
            myRef.setValue(item)
            edit_phone.setText("")
            edit_car.setText("")
        }
    }

    fun removeItem(position: Int) {
        item.removeAt(position)
        myRef.setValue(item)
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
