package com.linjf.demo;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.panoramagl.PLCubicPanorama;
import com.panoramagl.PLIView;
import com.panoramagl.PLImage;
import com.panoramagl.PLTexture;
import com.panoramagl.PLView;
import com.panoramagl.PLViewEventListener;
import com.panoramagl.enumeration.PLCubeFaceOrientation;
import com.panoramagl.hotspots.PLHotspot;
import com.panoramagl.ios.structs.CGPoint;
import com.panoramagl.structs.PLPosition;
import com.panoramagl.transitions.PLITransition;
import com.panoramagl.transitions.PLTransitionFadeOut;
import com.panoramagl.utils.PLUtils;

/** 
 * @author linjinfa 331710168@qq.com 
 * @version 创建时间：2014年6月27日 下午3:11:56 
 */
public class SecondActivity extends PLView{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.second_activity);
//System.out.println("ddddd=====>   "+getCacheDir().getAbsolutePath());
//		MainActivity.mainActivity.findViewById(android.R.id.content).setBackgroundColor(Color.RED);
		
		setListener(new PLViewEventListener(){
			@Override
			public void onDidClickHotspot(PLIView pView, PLHotspot hotspot, CGPoint screenPoint, PLPosition scene3dPoint) {
System.out.println("=====>  点击热点");

				PLTransitionFadeOut fadeout = new PLTransitionFadeOut(0.05f);
				fadeout.setFadeStep(1f);
				executeTransition(fadeout);
			}

			@Override
			public void onDidEndTransition(PLIView pView, PLITransition transition) {
System.out.println("=======> transition");

				clear();
				init();
			}
		});
		setValidForTransition(true);
		init();
	}
	
	private void init(){
		setBlocked(true);
		
		//加载全景图片	共6张 前后左右上下
		GL10 gl= getCurrentGL();
		PLCubicPanorama cubicPanorama = (PLCubicPanorama) getPanorama(); 
		if(cubicPanorama==null){
			cubicPanorama = new PLCubicPanorama();
		}
        cubicPanorama.setImage(gl, PLImage.imageWithBitmap(PLUtils.getBitmap(this, R.raw.pano_f), false), PLCubeFaceOrientation.PLCubeFaceOrientationFront); 
        cubicPanorama.setImage(gl, PLImage.imageWithBitmap(PLUtils.getBitmap(this, R.raw.pano_b), false), PLCubeFaceOrientation.PLCubeFaceOrientationBack); 
        cubicPanorama.setImage(gl, PLImage.imageWithBitmap(PLUtils.getBitmap(this, R.raw.pano_l), false), PLCubeFaceOrientation.PLCubeFaceOrientationLeft); 
        cubicPanorama.setImage(gl, PLImage.imageWithBitmap(PLUtils.getBitmap(this, R.raw.pano_r), false), PLCubeFaceOrientation.PLCubeFaceOrientationRight); 
        cubicPanorama.setImage(gl, PLImage.imageWithBitmap(PLUtils.getBitmap(this, R.raw.pano_u), false), PLCubeFaceOrientation.PLCubeFaceOrientationUp); 
        cubicPanorama.setImage(gl, PLImage.imageWithBitmap(PLUtils.getBitmap(this, R.raw.pano_d), false), PLCubeFaceOrientation.PLCubeFaceOrientationDown); 
		reset();
		setPanorama(cubicPanorama);
		
		//俯视角度的范围
		getCamera().setPitchRange(-45, 45);
		//水平角度的范围
		getCamera().setYawRange(-180, 180);
		//设置初始角度
		getCamera().setInitialLookAt(26.571f,-1394.154f);
		
		//添加热点
		PLImage plImage = PLImage.imageWithBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wanyangcheng),false);
		PLHotspot plHotspot = new PLHotspot(0, PLTexture.textureWithImage(plImage), 20.938f, 52.211f, 0.07f, 0.07f);
		getPanorama().addHotspot(plHotspot);
		plImage = PLImage.imageWithBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.longyanxinzhongguojiyouxiangongsi),false);
		plHotspot = new PLHotspot(0L, plImage, 18.205f, -86.381f,  196/91*0.09f, 0.08f);
		getPanorama().addHotspot(plHotspot);
		
		setBlocked(false);
	}
	
	@Override
	protected void onGLContextCreated(GL10 gl) {
		TextView textView = new TextView(this);
		textView.setText("快卡死的");
		textView.setTextColor(Color.WHITE);
		textView.setGravity(Gravity.CENTER);
		textView.setBackgroundColor(Color.RED);
		addContentView(textView,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,100));
	}

	@Override
	protected void onDestroy() {
System.out.println("======>SecondActivity 销毁  ");
		super.onDestroy();
	}

	public void backClick(View view){
//		finishAffinity();
		finish();
	}
	
}
