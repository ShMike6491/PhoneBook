package com.phonebook

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.phonebook.databinding.ActivityMainBinding

const val REQUEST_CODE = 12

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        checkPermission()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            REQUEST_CODE -> {
                handleResult(grantResults)
                return
            }
        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            == PackageManager.PERMISSION_GRANTED) {
            getContacts()
        } else {
            requestPermission()
        }
    }

    private fun handleResult(grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getContacts()
        } else {
            AlertDialog.Builder(this)
                .setTitle("Доступ к контактам")
                .setMessage("Нам необходим доступ, чтобы украсть всю личную информацию")
                .setNegativeButton("Ок") { dialog, _ -> dialog.dismiss()}
                .create()
                .show()
        }
    }

    private fun getContacts() {
        val contentResolver: ContentResolver = this.contentResolver
        val cursor: Cursor? = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        )
        cursor?.let {
            for (i in 0..it.count) {
                if (it.moveToPosition(i)) {
                    val name =
                        it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

                    addContact(name)
                }
            }
        }
        cursor?.close()
    }

    private fun addContact(name: String?) {
        binding.contacts.addView(AppCompatTextView(this).apply {
            text = name
        })
    }

    private fun requestPermission() {
        requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_CODE)
    }

}