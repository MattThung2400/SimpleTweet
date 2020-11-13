package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.parceler.Parcels;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    public static final String TAG = "ComposeActivity";
    public static final int MAX_TWEET_LENGTH = 280;
    public static final String FILE_NAME = "localTweet.txt";

    EditText etCompose;
    Button btnTweet;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApp.getRestClient(this);

        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);

        setupFloatingLabelError(); // For error checking w/ tweet composition editText.

        // If there was a previously drafted tweet, grab it now...
        LoadDraft();

        // Set a click listener on the button...
        // Now make an API call to Twitter to publish the tweet.
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String tweetContent = etCompose.getText().toString();

                if (!tweetContent.isEmpty() && !(tweetContent.length() > MAX_TWEET_LENGTH)) {
                    client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "onSuccess to publish tweet");
                            try {
                                Tweet tweet = Tweet.fromJson(json.jsonObject);
                                Log.i(TAG, "Published tweet says: " + tweet.body);

                                Intent intent = new Intent();
                                intent.putExtra("tweet", Parcels.wrap(tweet));
                                // Set the result code and bundle data for response
                                setResult(RESULT_OK, intent);
                                // Closes the activity, pass data to parent
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "onFailure to publish tweet", throwable);
                        }
                    });
                }
            }
        });

    }

    private void setupFloatingLabelError() {
        final TextInputLayout floatingTweetLabel = (TextInputLayout) findViewById(R.id.tweet_text_input_layout);
        floatingTweetLabel.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() == 0 && etCompose.hasFocus()) {
                    floatingTweetLabel.setError(getString(R.string.tweet_empty));
                    floatingTweetLabel.setErrorEnabled(true);
                } else if (text.length() > MAX_TWEET_LENGTH) {
                    floatingTweetLabel.setError(getString(R.string.tweet_too_long));
                    floatingTweetLabel.setErrorEnabled(true);
                } else {
                    floatingTweetLabel.setErrorEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle("Closing Activity");
        builder.setMessage("Do you want to save your tweet as a draft for future usage?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SaveDraft();
                finish();
            }

        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ClearDraft();
                finish();
            }
        });
        builder.show();
    }

    public void SaveDraft() {
        String text = etCompose.getText().toString();
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(text.getBytes());
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found" , e);
        } catch (IOException e) {
            Log.e(TAG, "Writing file error" , e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.e(TAG, "Writer error" , e);
                }
            }
        }
    }

    public void LoadDraft() {
        FileInputStream fis = null;
        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }

            if (sb.toString().compareTo("") == 0) {
                etCompose.setText(sb.toString());
                etCompose.clearFocus();
            } else {
                etCompose.setText(sb.toString());
                etCompose.requestFocus();
            }

        } catch (FileNotFoundException e) {
            Log.e(TAG, "Data file not found" , e);
        } catch (IOException e) {
            Log.e(TAG, "Read file error" , e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void ClearDraft() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // Use Activity method to create a file in the writeable directory
                FileOutputStream fos = null;
                try {
                    fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "Clearing file error" , e);
                }
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.e(TAG, "Clearing file error" , e);
                }
            }
        });
    }
}