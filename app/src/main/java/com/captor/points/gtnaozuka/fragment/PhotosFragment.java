package com.captor.points.gtnaozuka.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.captor.points.gtnaozuka.adapter.PhotosAdapter;
import com.captor.points.gtnaozuka.dialog.DeleteConfirmationDialog;
import com.captor.points.gtnaozuka.dialog.PhotosDialog;
import com.captor.points.gtnaozuka.util.DisplayToast;
import com.captor.points.gtnaozuka.util.image.ImageCache;
import com.captor.points.gtnaozuka.util.image.ImageResizer;
import com.captor.points.gtnaozuka.util.Versions;
import com.captor.points.gtnaozuka.pointscaptor.BuildConfig;
import com.captor.points.gtnaozuka.pointscaptor.FullScreenViewActivity;
import com.captor.points.gtnaozuka.pointscaptor.R;
import com.captor.points.gtnaozuka.util.Constants;
import com.captor.points.gtnaozuka.util.operations.FileOperations;

import java.io.File;

public class PhotosFragment extends Fragment implements AdapterView.OnItemClickListener,
        AbsListView.OnScrollListener, ViewTreeObserver.OnGlobalLayoutListener,
        AdapterView.OnItemLongClickListener {

    private GridView gridView;
    private TextView emptyView;

    private AppCompatActivity context;

    private int imageThumbSize;
    private int imageThumbSpacing;
    private PhotosAdapter adapter;
    private ImageResizer imageResizer;
    private String[] photos;

    private String currentFilePath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photos, container, false);

        gridView = (GridView) rootView.findViewById(R.id.grid_view);
        emptyView = (TextView) rootView.findViewById(R.id.empty_view);
        updatePhotoList();

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = (AppCompatActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void updatePhotoList() {
        photos = FileOperations.listAllFiles(context, FileOperations.PHOTOS_PATH);
        if (photos == null || photos.length == 0) {
            gridView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            gridView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

            imageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
            imageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);

            ImageCache.ImageCacheParams cacheParams =
                    new ImageCache.ImageCacheParams(getActivity(), FileOperations.THUMBS_CACHE_FOLDER);
            cacheParams.setMemCacheSizePercent(0.25f);

            imageResizer = new ImageResizer(getActivity(), imageThumbSize);
            imageResizer.setLoadingImage(R.drawable.empty_photo);
            imageResizer.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);

            adapter = new PhotosAdapter(getActivity(), photos, imageResizer);

            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(this);
            gridView.setOnScrollListener(this);
            gridView.getViewTreeObserver().addOnGlobalLayoutListener(this);
            gridView.setOnItemLongClickListener(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (photos != null && photos.length != 0) {
            imageResizer.setExitTasksEarly(false);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (photos != null && photos.length != 0) {
            imageResizer.setPauseWork(false);
            imageResizer.setExitTasksEarly(true);
            imageResizer.flushCache();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (photos != null && photos.length != 0) {
            imageResizer.closeCache();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        final Intent i = new Intent(getActivity(), FullScreenViewActivity.class);
        i.putExtra(Constants.POSITION_MSG, (int) id);
        i.putExtra(Constants.PHOTOS_MSG, photos);
        if (Versions.hasJellyBean()) {
            ActivityOptions options =
                    ActivityOptions.makeScaleUpAnimation(v, 0, 0, v.getWidth(), v.getHeight());
            getActivity().startActivity(i, options.toBundle());
        } else {
            startActivity(i);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
            if (!Versions.hasHoneycomb()) {
                imageResizer.setPauseWork(true);
            }
        } else {
            imageResizer.setPauseWork(false);
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onGlobalLayout() {
        if (adapter.getNumColumns() == 0) {
            final int numColumns = (int) Math.floor(
                    gridView.getWidth() / (imageThumbSize + imageThumbSpacing));
            if (numColumns > 0) {
                final int columnWidth =
                        (gridView.getWidth() / numColumns) - imageThumbSpacing;
                adapter.setNumColumns(numColumns);
                adapter.setItemHeight(columnWidth);
                if (BuildConfig.DEBUG) {
                    Log.d("PhotosFragment", "onCreateView - numColumns set to " + numColumns);
                }
                if (Versions.hasJellyBean()) {
                    gridView.getViewTreeObserver()
                            .removeOnGlobalLayoutListener(this);
                } else {
                    gridView.getViewTreeObserver()
                            .removeGlobalOnLayoutListener(this);
                }
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        currentFilePath = FileOperations.PHOTOS_PATH + File.separator + photos[(int) id];

        DialogFragment dialog = new PhotosDialog();
        dialog.show(context.getFragmentManager(), "PhotosDialog");
        return true;
    }

    public void sharePhoto() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("mailto:"));
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.photo_email_subject));
        intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.photo_email_body));
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(currentFilePath)));

        try {
            startActivity(Intent.createChooser(intent, getResources().getString(R.string.choose_option)));
        } catch (ActivityNotFoundException ex) {
            new Handler().post(new DisplayToast(context, getResources().getString(R.string.no_email_client)));
        }
    }

    public void showDeleteDialog(DialogFragment dialog) {
        dialog.dismiss();

        DialogFragment newDialog = new DeleteConfirmationDialog();
        Bundle dialogBundle = new Bundle();
        dialogBundle.putInt(Constants.TYPE_MSG, Constants.PHOTO);
        newDialog.setArguments(dialogBundle);
        newDialog.show(context.getFragmentManager(), "DeleteConfirmationDialog");
    }

    public void deletePhoto() {
        FileOperations.delete(new File(currentFilePath));

        new Handler().post(new DisplayToast(context, getResources().getString(R.string.photo_deleted_successfully)));
        updatePhotoList();
    }
}
