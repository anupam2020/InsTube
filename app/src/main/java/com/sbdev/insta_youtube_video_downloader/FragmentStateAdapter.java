package com.sbdev.insta_youtube_video_downloader;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

public class FragmentStateAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {

    public FragmentStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position)
        {
            case 0:
                return new InstaFragment();
            case 1:
                return new YoutubeFragment();
        }

        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
