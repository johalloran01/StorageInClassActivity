package com.example.networkapp

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException
import java.lang.StringBuilder

// TODO (1: Fix any bugs)
// TODO (2: Add function saveComic(...) to save and load comic info automatically when app starts)

private const val AUTO_SAVE_KEY = "auto_save"
class MainActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    lateinit var titleTextView: TextView
    lateinit var descriptionTextView: TextView
    lateinit var numberEditText: EditText
    lateinit var showButton: Button
    lateinit var comicImageView: ImageView

    //Create values and variables for save functionality
    private var autoSave = false
    private lateinit var preferences: SharedPreferences
    private val internalFileName = "my_file"
    private lateinit var file: File

    private fun downloadComic (comicId: String) {
        val url = "https://xkcd.com/$comicId/info.0.json"
        requestQueue.add (
            JsonObjectRequest(url, {showComic(it)}, {
            })
        )
    }

    private fun showComic (comicObject: JSONObject){

        titleTextView.text = comicObject.getString("title")
        descriptionTextView.text = comicObject.getString("alt")
        Picasso.get().load(comicObject.getString("img")).into(comicImageView)
        saveComic(comicObject)
    }

    //Create a function called save comic
    private fun saveComic(comicObject: JSONObject) {
        val outputStream = FileOutputStream(file)
        outputStream.write(comicObject.toString().toByteArray())
        outputStream.close()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Get preferences
        preferences = getPreferences(MODE_PRIVATE)

        //Create file reference for app specific file
        file = File(filesDir, internalFileName)

        // Read last saved value from preferences, or false if no value saved
        autoSave = preferences.getBoolean(AUTO_SAVE_KEY, false)

        requestQueue = Volley.newRequestQueue(this)

        titleTextView = findViewById<TextView>(R.id.comicTitleTextView)
        descriptionTextView = findViewById<TextView>(R.id.comicDescriptionTextView)
        numberEditText = findViewById<EditText>(R.id.comicNumberEditText)
        showButton = findViewById<Button>(R.id.showComicButton)
        comicImageView = findViewById<ImageView>(R.id.comicImageView)

        if (file.exists()) {
            val br = BufferedReader(FileReader(file))
            val text = StringBuilder()
            var line: String?
            while (br.readLine().also { line = it } != null) {
                text.append(line)
                text.append('\n')
            }
            showComic(JSONObject(text.toString()))

        }

        showButton.setOnClickListener {
            downloadComic(numberEditText.text.toString())
        }

    }



}