package com.ozancamur.sharephotosbyfirebase.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ozancamur.sharephotosbyfirebase.model.Post
import com.ozancamur.sharephotosbyfirebase.R
import com.ozancamur.sharephotosbyfirebase.adapter.FeedRecyclerAdapter
import kotlinx.android.synthetic.main.activity_feed_acitivty.*

class FeedAcitivty : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseFirestore

    private lateinit var recyclerViewAdapter : FeedRecyclerAdapter

    var postList = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_acitivty)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        getDatas()
        
        var layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerViewAdapter = FeedRecyclerAdapter(postList)
        recyclerView.adapter = recyclerViewAdapter

    }

    fun getDatas() {

        database.collection("post")
            .orderBy("date",Query.Direction.DESCENDING) // date'e göre postları dizmek istiyoruz. Query.Direction.DESCENDING -> düşen (en son girilen tarih en başta çıkacak)
            .addSnapshotListener { snapshot, exception ->

            if ( exception != null ) {

                Toast.makeText(this,exception.localizedMessage,Toast.LENGTH_LONG).show()

            } else {

                if ( snapshot != null ) {
                    // !snapshot.isEmpty -> ünlem işareti değilini gösterir.
                    if ( !snapshot.isEmpty ) {

                        val documents = snapshot.documents

                        postList.clear()

                        // dökümanlara tek tek ulaşmak istiyoruz.
                        for ( document in documents ) {
                            val userEmail = document.get("userEmail") as String
                            val userExplanation = document.get("userExplanation") as String
                            val imageURL = document.get("imageURL") as String

                            val uploadedPost = Post(userEmail,userExplanation,imageURL)
                            postList.add(uploadedPost)

                        }
                        // yeni veri gelince kendini güncelle
                        recyclerViewAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflate = menuInflater
        menuInflater.inflate(R.menu.options_menu,menu)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if ( item.itemId == R.id.share_photo) {

            val intent = Intent(this, SharePhotoActivity::class.java)
            startActivity(intent)

        } else if ( item.itemId == R.id.exit) {

            auth.signOut()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()

        }


        return super.onOptionsItemSelected(item)
    }


}