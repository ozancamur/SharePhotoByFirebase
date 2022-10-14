package com.ozancamur.sharephotosbyfirebase.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.ozancamur.sharephotosbyfirebase.R

import kotlinx.android.synthetic.main.activity_share_photo.*
import java.util.UUID

class SharePhotoActivity : AppCompatActivity() {

    var selectedImage : Uri? = null
    var selectedBitmap : Bitmap? = null
    private lateinit var storage : FirebaseStorage
    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_photo)

        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()


    }

    fun share (view : View) {

        // UUID -> Universal Unique ID
        val uuid = UUID.randomUUID()
        val imageName = "${uuid}.jpg"

        //depo işlemleri
        // referans oluşturuyoruz. -> görseli nereye kaydedeceğimizi bu referans sayesinde tek tek söylebiliyoruz.
        val reference = storage.reference
        // reference -> depomuza işaret ediyoruz. firebase storage sayfası
        // .child("images") -> storage'de oluşturdupumuz image dosyasını referans veriyor.
        // .child("selectedimage.jpg") -> storage/images  içerisinde selectedimage.jpg bir dosya olucağını söylüyor ve oraya referans veriyor.
        val imageReference = reference.child("images").child(imageName)

        if ( selectedImage != null ) {

            imageReference.putFile(selectedImage!!).addOnSuccessListener { taskSnapshot ->
                // yollamak için belirlediğimiz bazı bilgiler buradan alınabilir.
                // yüklenen verinin nereye yüklendiğini yeni bir referans oluşturarak alabiliyoruz.
                val savedImageReference = FirebaseStorage.getInstance().reference.child("images").child(imageName)
                savedImageReference.downloadUrl.addOnSuccessListener { uri ->

                    val dowloandURL = uri.toString()  // görselin kaydedildi urlyi aldık
                    // veritabani işlemleri
                    val currentUserEmail = auth.currentUser!!.email.toString()
                    val userExplanation = txtExplanation.text.toString()
                    val date = Timestamp.now()

                    val postHashMap = hashMapOf<String, Any>()
                    postHashMap.put("imageURL",dowloandURL)
                    postHashMap.put("userEmail",currentUserEmail)
                    postHashMap.put("userExplanation",userExplanation)
                    postHashMap.put("date",date)
                    //hangi koleksiyondan okuma veya yazma işlemi yapıcağımı belirtmemiz gerkeiyor
                    database.collection("post").add(postHashMap).addOnCompleteListener { task ->
                        if ( task.isSuccessful ) {
                            finish()
                        }
                    }.addOnFailureListener { exception ->
                        Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
                    }
                    //hashmap olarak oluşturmak -> anahtar elime ve değer eşleşmesi isteniyor.


                }
                // görsel kaydederken aynı komutu kullanıyoruz fakat görsel yüklendikten sonra bu komutu oluşturuyoruz ve şu şekilde kullanabiliyoruz


            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
            }

        }





    }


    fun chooseImage (view : View) {

        if ( ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@SharePhotoActivity,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
        } else {
            val galeriIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galeriIntent,2)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if ( requestCode == 1 ){
            // grantResult -> bize verilen sonuçlar dizisi
            if ( grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                // izin verilince yapılacaklar.
                val galeriIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent, 2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    // izin alındıktan sonra yapılacaklar
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // kullanıcı galeryi açtıktan sonra geride dönebilir. // geriye bir veri geldiyse
        if ( requestCode == 2 && resultCode == Activity.RESULT_OK && data != null ) {
            // Uri -> seçtiğimiz görselin telefonda nerede kayıtlı olduğu gösteren bir konum bilgisidir.
            // uri ve sonradan çevireceğimiz bir bitmap, bitmap yukarıda tanımlanmıştır.
            selectedImage = data.data

            if ( selectedImage != null ) {

                if ( Build.VERSION.SDK_INT >= 28 ) {
                    // SDK 28 üstü ise imageDecoder çalışır

                    val source = ImageDecoder.createSource(this.contentResolver,selectedImage!!)
                    selectedBitmap = ImageDecoder.decodeBitmap(source)
                    imageView.setImageBitmap(selectedBitmap)

                } else {

                    // deprecated -> belli bir sdk üstü telefonda çalışmaz,
                    // selectedImage uri'dan contentResult ile Bitmap'e çevriliyor.
                    selectedBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,selectedImage)
                    imageView.setImageBitmap(selectedBitmap)

                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }



}