package com.example.example;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.example.example.bean.ChinaCityBean;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 对第三方 Android-PickerView 的使用
 */
public class PickerViewActivity extends AppCompatActivity {
    private List<ChinaCityBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<ChinaCityBean.CityListBeanX>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<ChinaCityBean.CityListBeanX.CityListBean>>> options3Items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker_view);
        initJsonData();
    }

    public void showPickerView(View view) {
        showPickerView();
    }

    public void showCustomerPickerView(View view) {
        showCustomerPickerView();
    }

    private void initJsonData() {//解析数据

        String newJson = getJson(this, "china_city_data.json");//获取assets目录下的json文件数据
        JSONArray jsonArray = null;
        ArrayList<ChinaCityBean> chinaCityBeanArrayList = new ArrayList<>();
        try {
            jsonArray = new JSONArray(newJson);
            Gson gson = new Gson();
            for (int i = 0; i < jsonArray.length(); i++) {
                ChinaCityBean entity = gson.fromJson(jsonArray.optJSONObject(i).toString(), ChinaCityBean.class);
                chinaCityBeanArrayList.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = chinaCityBeanArrayList;// 所有省份
//        options2Items 所有城市
//        options3Items 所有市区
        for (int i = 0; i < chinaCityBeanArrayList.size(); i++) {
            ChinaCityBean chinaCityBean = chinaCityBeanArrayList.get(i);
            options2Items.add((ArrayList<ChinaCityBean.CityListBeanX>) chinaCityBean.getCityList());
            ArrayList<ArrayList<ChinaCityBean.CityListBeanX.CityListBean>> province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）
            for (int j = 0; j < chinaCityBean.getCityList().size(); j++) {
                List<ChinaCityBean.CityListBeanX.CityListBean> cityList = chinaCityBean.getCityList().get(j).getCityList();
                province_AreaList.add((ArrayList<ChinaCityBean.CityListBeanX.CityListBean>) cityList);
            }
            options3Items.add(province_AreaList);
        }


    }

    public String getJson(Context context, String fileName) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    // 默认的三级联动
    private void showPickerView() {// 弹出选择器

        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
//                //返回的分别是三个级别的选中位置
                String opt1tx = options1Items.size() > 0 ?
                        options1Items.get(options1).getPickerViewText() : "";

                ChinaCityBean.CityListBeanX opt2tx = options2Items.size() > 0
                        && options2Items.get(options1).size() > 0 ?
                        options2Items.get(options1).get(options2) : null;

                ChinaCityBean.CityListBeanX.CityListBean opt3tx = options2Items.size() > 0
                        && options3Items.get(options1).size() > 0
                        && options3Items.get(options1).get(options2).size() > 0 ?
                        options3Items.get(options1).get(options2).get(options3) : null;

                String tx = opt1tx + opt2tx + opt3tx;
                Toast.makeText(PickerViewActivity.this, tx, Toast.LENGTH_SHORT).show();
            }
        })

                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .build();

        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }

    private OptionsPickerView customerPickerView;

    private void showCustomerPickerView() {// 弹出选择器
        if (customerPickerView != null)
            customerPickerView = null;

        customerPickerView = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
//                //返回的分别是三个级别的选中位置
                String opt1tx = options1Items.size() > 0 ?
                        options1Items.get(options1).getPickerViewText() : "";

                ChinaCityBean.CityListBeanX opt2tx = options2Items.size() > 0
                        && options2Items.get(options1).size() > 0 ?
                        options2Items.get(options1).get(options2) : null;

                ChinaCityBean.CityListBeanX.CityListBean opt3tx = options2Items.size() > 0
                        && options3Items.get(options1).size() > 0
                        && options3Items.get(options1).get(options2).size() > 0 ?
                        options3Items.get(options1).get(options2).get(options3) : null;

                String tx = opt1tx + opt2tx + opt3tx;
                Toast.makeText(PickerViewActivity.this, tx, Toast.LENGTH_SHORT).show();
            }
        }).setLayoutRes(R.layout.picker_view_select_city, new CustomListener() {
            @Override
            public void customLayout(View v) {
                bindView(v);
            }
        })

                .setTextColorCenter(Color.parseColor("#4382FF"))
                .isDialog(false)// 设置是否以对话框形式出现（居中）
                .setOutSideCancelable(true)
                .build();

        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        customerPickerView.setPicker(options1Items, options2Items, options3Items);//三级选择器
        customerPickerView.show();
    }

    private void bindView(View view) {
        TextView tvSubmit = view.findViewById(R.id.tv_finish);
        TextView tvCancel = view.findViewById(R.id.tv_cancel);
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customerPickerView.returnData();
                customerPickerView.dismiss();
                customerPickerView = null;
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customerPickerView.dismiss();
                customerPickerView = null;
            }
        });
    }


}
