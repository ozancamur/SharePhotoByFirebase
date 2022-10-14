package com.ozancamur.sharephotosbyfirebase.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.ozancamur.sharephotosbyfirebase.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser

        if ( currentUser != null ) {
            val intent = Intent(this, FeedAcitivty::class.java)
            startActivity(intent)
        }



    }

    fun girisYap ( view : View) {


        auth.signInWithEmailAndPassword(txtEmail.text.toString(),txtPassword.text.toString()).addOnCompleteListener { task ->
            if ( task.isSuccessful ) {

                val currentUser = auth.currentUser?.email.toString()
                Toast.makeText(this,"Hoşgeldiniz: ${currentUser}",Toast.LENGTH_LONG).show()

                val intent = Intent(this, FeedAcitivty::class.java)
                startActivity(intent)

            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this,exception.localizedMessage,Toast.LENGTH_LONG).show()
        }
        finish()


    }

    fun kayitOl ( view : View ) {

        val email = txtEmail.text.toString()
        val password = txtPassword.text.toString()

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if ( task.isSuccessful ) {
                val intent = Intent(this, FeedAcitivty::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
        }
        /*
        listener ekleyebiliriz.
        çünküinternetten işlem yapmaya başlıyoruz.
        veri çekme/alma, diğer işlemler, kaç sn de olacak cevap verecek bilmiyoruz
        bunları asenkron şekilde çalıştırmamız gerkeiyor ki
        main tredy boklamayalım
        bu aynı handler bölümünde gördüğpümüz gibi kullanıcı arayüzü bloklamadan
        arka planda işlem yaptırmakla aynı şey
        biz internetten bu isteğin ne zaman gideceğini kullanıcının nezamn oluşualcağını bişmioyuz
        ama bundan sonra hemen yazdığımız kod saniyesinde hemen çalıştırılacağı için çok mantıklı olmaz
        biz burada işlemleri yapıldı mı yapılmadı mı ona göre işlem yaptırmak istiyoruz.
        o sebebplede sdk ieçrisinde hazır bizim için tanımlanmış asenkron çalışan senkronize olmayan bir şekilde
        zamana göre değilde gelen cevaba göre çalışan fonksiyonlar öevcut.
         */

    }

}