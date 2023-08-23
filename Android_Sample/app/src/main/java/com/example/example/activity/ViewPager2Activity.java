package com.example.example.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.example.R;
import com.example.example.util.BannerUtils;
import com.example.example.widget.BannerConfig;
import com.example.example.widget.CircleIndicator;
import com.bumptech.glide.signature.ObjectKey;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * viewpager2 无限轮播图
 * 注意，请勿在 viewpager2 处在最后一个item 翻页状态中又调用翻页，这样会导致页面产生倒退效果回到第一页
 * 修改自： https://github.com/youth5201314/banner
 */
public class ViewPager2Activity extends AppCompatActivity {

    private ImageView mIvBackground;
    private Button mBtnLeftImageButton;
    private Button mBtnRightImageButtom;
    private ViewPager2 mBannerViewpager2;
    private CircleIndicator mContainerIndicator;
    private Handler mHandler =new Handler();
    private BannerPagerAdapter mAdapter;
    // 循环时间
    private long mLoopTime = 3 * 1000L;
    public static final int INVALID_VALUE = -1;
    // 防止页面出现倒退效果回到第一页
    private boolean viewPager2CanChange = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager2);
        initView();

        ArrayList<Integer> bannerlist = new ArrayList<>();
        bannerlist.add(R.drawable.banner1);
        bannerlist.add(R.drawable.banner2);
        mAdapter = new BannerPagerAdapter(bannerlist);
        mBannerViewpager2.setOffscreenPageLimit(2);
        mBannerViewpager2.registerOnPageChangeCallback(new BannerOnPageChangeCallback());
        mBannerViewpager2.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        mContainerIndicator.onPageChanged(getRealCount(), 0);
        mBannerViewpager2.setCurrentItem(1, false);
    }

    private void initView() {
        mIvBackground = (ImageView) findViewById(R.id.iv_background);
        mBtnLeftImageButton = (Button) findViewById(R.id.btn_left_imageButton);
        mBtnRightImageButtom = (Button) findViewById(R.id.btn_right_imageButtom);
        mBannerViewpager2 = (ViewPager2) findViewById(R.id.banner_viewpager2);
        mContainerIndicator = (CircleIndicator) findViewById(R.id.container_indicator);
        mBtnLeftImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preImg();
            }
        });
        mBtnRightImageButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextImg();
            }
        });




    }

    private boolean nextImg() {
        //避免快速翻页导致回滚的问题
        if ( viewPager2CanChange) {
            mHandler.removeCallbacksAndMessages(null);
            int count = mAdapter.getItemCount();
            if (count != 0) {
                int next = (mBannerViewpager2.getCurrentItem() + 1) % count;
                setCurrentItem(next);
            }
            mHandler.postDelayed(bannerLoop, mLoopTime);
            return true;
        }
        return false;

    }

    private boolean preImg() {
        if ( viewPager2CanChange) {
            mHandler.removeCallbacksAndMessages(null);
            int count = mAdapter.getItemCount();
            if (count != 0) {
                int next = (mBannerViewpager2.getCurrentItem() - 1) % count;
                setCurrentItem(next);
            }
            mHandler.postDelayed(bannerLoop, mLoopTime);
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(bannerLoop, mLoopTime);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacksAndMessages(null);
    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            preImg();
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            nextImg();
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 返回banner真实总数
     */
    private int getRealCount() {
        return mAdapter.getRealCount();

    }

    private int  getItemCount() {
        return mAdapter.getItemCount();
    }

    private int  getCurrentItem() {
        return mBannerViewpager2.getCurrentItem();
    }

    /**
     * 跳转到指定位置（最好在设置了数据后在调用，不然没有意义）
     * @param position
     * @return
     */
    private void  setCurrentItem(int position) {
        mBannerViewpager2.setCurrentItem(position, true);
    }

    private final Runnable bannerLoop  = new Runnable() {
        @Override
        public void run() {
            int itemCount = mAdapter.getItemCount();
            if (itemCount == 0){
                mHandler.postDelayed(this, mLoopTime);
                return;
            }
            int next = (mBannerViewpager2.getCurrentItem() + 1) % itemCount;
            setCurrentItem(next);
            mHandler.postDelayed(this, mLoopTime);

        }

    };

    class BannerOnPageChangeCallback extends ViewPager2.OnPageChangeCallback {
        private int mTempPosition = INVALID_VALUE;
        private boolean isScrolled;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int realPosition = BannerUtils.getRealPosition(true, position, getRealCount());

            if (mContainerIndicator != null && realPosition == getCurrentItem() - 1) {
                mContainerIndicator.onPageScrolled(realPosition, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (isScrolled) {
                mTempPosition = position;
                int realPosition = BannerUtils.getRealPosition(true, position, getRealCount());
                if (mContainerIndicator != null) {
                    mContainerIndicator.onPageSelected(realPosition);
                }

            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            //手势滑动中,代码执行滑动中
            if (state == ViewPager2.SCROLL_STATE_DRAGGING || state == ViewPager2.SCROLL_STATE_SETTLING) {
                isScrolled = true;
                viewPager2CanChange = false;
            } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                //滑动闲置或滑动结束
                isScrolled = false;
                viewPager2CanChange = true;
                if (mTempPosition != INVALID_VALUE && true) {
                    if (mTempPosition == 0) {
                        mBannerViewpager2.setCurrentItem(getRealCount(), false);
                    } else if (mTempPosition == getItemCount() - 1) {
                        mBannerViewpager2.setCurrentItem(1, false);
                    }
                }
            }
            if (mContainerIndicator != null) {
                mContainerIndicator.onPageScrollStateChanged(state);
            }

        }

    }


    private class BannerPagerAdapter extends RecyclerView.Adapter<BannerPagerAdapter.ViewHolder>{
        private int mIncreaseCount = BannerConfig.INCREASE_COUNT;
        private List<Integer> imgList;

        public BannerPagerAdapter(List<Integer> imgList) {
            this.imgList = imgList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_banner, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            int real = getRealPosition(position);
            int signature = imgList.get(real);
            // 给 glide 设置 signature 可以防止遇到同名同地址的文件不重新加载的问题，需要注意 signature 必须是唯一
            Glide.with(holder.ivAd.getContext())
                    .load(imgList.get(real))
                    .signature(new ObjectKey(signature))
                    .centerCrop()
                    .into(holder.ivAd);
        }

        public int getItemCount() {
            return getRealCount() > 1 ? getRealCount() + mIncreaseCount : getRealCount();
        }

        public int getRealCount() {
            return imgList == null ? 0 : imgList.size();
        }

        public int getRealPosition(int position) {
            return BannerUtils.getRealPosition(mIncreaseCount == BannerConfig.INCREASE_COUNT, position, getRealCount());
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivAd;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivAd = itemView.findViewById(R.id.iv_ad);
            }
        }
    }
}