package com.verdugo.tictactoe

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_create_room.*
import java.util.*

class createRoomActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_room)

        val database =  FirebaseDatabase.getInstance().reference
        val idRoom: String = getIntent().getExtras().getString("idRoom")

        database.child("Room").child(idRoom).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()){
                    textViewPlayer1.text = dataSnapshot.child("Player1").getValue().toString()
                    textViewPlayer2.text = dataSnapshot.child("Player2").getValue().toString()
                    textViewCode.text = dataSnapshot.child("Code").getValue().toString()
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        buttonInvitar.setOnClickListener {
            generateDynamicLink(idRoom)
        }

    }

     fun generateDynamicLink(idRoom: String) {
        Firebase.dynamicLinks.shortLinkAsync {
            link = Uri.parse("https://verdugo.page.link/mVFa?code=$idRoom")
            domainUriPrefix = "https://verdugo.page.link"
            // Set parameters
            androidParameters("com.verdugo.tictactoe") {
                minimumVersion = 125
            }
            socialMetaTagParameters {
                title = "Tic Tac Toe Código:$idRoom"
                description = "¡Ven y juega conmigo!"
                imageUrl = Uri.parse("https://miro.medium.com/max/395/1*mIjIjWIUc45MQjLDVkOC-w.png")
            }
        }.addOnSuccessListener { (shortLink, flowchartLink) ->
            // Short link created
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, shortLink.toString())
            sendIntent.type = "text/plain"
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }.addOnFailureListener {
            // Error
            Toast.makeText(this@createRoomActivity, "Error al generar el enlace", Toast.LENGTH_SHORT).show()

        }
     }

}
