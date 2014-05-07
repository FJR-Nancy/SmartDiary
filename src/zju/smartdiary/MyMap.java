package zju.smartdiary;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Date;

import zju.smartdiary.MainActivity.SectionsPagerAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.graphics.Color;
import android.location.Location;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.MyLocationStyle;

@SuppressLint("ValidFragment")
public  class MyMap extends Fragment implements LocationSource,AMapLocationListener{
	
    private MapView mapView;
	private AMap aMap;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;	
	private HashMap<LocatePoint, String> locateMap = new HashMap<LocatePoint, String>();
	private MyDataBaseAdapter m_MyDataBaseAdapter;
	
	//记录到数据库的数据
	private Timestamp timestamp;
//	private int year,month,day,hour,minute,second;
	private int screenState;
	private double geoLat,geoLng;
	private String addr;
	
//    static MyMap newInstance(MyDataBaseAdapter m_MyDataBaseAdapter) {
//    	MyMap f = new MyMap();
//        return f;
//    }
	
	public MyMap(){}
	
	public MyMap(MyDataBaseAdapter m_MyDataBaseAdapter){
		this.m_MyDataBaseAdapter=m_MyDataBaseAdapter;
	}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
//        /* 构造MyDataBaseAdapter对象 */
//		m_MyDataBaseAdapter = new MyDataBaseAdapter(this.getActivity());
//		
//		/* 取得数据库对象 */
//		m_MyDataBaseAdapter.open();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {	
    	View v = inflater.inflate(R.layout.map, container, false);     
        mapView = (MapView) v.findViewById(R.id.mapview);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
		setLocateMap();
		
        return v;
    }

    /**
	 * 初始化AMap对象
	 */
	private void init() {
//		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
//		}
	}
	
	/**
	 * 设置一些amap的属性
	 */
	private void setUpMap() {
		// 自定义系统定位小蓝点
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.location_marker));// 设置小蓝点的图标
		myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
		myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
		// myLocationStyle.anchor(int,int)//设置小蓝点的锚点
		myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
		aMap.setMyLocationStyle(myLocationStyle);
		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
	}
	
	/**
	 * 方法必须重写
	 */
	@Override
	public void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onPause() {
		super.onPause();
		mapView.onPause();
		deactivate();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}
	
	/**
	 * 此方法已经废弃
	 */
	@Override
	public void onLocationChanged(Location location) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}
	
	/**
	 * 激活定位
	 */
	@Override 
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this.getActivity());
			/*
			 * mAMapLocManager.setGpsEnable(false);
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
			 * API定位采用GPS和网络混合定位方式
			 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
			 */
			mAMapLocationManager.requestLocationUpdates(
	//				LocationManagerProxy.GPS_PROVIDER, 2000, 10, this);
					LocationProviderProxy.AMapNetwork, 2000, 10, this);
		}
	}
	
	/**
	 * 停止定位
	 */
	@Override 
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
	}
	
	/**
	 * gps定位回调方法
	 */
	@Override
	public void onLocationChanged(AMapLocation aLocation) {
			getScreenState();//获取"是否锁屏"状态
//			getCurrentTime();//获取当前时间
			getLatLng(aLocation);//获取经纬度和地址
			insertData();//把数据插入数据库
	}
	
	/*获取是否锁屏的状态*/
	private void getScreenState(){
		this.getActivity();
		android.app.KeyguardManager mKeyguardManager = (KeyguardManager) this
                .getActivity().getSystemService(Context.KEYGUARD_SERVICE);
		if(mKeyguardManager.inKeyguardRestrictedInputMode())
			screenState=1;
		else
			screenState=0;
    }  
	
	/*获取当前时间*/
//	private void getCurrentTime(){  
//		Date date =new Date();
//		timestamp=new Timestamp(date.getTime());
//		Calendar c = Calendar.getInstance();
//		year = c.get(Calendar.YEAR);
//	    month = c.get(Calendar.MONTH);  
//	    day = c.get(Calendar.DAY_OF_MONTH);
//	    hour = c.get(Calendar.HOUR_OF_DAY);  
//      minute = c.get(Calendar.MINUTE);
//      second = c.get(Calendar.SECOND); 
//	}
	
	public void getLatLng(AMapLocation aLocation){
		if (mListener != null && aLocation != null) {
			mListener.onLocationChanged(aLocation);// 显示系统小蓝点
			
			geoLat = aLocation.getLatitude();
			geoLng = aLocation.getLongitude();
			
			//保留4位有效数字
			BigDecimal bLat = new BigDecimal(geoLat);  
			double stdgeoLat = bLat.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
			BigDecimal bLng = new BigDecimal(geoLng);  
			double stdgeoLng = bLng.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue(); 
			
			LocatePoint locatePoint = new LocatePoint(stdgeoLat, stdgeoLng);
			
//			Toast.makeText(this.getActivity(),"纬度:"+stdgeoLat+"经度:"+stdgeoLng, Toast.LENGTH_LONG).show();
			getAddress(locatePoint);
		}
	}
	
	public void getAddress(LocatePoint latLonPoint) {		
		addr=locateMap.get(latLonPoint);
		if(addr==null){
			Toast.makeText(this.getActivity(), R.string.no_result, Toast.LENGTH_LONG).show();
		}
		else{
			Toast.makeText(this.getActivity(), addr, Toast.LENGTH_LONG).show();
		}
	}
	
	/*经纬度与地址映射表*/
	private void setLocateMap(){
		locateMap.put(new LocatePoint((double)30.2636,(double)120.1205), "图书馆");
		locateMap.put(new LocatePoint((double)30.2656,(double)120.1223), "8舍");
		locateMap.put(new LocatePoint((double)30.2585,(double)120.1204), "曹楼");
		locateMap.put(new LocatePoint((double)30.2634,(double)120.1240), "操场");
	}
	
	void insertData(){
		m_MyDataBaseAdapter.insertData(screenState,geoLat,geoLng,addr);
	}
}