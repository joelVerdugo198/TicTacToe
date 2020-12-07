package com.verdugo.tictactoe

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private val database =  FirebaseDatabase.getInstance().reference
    private val random = Random()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val room : MutableMap <String, String> = mutableMapOf()
        var idRoom: String = random.nextInt(100000..999999).toString()
        idRoom = validateRandom(idRoom)

        tryToGetDynamicLink()

        buttonCreateRoom.setOnClickListener {
            if (editTextName.text.toString().isEmpty()){
                textViewMessageName.text = "Ingresa un nombre"
            }else{
                room.put("Player1", editTextName.text.toString())
                room.put("Player2","")
                room.put("Code", validateRandom(idRoom))

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

                database.child("Room").child(editTextCode.text.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()){
                            if (!dataSnapshot.child("Player2").getValue().toString().isEmpty()) {
                                Toast.makeText(this@MainActivity, "No hay lugar para otro jugador", Toast.LENGTH_SHORT).show()
                            }else{
                                room.put("Player1", dataSnapshot.child("Player1").getValue().toString())
                                room.put("Code", dataSnapshot.child("Code").getValue().toString())
                                database.child("Room").child(editTextCode.text.toString()).setValue(room)
                                intent(editTextCode.text.toString())
                            }
                        }else{
                            Toast.makeText(this@MainActivity, "No se encontro una sala con ese código", Toast.LENGTH_SHORT).show()
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

    fun validateRandom(idRoom: String): String{
        database.child("Room").child(editTextCode.text.toString()).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()){
                    if (idRoom.equals(dataSnapshot.child("Code").getValue().toString())){
                        validateRandom(random.nextInt(100000..999999).toString())
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        return idRoom
    }

    fun intent(idRoom: String){
        val intent = Intent(this, createRoomActivity::class.java)
        intent.putExtra("idRoom", idRoom)
        startActivity(intent)
    }

    fun tryToGetDynamicLink(){
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                    if (deepLink != null){
                        deepLink = pendingDynamicLinkData.link

                        if (deepLink != null) {
                            val code = deepLink.getQueryParameter("code")
                            if (code != null) {
                                editTextCode.setText(code)
                                buttonCreateRoom.setVisibility(View.GONE)
                            }
                        }
                    }
                }


            }
            .addOnFailureListener(this) { e -> Log.w("Error", "Fallo al redireccionar", e) }
    }

}

