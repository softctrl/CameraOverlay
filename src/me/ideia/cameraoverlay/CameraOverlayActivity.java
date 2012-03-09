package me.ideia.cameraoverlay;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

// TODO: Menu flutuante para seleção de efeitos
// TODO: Long click take screen shot -> big long click salva foto com o efeito atual sem foto base
// TODO: Configurações da camera
// TODO: Gravar ultima imagem e recarregar na abertura da aplicação
// TODO: Gravar configuraçoes e permitir carregar retornando ao estado "preferido"
// TODO: Redimensionamento da imagem base na view está distorcida -> nao casa com a camera
// TODO: (bloqueia escurecimento da tela)
// TODO: Camera preview nao resgata a camera
// TODO: Controle de brilho da tela -> ok


public class CameraOverlayActivity extends Activity {

	Preview preview;
	SurfaceHolder surfaceHolder;
	boolean previewing = false;
	LayoutInflater controlInflater = null;
	DrawOnTop photoBase;
	View effects;
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
    static String basefile;

    private int effect = -1;
    private int seekEffect = 0;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    
    boolean running = false;

    public void applyEffect(final View v) {
        // Handle item selection
    	if (!running) {
			running = true;
			final Button switchEffect = (Button)findViewById(R.id.switchEffect);
			final ProgressBar loadingSE = (ProgressBar)findViewById(R.id.loadingSE);
			final ProgressBar loadingSSE = (ProgressBar)findViewById(R.id.loadingSSE);
	    	final SeekBar alphaValue = (SeekBar) findViewById(R.id.alphaValue);
			final Button switchSeekEffect = (Button) findViewById(R.id.switchSeekEffect);
			final TextView switchSeekEffectLabel = (TextView) findViewById(R.id.switchSeekEffectLabel);
			final TextView switchEffectLabel = (TextView) findViewById(R.id.switchEffectLabel);
			switch (v.getId()) {
	            case R.id.alpha1: case R.id.alpha2: case R.id.alpha3: case R.id.alpha4:
	            case R.id.alpha5: case R.id.gray_scale: case R.id.highcontrast:
	            case R.id.contrastbw: case R.id.hue1: case R.id.hue2:
	            case R.id.edgedetect: case R.id.edgedetecttransparent:
	            	loadingSE.setVisibility(View.VISIBLE);
	            	switchEffect.setVisibility(View.INVISIBLE);
	            	break;
//	            case R.id.grid: case R.id.horizontal: case R.id.vertical:
//	            	loadingSSE.setVisibility(View.VISIBLE);
//	            	switchSeekEffect.setVisibility(View.INVISIBLE);
	        }

			new Handler().postDelayed(new Thread() {
				@Override
				public void run() {
			        switch (v.getId()) {
			            case R.id.aboutitem:
							Intent activity = new Intent(CameraOverlayActivity.this, AboutActivity.class);
							startActivity(activity);
							break;
			            case R.id.invert:
							photoBase.invert();
			                break;
			            case R.id.alpha1:
			            	photoBase.alpha1();
			            	switchEffect.setBackgroundResource(R.drawable.alpha1);
							switchEffectLabel.setText(getString(R.string.alpha1));
			            	break;
			            case R.id.alpha2:
			            	photoBase.alpha2();
			            	switchEffect.setBackgroundResource(R.drawable.alpha2);
							switchEffectLabel.setText(getString(R.string.alpha2));
			            	break;
			            case R.id.alpha3:
			            	photoBase.alpha3();
			            	switchEffect.setBackgroundResource(R.drawable.alpha3);
							switchEffectLabel.setText(getString(R.string.alpha3));
			            	break;
			            case R.id.alpha4:
			            	photoBase.alpha4();
			            	switchEffect.setBackgroundResource(R.drawable.alpha4);
							switchEffectLabel.setText(getString(R.string.alpha4));
			            	break;
			            case R.id.alpha5:
			            	photoBase.alpha5();
			            	switchEffect.setBackgroundResource(R.drawable.alpha5);
							switchEffectLabel.setText(getString(R.string.alpha5));
			            	break;
			            case R.id.gray_scale:
			            	photoBase.grayScale();
			            	switchEffect.setBackgroundResource(R.drawable.grayscale);
							switchEffectLabel.setText(getString(R.string.gray_scale));
			            	break;
			            case R.id.highcontrast:
			            	photoBase.highContrast();
			            	switchEffect.setBackgroundResource(R.drawable.highcontrast);
							switchEffectLabel.setText(getString(R.string.high_contrast));
			            	break;
			            case R.id.contrastbw:
			            	photoBase.contrastBW();
			            	switchEffect.setBackgroundResource(R.drawable.contrastbw);
							switchEffectLabel.setText(getString(R.string.contrast_bw));
			            	break;
			            case R.id.hue1:
			            	photoBase.hue1();
			            	switchEffect.setBackgroundResource(R.drawable.hue1);
							switchEffectLabel.setText(getString(R.string.hue1));
			                break;
			            case R.id.hue2:
			            	photoBase.hue2();
			            	switchEffect.setBackgroundResource(R.drawable.hue2);
							switchEffectLabel.setText(getString(R.string.hue2));
			                break;
			            case R.id.edgedetect:
			            	photoBase.edgeDetect();
			            	switchEffect.setBackgroundResource(R.drawable.edgedetect);
							switchEffectLabel.setText(getString(R.string.edgedetect));
			                break;
			            case R.id.edgedetecttransparent:
			            	photoBase.edgeDetectTransparent();
			            	switchEffect.setBackgroundResource(R.drawable.edgedetect);
							switchEffectLabel.setText(getString(R.string.edgedetecttransparent));
			                break;
//			            case R.id.grid:
//			            	mDraw.grid(10);
//			            	seekEffect = 1;
//							alphaValue.setProgress(mDraw.getGrid() * 255 / 100);
//							switchSeekEffect.setBackgroundResource(R.drawable.grid);
//							switchSeekEffectLabel.setText(getString(R.string.grid));
//			                break;
//			            case R.id.horizontal:
//			            	mDraw.horizontal(10);
//			            	seekEffect = 2;
//							alphaValue.setProgress(mDraw.getHorizontal() * 255 / 100);
//							switchSeekEffect.setBackgroundResource(R.drawable.horizontal);
//							switchSeekEffectLabel.setText(getString(R.string.horizontal));
//			                break;
//			            case R.id.vertical:
//			            	mDraw.vertical(10);
//			            	seekEffect = 3;
//							alphaValue.setProgress(mDraw.getVertical() * 255 / 100);
//							switchSeekEffect.setBackgroundResource(R.drawable.vertical);
//							switchSeekEffectLabel.setText(getString(R.string.vertical));
//			                break;
			            case R.id.original:
			            	photoBase.resetEffect();
			            	switchEffect.setBackgroundResource(R.drawable.icon64);
							switchEffectLabel.setText(getString(R.string.original));
			                break;
			            case R.id.takelayeredpic:
							photoBase.takeLayeredPic();
			                break;
			            
			        }
			        running = false;
	            	loadingSE.setVisibility(View.INVISIBLE);
	            	loadingSSE.setVisibility(View.INVISIBLE);
	            	switchEffect.setVisibility(View.VISIBLE);
	            	switchSeekEffect.setVisibility(View.VISIBLE);
				}
			}, 10);
    	}
    }

    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

		getWindow().setFormat(PixelFormat.UNKNOWN);
		setFullBrightness();
		
		try {
			File dir = new File("/sdcard/CameraOverlay");
			if (!dir.exists()) dir.mkdir();
		} catch (Exception e) {
		}

		preview = new Preview(this);
		this.setContentView(preview);

		photoBase = new DrawOnTop(this);

		controlInflater = LayoutInflater.from(getBaseContext());
		View viewControl = controlInflater.inflate(R.layout.main, null);
		LayoutParams layoutParamsControl = new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		this.addContentView(photoBase, layoutParamsControl);

		this.addContentView(viewControl, layoutParamsControl);
		
		effects = controlInflater.inflate(R.layout.effects, null);
		effects.setVisibility(View.GONE);
		LayoutParams layoutParamsEffects = new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		View scroll = findViewById(R.id.scrollEffects);
		this.addContentView(effects, layoutParamsEffects);
		

		final Button takePicture = (Button) findViewById(R.id.takepicture);
		takePicture.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (basefile == null) {
					basefile = "/sdcard/CameraOverlay/image" + sdf.format(new Date()) + ".jpg";
				}
				preview.takePicture(basefile);
				takePicture.setVisibility(View.GONE);
				Button takeNewPicture = (Button) findViewById(R.id.takenewpicture);
				takeNewPicture.setVisibility(View.VISIBLE);
				mensagemTemporaria(getString(R.string.savingAs) + preview.file);
			}
		});
		
		final Button takeNewPicture = (Button) findViewById(R.id.takenewpicture);
		takeNewPicture.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (photoBase.withoutPicture()) {
					Uri uri = Uri.fromFile(new File(preview.file));
					basefile = preview.file;
					loadBaseImage(uri);
				}
				takePicture.setVisibility(View.VISIBLE);
				takeNewPicture.setVisibility(View.GONE);
				try {
					preview.stopCamera();
				} catch (Exception e) {
				}
				preview.startCamera();
			}
		});
		
		Button loadImage = (Button) findViewById(R.id.loadimage);
		loadImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				try {
					preview.stopCamera();
					Intent photo = new Intent(Intent.ACTION_PICK);
					photo.setType("image/*");
					startActivityForResult(photo, 1);
				} catch (Exception e) {
					mensagemTemporaria(getString(R.string.waitloadimages));
				}

				takePicture.setVisibility(View.VISIBLE);
				takeNewPicture.setVisibility(View.GONE);
			}
		});


		final Button switchSeekEffect = (Button) findViewById(R.id.switchSeekEffect);
		final SeekBar alphaValue = (SeekBar) findViewById(R.id.alphaValue);
		alphaValue.setOnTouchListener(new View.OnTouchListener() {
			private boolean running = false;
			@Override
			public boolean onTouch(final View v, MotionEvent event) {
				final ProgressBar loadingSSE = (ProgressBar) findViewById(R.id.loadingSSE);

            	loadingSSE.setVisibility(View.VISIBLE);
            	switchSeekEffect.setVisibility(View.INVISIBLE);
				new Handler().postDelayed(new Thread() {
					@Override
					public void run() {
						if (!running) {
							running = true;
							int progress = ((SeekBar)v).getProgress();
							switch (seekEffect) {
							case 0:
								photoBase.setAlpha(progress);	
								break;
							case 1:
								photoBase.grid(progress * 100 / 255);	
								break;
							case 2:
								photoBase.horizontal(progress * 100 / 255);	
								break;
							case 3:
								photoBase.vertical(progress * 100 / 255);	
								break;
							default:
								break;
							}

			            	loadingSSE.setVisibility(View.INVISIBLE);
			            	switchSeekEffect.setVisibility(View.VISIBLE);
							running = false;
						}
					}
				}, 10);
				return false;
			}
		});

		
		switchSeekEffect.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				final TextView switchSeekEffectLabel = (TextView) findViewById(R.id.switchSeekEffectLabel);

				final ProgressBar loadingSSE = (ProgressBar) findViewById(R.id.loadingSSE);
            	loadingSSE.setVisibility(View.VISIBLE);
            	switchSeekEffect.setVisibility(View.INVISIBLE);
				new Handler().postDelayed(new Thread() {
					@Override
					public void run() {
						seekEffect++;
						if (seekEffect > 3)
							seekEffect = 0;
						switch (seekEffect) {
							case 0:
								alphaValue.setProgress(photoBase.getAlpha());
								switchSeekEffect.setBackgroundResource(R.drawable.alpha);
								switchSeekEffectLabel.setText(getString(R.string.alpha));
								break;
							case 1:
								alphaValue.setProgress(photoBase.getGrid() * 255 / 100);
								switchSeekEffect.setBackgroundResource(R.drawable.grid);
								switchSeekEffectLabel.setText(getString(R.string.grid));
								break;
							case 2:
								alphaValue.setProgress(photoBase.getHorizontal() * 255 / 100);
								switchSeekEffect.setBackgroundResource(R.drawable.horizontal);
								switchSeekEffectLabel.setText(getString(R.string.horizontal));
								break;
							case 3:
								alphaValue.setProgress(photoBase.getVertical() * 255 / 100);
								switchSeekEffect.setBackgroundResource(R.drawable.vertical);
								switchSeekEffectLabel.setText(getString(R.string.vertical));
								break;
							default:
								break;
						}

		            	loadingSSE.setVisibility(View.INVISIBLE);
		            	switchSeekEffect.setVisibility(View.VISIBLE);
					}
					
				}, 10);
			}
		});
		final Button switchEffect = (Button) findViewById(R.id.switchEffect);
//		switchEffect.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				final TextView switchEffectLabel = (TextView) findViewById(R.id.switchEffectLabel);
//
//				final ProgressBar loadingSE = (ProgressBar) findViewById(R.id.loadingSE);
//            	loadingSE.setVisibility(View.VISIBLE);
//            	switchEffect.setVisibility(View.INVISIBLE);
//				new Handler().postDelayed(new Thread() {
//					@Override
//					public void run() {
//						effect++;
//						if (effect > 12)
//							effect = 0;
//						switch (effect) {
//						case 0:
//			            	mDraw.alpha1();
//							switchEffect.setBackgroundResource(R.drawable.alpha1);
//							switchEffectLabel.setText(getString(R.string.alpha1));
//			            	break;
//			            case 1:
//			            	mDraw.alpha2();
//							switchEffect.setBackgroundResource(R.drawable.alpha2);
//							switchEffectLabel.setText(getString(R.string.alpha2));
//			            	break;
//			            case 2:
//			            	mDraw.alpha3();
//							switchEffect.setBackgroundResource(R.drawable.alpha3);
//							switchEffectLabel.setText(getString(R.string.alpha3));
//			            	break;
//			            case 3:
//			            	mDraw.alpha4();
//							switchEffect.setBackgroundResource(R.drawable.alpha2);
//							switchEffectLabel.setText(getString(R.string.alpha2));
//			            	break;
//			            case 4:
//			            	mDraw.alpha5();
//							switchEffect.setBackgroundResource(R.drawable.alpha3);
//							switchEffectLabel.setText(getString(R.string.alpha3));
//			            	break;
//			            case 5:
//			            	mDraw.grayScale();
//							switchEffect.setBackgroundResource(R.drawable.grayscale);
//							switchEffectLabel.setText(getString(R.string.gray_scale));
//			            	break;
//			            case 6:
//			            	mDraw.highContrast();
//							switchEffect.setBackgroundResource(R.drawable.highcontrast);
//							switchEffectLabel.setText(getString(R.string.high_contrast));
//			            	break;
//			            case 7:
//			            	mDraw.contrastBW();
//							switchEffect.setBackgroundResource(R.drawable.highcontrastbw);
//							switchEffectLabel.setText(getString(R.string.contrast_bw));
//			            	break;
//			            case 8:
//			            	mDraw.hue1();
//							switchEffect.setBackgroundResource(R.drawable.hue1);
//							switchEffectLabel.setText(getString(R.string.hue1));
//			                break;
//			            case 9:
//			            	mDraw.hue2();
//							switchEffect.setBackgroundResource(R.drawable.hue2);
//							switchEffectLabel.setText(getString(R.string.hue2));
//			                break;
//			            case 10:
//			            	mDraw.edgeDetect();
//							switchEffect.setBackgroundResource(R.drawable.edgedetect);
//							switchEffectLabel.setText(getString(R.string.edgedetect));
//			                break;
//			            case 11:
//			            	mDraw.edgeDetectTransparent();
//							switchEffect.setBackgroundResource(R.drawable.edgedetect);
//							switchEffectLabel.setText(getString(R.string.edgedetect));
//			                break;
//			            case 12:
//			            	mDraw.resetEffect();
//							switchEffect.setBackgroundResource(R.drawable.icon64);
//							switchEffectLabel.setText(getString(R.string.original));
//			                break;
//						default:
//							break;
//						}
//		            	loadingSE.setVisibility(View.INVISIBLE);
//		            	switchEffect.setVisibility(View.VISIBLE);
//					}
//				}, 10);
//			}
//		});
		final Button invert = (Button) findViewById(R.id.invert);
		invert.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				photoBase.invert();
				if (photoBase.isInverted()) {
					invert.setBackgroundResource(R.drawable.icon64);
				} else {
					invert.setBackgroundResource(R.drawable.invert);
				}
			}
		});
	}
	
	public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		if (resultCode == RESULT_OK) {
			Uri selectedImage = imageReturnedIntent.getData();
			basefile = preview.file;getRealPathFromURI(selectedImage);
			loadBaseImage(selectedImage);
		}
		try {
			preview.startCamera();
		} catch (Exception e) {
			mensagemTemporaria("Ops!" + e.getMessage());
			e.printStackTrace();
		}
	}

	private void loadBaseImage(Uri selectedImage) {
		try {
			InputStream imageStream = getContentResolver().openInputStream(selectedImage);
			Bitmap bmp = BitmapFactory.decodeStream(imageStream);
			photoBase.setBitmap(bmp);
			photoBase.selectedPicture();
		} catch (Exception e) {
			mensagemTemporaria("Ops!" + e.getMessage());
			e.printStackTrace();
		}
	}

	public void mensagemTemporaria(String dados) {
		final Toast t = Toast.makeText(this, dados, Toast.LENGTH_LONG);
		t.setGravity(Gravity.CENTER, 0, 0);
		t.show();
	}
	private void setFullBrightness() {  
		WindowManager.LayoutParams layout = getWindow().getAttributes();
		layout.screenBrightness = 1F;
		getWindow().setAttributes(layout);
	}
	
	public void toggleEffects(View v) {
		if (v.getId() == R.id.switchEffect) {
			if (effects.getVisibility() == View.GONE)
				effects.setVisibility(View.VISIBLE);
			else
				effects.setVisibility(View.GONE);
		}
	}
}