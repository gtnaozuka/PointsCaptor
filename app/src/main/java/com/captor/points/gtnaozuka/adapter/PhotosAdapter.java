package com.captor.points.gtnaozuka.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.captor.points.gtnaozuka.entity.RecyclingImageView;
import com.captor.points.gtnaozuka.util.image.ImageResizer;
import com.captor.points.gtnaozuka.util.operations.FileOperations;

import java.io.File;

public class PhotosAdapter extends BaseAdapter {

    private Context context;
    private String[] photos;
    private ImageResizer imageResizer;

    private int itemHeight = 0;
    private int numColumns = 0;
    private GridView.LayoutParams imageViewLayoutParams;

    public PhotosAdapter(Context context, String[] photos, ImageResizer imageResizer) {
        this.context = context;
        this.photos = photos;
        this.imageResizer = imageResizer;

        imageViewLayoutParams = new GridView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public int getCount() {
        if (getNumColumns() == 0) {
            return 0;
        }
        return this.photos.length + numColumns;
    }

    @Override
    public Object getItem(int position) {
        return position < numColumns ?
                null : this.photos[position - numColumns];
    }

    @Override
    public long getItemId(int position) {
        return position < numColumns ? 0 : position - numColumns;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return (position < numColumns) ? 1 : 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position < numColumns) {
            if (convertView == null) {
                convertView = new View(context);
            }
            return convertView;
        }

        ImageView imageView;
        if (convertView == null) {
            imageView = new RecyclingImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(imageViewLayoutParams);
        } else {
            imageView = (ImageView) convertView;
        }

        if (imageView.getLayoutParams().height != itemHeight) {
            imageView.setLayoutParams(imageViewLayoutParams);
        }

        imageResizer.loadImage(FileOperations.PHOTOS_PATH + File.separator + this.photos[position - numColumns], imageView);
        return imageView;

        /*if (convertView == null) {
            imageView = new ImageView(activity);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(photoWidth, photoWidth));
        imageView.setOnClickListener(new OnImageClickListener(position));

        decodeFile(FileOperations.FILES_PATH + File.separator + photos[position]);

        return imageView;*/
    }

    public void setItemHeight(int height) {
        if (height == itemHeight) {
            return;
        }
        itemHeight = height;
        imageViewLayoutParams =
                new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight);
        imageResizer.setImageSize(height);
        notifyDataSetChanged();
    }

    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
    }

    public int getNumColumns() {
        return numColumns;
    }

    /*private void decodeFile(String photoPath) {
        File f = new File(photoPath);

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        loadBitmap(imageView, f, o);

        o.inSampleSize = PhotoOperations.calculateInSampleSize(o, photoWidth, photoWidth);
        o.inJustDecodeBounds = false;
        loadBitmap(imageView, f, o);
    }

    private void loadBitmap(ImageView imageView, File file, BitmapFactory.Options options) {
        final Bitmap bitmap = MemoryCache.getBitmapFromMemCache(file.getAbsolutePath());
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else if (cancelPotentialWork(imageView, file)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView, file, options);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(activity.getResources(), null, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute();
        }
    }

    private boolean cancelPotentialWork(ImageView imageView, File file) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final File bitmapFile = bitmapWorkerTask.file;
            if (bitmapFile == null || bitmapFile != file) {
                bitmapWorkerTask.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }

    private BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    /*private class OnImageClickListener implements OnClickListener {

        private int position;

        public OnImageClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(activity, FullScreenViewActivity.class);
            i.putExtra(Constants.POSITION_MSG, position);
            i.putExtra(Constants.PHOTOS_MSG, photos);
            activity.startActivity(i);
        }
    }

    private class BitmapWorkerTask extends AsyncTask<Void, Void, Bitmap> {

        private final WeakReference<ImageView> imageViewReference;
        private File file;
        private BitmapFactory.Options options;

        public BitmapWorkerTask(ImageView imageView, File file, BitmapFactory.Options options) {
            this.imageViewReference = new WeakReference<>(imageView);
            this.file = file;
            this.options = options;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                final Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file), null, options);
                MemoryCache.addBitmapToMemoryCache(file.getAbsolutePath(), bitmap);
                return bitmap;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    private class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference<>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }*/
}
