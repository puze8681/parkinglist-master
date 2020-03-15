package kr.puze.parkinglist

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_recycler.view.*

class RecyclerAdapter(var items: ArrayList<RecyclerData>, var context: Context) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    @SuppressLint("LongLogTag")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("LOGTAG, ChatRecyclerAdapter", "onCreate")
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recycler, null))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position)
        holder.itemView.setOnClickListener {
            itemClick?.onItemClick(holder.itemView, position)
        }
        holder.itemView.delete.setOnClickListener {
            MainActivity().removeItem(items[position].key.toString())
            items.removeAt(position)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val context = itemView.context!!
        fun bind(item: RecyclerData, position: Int) {
            if(position == 0){
                itemView.delete.visibility = View.INVISIBLE
                itemView.num.text = "순번"
            }else{
                itemView.num.text = position.toString()
            }
            itemView.phone.text = item.phone.toString()
            itemView.car.text = item.car.toString()

            if(item.out){
                itemView.setBackgroundColor(Color.parseColor("#dedede"))
            }
        }
    }

    private var itemClick: ItemClick? = null

    interface ItemClick {
        fun onItemClick(view: View?, position: Int)
    }

    fun addItem(key:String, phone: String, car: String){
        items.add(RecyclerData(key, phone, car, false))
        notifyDataSetChanged()
    }

    fun findItem(carString: String){
        loop@ for(i in 0 until itemCount){
            if(items[i].car.equals(carString)){
                MainActivity().notification(items[i].car.toString())
                items[i].out = true
                notifyDataSetChanged()
                break@loop
            }
        }
    }
}
