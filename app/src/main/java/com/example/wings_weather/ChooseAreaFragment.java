package com.example.wings_weather;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.wings_weather.db.City;
import com.example.wings_weather.db.County;
import com.example.wings_weather.db.Province;
import com.example.wings_weather.util.HttpUtil;
import com.example.wings_weather.util.Utility;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;//省
    public static final int LEVEL_CITY = 1;//市
    public static final int LEVEL_COUNTY =2;//县

    private ProgressDialog progressDialog;//进度对话框

    private TextView titleView;//标题

    private Button backButtion; //返回按钮

    private ListView listView; //各省份地区展示

    private ArrayAdapter<String>  adapter;//ListView子布局配饰器；

    private List<String> dataList = new ArrayList<>();//查询的数据列表

    /**
     * 省列表
     */
    private List<Province> provinceList;

    /**
     * 市列表
     */
    private List<City> cityList;

    /**
     * 县列表
     */
    private List<County> countyList;

    /**
     * 选中的省份
     */
    private Province selectedProvince;

    /**
     * 选中的城市
     */
    private City selectedCity;

    /**
     * 当前选中的级别
     */
    private int currentLevel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area,container,false);//加载碎片布局
        titleView = (TextView)view.findViewById(R.id.title_text);
        backButtion = (Button)view.findViewById(R.id.back_button);
        listView = (ListView)view.findViewById(R.id.list_view);

        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { //设置一个ListView的子布局监听器
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //当按下子布局时
                if (currentLevel == LEVEL_PROVINCE){//如果是省的话
                    selectedProvince = provinceList.get(position); //获取当前按下的是哪个省
                    queryCities();//查询省对应的市
                }else if (currentLevel == LEVEL_CITY)//如果是市的话
                {
                    selectedCity = cityList.get(position);//获取当前按下的是哪个市
                    queryCounties();//查询对应的县
                }
            }
        });
        backButtion.setOnClickListener(new View.OnClickListener() {//如果按下了返回按钮
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY){//当前是县的话则返回到市
                    queryCities();
                }else if (currentLevel == LEVEL_CITY)//如果是市的话返回到省
                {
                    queryProvinces();
                }
            }
        });
        queryProvinces();//默认显示省
    }

    /**
     * 查询全国所有的省份，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryProvinces(){
        titleView.setText("中国");
        backButtion.setVisibility(View.GONE);//如果是省的话，默认隐藏掉返回按钮
        provinceList = LitePal.findAll(Province.class);//先在数据库中查出所有的省
        if (provinceList.size()>0){//如果数据库中有的话
            dataList.clear();
            for (Province province : provinceList){//遍历将省份名字添加进list
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();//通知其内容发生变化重新加载
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE; //将其当前选择等级设为省
        }else { //如果数据库中没有则进行网络请求查询
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }

    /**
     * 查询选中省份内的所有市，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCities(){
        titleView.setText(selectedProvince.getProvinceName());
        backButtion.setVisibility(View.VISIBLE);
        cityList = LitePal.where("provinceid = ?",String.valueOf(selectedProvince.getId())).find(City.class);
        //在City映射的数据库中寻找 provinceid 等于选择省份的ID
        if(cityList.size()>0){
            dataList.clear();
            for (City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel =LEVEL_CITY;
        }else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/"+provinceCode;
            queryFromServer(address,"city");
        }
    }

    /**
     * 查询选中市内的所有县，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCounties(){
        titleView.setText(selectedCity.getCityName());
        backButtion.setVisibility(View.VISIBLE);
        countyList = LitePal.where("cityid = ?",String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size()>0){
            dataList.clear();
//            countyList.remove(0);
//            for (County county : countyList){
//                dataList.add(county.getCountyName());
//            }
            for (int i = 1;i<countyList.size();i++){
                dataList.add(countyList.get(i).getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(address,"county");
        }
    }

    /**
     * 根据传入的地址和类型从服务器上查询省市县的数据
     * @param address
     * @param type
     */
    private void queryFromServer(String address ,final String type){
        showProgressDialog();//显示进度条
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            /**
             * 请求失败时的处理函数
             * @param call
             * @param e
             */
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            /**
             * 请求成功时的处理函数
             * @param call
             * @param response
             * @throws IOException
             */
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseText = response.body().string();//获取到响应体
                boolean result = false;
                if ("province".equals(type)){ //如果请求的是省
                    result = Utility.handleProvinceResponse(responseText);//调用自己定义的省请求处理函数，将其结果保存在数据库中
                } else if ("city".equals(type)){ //如果请求的是市
                    result = Utility.handleCityResponse(responseText,selectedProvince.getId());
                    //调用自己定义的市请求处理函数，将其结果保存在数据库中
                } else  if ("county".equals(type)){//如果请求的是县
                    result = Utility.handleCountyResponse(responseText,selectedCity.getId());
                    //调用自己定义的县请求处理函数，将其结果保存在数据库中
                }

                if (result){ //如果保存成功则重新进入主线程加载其数据。
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }


    /**
     * 显示进度条
     */
    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 隐藏进度条
     */
    private void closeProgressDialog(){
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }
}
