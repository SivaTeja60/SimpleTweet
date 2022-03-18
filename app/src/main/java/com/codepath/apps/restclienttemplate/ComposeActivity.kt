package com.codepath.apps.restclienttemplate

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class ComposeActivity : AppCompatActivity() {

    lateinit var etCompose: EditText
    lateinit var btnTweet: Button
    lateinit var charCount: TextView
    lateinit var client: TwitterClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        etCompose = findViewById(R.id.etTweetCompose)
        btnTweet = findViewById(R.id.btnTweet)
        charCount = findViewById(R.id.charCount)
        client = TwitterApplication.getRestClient(this)

        // set event for edit text change
        etCompose.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.i(TAG, "Before text change")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.i(TAG, "Text has changed!")
            }

            override fun afterTextChanged(p0: Editable?) {
                val lengthOfTweet = etCompose.text.toString().length
                val remainingChars = 280 - lengthOfTweet
                // turn text red and disable button
                if (remainingChars < 0) {
                    charCount.setTextColor(Color.RED)
                    btnTweet.isClickable = false
                    btnTweet.isEnabled = false
                } else {
                    charCount.setTextColor(Color.BLACK)
                    btnTweet.isClickable = true
                    btnTweet.isEnabled = true
                }
                // update remaining chars
                charCount.setText(remainingChars.toString())

            }
        })

        // Handling the tweet click
        btnTweet.setOnClickListener{

            //Grab the content of edittext(etCompose)
            val tweetContent = etCompose.text.toString()

            //1. Make sure the tweet isn't empty
            if (tweetContent.isEmpty()){
                Toast.makeText(this,"Empty tweets not allowed!", Toast.LENGTH_SHORT).show()
            }
            //2. Make sure the tweet is under character count
            else if(tweetContent.length > 280){
                Toast.makeText(this,"Tweet is too long! Limit is 280 characters", Toast.LENGTH_SHORT).show()
            }
            else{
                client.publishTweet(tweetContent, object : JsonHttpResponseHandler() {
                    override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                        //Send the tweet back to Timeline Activity
                        Log.i(TAG,"Successfully published tweet!")

                        val tweet = Tweet.fromJson(json.jsonObject)

                        val intent= Intent()
                        intent.putExtra("tweet", tweet)
                        setResult(RESULT_OK,intent)
                        finish()

                    }
                    override fun onFailure(
                        statusCode: Int,
                        headers: Headers?,
                        response: String?,
                        throwable: Throwable?
                    ) {
                        Log.e(TAG, "Failed to publish Tweet", throwable)
                    }

                })
            }


            }
    }
    companion object{
        val TAG = "ComposeActivity"
    }
}