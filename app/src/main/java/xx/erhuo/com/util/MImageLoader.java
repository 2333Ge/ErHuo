package xx.erhuo.com.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.erhuo.utils.HttpUtils;
import com.erhuo.utils.LogUtils;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import xx.erhuo.bmob.OthersBean;
import xx.erhuo.com.R;

public class MImageLoader extends ImageLoader {
    private List<OthersBean> imgUrlList;
    private int num;
    public MImageLoader(List<OthersBean> imgUrlList){
        this.imgUrlList  = imgUrlList;
        num = 0;

    }
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {

        //int[] resId = new int[]{R.drawable.mine48_selected,R.drawable.history48_selected,R.drawable.find48};
        //imgUrlList = new ArrayList<>();
        //imageView.setImageResource(resId[rand.nextInt(3)]);
        LogUtils.e("img=====",imgUrlList.get(0).getImgUrl());

        HttpUtils.setImageView(imageView,imgUrlList.get(num++ % imgUrlList.size()).getImgUrl());
    }

    //public disPlayMsg
}
