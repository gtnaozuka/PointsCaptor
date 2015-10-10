package com.captor.points.gtnaozuka.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.captor.points.gtnaozuka.fragment.FullScreenPhotoFragment;
import com.captor.points.gtnaozuka.util.operations.FileOperations;

import java.io.File;

public class FullScreenPhotoAdapter extends FragmentStatePagerAdapter {

    private String[] photos;

    public FullScreenPhotoAdapter(FragmentManager fm, String[] photos) {
        super(fm);
        this.photos = photos;
    }

    @Override
    public int getCount() {
        return this.photos.length;
    }

    @Override
    public Fragment getItem(int position) {
        return FullScreenPhotoFragment.newInstance(FileOperations.PHOTOS_PATH + File.separator + this.photos[position]);
    }

    /*@Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fullscreen_photo, container, false);

        imageView = (ImageView) view.findViewById(R.id.img_display);
        decodeFile(FileOperations.FILES_PATH + File.separator + photos[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

    private void decodeFile(String photoPath) {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inPreferredConfig = Bitmap.Config.ARGB_8888;

        final Bitmap bitmap = MemoryCache.getBitmapFromMemCache(photoPath);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else if (cancelPotentialWork(imageView, photoPath)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView, photoPath, o);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(activity.getResources(), null, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute();
        }
    }

    private boolean cancelPotentialWork(ImageView imageView, String photoPath) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final String bitmapPhotoPath = bitmapWorkerTask.photoPath;
            if (bitmapPhotoPath == null || !bitmapPhotoPath.equals(photoPath)) {
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

    private class BitmapWorkerTask extends AsyncTask<Void, Void, Bitmap> {

        private final WeakReference<ImageView> imageViewReference;
        private String photoPath;
        private BitmapFactory.Options options;

        public BitmapWorkerTask(ImageView imageView, String photoPath, BitmapFactory.Options options) {
            this.imageViewReference = new WeakReference<>(imageView);
            this.photoPath = photoPath;
            this.options = options;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            final Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
            MemoryCache.addBitmapToMemoryCache(photoPath, bitmap);
            return bitmap;
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
