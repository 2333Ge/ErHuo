package xx.erhuo.com;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.erhuo.utils.HttpUtils;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class DisplayImgActivity extends Activity {

    public static void actionStart(Context context,String imgUrl){
        Intent intent = new Intent(context,DisplayImgActivity.class);
        intent.putExtra(IMG_URL,imgUrl);
        context.startActivity(intent);
    }

    private static final String IMG_URL = "imgUrl";
    private PhotoView pv_imgDisplay;
    private LinearLayout ll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_img);
        pv_imgDisplay = findViewById(R.id.pv_imgDisplay);
        ll = findViewById(R.id.ll_imgDisplay);
        HttpUtils.setImageView(pv_imgDisplay,getIntent().getStringExtra(IMG_URL));
        pv_imgDisplay.setOnPhotoTapListener(mOnClickListener);

    }


    private PhotoViewAttacher.OnPhotoTapListener mOnClickListener = new PhotoViewAttacher.OnPhotoTapListener() {

        @Override
        public void onPhotoTap(View view, float x, float y) {
            finish();
        }
    };
}
