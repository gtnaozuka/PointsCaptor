package com.captor.points.gtnaozuka.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.captor.points.gtnaozuka.util.Constants;
import com.captor.points.gtnaozuka.util.image.ImageResizer;
import com.captor.points.gtnaozuka.util.image.ImageWorker;
import com.captor.points.gtnaozuka.util.Versions;
import com.captor.points.gtnaozuka.pointscaptor.FullScreenViewActivity;
import com.captor.points.gtnaozuka.pointscaptor.R;

public class FullScreenPhotoFragment extends Fragment {

    private String imagePath;
    private ImageView imageView;

    public static FullScreenPhotoFragment newInstance(String imagePath) {
        final FullScreenPhotoFragment f = new FullScreenPhotoFragment();

        final Bundle args = new Bundle();
        args.putString(Constants.PHOTO_MSG, imagePath);
        f.setArguments(args);

        return f;
    }

    public FullScreenPhotoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePath = getArguments() != null ? getArguments().getString(Constants.PHOTO_MSG) : null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fullscreen_photo, container, false);
        imageView = (ImageView) v.findViewById(R.id.image_view);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (FullScreenViewActivity.class.isInstance(getActivity())) {
            ImageResizer imageResizer = ((FullScreenViewActivity) getActivity()).getImageResizer();
            imageResizer.loadImage(imagePath, imageView);
        }

        if (OnClickListener.class.isInstance(getActivity()) && Versions.hasHoneycomb()) {
            imageView.setOnClickListener((OnClickListener) getActivity());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (imageView != null) {
            ImageWorker.cancelWork(imageView);
            imageView.setImageDrawable(null);
        }
    }
}
