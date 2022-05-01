package com.example.sismov

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.drawToBitmap
import com.example.sismov.Clases.Usuario
import com.google.gson.Gson
import com.vishnusivadas.advanced_httpurlconnection.PutData
import java.io.ByteArrayOutputStream
import java.sql.Blob
import kotlin.math.log


class UserFragment : Fragment() {
    private var user = Usuario()

    private var btnConfirmChanges = view?.findViewById<Button>(R.id.btnGuardarCambios)

    private var profilePic = view?.findViewById<ImageView>(R.id.ivProfilePicture)
    private var profileName = view?.findViewById<EditText>(R.id.edProfileName)
    private var profileSName = view?.findViewById<EditText>(R.id.edProfileSecondName)
    private var profilePass = view?.findViewById<EditText>(R.id.edProfilePassword)
    private var profileNewPass = view?.findViewById<EditText>(R.id.edNewPassword)
    private var profileNewPassConfirm = view?.findViewById<EditText>(R.id.edNewPasswordConfirm)
    private var profileEmail = view?.findViewById<EditText>(R.id.edProfileEmail)
    private var pbProfile = view?.findViewById<ProgressBar>(R.id.progressBarProfile)

    private val activeUser = ActiveUser()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        getUserInfo()

        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onResume() {
        btnConfirmChanges = view?.findViewById<Button>(R.id.btnGuardarCambios)

        profilePic = view?.findViewById<ImageView>(R.id.ivProfilePicture)

        profileName = view?.findViewById<EditText>(R.id.edProfileName)
        profileSName = view?.findViewById<EditText>(R.id.edProfileSecondName)
        profilePass = view?.findViewById<EditText>(R.id.edProfilePassword)
        profileNewPass = view?.findViewById<EditText>(R.id.edNewPassword)
        profileNewPassConfirm = view?.findViewById<EditText>(R.id.edNewPasswordConfirm)
        profileEmail = view?.findViewById<EditText>(R.id.edProfileEmail)
        pbProfile = view?.findViewById<ProgressBar>(R.id.progressBarProfile)

        btnConfirmChanges?.setOnClickListener {
            var canChange = true
            val email = profileEmail?.text.toString()

            if (email == "") {
                canChange = false
            }

            val newPass = profileNewPass?.text.toString()
            val confNewPass = profileNewPassConfirm?.text.toString()

            if (newPass != confNewPass) {
                canChange = false
            }

            if(canChange) {

                var txtPass = ""

                if (newPass != "") {
                    txtPass = newPass
                } else {
                    txtPass = activeUser.password
                }

                val strBlob = profilePic?.drawToBitmap()?.let { it1 -> BitMapToString(it1) }

                val handler = Handler(Looper.getMainLooper())
                pbProfile?.visibility = View.VISIBLE;
                handler.post(Runnable {
                    //Starting Write and Read data with URL
                    //Creating array for parameters
                    val field = arrayOfNulls<String>(6)
                    field[0] = "id"
                    field[1] = "name"
                    field[2] = "secondname"
                    field[3] = "email"
                    field[4] = "password"
                    field[5] = "image"
                    //Creating array for data
                    val data = arrayOfNulls<String>(6)
                    data[0] = activeUser.id.toString();
                    data[1] = profileName?.text.toString()
                    data[2] = profileSName?.text.toString()
                    data[3] = profileEmail?.text.toString()
                    data[4] = txtPass
                    data[5] = strBlob
                    val putData = PutData(
                        "http://192.168.100.9/php/sistemas-moviles-php/updateUser.php",
                        "POST",
                        field,
                        data
                    )
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            val result = putData.result
                            Log.i("PutData", result)
                            pbProfile?.visibility = View.GONE;
                            if (result == "Cambios realizados con exito") {
                                pbProfile?.visibility = View.GONE;
                                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                                activeUser.email = profileEmail?.text.toString()
                                profileNewPass?.setText("");
                                profileNewPassConfirm?.setText("");
                                getUserInfo()
                            } else {
                                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    //End Write and Read data with URL
                })

            } else {
                Toast.makeText(context, "Revisa tu información", Toast.LENGTH_LONG).show();
            }

        }

        profilePic?.setOnClickListener {
            pickImageFromGallery()
        }

        super.onResume()
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 3)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK && data != null){
            val uri = data?.data
            var bmp = MediaStore.Images.Media.getBitmap(this.context?.contentResolver, uri)
            profilePic?.setImageBitmap(bmp)
        }
    }


    private fun fillInfo() {
        activeUser.id = user.id
        activeUser.user_type_id = user.User_type_id
        activeUser.name = user.name
        activeUser.second_name = user.second_name
        activeUser.email = user.email
        activeUser.password = user.password
        activeUser.imagen = user.imagen
        activeUser.phone = user.phone
        activeUser.creation_date = user.creation_date
        activeUser.active = user.active

        if(activeUser.imagen != null) {
            val byte = Base64.decode(activeUser.imagen, 0)
            val bmp = BitmapFactory.decodeByteArray(byte, 0, byte.size)
            profilePic?.setImageBitmap(bmp)
        }

        // Tipo de usuario 0 -> Cliente
        // Tipo de usuario 1 -> Restaurante

        profileName?.setText(activeUser.name)
        profileSName?.setText(activeUser.second_name)
        profilePass?.setText(activeUser.password)
        profileEmail?.setText(activeUser.email)


    }

    private fun getUserInfo() {
        val handler = Handler(Looper.getMainLooper())
        pbProfile?.visibility = View.VISIBLE;
        handler.post(Runnable {
            //Starting Write and Read data with URL
            //Creating array for parameters
            val field = arrayOfNulls<String>(1)
            field[0] = "email"
            //Creating array for data
            val data = arrayOfNulls<String>(1)
            data[0] = activeUser.email;
            val putData = PutData(
                "http://192.168.100.9/php/sistemas-moviles-php/getUser.php",
                "POST",
                field,
                data
            )
            if (putData.startPut()) {
                if (putData.onComplete()) {
                    val result = putData.result
                    Log.i("PutData", result)
                    if (result != "false") {
                        pbProfile?.visibility = View.GONE;
                        val gson = Gson()
                        user = gson.fromJson(result, Usuario::class.java)
                        fillInfo()
                    } else {
                        Toast.makeText(context, result, Toast.LENGTH_LONG).show()
                    }
                }
            }
            //End Write and Read data with URL
        })
    }

    fun BitMapToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

}