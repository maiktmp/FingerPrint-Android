package mx.com.satoritech.satorifinger;

import com.fpreader.fpdevice.Constants;
import com.fpreader.fpdevice.UsbReader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Build;

import androidx.annotation.NonNull;

public class UsbReaderTestActivity extends Activity {

		private UsbReader fpModule;

	private boolean isopening=false;
	private boolean isworking=false;

	private TextView tvStatus=null;
	private ImageView ivImage=null;
	private EditText mEditText;

	private byte bmpdata[]=new byte[93238];
	private int bmpsize[]=new int[1];
	private byte refdata[]=new byte[512];
	private int refsize[]=new int[1];
	private byte matdata[]=new byte[512];
	private int matsize[]=new int[1];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_usb_reader_test);

		final Button bt1=(Button)findViewById(R.id.button1);
		final Button bt2=(Button)findViewById(R.id.button2);
		final Button bt10=(Button)findViewById(R.id.button10);
		final Button bt11=(Button)findViewById(R.id.button11);

		tvStatus=(TextView)findViewById(R.id.textView1);
		ivImage=(ImageView)findViewById(R.id.imageView1);
		mEditText = (EditText) findViewById(R.id.editText1);
		mEditText.setVisibility(View.GONE);

		fpModule=new UsbReader();
		fpModule.InitMatch();
		fpModule.SetContextHandler(this,mHandler);

		bt1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(fpModule.OpenDevice()==0){
					tvStatus.setText("Open Device OK");
					isopening=true;
					isworking=false;
				}else{
					fpModule.requestPermission();
					tvStatus.setText("Request Permission");
				}
			}
		});

		bt2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				fpModule.CloseDevice();
				tvStatus.setText("Close Device");
			}
		});

		bt10.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(isopening){
					if(isworking) return;
					fpModule.EnrolTemplate();
					isworking=true;
				}
			}
		});

		bt11.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(isopening){
					if(isworking) return;
					fpModule.GenerateTemplate();
					isworking=true;
				}
			}
		});

	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch (msg.what){
				case Constants.FPM_DEVICE:{
					switch(msg.arg1){
						case Constants.DEV_ATTACHED:
							break;
						case Constants.DEV_DETACHED:
							isopening=false;
							isworking=false;
							fpModule.CloseDevice();
							tvStatus.setText("Close");
							break;
						case Constants.DEV_OK:
							isopening=true;
							isworking=false;
							tvStatus.setText("Open Device OK");
							break;
						default:
							tvStatus.setText("Open Device Fail");
							break;
					}
				}
				break;
				case Constants.FPM_PLACE:
					tvStatus.setText("Place Finger");
					break;
				case Constants.FPM_LIFT:
					tvStatus.setText("Lift Finger");
					break;
				case Constants.FPM_CAPTURE:{
					if(msg.arg1==1){
						tvStatus.setText("Capture Image OK");
					}else{
						tvStatus.setText("Capture Image Fail");
					}
					isworking=false;
				}
				break;
				case Constants.FPM_GENCHAR:{
					if(msg.arg1==1){
						tvStatus.setText("Generate Template OK");
						fpModule.GetTemplateByGen(matdata, matsize);
						int mret=fpModule.MatchTemplate(refdata, matdata);
						tvStatus.setText(String.format("Match Return:%d",mret));
					}else{
						tvStatus.setText("Generate Template Fail");
					}
					isworking=false;
				}
				break;
				case Constants.FPM_ENRFPT:{
					if(msg.arg1==1){
						tvStatus.setText("Enrol Template OK");
						fpModule.GetTemplateByEnl(refdata,refsize);
					}else{
						tvStatus.setText("Enrol Template Fail");
					}
					isworking=false;
				}
				break;
				case Constants.FPM_NEWIMAGE:{
					fpModule.GetBmpImage(bmpdata,bmpsize);
					Bitmap bm1=BitmapFactory.decodeByteArray(bmpdata, 0, bmpsize[0]);
					ivImage.setImageBitmap(bm1);
				}
				break;
				case Constants.FPM_TIMEOUT:
					tvStatus.setText("Time Out");
					isworking=false;
					break;
			}
		}
	};

	@Override
	protected void onPause() {
		fpModule.PauseUnRegister();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		fpModule.ResumeRegister();
	}

	@Override
	public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
			fpModule.CloseDevice();
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
