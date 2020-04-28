package com.ctop.studentcard.feature.setting.sound;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.ctop.studentcard.R;
import com.ctop.studentcard.base.BaseActivity;
import com.ctop.studentcard.util.LogUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 手机铃声
 */
public class RingActivity extends BaseActivity {

    private ListView listview;
//    public RingAdapter adapter;

    public Context context;

    public Cursor cursor;

    public SharedPreferences.Editor editor;

    public int index = 0;

    public MediaPlayer mMediaPlayer;

    public TelephonyManager mTelephonyMgr;

    public List<String> ringList = new ArrayList<String>();

    public RingtoneManager rm;

    public SharedPreferences sp;

//    public void getRing() {
//        this.rm = new RingtoneManager(this);
//        this.rm.setType(1);
//        this.cursor = this.rm.getCursor();
//        if (this.cursor.moveToFirst())
//            do {
//                this.ringList.add(this.cursor.getString(1));
//            } while (this.cursor.moveToNext());
//        this.adapter = new RingAdapter();
//        this.listview.setAdapter(this.adapter);
//        this.listview.requestFocus();
//        this.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> param1AdapterView, View param1View, int posotion, long param1Long) {
//                LogUtil.e("position = "+posotion);
//                RingtoneManager.setActualDefaultRingtoneUri(RingActivity.this.context, 1, RingActivity.this.rm.getRingtoneUri(posotion));
//                RingActivity.this.index = posotion;
//                RingActivity.this.adapter.setSelectedItem(RingActivity.this.index);
//                RingActivity.this.adapter.notifyDataSetChanged();
//                RingActivity.this.editor.putString("ringname", RingActivity.this.ringList.get(posotion));
//                RingActivity.this.editor.commit();
//                RingActivity.this.playRing();
//            }
//        });
//    }

    protected void initView() {
        this.mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        this.mTopTitle.setText(getString(2131492915));
//        this.sp = getSharedPreferences("call_ring", 0);
//        this.editor = this.sp.edit();
    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.base_slide_right_out);
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        this.context = this;
        setContentView(R.layout.ring_acitvity);
        initView();
//        getRing();
        try{


//            // 外部调用传入一个url
//            RingtoneSetting.setmUrl("android.resource://" + getPackageName() + "/" + R.raw.ring1);
////            getResources().openRawResource(R.raw.ring1);
//            // 开始设置
//            RingtoneSetting.setting(RingActivity.this);



//            setMyMessage(context,"android.resource://" + getPackageName() + "/" + R.raw.ring1);
//            Uri mUri =  Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ring1);
//            RingtoneManager.setActualDefaultRingtoneUri(RingActivity.this.context,  RingtoneManager.TYPE_RINGTONE, mUri);
//            String filepatsh=mUri.getPath();
//            File chosenFile=new File("android.resource://" + getPackageName() + "/" + R.raw.ring1);
//
////            File chosenFile = uriToFile(mUri,context);
////             new File(path);
//
//            //We get the Uri here fro ma file's absolute path.
////            Uri ringtoneUri = Uri.parse(chosenFile.getAbsolutePath());
//
//            //We now create a new content values object to store all the information
//            //about the ringtone.
//            ContentValues values = new ContentValues();
//            values.put(MediaStore.MediaColumns.DATA, chosenFile.getAbsolutePath());
//            values.put(MediaStore.MediaColumns.TITLE, chosenFile.getName());
//            values.put(MediaStore.MediaColumns.SIZE, chosenFile.length());
//            values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
//            values.put(MediaStore.Audio.AudioColumns.ARTIST, context.getString(R.string.app_name));
//            values.put(MediaStore.Audio.AudioColumns.IS_RINGTONE, true);
//            values.put(MediaStore.Audio.AudioColumns.IS_NOTIFICATION, false);
//            values.put(MediaStore.Audio.AudioColumns.IS_ALARM, false);
//            values.put(MediaStore.Audio.AudioColumns.IS_MUSIC, false);
//
//            //Work with the content resolver now
//            //First get the file we may have added previously and delete it,
//            //otherwise we will fill up the ringtone manager with a bunch of copies over time.
//            Uri uri = MediaStore.Audio.Media.getContentUriForPath(chosenFile.getAbsolutePath());
//            context.getContentResolver().delete(uri,
//                    MediaStore.MediaColumns.DATA + "=\"" + chosenFile.getAbsolutePath() + "\"",
//                    null);
//
//            //Ok now insert it
//            Uri newUri = context.getContentResolver().insert(uri, values);
//
//            //Ok now set the ringtone from the content manager's uri, NOT the file's uri
//            RingtoneManager.setActualDefaultRingtoneUri(
//                    context,
//                    RingtoneManager.TYPE_RINGTONE,
//                    newUri
//            );




//            if (hasFolder(strRingtoneFolder)) {
//                // 打开系统铃声设置
//                Intent intent = new Intent(
//                        RingtoneManager.ACTION_RINGTONE_PICKER);
//                // 类型为来电RINGTONE
//                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
//                        RingtoneManager.TYPE_RINGTONE);
//                // 设置显示的title
//                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE,
//                        "欧阳鹏设置来电铃声");
//                // 当设置完成之后返回到当前的Activity
//                startActivityForResult(intent, CODE_RINGSTONE);
//            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String strRingtoneFolder = "/system/media/audio/ringtones";

    public static final int CODE_RINGSTONE = 0;

    private boolean hasFolder(String strFolder) {
        boolean btmp = false;
        File f = new File(strFolder);
        if (!f.exists()) {
            if (f.mkdirs()) {
                btmp = true;
            } else {
                btmp = false;
            }
        } else {
            btmp = true;
        }
        return btmp;
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode != RESULT_OK) {
//            return;
//        }
//        // 得到我们选择的铃声
//        Uri pickedUri = data
//                .getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
//        if (pickedUri != null) {
//            switch (requestCode) {
//                case CODE_RINGSTONE:
//                    // 将我们选择的铃声设置成为默认来电铃声
//                    RingtoneManager.setActualDefaultRingtoneUri(this,
//                            RingtoneManager.TYPE_RINGTONE, pickedUri);
//                    break;
//
//            }
//        }
//    }
//


    //设置为短信铃声
    static void setMyMessage(Context context, String path) {
        LogUtil.d("absoultpath:"+path);
        File sdfile = new File(path);
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, sdfile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, sdfile.getName());
        values.put(MediaStore.MediaColumns.SIZE, sdfile.length());
        values.put(MediaStore.Audio.Media.IS_RINGTONE, false);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
        values.put(MediaStore.Audio.Media.IS_ALARM, false);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(path);
        ContentResolver cr =context.getContentResolver();
        Cursor cursor = cr.query(uri,new String[]{MediaStore.MediaColumns._ID},"_data = ? ",new String[]{path},null,null);
//        cursor.moveToFirst();
        cursor.moveToNext();
        int ringtoneID = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
        Uri newUri = ContentUris.withAppendedId(uri, Long.valueOf(ringtoneID));
        RingtoneManager.setActualDefaultRingtoneUri(context,RingtoneManager.TYPE_NOTIFICATION,newUri);
        Intent i = new Intent("com.android.mms.change.ring");
        i.putExtra("newUri", uri.toString());
        context.sendBroadcast(i);//发一个广播到短信模块，让短信模块自己修改自己的SharedPreferences

        Toast.makeText(context, "setMessage-----"+sdfile.getName()+"---------------->",
                Toast.LENGTH_SHORT).show();
    }

//    protected void onDestroy() {
//        super.onDestroy();
//        if (!this.cursor.isClosed())
//            this.cursor.close();
//        if (this.mMediaPlayer != null && this.mMediaPlayer.isPlaying()) {
//            this.mMediaPlayer.stop();
//            this.mMediaPlayer.reset();
//            this.mMediaPlayer.release();
//            this.mMediaPlayer = null;
//        }
//    }

//    protected void onPause() {
//        super.onPause();
//        if (this.mMediaPlayer != null && this.mMediaPlayer.isPlaying()) {
//            this.mMediaPlayer.stop();
//            this.mMediaPlayer.reset();
//            this.mMediaPlayer.release();
//            this.mMediaPlayer = null;
//        }
//    }

//    protected void onResume() {
//        super.onResume();
//        for (int i = 0; i < this.ringList.size(); i++) {
//            if (this.rm.getRingtoneUri(i).toString().equals(Settings.System.getString(getContentResolver(), "ringtone"))) {
//                StringBuilder stringBuilder = new StringBuilder();
//                stringBuilder.append("ringList index:");
//                stringBuilder.append(i);
//                Log.d("zhangtong", stringBuilder.toString());
//                this.index = i;
//                this.listview.post(new Runnable() {
//                    public void run() {
//                        RingActivity.this.adapter.setSelectedItem(RingActivity.this.index);
//                        RingActivity.this.adapter.notifyDataSetChanged();
//                        RingActivity.this.listview.requestFocusFromTouch();
//                        RingActivity.this.listview.setSelection(RingActivity.this.index);
//                    }
//                });
//                return;
//            }
//        }
//    }

    public void playRing() {
        if (this.mMediaPlayer != null && this.mMediaPlayer.isPlaying()) {
            this.mMediaPlayer.stop();
            this.mMediaPlayer.reset();
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
        }
        try {
            this.mMediaPlayer = new MediaPlayer();
            this.mMediaPlayer.setDataSource((Context) this, this.rm.getRingtoneUri(this.index));
            this.mMediaPlayer.prepare();
            this.mMediaPlayer.start();
            return;
        } catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
    }

    protected void setUpView() {
    }

//    public class RingAdapter extends BaseAdapter {
//        private int selectedItem = -1;
//
//        public int getCount() {
//            return RingActivity.this.ringList.size();
//        }
//
//        public Object getItem(int param1Int) {
//            return null;
//        }
//
//        public long getItemId(int param1Int) {
//            return 0L;
//        }
//
//        public View getView(int param1Int, View param1View, ViewGroup param1ViewGroup) {
//            View view;
//            RingActivity.ViewHolder viewHolder;
//            if (param1View == null) {
//                viewHolder = new RingActivity.ViewHolder();
//                object = LayoutInflater.from(RingActivity.this.context).inflate(2131361832, null);
//                viewHolder.textView= param1View.findViewById(2131230842);
//                param1View. findViewById(2131230770));
//                view.setTag(viewHolder);
//            } else {
//                RingActivity.ViewHolder viewHolder1 = (RingActivity.ViewHolder) viewHolder.getTag();
//                view = viewHolder;
//                viewHolder = viewHolder1;
//            }
//            viewHolder.textView.setText(RingActivity.this.ringList.get(param1Int));
//            if (this.selectedItem == param1Int) {
//                viewHolder.imageView.setVisibility(0);
//                viewHolder.imageView.setImageResource(2131165301);
//                return (View) object;
//            }
//            viewHolder.imageView.setVisibility(8);
//            return (View) object;
//        }
//
//        public void setSelectedItem(int param1Int) {
//            this.selectedItem = param1Int;
//        }
//    }

    public class ViewHolder {
        private ImageView imageView;

        private MarqueeText textView;
    }



//    public static File uriToFile(Uri uri,Context context) {
//        String path = null;
//        if ("file".equals(uri.getScheme())) {
//            LogUtil.e("aaaaaaaaaaaaaaaaaaaaaaa");
//        } else if ("content".equals(uri.getScheme())) {
//            LogUtil.e("bbbbbbbbbbbbbbbbbbbb");
//            // 4.2.2以后
//            String[] proj = { MediaStore.Images.Media.DATA };
//            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
//            if (cursor.moveToFirst()) {
//                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                path = cursor.getString(columnIndex);
//            }
//            cursor.close();
//
//            return new File(path);
//        } else {
//            //Log.i(TAG, "Uri Scheme:" + uri.getScheme());
//        }
//        return null;
//    }
}
