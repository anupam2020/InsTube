package com.sbdev.insta_youtube_video_downloader;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputEditText;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.HashMap;

public class FeedbackFragment extends Fragment {

    private TextInputEditText email;
    private Button send;

    private ImageView greenEmoji,yellowEmoji,redEmoji;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        email=view.findViewById(R.id.feedbackEditText1);

        send=view.findViewById(R.id.feedbackButton);

        greenEmoji=view.findViewById(R.id.verySatisfied);
        yellowEmoji=view.findViewById(R.id.satisfied);
        redEmoji=view.findViewById(R.id.veryDissatisfied);


        greenEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                greenEmoji.setColorFilter(ContextCompat.getColor(getContext(), R.color.green));
                yellowEmoji.setColorFilter(ContextCompat.getColor(getContext(), R.color.grey));
                redEmoji.setColorFilter(ContextCompat.getColor(getContext(), R.color.grey));

                String feedback="Great to hear it from you!";

                DynamicToast.make(getActivity(), feedback, getResources().getDrawable(R.drawable.ic_baseline_sentiment_very_satisfied_24),
                        getResources().getColor(R.color.green), getResources().getColor(R.color.black), 2000).show();


            }
        });


        yellowEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                greenEmoji.setColorFilter(ContextCompat.getColor(getContext(), R.color.grey));
                yellowEmoji.setColorFilter(ContextCompat.getColor(getContext(), R.color.yellow));
                redEmoji.setColorFilter(ContextCompat.getColor(getContext(), R.color.grey));

                String feedback="Thank You!";

                DynamicToast.make(getActivity(), feedback, getResources().getDrawable(R.drawable.ic_baseline_sentiment_satisfied_alt_24),
                        getResources().getColor(R.color.yellow), getResources().getColor(R.color.black), 2000).show();


            }
        });


        redEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                greenEmoji.setColorFilter(ContextCompat.getColor(getContext(), R.color.grey));
                yellowEmoji.setColorFilter(ContextCompat.getColor(getContext(), R.color.grey));
                redEmoji.setColorFilter(ContextCompat.getColor(getContext(), R.color.red));

                String feedback="We'll try to improve next time!";

                DynamicToast.make(getActivity(), feedback, getResources().getDrawable(R.drawable.ic_baseline_sentiment_very_dissatisfied_24),
                        getResources().getColor(R.color.red), getResources().getColor(R.color.black), 2000).show();

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message=email.getText().toString();
                message=message.trim();

                if(message.isEmpty())
                {
                    DynamicToast.makeWarning(getActivity(),"Please enter the message!",2000).show();
                }
                else
                {
                    Intent sendEmail = new Intent();
                    sendEmail.setAction(Intent.ACTION_SEND);
                    //email.setData(Uri.parse("mailto:"));
                    sendEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{"anupam00basak@gmail.com"});
                    sendEmail.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                    sendEmail.putExtra(Intent.EXTRA_TEXT, message);
                    sendEmail.setType("text/plain");

                    //need this to prompts email client only
                    //email.setType("message/rfc822");

                    try {
                        startActivity(sendEmail);
                    } catch (android.content.ActivityNotFoundException ex) {
                        DynamicToast.makeError(getActivity(), "There is no email client installed.", 2000).show();
                    }
                }

            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feedback, container, false);
    }
}