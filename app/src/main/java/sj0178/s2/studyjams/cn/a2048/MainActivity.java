package sj0178.s2.studyjams.cn.a2048;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    int Numbers[][] = new int[4][4];
    static int point  = 0;

    SharedPreferences pref;
    SharedPreferences.Editor editor;


    ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
    // 添加到map集合中

    SoundPool sp;
    SimpleAdapter sa;
    GridView gv;
    TextView pointText;
    private int musicMove, musicMerge;//定义一个整型用load（）；来设置suondID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String begin = getIntent().getStringExtra("start");
        gv = (GridView) findViewById(R.id.gridview);
        Button upBtn = (Button) findViewById(R.id.up);
        Button downBtn = (Button) findViewById(R.id.down);
        Button leftBtn = (Button) findViewById(R.id.left);
        Button rightBtn = (Button) findViewById(R.id.right);
        Button storeBtn = (Button) findViewById(R.id.store);
        Button loadBtn= (Button) findViewById(R.id.load);
        Button resetBtn= (Button) findViewById(R.id.reset);
        pointText = (TextView) findViewById(R.id.point_text);

        upBtn.setOnClickListener(this);
        downBtn.setOnClickListener(this);
        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
        storeBtn.setOnClickListener(this);
        loadBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);


        pref = PreferenceManager.getDefaultSharedPreferences(this);


        sp = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量

        musicMove = sp.load(this, R.raw.move, 1);
        musicMerge = sp.load(this, R.raw.merge, 1);

        startGame(Integer.valueOf(begin));
        generate();
        setAdapter();

    }

    void generate() {
        int times = 5;

        while(times--!=0){
            int x = (int) (4 * Math.random());
            int y = (int) (4 * Math.random());
            if (Numbers[x][y] == 0)
                Numbers[x][y] = 2 * (int) (2 * Math.random()) + 2;

        }
    }

    boolean over(){
        for(int i=0;i<4;i++)
            for(int j=0;j<4;j++)
                if(Numbers[i][j]==0)
                    return false;
        for(int i=0;i<4-1;i++)
            for(int j=0;j<4-1;j++)
                if(Numbers[i][j]==Numbers[i][j+1]|Numbers[i][j]==Numbers[i+1][j])
                    return false;
        for(int i=0;i<4-1;i++)
            if(Numbers[3][i]==Numbers[3][i+1]|Numbers[i][3]==Numbers[i+1][3])
                return false;
        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.up:
                moveup();
                playSound(0);
                generate();
                setAdapter();
                break;
            case R.id.down:
                movedown();
                playSound(0);
                generate();
                setAdapter();
                break;
            case R.id.left:
                moveleft();
                playSound(0);
                generate();
                setAdapter();
                break;
            case R.id.right:
                moveright();
                playSound(0);
                generate();
                setAdapter();
                break;
            case R.id.store:
                editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                for(int i=0;i<4;i++){
                    for(int j=0;j<4;j++)
                        editor.putInt("number" + String.valueOf(i) + String.valueOf(j),Numbers[i][j]);
                }
                editor.putInt("point",point);
                editor.apply();
                break;
            case R.id.load:
                pref = getSharedPreferences("data",MODE_PRIVATE);
                for(int i=0;i<4;i++){
                    for(int j=0;j<4;j++){
                        Numbers[i][j] =  pref.getInt("number" + String.valueOf(i) + String.valueOf(j), 0);
                    }
                }
                point = pref.getInt("point",0);
                setAdapter();
                break;
            case R.id.reset:
                for(int i=0;i<4;i++){
                    for(int j=0;j<4;j++){
                        Numbers[i][j] =  0;
                    }
                }
                point = 0;
                generate();
                setAdapter();
                break;
            default:
                break;
        }
        if(over() == true){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("游戏结束");
            builder.setMessage("GG");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Intent intent = new Intent(MainActivity.this,SignInActivity.class);
                    startActivity(intent);
                }
            });
            builder.create().show();
        }

        pointText.setText("Your point: " + String.valueOf(point));

    }

    void moveup() {
        for (int l = 0; l < 4; l++)
            for (int h = 0; h < 4; h++)
                if (Numbers[h][l] != 0) {
                    int temp = Numbers[h][l];
                    int pre = h - 1;
                    while (pre >= 0 && Numbers[pre][l] == 0) {
                        Numbers[pre][l] = temp;
                        Numbers[pre + 1][l] = 0;
                        pre--;

                    }
                }
        for (int l = 0; l < 4; l++)
            for (int h = 0; h < 4; h++)
                if (h + 1 < 4
                        && (Numbers[h][l] == Numbers[h + 1][l])
                        && (Numbers[h][l] != 0 || Numbers[h + 1][l] != 0)) {
                    Numbers[h][l] = Numbers[h][l] + Numbers[h + 1][l];
                    point += 2*Numbers[h][l];
                    Numbers[h + 1][l] = 0;
                    playSound(1);

                }

        for (int l = 0; l < 4; l++)
            for (int h = 0; h < 4; h++)
                if (Numbers[h][l] != 0) {
                    int temp = Numbers[h][l];
                    int pre = h - 1;
                    while (pre >= 0 && Numbers[pre][l] == 0) {
                        Numbers[pre][l] = temp;
                        Numbers[pre + 1][l] = 0;
                        pre--;

                    }
                }

    }

    void movedown() {
        for (int l = 3; l >= 0; l--)
            for (int h = 3; h >= 0; h--)
                if (Numbers[h][l] != 0) {
                    int temp = Numbers[h][l];
                    int pre = h + 1;
                    while (pre <= 3 && Numbers[pre][l] == 0) {
                        Numbers[pre][l] = temp;
                        Numbers[pre - 1][l] = 0;
                        pre++;

                    }
                }
        for (int l = 3; l >= 0; l--)
            for (int h = 3; h >= 0; h--)
                if (h + 1 < 4
                        && (Numbers[h][l] == Numbers[h + 1][l])
                        && (Numbers[h][l] != 0 || Numbers[h + 1][l] != 0)) {
                    Numbers[h + 1][l] = Numbers[h][l]
                            + Numbers[h + 1][l];
                    point += 2*Numbers[h][l];
                    Numbers[h][l] = 0;
                    playSound(1);

                }

        for (int l = 3; l >= 0; l--)
            for (int h = 3; h >= 0; h--)
                if (Numbers[h][l] != 0) {
                    int temp = Numbers[h][l];
                    int pre = h + 1;
                    while (pre <= 3 && Numbers[pre][l] == 0) {
                        Numbers[pre][l] = temp;
                        Numbers[pre - 1][l] = 0;
                        pre++;

                    }
                }


    }

    void moveleft() {

        for (int h = 0; h < 4; h++)
            for (int l = 0; l < 4; l++)
                if (Numbers[h][l] != 0) {
                    int temp = Numbers[h][l];
                    int pre = l - 1;
                    while (pre >= 0 && Numbers[h][pre] == 0) {
                        Numbers[h][pre] = temp;
                        Numbers[h][pre + 1] = 0;
                        pre--;

                    }
                }
        for (int h = 0; h < 4; h++)
            for (int l = 0; l < 4; l++)
                if (l + 1 < 4
                        && (Numbers[h][l] == Numbers[h][l + 1])
                        && (Numbers[h][l] != 0 || Numbers[h][l + 1] != 0)) {
                    Numbers[h][l] = Numbers[h][l] + Numbers[h][l + 1];
                    point += 2*Numbers[h][l];
                    Numbers[h][l + 1] = 0;
                    playSound(1);

                }

        for (int h = 0; h < 4; h++)
            for (int l = 0; l < 4; l++)
                if (Numbers[h][l] != 0) {
                    int temp = Numbers[h][l];
                    int pre = l - 1;
                    while (pre >= 0 && Numbers[h][pre] == 0) {
                        Numbers[h][pre] = temp;
                        Numbers[h][pre + 1] = 0;
                        pre--;

                    }
                }


    }

    void moveright() {
        for (int h = 3; h >= 0; h--)
            for (int l = 3; l >= 0; l--)
                if (Numbers[h][l] != 0) {
                    int temp = Numbers[h][l];
                    int pre = l + 1;
                    while (pre <= 3 && Numbers[h][pre] == 0) {
                        Numbers[h][pre] = temp;
                        Numbers[h][pre - 1] = 0;
                        pre++;

                    }
                }

        for (int h = 3; h >= 0; h--)
            for (int l = 3; l >= 0; l--)
                if (l + 1 < 4
                        && (Numbers[h][l] == Numbers[h][l + 1])
                        && (Numbers[h][l] != 0 || Numbers[h][l + 1] != 0)) {
                    Numbers[h][l + 1] = Numbers[h][l]
                            + Numbers[h][l + 1];
                    point += 2*Numbers[h][l];
                    Numbers[h][l] = 0;
                    playSound(1);

                }
        for (int h = 3; h >= 0; h--)
            for (int l = 3; l >= 0; l--)
                if (Numbers[h][l] != 0) {
                    int temp = Numbers[h][l];
                    int pre = l + 1;
                    while (pre <= 3 && Numbers[h][pre] == 0) {
                        Numbers[h][pre] = temp;
                        Numbers[h][pre - 1] = 0;
                        pre++;

                    }
                }
    }

    void setAdapter() {
        arrayList.clear();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("title", String.valueOf(Numbers[i][j]));
                arrayList.add(map);
            }
        }

        sa = new SimpleAdapter(this, arrayList,
                R.layout.gridview_item, new String[]{"title"},
                new int[]{R.id.tv});
        //为GridView绑定Adapter
        gv.setAdapter(sa);
    }


    private void playSound(int song) {

        if (song == 0) {
            sp.play(
                    musicMove,
                    0.1f,   //左耳道音量【0~1】
                    0.5f,   //右耳道音量【0~1】
                    0,     //播放优先级【0表示最低优先级】
                    0,     //循环模式【0表示循环一次，-1表示一直循环，其他表示数字+1表示当前数字对应的循环次数】
                    1     //播放速度【1是正常，范围从0~2】
            );
        } else {
            sp.play(musicMerge, 0.1f, 0.5f, 0, 0, 1);
        }
    }

    void startGame(int begin){
        if(begin == 1){
            pref = getSharedPreferences("data",MODE_PRIVATE);
            for(int i=0;i<4;i++){
                for(int j=0;j<4;j++){
                    Numbers[i][j] =  pref.getInt("number" + String.valueOf(i) + String.valueOf(j), 0);
                }
            }
            point = pref.getInt("point",0);
            setAdapter();
            pointText.setText("Your point: " + String.valueOf(point));
        }
    }

}
