package com.verdugo.tictactoe

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val database =  FirebaseDatabase.getInstance().reference
        val room : MutableMap <String, String> = mutableMapOf()
        val random = Random()
        var idRoom =  random.nextInt(500000..999999).toString()

        buttonCreateRoom.setOnClickListener {
            if (editTextName.text.toString().isEmpty()){
                textViewMessageName.text = "Ingresa un nombre"
            }else{
                room.put("Player1", editTextName.text.toString())
                room.put("Player2","")
                room.put("Type", "privada")
                room.put("Code", idRoom)

                database.child("Room").child(idRoom).setValue(room)

                intent(idRoom)
            }
        }

        imageButtonCode.setOnClickListener {
            if (editTextCode.text.toString().isEmpty()){
                Toast.makeText(this, "Ingresa el código", Toast.LENGTH_SHORT).show()
            }else{
                if (editTextName.text.toString().isEmpty()) {
                    textViewMessageName.text = "Ingresa un nombre"
                }else{
                    room.put("Player2", editTextName.text.toString())
                }

                database.child("Room").child(editTextCode.text.toString()).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()){
                            if (dataSnapshot.child("Player1").getValue().toString().isEmpty()) {
                                room.put("Player1", dataSnapshot.child("Player1").getValue().toString())
                                room.put("Code", dataSnapshot.child("Code").getValue().toString())
                                room.put("Type", dataSnapshot.child("Type").getValue().toString())
                                database.child("Room").child(editTextCode.text.toString()).setValue(room)
                                intent(editTextCode.text.toString())
                            }else{
                                Toast.makeText(this@MainActivity, "No hay lugar para otro jugador", Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(this@MainActivity, "No se encontro sala con ese código", Toast.LENGTH_SHORT).show()
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            }
        }

    }

    fun Random.nextInt(range: IntRange): Int {
        return range.start + nextInt(range.last - range.start)
    }

    fun intent(idRoom: String){
        val intent = Intent(this, createRoomActivity::class.java)
        intent.putExtra("idRoom", idRoom)
        startActivity(intent)
    }

}

