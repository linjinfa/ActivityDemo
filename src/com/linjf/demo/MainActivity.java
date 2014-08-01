package com.linjf.demo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.MaskFilterSpan;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.linjf.demo.util.DataDealUtil;
import com.linjf.demo.util.pojo.RouteGroup;
import com.linjf.demo.util.pojo.RouteSteps;
import com.linjf.demo.util.pojo.Routes;
import com.linjf.demo.view.LineView;
import com.linjf.demo.view.SimpleMapView;

/**
 * 
* @author linjinfa 331710168@qq.com 
* @version 创建时间：2014年6月27日 下午2:53:44
 */
public class MainActivity extends Activity{
	
	public static Activity mainActivity = null;
	SimpleMapView simpleMapView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		mainActivity = this;
		
		simpleMapView = (SimpleMapView) findViewById(R.id.simpleMapView);
		
		FrameLayout frameLayout = (FrameLayout)findViewById(R.id.frameLay);
		TextView tx = new TextView(this);
		tx.setText("测试===");
		tx.setTextColor(Color.BLUE);
		tx.setBackgroundResource(R.drawable.rect_bg_normal);
		tx.setGravity(Gravity.CENTER);
		FrameLayout.LayoutParams lpParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		lpParams.gravity = Gravity.TOP|Gravity.LEFT;
//		lpParams.setMargins(0, 100, 0, 0);
		lpParams.leftMargin = 200;
		lpParams.topMargin = 20;
		tx.setLayoutParams(lpParams);
		
		setView();
System.out.println("==============第一次提交");
System.out.println("==============第二次提交");
System.out.println("==============第三次提交 ");
System.out.println("==============dev分支提交 1");
System.out.println("==============master分支提交 1");
System.out.println("==============master分支提交 2");
System.out.println("==============master分支提交 3");
	}
	
	LineView lineView;
	View subView;
	List<RouteGroup> routeGroupList;
	
	private void setView(){
		Bitmap bitmap = ImageUtil.readAssetsBitmap(this, "Map@2x.png");
		simpleMapView.setImageBitmap(bitmap);
		
		List<Routes> routeList = DataDealUtil.getRoutes(this);
		routeGroupList = getRouteGroupList(routeList);
		float x = 0,y=0;
		float currXY[] = DataDealUtil.getCoordinate(routeGroupList.get(0).getCenter());
		x = currXY[0];
		y = currXY[1];
		simpleMapView.setEnableAccelerometer(true);
		simpleMapView.setAddViewAnim(true);
		simpleMapView.scrollToCenterAnim(x, y,new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				for(int i=0;i<routeGroupList.size();i++){
					RouteGroup currRouteGroup = routeGroupList.get(i);
					float currXY[] = DataDealUtil.getCoordinate(currRouteGroup.getCenter());
					if(currXY==null){
						continue;
					}
					if(i+1<routeGroupList.size()){
						RouteGroup nextRouteGroup = routeGroupList.get(i+1);
						float nextXY[] = DataDealUtil.getCoordinate(nextRouteGroup.getCenter());
						if(nextXY!=null){
							lineView = simpleMapView.addPathView(currXY[0], currXY[1], nextXY[0], nextXY[1]);
						}
					}
					subView = simpleMapView.addSubTipView(currRouteGroup.getName(),getResources().getDrawable(R.drawable.rect_bg_normal), currXY[0], currXY[1]);
				}
			}
		});
	}
	
	/**
	 * RouteGroupList
	 * @param routeList
	 * @return
	 */
	private List<RouteGroup> getRouteGroupList(List<Routes> routeList){
		List<RouteGroup> routeGroupList = new ArrayList<RouteGroup>();
		if(routeList!=null && routeList.size()!=0){
			for(Routes routes : routeList){
				if(!"二日游".equals(routes.getTitle())){
					continue;
				}
				if(routes.getSteps()!=null && routes.getSteps().size()!=0){
					for(RouteSteps routeSteps : routes.getSteps()){
						if(routeSteps.getGroup()!=null && routeSteps.getGroup().size()!=0){
							for(RouteGroup routeGroup : routeSteps.getGroup()){
								routeGroup.setGroupname(routeSteps.getGroupname());
								routeGroupList.add(routeGroup);
							}
						}
					}
				}
			}
		}
		return routeGroupList;
	}
	
	@Override
	protected void onDestroy() {
System.out.println("======>MainActivity 销毁  ");
		super.onDestroy();
	}
	
	/**
	 * 
	 * @param view
	 */
	public void onSearchClick(View view){
		Intent intent = new Intent(this,SearchActivity.class);
		startActivity(intent);
	}
	
	private int index = 0;
	
	public void onRetrofitClick(View view){
		Button btn = (Button) view;
		SpannableString spannableString = new SpannableString("卡开始的");
		spannableString.setSpan(new MaskFilterSpan(new MaskFilter()), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		btn.setText(spannableString);
		
		lineView.setPaintAlphaF(100);
		subView.setBackgroundResource(R.drawable.rect_bg_press);
		if(index>=routeGroupList.size()){
			index = 0;
		}
		RouteGroup nextRouteGroup = routeGroupList.get(index);
		final float nextXY[] = DataDealUtil.getCoordinate(nextRouteGroup.getCenter());
//		if(simpleMapView.getCurrScale()!=1){
//			simpleMapView.scaleAnim(1, new ScaleAnimListener() {
//				@Override
//				public void onScaleStart(float fromScale) {}
//				@Override
//				public void onScaleEnd(float endScale) {
//					simpleMapView.scrollToCenterAnim(nextXY[0], nextXY[1], null);
//				}
//			});
//		}else{
//			simpleMapView.scrollToCenterAnim(nextXY[0], nextXY[1], null);
//		}
		simpleMapView.scrollToCenterAnim(nextXY[0], nextXY[1], null);
		index++;
		
		
//		Button btn = (Button) view;
//		btn.setTextSize(DensityUtil.px2sp(this, 38));
//		btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, 26);
//System.out.println("mmmmmmmmm======>"+btn.getTextSize());
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					long t1 = System.currentTimeMillis();
//					RestAdapter restAdapter = new RestAdapter.Builder().setConverter(new HLGsonConverter(new Gson(),false)).setLogLevel(LogLevel.FULL).setEndpoint(ITourService.ROOT_URL).build();
//					ITourService tourService = restAdapter.create(ITourService.class);
//					List<Scenic> scenicList = tourService.getLandscapes();
//					long t2 = System.currentTimeMillis();
//					System.out.println(" 消耗事假====》  "+(t2-t1));
////					System.out.println("k=aaaaa=======> "+tourService.landscapes());;
//				} catch (RetrofitError e) {
//					System.out.println("异常====》"+e);
//					e.printStackTrace();
////					System.out.println("asdasdasdassdasdasd=======>  "+e.isNetworkError()+"  "+e.getMessage()+"  "+e.getResponse().getStatus());
////					try {
////						String ss = InputStreamUtil.InputStreamTOStringUTF8(e.getResponse().getBody().in());
////						System.out.println("kkkkk======>  "+ss);
////					} catch (IOException e1) {
////						e1.printStackTrace();
////					}
//				}
//			}
//		}).start();
		
//		new Thread(new ResponseTask(ServerInfo.LANDSCAPES+"dd",new TypeToken<List<Scenic>>(){}.getType())).start();
	}
	
	public void onStartSecondClick(View view){
		Intent intent = new Intent(this,SecondActivity.class);
		startActivity(intent);
//		startActivities(new Intent[]{new Intent(this,SecondActivity.class),new Intent(this,MainActivity.class)});
	}
	
}
