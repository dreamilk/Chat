package cn.shanghq.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import cn.shanghq.chat.Utils.HttpCallbackListener;
import cn.shanghq.chat.Utils.HttpUtil;

public class WelcomeActivity extends AppCompatActivity {
    private boolean isOpenBefore = false;
    private View view1, view2, view3;
    private List<View> viewList;
    private ImageView imageView;
    private android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.obj!=null){
                Log.d("test","222");
                Bitmap bitmap=(Bitmap) msg.obj;
                int screenWidth=getWindowManager().getDefaultDisplay().getWidth();
                int screenHeight=getWindowManager().getDefaultDisplay().getHeight();
                float scaleWidth = ((float) screenWidth) / bitmap.getWidth();
                float scaleHeight = ((float) screenHeight) / bitmap.getHeight();
                Matrix matrix=new Matrix();
                matrix.postScale(scaleWidth,scaleHeight);
                Bitmap bm=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
                imageView.setImageBitmap(bm);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        imageView = (ImageView) findViewById(R.id.welcome_img);
        SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
        isOpenBefore = preferences.getBoolean("isOpenBefore", false);
        if (!isOpenBefore) {
            SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
            editor.putBoolean("isOpenBefore", true);
            editor.commit();
            imageView.setVisibility(View.GONE);
            initView(viewPager);
        } else {
            viewPager.setVisibility(View.GONE);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap=HttpUtil.getHttpBitmap();
                    Log.d("lalal","lalal");
                    Message message=new Message();
                    message.obj=bitmap;
                    handler.sendMessage(message);
                }
            }).start();

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(WelcomeActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

        }
    }

    public void Click(View view) {
        Log.d("click", "you click this");
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void initView(ViewPager viewPager) {
        view1 = getLayoutInflater().from(WelcomeActivity.this).inflate(R.layout.viewpager1, null);
        view2 = getLayoutInflater().from(WelcomeActivity.this).inflate(R.layout.viewpager2, null);
        view3 = getLayoutInflater().from(WelcomeActivity.this).inflate(R.layout.viewpager3, null);

        viewList = new ArrayList<>();
        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);

        PagerAdapter pagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(viewList.get(position));
                return viewList.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(viewList.get(position));
            }
        };

        viewPager.setAdapter(pagerAdapter);

    }
}
