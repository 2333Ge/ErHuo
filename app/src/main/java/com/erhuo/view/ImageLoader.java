package com.erhuo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**通过url异步加载imageView,并使用本地LruCache提高加载速度与节省流量*/
public class ImageLoader {
	
	private LruCache<String, Bitmap> mCaches;
	public ImageLoader(){
		//获取最大可用内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		//取其中的1/4
		int cacheSize = maxMemory/4;
		mCaches = new LruCache<String, Bitmap>(cacheSize){
			protected int sizeOf(String key, Bitmap value) {
				return value.getByteCount();
			};
		};
	}
	
	/**从url中获取图片，并加载到imageView,注意imgView和url要setTag绑定*/
	public void setImageViewFromUrl(ImageView imageView, String url){
		new mAsyncTask(imageView, url).execute(url);
	}
	/**从本地路径中获取图片，并加载到imageView,注意imgView和imgPath要setTag绑定**/
	public void setImageViewFromPath(Context context, ImageView imageView, String imgPath, int dw, int dh){
		new PathImgAsyncTask(context, imageView, imgPath, dw, dh).execute(imgPath);
	}
	
	class mAsyncTask extends AsyncTask<String, Void, Bitmap> {
		ImageView imageView;
		String url;
		public mAsyncTask(ImageView imageView, String url){
			this.imageView = imageView;
			this.url = url;
		}
		@Override
		protected Bitmap doInBackground(String... params) {
			String urlpath = params[0];
			Bitmap bitmap = mCaches.get(urlpath);
			if(bitmap == null){
				try {
					bitmap = getBitmapFromUrl(urlpath);
					mCaches.put(urlpath, bitmap);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return bitmap;
		}
		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			//防止网络延迟等原因导致图片内容不匹配
			if(imageView.getTag() == url){
				imageView.setImageBitmap(result);
			}
			
		}
	}
	
	class PathImgAsyncTask extends AsyncTask<String, Void, Bitmap> {
		ImageView imageView;
		String imgPath;
		Context context;
		int dw;
		int dh;
		public PathImgAsyncTask(Context context, ImageView imageView, String imgPath, int dw, int dh){
			this.imageView = imageView;
			this.imgPath = imgPath;
			this.context = context;
			this.dw = dw;
			this.dh = dh;
		}
		@Override
		protected Bitmap doInBackground(String... params) {
			String path = params[0];
			Bitmap bitmap = mCaches.get(path);
			if(bitmap == null){
				try {
					bitmap = getBitmapFromPath(context, Uri.fromFile(new File(path)), dw, dh);
					mCaches.put(path, bitmap);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return bitmap;
		}
		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			//防止网络延迟等原因导致图片内容不匹配
			if(imageView.getTag() == imgPath){
				imageView.setImageBitmap(result);
			}
			
		}
		
	}
	/**从网络中获取图片*/
	public Bitmap getBitmapFromUrl(String imgUrl) throws IOException {
		URL url = new URL(imgUrl);
		Bitmap result = null;
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		//设置请求方法，注意大写
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5000);
		conn.setReadTimeout(5000);
		conn.connect();
		if(conn.getResponseCode() == 200){
			InputStream in = conn.getInputStream();
			result = BitmapFactory.decodeStream(in);
		}else{
			Log.w("getMessage", "连接失败");
			result = null;
		}
		return result;
	}
	
	/**从uri中获取图片*/
	public Bitmap getBitmapFromPath(Context context, Uri imgpath, int dw, int dh) throws FileNotFoundException {
		BitmapFactory.Options op = new BitmapFactory.Options();
	        op.inJustDecodeBounds = true;  
	        //由于使用了MediaStore存储，这里根据URI获取输入流的形式    
	        Bitmap result = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imgpath),
	                null, op);
	        
	        int wRatio = (int) Math.ceil(op.outWidth / (float) dw); //计算宽度比例
	        int hRatio = (int) Math.ceil(op.outHeight / (float) dh); //计算高度比例
	        
	        /** 
	         * 接下来，我们就需要判断是否需要缩放以及到底对宽还是高进行缩放。 
	         * 如果高和宽不是全都超出了屏幕，那么无需缩放。 
	         * 如果高和宽都超出了屏幕大小，则如何选择缩放呢》 
	         * 这需要判断wRatio和hRatio的大小 
	         * 大的一个将被缩放，因为缩放大的时，小的应该自动进行同比率缩放。 
	         * 缩放使用的还是inSampleSize变量 
	         */  
	        if (wRatio > 1 && hRatio > 1) {  
	            if (wRatio > hRatio) {  
	                op.inSampleSize = wRatio;  
	            } else {  
	                op.inSampleSize = hRatio;  
	            }  
	        }  
	        op.inJustDecodeBounds = false; //注意这里，一定要设置为false，因为上面我们将其设置为true来获取图片尺寸了  
	        result = BitmapFactory.decodeStream(context.getContentResolver()
	                .openInputStream(imgpath), null, op);
		return result;
	}

}
